package ca.exp.soundboard.remake.util;

import java.io.OutputStream;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

/**
 * A slight modification of <tt>ConsoleHandler</> to work on streams other than <tt><System.err/tt>.
 * Less efficient than <tt>ConsoleHandler</> since a flush is made every publish, but much better log
 * retention on fatal crashes.
 */
public class ImmediateStreamHandler extends StreamHandler {

    /**
     * Create a <tt>ImmediateStreamHandler</tt>, with no current output stream.
     */
    public ImmediateStreamHandler() {
        super();
    }

    /**
     * Create a <tt>ImmediateStreamHandler</tt> with a given <tt>Formatter</tt>
     * and output stream.
     * <p>
     * @param out         the target output stream
     * @param formatter   Formatter to be used to format output
     */
    public ImmediateStreamHandler(OutputStream output, Formatter formatter) {
        super(output, formatter);
    }

    /**
     * Publish a <tt>LogRecord</tt>.
     * <p>
     * The logging request was made initially to a <tt>Logger</tt> object,
     * which initialized the <tt>LogRecord</tt> and forwarded it here.
     * <p>
     * @param  record  description of the log event. A null record is
     *                 silently ignored and is not published
     */
    @Override
    public synchronized void publish(LogRecord record) {
        super.publish(record);
        flush();
    }

    /**
     * Override <tt>StreamHandler.close</tt> to do a flush but not
     * to close the output stream.  That is, we do <b>not</b>
     * close the <tt>OutputStream</tt>.
     */
    @Override
    public synchronized void close() throws SecurityException {
        flush();
    }
}
