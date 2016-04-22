# vcr4j-purejavacomm

A `VideoIO` implementation for RS422 devices using [purejavacomm](https://github.com/nyholku/purejavacomm)

```xml
dependency>
    <groupId>org.mbari.vcr4j</groupId>
    <artifactId>vcr4j-purejavacomm</artifactId>
    <version>${vcr4j.version}</version>
</dependency>
```


## Usage

```
VideoIO<RS422State, RS422Error> io = PJCVideoIO.open(serialPortName);
```