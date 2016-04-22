# vcr4j-jssc

A `VideoIO` implementation for RS422 devices using [java-simple-serial-connector](https://github.com/scream3r/java-simple-serial-connector)

```xml
dependency>
    <groupId>org.mbari.vcr4j</groupId>
    <artifactId>vcr4j-jssc</artifactId>
    <version>${vcr4j.version}</version>
</dependency>
```


## Usage

```
VideoIO<RS422State, RS422Error> io = JSSCVideoIO.open(serialPortName);
```