package org.mbari.vcr4j.rxtx;

import java.io.File;
import org.mbari.nativelib.Native;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Loads the native RXTX libraries needed. Just call:
 *
 * <pre>
 * RXTX.setup();
 * </pre>
 */
public class RXTX {
    public static final String LIBRARY_NAME = "rxtxSerial";
    private static final Logger log = LoggerFactory.getLogger(RXTX.class);

    public static void setup() {
        try {
            System.loadLibrary(LIBRARY_NAME);
        }
        catch (UnsatisfiedLinkError e) {
            extractAndLoadNativeLibraries();
        }
    }

    private static void extractAndLoadNativeLibraries() {
        String libraryName = System.mapLibraryName(LIBRARY_NAME);
        String os = System.getProperty("os.name");

        if (libraryName != null) {
            File libraryHome = getLibraryHome();
            if (!libraryHome.exists()) {
                libraryHome.mkdirs();
            }
            if (!libraryHome.canWrite()) {
                throw new RuntimeException("Unable to extract native libary to " + libraryHome +
                        ". Verify that you have write access to that directory");
            }
            new Native(LIBRARY_NAME, "native", libraryHome, RXTX.class.getClassLoader());
        }
        else {
            log.error("A native RXTX library for your platform is not available. " +
                    "You will not be able to control your VCR");
        }
    }

    private static File getLibraryHome() {
        File tempDir = new File(System.getProperty("java.io.tmpdir"));
        return new File(tempDir, "vcr4j");
    }
}