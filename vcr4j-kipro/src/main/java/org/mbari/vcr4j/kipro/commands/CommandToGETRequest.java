package org.mbari.vcr4j.kipro.commands;

import org.mbari.vcr4j.VideoCommand;
import org.mbari.vcr4j.commands.VideoCommands;
import org.mbari.vcr4j.time.HMSF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Mapping a command to a request requires knowing 2 pieces of state: The http address of the kipro and the
 * connection id. This connection id may timeout. When that happends we ditch this instance and create a new one.
 * @author Brian Schlining
 * @since 2016-02-08T16:11:00
 */
public class CommandToGETRequest {

    private static final Logger log = LoggerFactory.getLogger(CommandToGETRequest.class);
    private static final HMSF HMSF_ZERO = new HMSF(0, 0, 0, 0);
    public static final String UNDEFINED = "";
    private final String httpAddress;
    private final int connectionID;

    public CommandToGETRequest(String httpAddress, int connectionID) {
        this.httpAddress = httpAddress.endsWith("/") ? httpAddress : httpAddress + "/";
        this.connectionID = connectionID;
    }

    public String apply(VideoCommand cmd) {
        String get;
        if (cmd instanceof QuadVideoCommands) {
            get = toGET((QuadVideoCommands) cmd);
        }
        else if (cmd instanceof VideoCommands) {
            get = toGET((VideoCommands) cmd);
        }
        else {
            get = UNDEFINED;
        }
        return get;
    }

    public String toGET(QuadVideoCommands cmd) {
        String get;
        switch (cmd) {
            case CONNECT:
                get = httpAddress + "config?action=connect";
                break;
            default:
                get = UNDEFINED;
        }

        return get;

    }

    public String toGET(VideoCommands cmd) {
        String get;
        switch (cmd) {
            case REQUEST_INDEX:
                // falls through
            case REQUEST_TIMECODE:
                get = httpAddress + "config?action=wait_for_config_events&connectionid=" + connectionID;
                break;
            default:
                get = UNDEFINED;
        }
        return get;
    }


}
