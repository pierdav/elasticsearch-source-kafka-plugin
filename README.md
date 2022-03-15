# Elasticsearch Source Kafka Plugin
![MAP-LITE#1 (3)](https://user-images.githubusercontent.com/582406/158040512-75e566bf-af9c-4d1e-9448-c588b81d34c9.jpg)


elasticsearch-source-kafka is a [Elasticsearch Plugin](https://www.elastic.co/guide/en/elasticsearch/reference/current/modules-plugins.html)
for producing data from indexing Elasticsearch to Kafka broker.

# Development

To build a development version you'll need a 7.17.1 version of Elasticsearch.

Clone the git project

```
git clone https://github.com/pierdavprod/elasticsearch-source-kafka.git
cd ./elasticsearch-source-kafka
```

You can build elasticsearch-source-kafka with Maven using the standard lifecycle phases.

```
mvn clean package
```

# Configuring
## Configure Elasticsearch Plugin access
*this step is optional*

Edit configuration file to access to settings document
```
vi eska-config.yml

host: localhost
port: 9200
protocol: https
index: .eska
docId: settings
username: elastic
password: changeme
```
then copy config file to Elasticsearch config directory:

```
cp eska-config.yml /usr/share/elasticsearch/config/
```

## Configure Elasticsearch jvm.options and java security policy 

add this line to jvm.options in Elasticsearch config directory:

```
-Djava.security.policy=/usr/share/elasticsearch/config/elasticsearch-source-kafka-plugin-security.policy 
```

then copy policy file to Elasticsearch config directory:
```
cp ./elasticsearch-source-kafka-plugin-security.policy /usr/share/elasticsearch/config/
```
*this file allows to give permissions to plugin*

## Install Elasticsearch Source Kafka Plugin
```
/usr/share/elasticsearch/bin/elasticsearch-plugin install file:///$(pwd)/target/elasticsearch-source-kafka-plugin.zip
```

## Restart Elasticsearch node
```
service elasticsearch restart
```

## Create an Elasticsearch Source Kafka Settings

This Elasticsearch document allows you to define your settings

**brokers:** an array of Kafka brokers access config object
- name: define a name of broker
- type: unique kafka
- host: domain or IP server of Kafka broker
- port: port server of Kafka broker

**pipelines:** an array of treatment stream config object
**- active: **true / false
**- tag:** define a tag of pipeline
**- name:** define a name of pipeline
**- indices: **an array of index - wildcard `*` accepted
**- ids:** an array of id - wildcard `*` accepted
**- operations:** an array of operation - values accepted : **INDEX** or **CREATE**
**- render:** the path to render message value
**- broker:** name of broker defined in brokers array
**- topic:** name of topic in Kafka broker

```
curl -u elastic:elastic -X POST "localhost:9200/.eska/_doc/settings" -H 'Content-Type: application/json' -d'
{
        "brokers": [
            {
                "name": "kafka01",
                "type": "kafka",
                "host": "localhost",
                "port": 9092
            }
        ],
        "pipelines": [
            {
                "active": true,
                "tag": "ack_to_iot",
                "name": "From eska-test to KAFKA_ALL",
                "indices": [
                    "eska-test*"
                ],
                "ids": [
                    "*"
                ],
                "operations": [
                    "CREATE",
                    "INDEX"
                ],
                "render": "source",
                "broker": "kafka01",
                "topic": "KAFKA_ALL"
            }
        ]
    }'
```


# Contribute

- Source Code: https://github.com/pierdav/elasticsearch-source-kafka-plugin
- Issue Tracker: https://github.com/pierdav/elasticsearch-source-kafka-plugin/issues
- Learn how to work with the plugin's source code by reading our [Development and Contribution guidelines](CONTRIBUTING.md).

## License

This repository is licensed under the [![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0.

