# VCR4J

Video controls for various devices. Used by MBARI's video annotation and reference system. The design of this library depends heavily on [ReactiveX](https://github.com/ReactiveX/RxJava) and can be summed up by:

```
VideoController ---> VideoIO ---> Observable<VideoError>
                             |--> Observable<VideoIndex>
                             |--> Observable<VideoState>
                             `--> Subject<VideoCommand, VideoCommand>
```

The `VideoIO` object sends `VideoCommand` objects via the `commandSubject`. Response are parsed and the appropriate observable: `errorObservable`, `indexObservable`, or `stateObservable` is updated. 

Any implentation of `VideoCommand` can be sent to a VideoIO object, but it will only respond to ones it knows about (you can modify this with decorators though).

Example usage can be found in the `vcr4j-examples` module.

## Usage

### Adding to your project  
Clone this repo and run `mvn install`. Pick the implementation or implementations that you need for your project and add the maven dependency for it. Here's an example for adding RXTX support for VCR's via RS422:

```xml
<dependency>
    <groupId>org.mbari.vcr4j</groupId>
    <artifactId>vcr4j-rxtx</artifactId>
    <version>${vcr4j.version}</version>
</dependency>
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


## Notes

- When testing on Mac OS X 10.11 using a GUC232A usb-to-serial device and prolific's drivers. THe port doesn't seem to close property with RXTX. I'm force to unplug and replug it in after each test to reset the port.
- When testing on Mac OS X 10.11 using a GUC232A usb-to-serial device neither JSSC or Purejavacomm appear to work. PJC does work with the serial ports on the DeckLink cards though.
