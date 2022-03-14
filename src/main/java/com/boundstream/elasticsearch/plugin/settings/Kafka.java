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

/**
 * 
 */
public class Kafka {

    private String hosts;
    private Boolean connected;
    private Integer counter;
    /**
     * 
     * @return
     */
    public String getHosts() {
        return hosts;
    }

    /**
     * 
     * @param hosts
     */
    public void setHosts(String hosts) {
        this.hosts = hosts;
    }

    /**
     * 
     * @return
     */
    public Boolean getConnected() {
        return connected;
    }

    /**
     * 
     * @param connected
     */
    public void setConnected(Boolean connected) {
        this.connected = connected;
    }

    /**
     * 
     * @return
     */
    public Integer getCounter() {
        return counter;
    }

    /**
     * 
     * @param counter
     */
    public void setCounter(Integer counter) {
        this.counter = counter;
    }

}
