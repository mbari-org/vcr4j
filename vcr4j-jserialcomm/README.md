# vcr4j-jserialcomm

A `VideoIO` implementation for RS422 devices using [jSerialComm](https://fazecast.github.io/jSerialComm/)

```xml
dependency>
    <groupId>org.mbari.vcr4j</groupId>
    <artifactId>vcr4j-jserialcomm</artifactId>
    <version>${vcr4j.version}</version>
</dependency>
```


## Usage

```
VideoIO<RS422State, RS422Error> io = SerialCommVideoIO.open(serialPortName);
```