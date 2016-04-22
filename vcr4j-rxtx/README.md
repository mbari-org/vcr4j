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

```
// You only need to call this once per app to configure the native libraries
RXTX.setup(); 

VideoIO<RS422State, RS422Error> io = RXTXVideoIO.open(serialPortName);
```