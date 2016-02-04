# VCR4J

Video controls for various devices. Used by MBARI's video annotation and reference system.


## Notes

- When testing on Mac OS X 10.11 using a GUC232A usb-to-serial device and prolific's drivers. THe port doesn't seem to close property with RXTX. I'm force to unplug and replug it in after each test to reset the port.
- When testing on Mac OS X 10.11 using a GUC232A usb-to-serial device neither JSSC or Purejavacomm appear to work. PJC does work with the serial ports on the DeckLink cards though.