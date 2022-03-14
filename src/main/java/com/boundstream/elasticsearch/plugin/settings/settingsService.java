/*
 * Copyright 2021 Pierdav.com or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.boundstream.elasticsearch.plugin.settings;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.SSLContext;

import com.boundstream.elasticsearch.plugin.output.outputService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.MapMessage;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;

public class settingsService {
    
    
    private static final Logger logger = LogManager.getLogger(settingsService.class);
    private MapMessage mapMsg;

    public boolean isInitSettingsRunning = false;
    public boolean isSettingsFound = false;
    Map<String, Object> concurrentMap;
    BsSettings bsSettings;

    /*
    * 
    * 
    * 
    */
    public settingsService(Map<String, Object> concurrentMap) {
        mapMsg = new MapMessage();
        this.concurrentMap = concurrentMap;
        this.bsSettings = (BsSettings)concurrentMap.get("bsSettings");
	}

    /*
    * 
    * 
    * 
    */
    public void load(ServiceLoader<outputService> outputLoader)  {
        if((Boolean)concurrentMap.get("isSettingsFound") == true)
        {
            for (outputService service : outputLoader) {
                Boolean isConnected = service.connect(concurrentMap);
                if(isConnected==true)
                {
                    mapMsg.clear();
                    mapMsg.put("message", service.getClass().getName()+" is connected ");
                    logger.trace(mapMsg);
                }
            }
        }
    }

    /*
    * 
    * 
    * 
    */
    public void store(JsonObject setting)  {
        BsSettings newBsSettings = new Gson().fromJson(setting, BsSettings.class);
        
        newBsSettings.setConfig(bsSettings.getConfig());
        concurrentMap.put("bsSettings", newBsSettings);
        concurrentMap.put("isInitSettingsRunning", true);
        concurrentMap.put("isSettingsFound", true);

        mapMsg.clear();
        mapMsg.put("message", "Settings stored");
        mapMsg.put("document", bsSettings.getConfig().getIndex() + "/_doc/" + bsSettings.getConfig().getDocId());
        logger.trace(mapMsg);
    }
    /*
    * 
    * 
    * 
    */
    public void set(Client client, ServiceLoader<outputService> outputLoader) throws InterruptedException {

        Thread remoteOutputStream = new Thread() {
            @Override
            public void run() {

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e3) {
                    e3.printStackTrace();
                }

                if(bsSettings.getConfig().getHost()!=null 
                && bsSettings.getConfig().getPassword()!=null 
                && bsSettings.getConfig().getUsername()!=null 
                && bsSettings.getConfig().getPort()!=null 
                && bsSettings.getConfig().getProtocol()!=null)
                {
                    try {

                        SSLContextBuilder sslContextBuilder = new SSLContextBuilder();
                        sslContextBuilder.loadTrustMaterial(null, new TrustAllStrategy());
                        SSLContext sslContext = sslContextBuilder.build();
    
                        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                        credentialsProvider.setCredentials(AuthScope.ANY,
                            new UsernamePasswordCredentials(bsSettings.getConfig().getUsername(), bsSettings.getConfig().getPassword()));
    
                        RestClientBuilder builder = RestClient.builder(new HttpHost(
                            bsSettings.getConfig().getHost(), 
                            bsSettings.getConfig().getPort(), 
                            bsSettings.getConfig().getProtocol()));
                        
    
                    
                        builder = builder.setHttpClientConfigCallback(httpAsyncClientBuilder -> httpAsyncClientBuilder
                        .setDefaultCredentialsProvider(credentialsProvider)
                        .setSSLContext(sslContext).setSSLHostnameVerifier(new NoopHostnameVerifier())
                        );
    
                        RestClient restClient = builder.build();
                        Request restRequestSettings = new Request("GET", bsSettings.getConfig().getIndex() + "/_doc/" + bsSettings.getConfig().getDocId());
                        Response restResponseSettings;
    
                        mapMsg.put("message", "Settings gotten");
                        mapMsg.put("document", bsSettings.getConfig().getIndex() + "/_doc/" + bsSettings.getConfig().getDocId());
                        logger.trace(mapMsg);
    
                        try {
                            restResponseSettings = restClient.performRequest(restRequestSettings);
                            String message = EntityUtils.toString(restResponseSettings.getEntity());
                            JsonObject docJson = new Gson().fromJson(message, JsonObject.class);
                            JsonObject sourceJson = docJson.get("_source").getAsJsonObject();
                            store(sourceJson);
                            load(outputLoader) ;
             
                        } catch (IOException e2) {
                            e2.printStackTrace();
                            isInitSettingsRunning = false;
                        }
                        restClient.close();
    
                    } catch (IOException | NoSuchAlgorithmException | KeyManagementException | KeyStoreException e2) {
                        isInitSettingsRunning = false;
                    }
                }
                else // Deprecated
                {
                    GetRequest getRequest = new GetRequest(bsSettings.getConfig().getIndex(), bsSettings.getConfig().getDocId());
                    GetResponse getResponse;
                    try {
                        getResponse = client.get(getRequest).get();
                        String message = getResponse.getSourceAsString();
                        JsonObject docJson = new Gson().fromJson(message, JsonObject.class);
                        store(docJson);
                        load(outputLoader);
                        
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                        isInitSettingsRunning = false;
                        
                    }
                }
                
            }
        };
        if(isInitSettingsRunning==false)
        {
            isInitSettingsRunning = true;
            remoteOutputStream.start();
        }
    }
}
