package org.mbari.vcr4j.examples.rxtx;

class SimpleDemo01 {
    

    public static void main(String[] args) {

        String prog = SimpleDemo01.class.getName();
        String doc = "Usage: " + prog + " <commport> [options]\n\n" +
                "Options:\n" +
                "  -h, --help";
        Map<String, Object> opts = new Docopt(doc).parse(args);

        String portName = (String) opts.get("<commport>");

        RXTX.setup(); // sets up native libraries
        RXTXVideoIO io = RXTXVideoIO.open(portName);
    }
}