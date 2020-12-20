# Apache Kafka® Configuration Made Easy

Use an approach like Database Migrations to manage your evolutionary
configurations for:

- Topics
- Schemas
- ACLs
- Clusters
- and more soon . . .

## Installation

TODO

## Released Features

- [x] Topic migrations
- [ ] Schema migrations
- [ ] ACL migrations
- [ ] Cluster migrations
- [ ] ksqlDB migrations

## Usage

### Common Options

- `--config-file`: Path to Kattlo configuration file for migrations
- `--kafka-config-file`: Path to properties file to be used for Kafka Admin Client

In the `.kattlo.yaml` configuration file you may define the following
properties:

```yaml
TODO
```

In the Kafka Admin client file you may put the properties described at [official documentation](https://kafka.apache.org/documentation/#adminclientconfigs).

__Example__: `kafka.properties`

```properties
bootstrap.servers=localhost:19092,localhost:29092
client.id=kattlo-cli
```

__Command__:

```bash
kattlo --config-file='.kattlo.yaml' \
       --kafka-config-file='kafka.properties'
```

## Examples

```bash
build/ottla-1.0-SNAPSHOT-runner \
  --config-file=src/test/resources/.kattlo.yaml \
  --kafka-config-file=src/test/resources/kafka.properties \
  topic \
  --directory=src/test/resources/topics/
```

## Internals

Kattlo needs to have all permissions to manage topics, ACLs and Schemas
configurations, outherwise you will be not able to perform the migrations.

In order to manage the migrations, we use four special topics:

- `__kattlo-topics-state`:
- `__kattlo_schema_migrations`:
- `__kattlo_acl_migrations`:
- `__kattlo_cluster_migrations`:

### `__kattlo-topics-state`

> To persist migrations per topic.

This topic has the following configurations:

- partitions: `50`
- replication-factor: `2`

```properties
cleanup.policy=compact
retention.ms=-1
segment.ms=3000
segment.bytes=104857600
compression.type=producer
message.timestamp.type=CreateTime
delete.retention.ms=0
min.cleanable.dirty.ratio=0.1
```

Kafka CLI command:

```bash
kafka-topics.sh --create \
  --bootstrap-server 'localhost:9092' \
  --replication-factor 1 \
  --partitions 50 \
  --topic '__kattlo-topics-state' \
  --config 'cleanup.policy=compact' \
  --config 'retention.ms=-1' \
  --config 'segment.ms=3000' \
  --config 'segment.bytes=104857600' \
  --config 'compression.type=producer' \
  --config 'message.timestamp.type=CreateTime' \
  --config 'delete.retention.ms=0' \
  --config 'min.cleanable.dirty.ratio=0.1'
```

## Build and Run

TODO

### Native

```bash
./gradlew build -Dquarkus.package.type=native \
 -Dquarkus.native.container-build=true \
 -Dquarkus.native.additional-build-args=--report-unsupported-elements-at-runtime,--allow-incomplete-classpathe
```

You can then execute your native executable with: `./build/ottla-1.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/gradle-tooling#building-a-native-executable.

### Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```
./gradlew quarkusDev
```

### Packaging and running the application

The application can be packaged using `./gradlew quarkusBuild`.
It produces the `ottla-1.0-SNAPSHOT-runner.jar` file in the `build` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `build/lib` directory.

The application is now runnable using `java -jar build/ottla-1.0-SNAPSHOT-runner.jar`.

If you want to build an _über-jar_, just add the `--uber-jar` option to the command line:
```
./gradlew quarkusBuild --uber-jar
```

## Made with :purple_heart: by

- fabiojose
