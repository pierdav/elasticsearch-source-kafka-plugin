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

package com.boundstream.elasticsearch.plugin;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import com.boundstream.elasticsearch.plugin.listener.streamIndexOperation;
import com.boundstream.elasticsearch.plugin.output.outputService;
import com.boundstream.elasticsearch.plugin.settings.BsSettings;
import com.boundstream.elasticsearch.plugin.settings.Config;
import com.boundstream.elasticsearch.plugin.settings.settingsService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.collect.ImmutableList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.MapMessage;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.ClusterChangedEvent;
import org.elasticsearch.cluster.LocalNodeMasterListener;
import org.elasticsearch.cluster.metadata.IndexNameExpressionResolver;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.cluster.service.ClusterService;
import org.elasticsearch.common.io.stream.NamedWriteableRegistry;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.env.NodeEnvironment;
import org.elasticsearch.index.IndexModule;
import org.elasticsearch.index.shard.IndexingOperationListener;
import org.elasticsearch.monitor.jvm.JvmService;
import org.elasticsearch.plugins.ActionPlugin;
import org.elasticsearch.plugins.DiscoveryPlugin;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.repositories.RepositoriesService;
import org.elasticsearch.script.ScriptService;
import org.elasticsearch.threadpool.ThreadPool;
import org.elasticsearch.watcher.ResourceWatcherService;
import org.elasticsearch.xcontent.NamedXContentRegistry;

public class BoundstreamPlugin extends Plugin implements ActionPlugin, DiscoveryPlugin, LocalNodeMasterListener, IndexingOperationListener {

    private static final Logger logger = LogManager.getLogger(BoundstreamPlugin.class);
    private ClusterService clusterService;
    private Client client;
    private MapMessage mapMsg;

    Map<String, Object> concurrentMap;
    ArrayList<String> indexList = new ArrayList<String>();
    Settings settings;
    BsSettings bsSettings;
    settingsService settingsSrv;
    ServiceLoader<outputService> outputLoader;

    /**
     * 
     * @param settings
     * @param configPath
     * @throws InterruptedException
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     */
    public BoundstreamPlugin(Settings settings, Path configPath)
            throws InterruptedException, JsonParseException, JsonMappingException, IOException {
        
        super();

        this.settings = settings;
        bsSettings = new BsSettings();
        mapMsg = new MapMessage();

        logger.trace("Starting Boundstream plugin on node name: "+ settings.get("node.name")+ " endpoint: " + settings.get("endpoint"));

        Environment esEnvironment = new Environment(settings, configPath);

        Path configDir = esEnvironment.configFile();
        Path appYamlFile = configDir.resolve("eska-config.yml");

        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory())
                .configure(MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS, false)
                .configure(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS, false);
        objectMapper.findAndRegisterModules();

        Config bsConfig = bsSettings.getConfig();
        try
        {
            bsConfig = objectMapper.readValue(appYamlFile.toFile(), Config.class);
            bsSettings.setConfig(bsConfig);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        
        /*
        */
        outputLoader = ServiceLoader.load(outputService.class);
        
        concurrentMap = new ConcurrentHashMap<String, Object>();
        concurrentMap.put("bsSettings", bsSettings);
        concurrentMap.put("esEnvironment", esEnvironment);
        concurrentMap.put("outputLoader", outputLoader);
        concurrentMap.put("isInitSettingsRunning", false);
        concurrentMap.put("isSettingsFound", false);
        
        settingsSrv = new settingsService(concurrentMap);
    }

    /**
     * 
     * @param client
     * @param clusterService
     * @param threadPool
     * @param resourceWatcherService
     * @param scriptService
     * @param xContentRegistry
     * @param environment
     * @param nodeEnvironment
     * @param namedWriteableRegistry
     * @param indexNameExpressionResolver
     * @param repositoriesServiceSupplier
     * @return
     */
    @Override
    public Collection<Object> createComponents(Client client, ClusterService clusterService, ThreadPool threadPool,
            ResourceWatcherService resourceWatcherService, ScriptService scriptService,
            NamedXContentRegistry xContentRegistry, Environment environment, NodeEnvironment nodeEnvironment,
            NamedWriteableRegistry namedWriteableRegistry, IndexNameExpressionResolver indexNameExpressionResolver,
            Supplier<RepositoriesService> repositoriesServiceSupplier) {
        
        clusterService.state();
        this.clusterService = clusterService;
        this.clusterService.addListener(this);
        this.clusterService.addLocalNodeMasterListener(this);
        this.client = client;

        JvmService jvmService = new JvmService(environment.settings());
        return ImmutableList.of(jvmService);
    }
    /**
     * 
     * @param indexModule
     */
    @Override
    public void onIndexModule(IndexModule indexModule) {
        
        if(indexModule.getIndex().getName().indexOf(bsSettings.getConfig().getIndex())>-1|| indexModule.getIndex().getName().indexOf(".")<0)
        {
            mapMsg.clear();
            mapMsg.put("message", "Add Index into Operation Listener");
            mapMsg.put("index name:", indexModule.getIndex().getName());
            mapMsg.put("index id:", indexModule.getIndex().getUUID());
            logger.trace(mapMsg);
            
            indexList.add(indexModule.getIndex().getName());

            streamIndexOperation boundstreamIndexOperationListener = new streamIndexOperation(concurrentMap, clusterService, outputLoader, indexModule.getIndex().getName());
            indexModule.addIndexOperationListener(boundstreamIndexOperationListener);
        }

        super.onIndexModule(indexModule);        
    }
    /**
     * 
     * @param event
     */
    @Override
    public void clusterChanged(ClusterChangedEvent event) {

        if (event.nodesRemoved()) {
        }

        if (DiscoveryNode.isMasterNode(settings)) {
            concurrentMap.put("isMasterNode", "true");
        }

        try {
            settingsSrv.set(client, outputLoader);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        DiscoveryNode localNode = clusterService.state().getNodes().getLocalNode();
        concurrentMap.put("nodeId",localNode.getId());
    }

    @Override
    public void onMaster() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void offMaster() {
        // TODO Auto-generated method stub
        
    }
}
