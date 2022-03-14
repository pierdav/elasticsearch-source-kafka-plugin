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

package com.boundstream.elasticsearch.plugin.output;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.boundstream.elasticsearch.plugin.settings.Broker;
import com.boundstream.elasticsearch.plugin.settings.BsSettings;
import com.boundstream.elasticsearch.plugin.settings.Pipeline;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 
 */
public class toKafka implements outputService{

    private static final Logger logger = LogManager.getLogger(toKafka.class);
    private  Producer<String, String> producer;
    public Boolean isEnabled = true;
    public Boolean isConnected = false;
    public Boolean testRun = false;
    
    String host;
    Integer port;

    /**
     * 
     */
    public toKafka() {}

    /**
     * 
     */
    @Override
    public String getName() {
        return "kafka";
    }

    /**
     * 
     */
    @Override
    public Boolean isEnabled() {
        return isEnabled;
    }

    /**
     * 
     */
    @Override
    public Boolean connect(Map<String, Object> concurrentMap) {
        if(isEnabled==false)
        {
            logger.trace("Kafka is not Enabled");
            return false;
        }
        if(isConnected==true)
        {
            logger.trace("Kafka producer is connected to broker");
            return true;
        }
        if(testRun==true)
        {
            logger.trace("Kafka producer connecting to broker");
            return false;
        }

        
        ClassLoader original = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(null);
        
        BsSettings bsSettings = (BsSettings)concurrentMap.get("bsSettings");

        for (Integer i = 0; i < bsSettings.getBrokers().size(); i++) {

            Broker broker = bsSettings.getBrokers().get(i);       

            if(broker.getType().equals("kafka"))
            {
                logger.trace("Broker "+broker.getType()+" : "+broker.getName()+" on "+broker.getHost());

                host = broker.getHost();
                port = broker.getPort();

                Properties props = new Properties();
                props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, host+":"+port.toString());
                // specify the protocol for SSL Encryption
                // props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
                
                props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
                props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
                
                props.put(ProducerConfig.ACKS_CONFIG, "all");
                props.put(ProducerConfig.RETRIES_CONFIG, 0);
                props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 1000);
                props.put(ProducerConfig.RECONNECT_BACKOFF_MS_CONFIG, 1000);
                props.put(ProducerConfig.RECONNECT_BACKOFF_MAX_MS_CONFIG, 5000);
                // props.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
                // props.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
                props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 1000);
                props.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 1000);
                producer = new KafkaProducer<>(props);
                try {
                    if(testRun==false)
                    {
                        isConnected = sendTestMessage();
                    }
                   
                } catch (Exception e) {
                    e.printStackTrace();
                    isConnected = false;
                }
            }
        }
        Thread.currentThread().setContextClassLoader(original);

        return isConnected;
    }
    
    /**
     * 
     */
    @Override
    public void sendMessage(Map<String, Object> concurrentMap, String message, Pipeline pipeline) {


            ClassLoader original = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(null);
            
            logger.trace("Kafka producer sending Message to host: "+host+" topic:" +pipeline.getTopic());
            
            ProducerRecord<String, String> record = new ProducerRecord<>(pipeline.getTopic(), message);
            record.headers().add("type", "record_created".getBytes(StandardCharsets.UTF_8));
            record.headers().add(new RecordHeader("nodeId", "nodeId".getBytes(StandardCharsets.UTF_8)));  

            try {
                Future<RecordMetadata> future = producer.send(record);
                RecordMetadata metadata = future.get();
                logger.trace("Message sent to Kafka");
            } catch (InterruptedException e) {
                e.printStackTrace();
                isConnected = false;
            } catch (ExecutionException e) {
                e.printStackTrace();
                isConnected = false;
            }
            Thread.currentThread().setContextClassLoader(original);
    }


    /**
     * 
     */
    private Boolean sendTestMessage() {
           
            testRun=true;

            ProducerRecord<String, String> record = new ProducerRecord<>("TEST_TOPIC", "{\"message\":\"zzzzzzz\"}");
            record.headers().add("type", "record_created".getBytes(StandardCharsets.UTF_8));
            record.headers().add(new RecordHeader("nodeId", "nodeId".getBytes(StandardCharsets.UTF_8)));  
            boolean ret = false;
            try {
                Future<RecordMetadata> future = producer.send(record);
                RecordMetadata metadata = future.get();
                ret = true;
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            testRun=false;
            return ret;
        }
}
