# vcr4j-rxtx

A `VideoIO` implementation for RS422 devices using [RXTX](https://github.com/rxtx/rxtx)

```xml
dependency>
    <groupId>org.mbari.vcr4j</groupId>
    <artifactId>vcr4j-rxtx</artifactId>
    <version>${vcr4j.version}</version>
</dependency>
```


## Usage

```java
// You only need to call this once per app to configure the native libraries
RXTX.setup(); 

VideoIO<RS422State, RS422Error> io = RXTXVideoIO.open(serialPortName);
```

## Developer Notes

The `rxtx` library included in this module is not the same as you will find in Maven Central. If you're planning to use this module for additional builds you should install the rxtx jar into your local maven repo. You can do this using:

```
cd vcr4j/vcr4j-rxtx/src/main/lib/
mvn install:install-file \
  -Dfile=rxtx-java-2.2.0.jar \
  -DgroupId=gnu.io.rxtx \
  -DartifactId=rxtx-java \
  -Dversion=2.2.0 \
  -Dpackaging=jar
  
```