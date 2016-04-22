# vcr4j-udp

A `VideoIO` implementation for talking to a timecode server aboard MBARI's research vessels

```xml
dependency>
    <groupId>org.mbari.vcr4j</groupId>
    <artifactId>vcr4j-upd</artifactId>
    <version>${vcr4j.version}</version>
</dependency>
```


## Usage

```
VideoIO<UDPState, UDPError> io = new UDPVideoIO(hostAsString, portAsInt) 
```