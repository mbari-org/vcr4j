# vcr4j-sharktopoda-client

API to simplify the creation of remote Java video players that support support communication via UDP (using the vcr4j-sharktopoda module)

## Usage:

Add the client to your project:

```xml
<repositories>
    <repository>
        <snapshots>
            <enabled>false</enabled>
        </snapshots>
            <id>bintray-org-mbari-maven</id>
        <name>org-mbari</name>
        <url>https://dl.bintray.com/org-mbari/maven</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>org.mbari.vcr4j</groupId>
        <artifactId>vcr4j-sharktopoda-client</artifactId>
        <version>${vcr4j.version}</version>
    </dependency>
</dependencies>
```

Applications need to implement the [ClientController](src/main/java/org/mbari/vcr4j/sharktopoda/client/ClientController.java) interface.

```java
// Setup
ClientController controller = new MyClientController();
int port = 5005; // Port for UDP comms
IO io = new IO(controller, port);
// That's it!!

// When done, clean up resources
io.close();
```

If you want to run a sanity check, you can configure a class to test remote configuration. Add the following dependency:

```xml
<dependency>
    <groupId>org.mbari.vcr4j</groupId>
    <artifactId>vcr4j-sharktopoda</artifactId>
    <version>${vcr4j.version}</version>
</dependency>
```

```java
// Run commands that will call your ClientController
SharktopodaVideoIO videoIO = new SharktopodaVideoIO(UUID.randomUUID(), "localhost", port);
videoIO.send(new OpenCmd(new URL("http://www.nowhere.org/mymovie.mp4")));
videoIO.send(VideoCommands.PLAY);
// videoIO.send(SharkCommands.SHOW);
// videoIO.send(SharkCommands.REQUEST_ALL_VIDEO_INFOS);
// videoIO.send(VideoCommands.PLAY);
// videoIO.send(VideoCommands.PAUSE);
// videoIO.send(VideoCommands.REQUEST_TIMESTAMP);
// videoIO.send(VideoCommands.REQUEST_ELAPSED_TIME);
// videoIO.send(new SeekElapsedTimeCmd(Duration.ofSeconds(10)));
// videoIO.send(SharkCommands.FRAMEADVANCE);
// videoIO.send(new FramecaptureCmd(UUID.randomUUID(), new File("/Foo")));
// videoIO.send(SharkCommands.CLOSE);
videoIO.close();
```