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

package com.boundstream.elasticsearch.plugin.render;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.boundstream.elasticsearch.plugin.event.inputEvent;
import com.boundstream.elasticsearch.plugin.settings.BsSettings;

import org.elasticsearch.common.Strings;
import org.elasticsearch.common.io.stream.BytesStreamOutput;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.xcontent.XContentBuilder;
import org.elasticsearch.xcontent.XContentType;
import org.elasticsearch.xcontent.json.JsonXContent;

/**
 * 
 */
public class sourceContent {
    

    /**
     * 
     * @param bsSettings
     * @param inputEvent
     * @return
     */
    public HashMap<String, String> jsonIoString(BsSettings bsSettings, inputEvent inputEvent) {


        HashMap<String, String> inOutDoc = new HashMap<String, String>();

        Set<String> preFilter = new HashSet<>();
        preFilter.add("source.*");
 
        Boolean sourceBuild = false;
        
        try {
            XContentBuilder preBuilder = new XContentBuilder(JsonXContent.jsonXContent, new BytesStreamOutput(), preFilter);
            StreamInput source = inputEvent.getSource().streamInput();
            preBuilder.startObject()
                    .field("index", inputEvent.getIndex())
                    .field("type", inputEvent.getType())
                    .field("id", inputEvent.getId())
                    .field("timestamp", inputEvent.getTimestamp())
                    .field("version", inputEvent.getVersion())
                    .field("operation", inputEvent.getOperation().toString());

            if (inputEvent.getSource() != null) {
                try {
                    preBuilder.rawField("source", source, XContentType.JSON);
                    sourceBuild = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            preBuilder.endObject();
            
            if(sourceBuild == true)
            {
                inOutDoc.put("input", Strings.toString(preBuilder));
            }
            preBuilder.close();
            return inOutDoc;
            
        } catch (IOException e) {
            inOutDoc.put("input", "{}");
            return inOutDoc;
        }
    }
}
