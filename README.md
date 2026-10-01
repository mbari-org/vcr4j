# VCR4J

![Build Status](https://github.com/mbari-org/vcr4j/actions/workflows/maven.yml/badge.svg)

Video controls for various devices. Used by MBARI's video annotation and reference system. The design of this library depends heavily on [ReactiveX](https://github.com/ReactiveX/RxJava) and can be summed up by:

```
VideoController ---> VideoIO ---> Observable<VideoError>
                             |--> Observable<VideoIndex>
                             |--> Observable<VideoState>
                             `--> Subject<VideoCommand>
```

The `VideoIO` object sends `VideoCommand` objects via the `commandSubject`. Response are parsed and the appropriate observable: `errorObservable`, `indexObservable`, or `stateObservable` is updated. 

Any implentation of `VideoCommand` can be sent to a VideoIO object, but it will only respond to ones it knows about (you can modify this with decorators though).

Example usage can be found in the `vcr4j-examples` module.

## Usage

### Adding to your project  

 

```xml
<dependencies>
    <!-- Here's an example for adding RXTX support for VCR's via RS422: -->
    <dependency>
        <groupId>org.mbari.vcr4j</groupId>
        <artifactId>vcr4j-rxtx</artifactId>
        <version>5.1.3.jre17</version>
    </dependency>
</dependencies>
```

### Creating a VideoIO object
`VideoIO` implementations manage the communication between java and the video device.  Simply create the `VideoIO` object you need for managing your video device. Typically, each VideoIO object has an `open` method that accepts the parameters need to connect to the video device. There are a number of decorators that you can add to modify the behavior of a VideoIO object. Here's an example:

```java
// A basic VideoIO object opens a connection. Sends commands and parses responses. 
VideoIO<RS422State, RS422Error> rawIO = RXTXVideoIO.open(serialPortName);

// Keep UI in sync by scheduling status/time requests
new VCRSyncDecorator<>(rawIO);

// Keep UI in sync by sending status/timecode requests after certain commands
new RS422StatusDecorator(rawIO);

// Log IO trafic
new LoggingDecorator(rawIO);

// Move all IO traffic off of the current thread to some Executor that you specify. 
// Extremely important for UI apps
VideoIO<RS422State, RS422Error>  io = new SchedulerVideoIO<>(simpleIO, Executors.newCachedThreadPool());

```

### Using a VideoIO object

You can either send command directly using the IO object or wrap it in a `VideoController`.

```java
VideoIO<RS422State, RS422Error>  io =  // ... see above about creating one

// watch for video indices (e.g. timecode) and print them out
io.getIndexObservable()
    .map(vi -> new VideoIndexAsString(vi))
    .subscribe(s -> System.out.println(s))

VideoController<RS422State, RS422Error> controller = new VideoController(io);

// You can send a play command using either of these methods
io.send(VideoCommands.PLAY)
controller.play()

// You can request video index using either of these methods
io.send(VideoCommands.REQUEST_INDEX
controller.requestIndex()

// etc.

// When done with a videoIO object close it to free resources
io.close()

```

## Testing

`mvn test` runs the unit tests for every module. These need no hardware or external services.

### vcr4j-remote integration tests

`vcr4j-remote` implements the client side of the Sharktopoda [UDP Remote Protocol](https://github.com/mbari-org/Sharktopoda/blob/main/Requirements/UDP_Remote_Protocol.md). `SharktopodaIT` checks it against a live, spec-compliant video player. It is **not** part of `mvn test`, because it needs a running player and it opens windows in it.

**Setup**

1. Start Sharktopoda 2 on the machine you are testing from and note the UDP port set under its Preferences (we use `8800`). Frame capture writes images to the local temp directory, so the player needs to be able to write to this machine's disk.
2. Have a video the player can open. A local file is fine, and a short one is quicker. Any length works: the tests seek to about 5 seconds in, or the midpoint of a shorter video.
3. Close any videos you already have open in the player, or the test `requestInformationFailsWhenNothingIsOpen` is skipped.

**Run**

```shell
mvn -pl vcr4j-remote -am test -Dtest=SharktopodaIT -Dsharktopoda.video=/path/to/video.mp4
```

| Setting | Environment variable | Default | Description |
|---|---|---|---|
| `sharktopoda.video` | `SHARKTOPODA_VIDEO` | none | URL or file path of the video to open |
| `sharktopoda.port` | `SHARKTOPODA_PORT` | `8800` | The player's UDP port |
| `sharktopoda.host` | `SHARKTOPODA_HOST` | `localhost` | The player's host |

A system property (`-D...`) takes precedence over the environment variable.

- If the player doesn't answer a ping, every test is skipped, so the run does not fail when Sharktopoda isn't running.
- If no video is set, only the tests that don't need one run. The rest are skipped.
- The tests clean up after themselves. They close the videos they open and delete the images they capture.
- Seek, play and frame advance are acknowledged by the player before they take effect, so the tests poll (up to 5 seconds) instead of reading back immediately. Code that uses `RemoteControl` should do the same.

The tests that don't need a player, such as `SpecComplianceTest`, run with `mvn test`. They talk to a `VideoControl` in the same JVM, so they check that the player side of this library follows the spec too.

## Notes

- When testing on Mac OS X 10.11 using a GUC232A usb-to-serial device and prolific's drivers. THe port doesn't seem to close property with RXTX. I'm force to unplug and replug it in after each test to reset the port.
- When testing on Mac OS X 10.11 using a GUC232A usb-to-serial device neither JSSC or Purejavacomm appear to work. PJC does work with the serial ports on the DeckLink cards though.

To run a demo:

```shell
mvn exec:java -Dexec.mainClass=org.mbari.vcr4j.examples.remote.FrameCaptureDemo01 -Dexec.args="8800 https://m3.shore.mbari.org/videos/M3/mezzanine/Ventana/1997/04/1234/V1234_19970411T182522.964Z_t5s1_sd_tc02050400_h264.mp4" -pl vcr4j-examples
```