# vcr4j-sharktopoda

A `VideoIO` implementation for the [Sharktopoda video player](https://github.com/mbari-media-management/Sharktopoda)

```xml
dependency>
    <groupId>org.mbari.vcr4j</groupId>
    <artifactId>vcr4j-sharktopoda</artifactId>
    <version>${vcr4j.version}</version>
</dependency>
```

## Usage

To connect to Sharktopoda:

```java
import java.util.UUID;
import org.mbari.vcr4j.sharktopoda.SharktopodaError;
import org.mbari.vcr4j.sharktopoda.SharktopodaState;
import org.mbari.vcr4j.sharktopoda.SharktopodaVideoIO;

// --- Configure connection to sharktopoda

// The UUID is used to tell sharktopoda which window we care about. 
// Lookup UUID from your video asset manager, or just use a random one.
UUID uuid = UUID.randomUUID(); 

// We can connect to sharktopoda on other hosts. 
// But in general it will be localhost
String host = "localhost"; 

// UDP port to connect to sharktopoda. 
// Configure this in Sharktopoda too
int port = 8800; 

VideoIO<SharktopodaState, SharktopodaError> io = new SharktopodaVideoIO(uuid, host, port);
```

If you want to grab frames from the video:

```java
import org.mbari.vcr4j.sharktopoda.commands.FramecaptureCmd;
import org.mbari.vcr4j.sharktopoda.decorators.FramecaptureDecorator;

// --- Setup for framecapture

// This is the port that Sharktopoda can send async notifications back 
// to our videoio class. Since framecapture takes a relatively long time
// we don't wait for a response to framecapture commands. Instead, 
// Sharktopoda will eventually notify us when the image has been
// captured and written to disk
int notificationPort = 8900;

FramecaptureDecorator decorator = new FramecaptureDecorator(io, notificationPort);

// When a frame has been captured print out it's location. You could 
// also do other things like read the image from disk or move the image,
// etc.
decorator.getFramecaptureObservable()
        .forEach(r -> System.out.println(r.getImageLocation()));

// --- Initiate an image capture

// This is used to tag which image we are talking about. When the image
// is captured, Sharktopoda will respond about it and the response will
// contain this ID.
UUID imageUUID = UUID.randomUUID();

File imageLocation = new File("/Path/to/same/image/to/myimage.png") // could also be ".jpg"

io.send(new FramecaptureCmd(imageUUID, imageLocation));
```
