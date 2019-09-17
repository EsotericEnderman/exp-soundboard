package ca.exp.soundboard.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LogFormatter extends Formatter {

    // format string for printing the log record
    protected static final String format = "[%1$td %1$tb, %1$tY / %1$tl:%1$tM:%1$tS %1$Tp] [%7$s] {%2$s} [%4$s] %5$s%6$s%n";
    protected static final String forma2 = "[%1$td %1$tb, %1$tY / %1$tl:%1$tM:%1$tS %1$Tp] [%7$s] {%3$s} [%4$s] %5$s%6$s%n";
    protected final Date dat = new Date();

    @Override
    public synchronized String format(LogRecord record) {
        dat.setTime(record.getMillis());

        String source;
        if (record.getSourceClassName() != null) {
            source = record.getSourceClassName();
            if (record.getSourceMethodName() != null) {
                source += " " + record.getSourceMethodName();
            }
        } else {
            source = record.getLoggerName();
        }

        String message = formatMessage(record);
        String throwable = "";
        if (record.getThrown() != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            pw.println();
            record.getThrown().printStackTrace(pw);
            pw.close();
            throwable = sw.toString();
        }

        return String.format(forma2,
                dat,
                source,
                record.getLoggerName(),
                record.getLevel().getLocalizedName(),
                message,
                throwable,
                Thread.currentThread().getName());
    }
}
