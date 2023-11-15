# vcr4j-remote

[![javadoc](https://javadoc.io/badge2/org.mbari.vcr4j/vcr4j-remote/javadoc.svg)](https://javadoc.io/doc/org.mbari.vcr4j/vcr4j-remote)

A VCR4J module for interacting with the [Sharktopoda](https://github.com/mbari-org/Sharktopoda) video player.

## Usage

### Add to a project

vcr4j-remote is available from [Maven central](https://mvnrepository.com/artifact/org.mbari.vcr4j/vcr4j-remote). To add it to a project:

```xml
<dependency>
    <groupId>org.mbari.vcr4j</groupId>
    <artifactId>vcr4j-core</artifactId>
    <version>${vcr4j.version}</version>
</dependency>
<dependency>
    <groupId>org.mbari.vcr4j</groupId>
    <artifactId>vcr4j-remote</artifactId>
    <version>${vcr4j.version}</version>
</dependency>
```

### Example code

```java
import java.util.UUID
import org.mbari.vcr4j.remote.control.RemoteControl
import org.mbari.vcr4j.VideoCommands;
import org.mbari.vcr4j.commands.RemoteCommands;

// A remote control is specific to one video. 
var videoUuid = UUID.randomUuid();              // unique identifier for the video
var rc = new RemoteControl.Builder(videoUuid) 
              .remotePort(8800)                // Sharktopoda's port
              .remoteHost("localhost")         // Can connect to other hosts if needed
              .port(5666)                      // This app's port. Sharktopoda can send commands to this
              .withMonitoring(false)           // use true if you need to montitor the video's index and status
              .withLogging(false)              // use true to debug network traffic          
              .withStatus(false)               // use true to request state after a command is sent to sharktopoda
              .whenFrameCaptureIsDone(cmd -> System.out.println("Image saved to " + cmd.getImageLocation())) // handle framecapture post processing
              .build()                         // returns an optional. Empty if an error occurs during build
              .get() 

var io = rc.getVideoIO();
io.getIndexObservable().subscribe(i -> System.out.println(i.getElapsedTime()))
io.getStateObservable().subscribe(s -> System.out.println(s.getState()))

io.send(new OpenCmd(videoUuid, someUrl));      // Open a video. Use a file url to open local files.

// Can send any control command
io.send(VideoCommands.PLAY);
io.send(VideoCommands.PAUSE); // or VideoCommand.STOP
io.send(VideoCommands.FAST_FORWARD);
io.send(VideoCommands.REWIND);
io.send(VideoCommands.REQUEST_INDEX);
io.send(VideoCommands.REQUEST_STATUS);
io.send(RemoteCommands.FRAMEADVANCE);
io.send(RemoteCommands.REQUEST_ALL_VIDEO_INFOS);
io.send(RemoteCommands.REQUEST_VIDEO_INFO);
io.send(RemoteCommands.SHOW);
var playbackRate = 0.5
io.send(new PlayCmd(videoUuid, playbackRate)) // play at half speed
io.send(new PlayCmd(videoUuid, -playbackRate)) // play in reverse at half speed

// capture an image of the current video frame and save it to disk
var imageUuid = UUID.randomUuid();
io.send(new FrameCaptureCmd(videoUuid, imageUuid, "/path/to/save/image.png"))

// TODO add docs on sending/receiving localizations to sharktpoda

io.send(RemoteCommands.CLOSE);
rc.close();

```
