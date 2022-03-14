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

package com.boundstream.elasticsearch.plugin.filter;

import java.util.regex.Pattern;

import com.boundstream.elasticsearch.plugin.event.inputEvent.Operation;
import com.boundstream.elasticsearch.plugin.settings.Pipeline;

/**
 * 
 */
public class metaFilter {
    
    public Operation operation;
    
    /**
     * 
     * @param wildcard
     * @return
     */
    public static String wildcardToRegex(String wildcard){
        StringBuffer s = new StringBuffer(wildcard.length());
        s.append('^');
        for (int i = 0, is = wildcard.length(); i < is; i++) {
            char c = wildcard.charAt(i);
            switch(c) {
                case '*':
                    s.append(".*");
                    break;
                case '?':
                    s.append(".");
                    break;
                    // escape special regexp-characters
                case '(': case ')': case '[': case ']': case '$':
                case '^': case '.': case '{': case '}': case '|':
                case '\\':
                    s.append("\\");
                    s.append(c);
                    break;
                default:
                    s.append(c);
                    break;
            }
        }
        s.append('$');
        return(s.toString());
    }

    /**
     * 
     * @param pipeline
     * @param operation
     * @param indexName
     * @param indexId
     * @param logEvent
     * @return
     */
    public Boolean matchPipeline (Pipeline pipeline, Operation operation, String indexName, String indexId)
    {
        boolean matchIndex = false;
        boolean matchId = false;

        if (pipeline.getOperations().contains(operation.toString()) == false) {
            return false;
        }
        
        for (Integer n = 0; n < pipeline.getIndices().size(); n++) {
            String regWild = wildcardToRegex(pipeline.getIndices().get(n));
            if (Pattern.matches(regWild, indexName)== true || 
            pipeline.getIndices().get(n).contains(indexName) == true) {
                matchIndex = true;
                break;
            }
        }
        
        if(matchIndex==false)
        {   
            return false;
        }

        // id match
        if(operation ==  Operation.INDEX)
        {
            for (Integer n = 0; n < pipeline.getIds().size(); n++) {
                String regWild = wildcardToRegex(pipeline.getIds().get(n));
                if (Pattern.matches(regWild, indexId)== true || 
                pipeline.getIds().get(n).contains(indexId) == true) {
                    matchId = true;
                    break;
                }
            }
        }
        else
        {
            matchId = true;
        }
        return matchId;
    }
}
