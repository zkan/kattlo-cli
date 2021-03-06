![Kattlo](./artwork/kattlo.png)

# Apache Kafka® Configuration Made Easy

Use an approach like Database Migrations to manage your evolutionary
configurations for:

- Topics
- Schemas
- ACLs
- ksqlDB
- Connect
- Cluster
- and more soon . . .

:bulb: Check the [examples directory](./examples) :bulb:

## Kattlo is good for ...

- enterprises that needs a stable way to change Apache Kafka® configurations
- maintain the configuration and avoid drifts
- helps to known when a topic was removed (when its managed by Kattlo)
- access the history of migrations
- your DevOps toolset to properly manages the topic within clusters

## Install

### Linux Binary

```bash
curl 'https://github.com/kattlo/kattlo-cli/releases/download/v0.1.1/kattlo-v0.1.1-linux' \
  -o 'kattlo'

sudo chmod +x kattlo
sudo mv kattlo /usr/local/sbin/kattlo
```

### Linux Packages

The are `.deb` and `.rpm` packages available. Do a check in
[the latest release](https://github.com/kattlo/kattlo-cli/releases/latest).

### MacOS

```bash
curl 'https://github.com/kattlo/kattlo-cli/releases/download/v0.1.1/kattlo-v0.1.1-mac' \
  -o 'kattlo'

sudo chmod +x kattlo
sudo mv kattlo /usr/local/bin/kattlo
```

### Windows

- download the [latest release](https://github.com/kattlo/kattlo-cli/releases/latest) package for windows
- unzip it
- copy `VCRUNTIME140.dll` to `C:\Windows\System32\`
- get the absolute path to that unzipped directory
- add the absolute path of Kattlo to your `PATH` environment variable
- open the prompt and type: `kattlo -V`

## Released Features

- [x] Topic migrations
  - [x] apply migrations
  - [x] import existing topics
  - [x] show info and history
  - [x] generate migration example
  - [ ] rules enforcement
- [ ] Schema migrations
- [ ] ACL migrations
- [ ] Connect migrations
- [ ] ksqlDB migrations
- [ ] Cluster migrations
- [x] Utilities
  - [x] init project
  - [ ] new config for consumers
  - [ ] new config for producers

## Usage

### Common Options

- `--config-file` (optional): Path to Kattlo configuration file for migrations
- `--kafka-config-file` (required): Path to properties file to be used for Kafka Admin Client

In the `.kattlo.yaml` configuration file you may define the following
properties:

```yaml
# to future uses, then, will be an empty file
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
       --kafka-config-file='kafka.properties' \
       <command>
       [command arguments]
```

## Examples

Set the directory with migrations using the `--directory` option:
```bash
kattlo \
  --config-file='examples/.kattlo.yaml' \
  --kafka-config-file='examples/kafka.properties' \
  topic \
  --directory='examples/topics/01_create_with_config'
```

Directory with migrations will be default to current, when `--directory` is
suppressed:
```bash
kattlo \
  --config-file='examples/.kattlo.yaml' \
  --kafka-config-file='examples/kafka.properties' \
  topic
```

The current directory contains `kafka.properties` and `.kattlo.yaml`:
```bash
kattlo \
  topic \
  --directory='examples/topics/01_create_with_config'
```

Want to use the `kafka.properties`, but in another cluster:
```bash
kattlo \
  --config-file='examples/.kattlo.yaml' \
  --kafka-config-file='examples/kafka.properties' \
  --bootstrap-servers='my.kafka:9092' \
  topic \
  --directory='examples/topics/01_create_with_config'
```

The option `--bootstrap-servers` overrides the config [`bootstrap.servers`](https://kafka.apache.org/documentation/#adminclientconfigs_bootstrap.servers).

Showing topic current state:

> Include the option `--format=JSON` to printout json instead plain text

```bash
kattlo \
  --config-file='examples/.kattlo.yaml' \
  --kafka-config-file='examples/kafka.properties' \
  info --resource=TOPIC \
  '<topic-name>'
```

Showing topic history:

> Include the option `--format=JSON` to printout json instead plain text

```bash
kattlo \
  --config-file='examples/.kattlo.yaml' \
  --kafka-config-file='examples/kafka.properties' \
  info --resource=TOPIC --history \
  '<topic-name>'
```

### Init

To init new Kattlo project you just run the following command:

```bash
kattlo init --directory='/path/to/initialize'
```

Use the `--bootstrap-servers` to generate the Kattlo config
with right Kafka addresses:

```bash
kattlo --bootstrap-servers='my-kafka-b1:9092,my-kafka-b2:9092' \
  init --directory='/path/to/initialize'
```

> If you suppress the `--directory` option, the current folder will be
initialized.

### Gen

To make easy the process to write down the migrations, you may use
then gen command to genereate migration files:

```bash
kattlo gen migration --resource=TOPIC --diretory='/path/to/gen/migration'
```

> If you suppress the `--directory` option, the migration example will
be gerenated in the current directory.

### Import

To import existing topics to Kattlo.

```bash
kattlo \
  --config-file='examples/.kattlo.yaml' \
  --kafka-config-file='examples/kafka.properties' \
  --bootstrap-servers='my.kafka:9092' \
  topic \
  --directory='/path/to/migrations/for/my/existing/topic' \
  import \
  --topic='my-existing-topic'
```

The operation above will import the existing topic, create the very first
migration with create operation and the necessary stuff to enable that
topic as a managed resource.

- file automatically create within `--directory`: `v0001_create-topic.yaml`

## Best Practices

- Always create a new migration file and never change an applied one
- Create a directory for each resource that you want to manage
  - Kattlo is able to process many distinct resources migrations within same directory,
  but you get better organization following that practice

### File Naming

All migrations are defined using physical files, and they must follow
this naming pattern:

- `v[0-9]{4}_[\\w\\-]{0,246}\\.ya?ml`

Simplifing:

- `v0000_the-name-of-my-migration.yaml`
- where `v0000` will be the version of resource migration, from `1` to `n`
- when a new migration is created, increase the version

### File Content

Every migration file must have exatcly one resource migration.

Never mix `create`, `patch` or `remove` in the same file or same operations
for distinct resources.

## Migrations

Kattlo provide a way to declare what we want using yaml notation. Based
on that files, Kattlo runs the necessary Admin commands to create, patch or
remove resources.

> See [examples](./examples) to see the variations of usage.

Resources can be:

- topics
- schemas
- ACLs

### Topics

This is the way to manage topics resources within Apache Kafka®.

To __create__ a topic:
```yaml
operation: create # The operation over the resource
notes: |
  Write down whatever you want to describe this.
  This can be a multiline text . . .
topic: topic_name
partitions: 1 #partitions is optional
replicationFactor: 1 #replicationFactor is optional
config: #config is optional
  compression.type: gzip
  cleanup.policy  : compact
  # Any configuration available here:
  #   https://kafka.apache.org/documentation/#topicconfigs
```

Notes about `create`:
- if you want all cluster default values for `partitions`, `replicationFactor`
and `config`, just suppress them

To __patch__ a topic:

```yaml
operation: patch # The operation over the resource
notes: Patch partitions to 3 # Describe your patch . . .
topic: 02_create_patch_partitions # Topic that was created before
partitions: 3 #partitions is optional
replicationFactor: 2 #replicationFactor is optional
config: #config is optional
  retention.ms: -1
  # Any configuration available here:
  #   https://kafka.apache.org/documentation/#topicconfigs

  compresstion.type: $default # Patch to cluster default value
```

Notes about `patch`:
- `partitions` can not be reduced
- at least one of `partitions`, `replicationFactor` or `config` must be present
in the migration file
- use the keyword `$default` to patch config to cluster default value

To __remove__ a topic:

```yaml
operation: remove
notes: Descrive your motivation to remove this topic
topic: 05_create_and_remove # Topic to remove
```

Notes about `remove`:
- remove will delete the entire topic data
- all migrations history about the topic will be maintained

## Internals

Kattlo needs to have all permissions to manage topics, ACLs and Schemas
configurations, outherwise you will be not able to perform the migrations.

In order to manage the migrations we use special topics:

- `__kattlo-topics-state`: the topics' migrations state
- `__kattlo-topics-history`: the topics' migrations history

### `__kattlo-topics-state`

> To persist the current state per topic.

This topic has the following configurations:

- partitions: `50`
- desired replication-factor: `2`

### `__kattlo-topics-history`

> To persist the histories for topics.

This topic has the following configurations:

- partitions: `50`
- desired replication-factor: `2`

## Build and Run

### Native

```bash
./gradlew clean build -Dquarkus.package.type=native \
 -Dquarkus.native.container-build=true \
 -Dquarkus.native.additional-build-args=--report-unsupported-elements-at-runtime,--allow-incomplete-classpath,-H:IncludeResources='.*yaml$',-H:Log=registerResource:
```

You can then execute your native executable with: `./build/kattlo-1.0-SNAPSHOT-runner`

> Configure `-Dquarkus.native.container-build` to `false` if you want o use your graalvm
installation instead of Docker image.

### Running in verbose mode

If you are experiencing issues, run Kattlo with logging set to DEBUG.

```bash
kattlo -Dquarkus.log.level=DEBUG ...
```

### Packaging and running the application

The application can be packaged using `./gradlew quarkusBuild`.
It produces the `kattlo-1.0-SNAPSHOT-runner.jar` file in the `build` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `build/lib` directory.

The application is now runnable using `java -jar build/kattlo-1.0-SNAPSHOT-runner.jar`.

If you want to build an _über-jar_, just add the `--uber-jar` option to the command line:

```
./gradlew quarkusBuild --uber-jar
```

## Notes

- Kattlo icon by <a href="http://www.freepik.com/" title="Freepik">Freepik</a> from <a href="https://www.flaticon.com/" title="Flaticon"> www.flaticon.com</a>
