# vcr4j-kipro

This is a __very__ basic VideoIO implementation for the AJA KiPro Quad. It currently only supports fetching timecode from the Quad across the network. No other controls are implemented. This minimal implementation is used by MBARI for real-time video annotation for our Mini-ROV.

# indexObservable

Since MBARI is only using this the KiPro VideoIO during real-time recording, the indexObservable will return VideoIndex objects with a __timecode from the KiPro__ and a __timestamp from the computer__ that is using the VideoIO object.