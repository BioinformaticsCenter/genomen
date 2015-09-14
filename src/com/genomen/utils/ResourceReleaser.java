package com.genomen.utils;
import java.io.Closeable;
import java.io.IOException;
import org.apache.log4j.Logger;


/**
 * Convenience class for releasing resources.
 * @author ciszek
 */
public class ResourceReleaser {

    /**
     * Closes the given closable.
     * @param closeable Closable to be closed.
     */
    public static void close( Closeable closeable ) {

        if ( closeable != null ) {
            try {
                closeable.close();
            }
            catch (IOException ex) {
                Logger.getLogger(ResourceReleaser.class).error(ex);
            }
        }

    }
}
