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

## Install Elasticsearch Source Kafka Plugin
```
/usr/share/elasticsearch/bin/elasticsearch-plugin install file:///$(pwd)/target/elasticsearch-source-kafka-plugin.zip
```

## Restart Elasticsearch node
```
service elasticsearch restart
```

## Create an Elasticsearch Source Kafka Settings
```
curl -u elastic:elastic -X POST "localhost:9200/.eska/_doc/settings" -H 'Content-Type: application/json' -d'
{
        "brokers": [
            {
                "name": "kafka01",
                "type": "kafka",
                "host": "localhost",
                "port": 9092,
                "protocol": "ssl",
                "username": "kafka",
                "password": "changeme"
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

- Source Code: https://github.com/pierdavprod/elasticsearch-source-kafka
- Issue Tracker: https://github.com/pierdavprod/elasticsearch-source-kafka/issues
- Learn how to work with the plugin's source code by reading our [Development and Contribution guidelines](CONTRIBUTING.md).

## License

This repository is licensed under the [MIT license](LICENSE-CODE).

