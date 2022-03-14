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
public class Config {

    private String host = "localhost";
    private Integer port = 9200;
    private String protocol = "https";
    private String username = "elastic";
    private String password = "changeme";
    private String index = ".eska";
    private String docId = "settings";
    // private List<Output> outputs = null;

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
    
    /**
     * 
     * @return
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * 
     * @param protocol
     */
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    /**
     * 
     * @return
     */
    public String getUsername() {
        return username;
    }

    /**
     * 
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 
     * @return
     */
    public String getPassword() {
        return password;
    }

    /**
     * 
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 
     * @return
     */
    public String getIndex() {
        return index;
    }

    /**
     * 
     * @param index
     */
    public void setIndex(String index) {
        this.index = index;
    }

    /**
     * 
     * @return
     */
    public String getDocId() {
        return docId;
    }

    /**
     * 
     * @param docId
     */
    public void setDocId(String docId) {
        this.docId = docId;
    }

    // /**
    //  * 
    //  * @return
    //  */
    // public List<Output> getOutputs() {
    //     return outputs;
    // }

    // /**
    //  * 
    //  * @param outputs
    //  */
    // public void setOutputs(List<Output> outputs) {
    //     this.outputs = outputs;
    // }

}
