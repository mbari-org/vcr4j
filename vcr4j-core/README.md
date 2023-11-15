# VCR4J-CORE

[![javadoc](https://javadoc.io/badge2/org.mbari.vcr4j/vcr4j-core/javadoc.svg)](https://javadoc.io/doc/org.mbari.vcr4j/vcr4j-core)

The design of the module is basically:

```text
VideoController ---> VideoIO ---> Observable<VideoError>
                             |--> Observable<VideoIndex>
                             |--> Observable<VideoState>
                             `--> Subject<VideoCommand, VideoCommand>
```

__VideoIO__: this is the core piece. It does the following:

- Sending the commands to a device/service.
- Managing the responses from the device/service. Responses are parsed and the appropriate _Observable_ is updated.
- You can monitor the commands by subscribing to the _commandSubject_
- Although many devices use request/response style communication, we chose to use `request -> observable will be updated at some point` style model.

__Observable<VideoState>__: Updates when the status of the device/service changes (playing, stopped, etc.)

__Observable<VideoIndex>__: This reports the index into the video. The index may be composed one or more of the following:

- A timecode (e.g. for VCR's)
- A timestamp, this is the date/time that that particular frame of video was recorded.
- An elapsed time, an index based on elapsed time since the start of the video.

__Observable<VideoError>__: Observable that monitors the error status. check the `hasError` method to see if everything is AOK.

__VideoController__: A wrapper around a VideoIO instance that simplfies the usage of VideoIO objects by providing a simple interface with play, fastforward, stop, etc. methods. 