package ca.exp.soundboard.util;

import java.io.OutputStream;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

/**
 * A slight modification of <tt>ConsoleHandler</> to work on streams other than <tt><System.err/tt>
 */
public class ImmediateStreamHandler extends StreamHandler {

    public ImmediateStreamHandler() {
        super();
    }

    public ImmediateStreamHandler(OutputStream output, Formatter formatter) {
        super(output, formatter);
    }

    @Override
    public synchronized void publish(LogRecord record) {
        super.publish(record);
        flush();
    }

    @Override
    public synchronized void close() throws SecurityException {
        flush();
    }
}
