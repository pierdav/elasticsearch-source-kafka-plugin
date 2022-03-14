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

package com.boundstream.elasticsearch.plugin.listener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.TimeZone;

import com.boundstream.elasticsearch.plugin.event.inputEvent;
import com.boundstream.elasticsearch.plugin.event.inputEvent.Operation;
import com.boundstream.elasticsearch.plugin.filter.metaFilter;
import com.boundstream.elasticsearch.plugin.output.outputService;
import com.boundstream.elasticsearch.plugin.render.sourceContent;
import com.boundstream.elasticsearch.plugin.settings.BsSettings;
import com.boundstream.elasticsearch.plugin.settings.Pipeline;
import com.boundstream.elasticsearch.plugin.settings.settingsService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.MapMessage;
import org.elasticsearch.cluster.service.ClusterService;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.common.xcontent.support.XContentMapValues;
import org.elasticsearch.index.engine.Engine;
import org.elasticsearch.index.shard.IndexingOperationListener;
import org.elasticsearch.index.shard.ShardId;
import org.joda.time.DateTime;

/**
 * 
 */
public class streamIndexOperation implements IndexingOperationListener {

    private static final Logger logger = LogManager.getLogger(streamIndexOperation.class);
    private MapMessage mapMsg;
    private inputEvent inputEvent;
    private settingsService settingsSrv;
    private Engine.IndexResult indexResult;
    private BytesReference indexSource;
    private Map<String, Object> concurrentMap;
    private ServiceLoader<outputService> outputLoader;
    private Operation operation;
    private String indexName;
    private String indexType;
    private String indexId;
    private long startNano;
    
    private ClusterService clusterService;
   
    /**
     * 
     * @param concurrentMap
     * @param clusterService
     * @param outputLoader
     * @param indexName
     */
    public streamIndexOperation(Map<String, Object> concurrentMap, ClusterService clusterService, ServiceLoader<outputService> outputLoader, String indexName) {
        this.concurrentMap = concurrentMap;
        this.outputLoader = outputLoader;
        this.clusterService = clusterService;
        settingsSrv = new settingsService(concurrentMap);
        mapMsg = new MapMessage();
    }
    /**
     * 
     * @param shardId
     * @param operation
     * @return
     */
    @Override
    public Engine.Index preIndex(ShardId shardId, Engine.Index operation) {
        indexName = shardId.getIndex().getName();
        return operation;
    }

    
    /**
     * 
     * @param shardId
     * @param index
     * @param result
     */
    @Override
    public void postIndex(ShardId shardId, Engine.Index index, Engine.IndexResult result) {

        BsSettings bsSettings = (BsSettings)concurrentMap.get("bsSettings");
        metaFilter meta = new metaFilter();
        boolean processNode = false;
        sourceContent sourceCnt = new sourceContent();

        mapMsg = new MapMessage();

        indexName = shardId.getIndex().getName();
        indexId = index.id();
        indexType = index.type();
        indexResult = result;
        indexSource = index.source();
        startNano = System.nanoTime();
        operation = Operation.INDEX;

        mapMsg.put("message", "Post Index Event Emitted");
        mapMsg.put("index name", indexName);
        mapMsg.put("index id", indexId);
        mapMsg.put("operation", operation.toString());
        logger.trace(mapMsg);

        
        for (Integer i = 0; i < clusterService.state().routingTable().index(indexName).shards().size(); i++) {
            if(concurrentMap.get("nodeId").equals(clusterService.state().routingTable().index(indexName).shard(i).primaryShard().currentNodeId()))
            {
                processNode = true;
                break;
            }   
        }
        if(processNode==false)
        {
            return;
        }

        if (indexSource == null || indexSource.length() == 0 || bsSettings.getPipelines()==null) {
            return;
        }
        
        if (indexResult.isCreated()==true) {
            operation = Operation.CREATE;
        }

        //register input
        inputEvent = new inputEvent(
            concurrentMap,
            indexName, 
            indexType, 
            indexId,
            operation,
            indexResult.getVersion(), 
            indexSource,
            new DateTime(), 
            startNano);

        //convert input to json
        HashMap<String, String> jsonIoDoc = sourceCnt.jsonIoString(bsSettings, inputEvent);

        if(indexName.equals(bsSettings.getConfig().getIndex()))
        {
            JsonObject docJson = new Gson().fromJson(jsonIoDoc.get("input"), JsonObject.class);
            settingsSrv.store(docJson);
        }

        ClassLoader original = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(null);
        
        try {
            for (Integer i = 0; i < bsSettings.getPipelines().size(); i++) {
                Pipeline pipeline = bsSettings.getPipelines().get(i);

                if(pipeline.getActive()==false)
                {
                    continue;
                }
                
                if(meta.matchPipeline(pipeline, operation, indexName, indexId)==false)
                {
                    continue;
                }
                
                Map<String, Object> retMap = new Gson().fromJson(
                    jsonIoDoc.get("input"), new TypeToken<HashMap<String, Object>>() {}.getType()
                );
                Map<String, Object> filteredMap = XContentMapValues.filter(retMap, new String[]{pipeline.getRender()}, Strings.EMPTY_ARRAY);
                jsonIoDoc.put("output", new Gson().toJson(filteredMap));
                jsonIoDoc.put("output", jsonIoDoc.get("input"));
                
                for (outputService serviceOutput : outputLoader) {
                    if(serviceOutput.connect(concurrentMap)==false || serviceOutput.isEnabled()==false)
                    {
                        continue;
                    }
                    if(serviceOutput.getName().equals(pipeline.getBroker()) && serviceOutput.isEnabled()==true)
                    {
                        mapMsg.clear();
                        mapMsg.put("title", "Send Message");
                        mapMsg.put("message", jsonIoDoc.get("output"));
                        mapMsg.put("broker", pipeline.getBroker());
                        mapMsg.put("pipeline", pipeline.getName());
                        mapMsg.put("tag", pipeline.getTag()==null ? "" : pipeline.getTag());
                        mapMsg.put("topic", pipeline.getTopic());
                        mapMsg.put("filter", pipeline.getFilter()==null ? "" : pipeline.getFilter());
                        mapMsg.put("render", pipeline.getRender()==null ? "" : pipeline.getRender());
                        logger.trace(mapMsg);
    
                        serviceOutput.sendMessage(concurrentMap, jsonIoDoc.get("output"), pipeline);
                    }
                }
            }
        } catch (Exception e2) {
            System.out.println("Exception"+e2.toString());
        }
        Thread.currentThread().setContextClassLoader(original);
    }
}
