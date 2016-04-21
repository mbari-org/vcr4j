# vcr4j-kipro

This is a __very__ basic VideoIO implementation for the AJA KiPro Quad. It currently only supports fetching timecode from the Quad across the network. No other controls are implemented. This minimal implementation is used by MBARI for real-time video annotation for our Mini-ROV.

## indexObservable

Since MBARI is only using this the KiPro VideoIO during real-time recording, the indexObservable will return VideoIndex objects with a __timecode from the KiPro__ and a __timestamp from the computer__ that is using the VideoIO object.

## Example Usage

The KiPro has a built in JavaScript library for it's web interface that polls the device for connection ids. The `QuadVideoIO` class itself doesn't do the polling. Instead it should be docoreated with the `ConnectionPollingDecorator`. Here's an example for constructing a non-blocking, automatic connection maintaining `VideoIO` object:

```java
//If you use open it automatically connects. If you use new QuadVideoIO, you 
// must remember to send a connect command yourself.
QuadVideoIO rawIO = QuadVideoIO.open(httpAddress); 

// Move the IO off of the current thread
VideoIO<QuadState, QuadError> scheduledIO = new SchedulerVideoIO<>(rawIO, Executors.newCachedThreadPool());

// Add connection polling to maintain your connection
new ConnectionPollingDecorator(scheduledIO);

// Optionally, if you want to log what the QuadVideoIO is doing under the hood:
new QuadLoggingDecorator(scheduledIO)

```