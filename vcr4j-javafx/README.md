# vcr4j-javafx

A `VideoIO` implementation for JavaFX's [MediaPlayer](https://docs.oracle.com/javase/8/javafx/api/javafx/scene/media/MediaPlayer.html)

```xml
dependency>
    <groupId>org.mbari.vcr4j</groupId>
    <artifactId>vcr4j-javafx</artifactId>
    <version>${vcr4j.version}</version>
</dependency>
```


## Usage

```
MediaPlayer mediaPlayer = // ... create javafx media requestHandler
VideoIO<JFXVideoState, SimpleVideoError> io = new JFXVideoIO(mediaPlayer);
```