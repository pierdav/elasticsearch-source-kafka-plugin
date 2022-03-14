
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
public class Pipeline {
    
    private String name;
    private boolean active;
    private String tag;
    private List<String> indices = null;
    private List<String> ids = null;
    private List<String> operations = null;
    private String filter;
    private String script;
    private String render;
    private Extend extend;
    private String broker;
    private String topic;
    private String lang;

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
    public boolean getActive() {
        return active;
    }

    /**
     * 
     * @param active
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * 
     * @return
     */
    public String getTag() {
        return tag;
    }

    /**
     * 
     * @param tag
     */
    public void setTag(String tag) {
        this.tag = tag;
    }

    /**
     * 
     * @return
     */
    public List<String> getIndices() {
        return indices;
    }

    /**
     * 
     * @param indices
     */
    public void setIndices(List<String> indices) {
        this.indices = indices;
    }

    /**
     * 
     * @return
     */
    public List<String> getIds() {
        return ids;
    }

    /**
     * 
     * @param ids
     */
    public void setIds(List<String> ids) {
        this.ids = ids;
    }

    /**
     * 
     * @return
     */
    public List<String> getOperations() {
        return operations;
    }

    /**
     * 
     * @param operations
     */
    public void setOperations(List<String> operations) {
        this.operations = operations;
    }

    /**
     * 
     * @return
     */
    public String getFilter() {
        return filter;
    }

    /**
     * 
     * @param filter
     */
    public void setFilter(String filter) {
        this.filter = filter;
    }

    /**
     * 
     * @return
     */
    public String getScript() {
        return script;
    }

    /**
     * 
     * @param script
     */
    public void setScript(String script) {
        this.script = script;
    }

    /**
     * 
     * @return
     */
    public String getRender() {
        return render;
    }

    /**
     * 
     * @param render
     */
    public void setRender(String render) {
        this.render = render;
    }

    /**
     * 
     * @return
     */
    public Extend getExtend() {
        return extend;
    }

    /**
     * 
     * @param extend
     */
    public void setExtend(Extend extend) {
        this.extend = extend;
    }

    /**
     * 
     * @return
     */
    public String getBroker() {
        return broker;
    }

    /**
     * 
     * @param broker
     */
    public void setBroker(String broker) {
        this.broker = broker;
    }

    /**
     * 
     * @return
     */
    public String getTopic() {
        return topic;
    }

    /**
     * 
     * @param topic
     */
    public void setTopic(String topic) {
        this.topic = topic;
    }

    /**
     * 
     * @return
     */
    public String getLang() {
        return lang;
    }

    /**
     * 
     * @param lang
     */
    public void setLang(String lang) {
        this.lang = lang;
    }

}
