
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
public class Broker {
    
    private String name;
    private String type;
    private String host;
    private Integer port;

    /**
     * 
     * @return
     */
    public String getName() {
        return name;
    }
    /**
     * 
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * 
     * @return
     */
    public String getType() {
        return type;
    }

    /**
     * 
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }
    /**
     * 
     * @return
     */
    public String getHost() {
        return host;
    }

    /**
     * 
     * @param host
     */
    public void setHost(String host) {
        this.host = host;
    }
    /**
     * 
     * @return
     */
    public Integer getPort() {
        return port;
    }

    /**
     * 
     * @param port
     */
    public void setPort(Integer port) {
        this.port = port;
    }

}
