
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

import java.util.List;

/**
 * 
 */
public class BsSettings {

    private Config config = new Config();
    private List<Pipeline> pipelines = null;
    private List<Broker> brokers = null;
    
    /**
     * 
     * @return
     */
    public Config getConfig() {
        return config;
    }

    /**
     * 
     * @param config
     */
    public void setConfig(Config config) {
        this.config = config;
    }

    /**
     * 
     * @return
     */
    public List<Broker> getBrokers() {
        return brokers;
    }

    /**
     * 
     * @param brokers
     */
    public void setBrokers(List<Broker> brokers) {
        this.brokers = brokers;
    }


    /**
     * 
     * @return
     */
    public List<Pipeline> getPipelines() {
        return pipelines;
    }

    /**
     * 
     * @param pipelines
     */
    public void setPipelines(List<Pipeline> pipelines) {
        this.pipelines = pipelines;
    }


}
