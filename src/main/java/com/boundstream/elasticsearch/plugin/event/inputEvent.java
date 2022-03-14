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

package com.boundstream.elasticsearch.plugin.event;

import java.util.Map;

import org.elasticsearch.common.bytes.BytesReference;
import org.joda.time.DateTime;

/**
 * 
 */
public class inputEvent {
    private  String index;
    private  String id;
    private  String type;
    private  Operation operation;
    private  long version;
    private  BytesReference source;
    private  DateTime timestamp;
    private  long startFullNano;

    /**
     * 
     */
    public enum Operation {
        INDEX,CREATE,DELETE
    }

    /**
     * 
     * @param concurrentMap
     * @param index
     * @param type
     * @param id
     * @param operation
     * @param version
     * @param source
     * @param timestamp
     * @param startFullNano
     */
    public inputEvent(Map<String, Object> concurrentMap, String index, String type, String id, Operation operation, long version, BytesReference source, DateTime timestamp, long startFullNano) {
        this.index = index;
        this.id = id;
        this.type = type;
        this.operation = operation;
        this.version = version;
        this.source = source;
        this.timestamp = timestamp;
        this.startFullNano = startFullNano;
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
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * 
     * @return
     */
    public Operation getOperation() {
        return operation;
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
     * @return
     */
    public long getVersion() {
        return version;
    }

    /**
     * 
     * @return
     */
    public BytesReference getSource() {
        return source;
    }

    /**
     * 
     * @return
     */
    public DateTime getTimestamp() {
        return timestamp;
    }

    /**
     * 
     * @return
     */
    public long getStartFullNano() {
        return startFullNano;
    }

}
