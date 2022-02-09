package me.akhsaul.common.custom;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.pattern.*;

import java.util.List;
import java.util.Map;

/**
 * Log4j2 {@link LogEventPatternConverter} colors output using the {@link AnsiOutput}
 * class. A single option 'styling' can be provided to the converter, or if not specified
 * color styling will be picked based on the logging level.
 */
@Plugin(name = "color", category = PatternConverter.CATEGORY)
@ConverterKeys({"clr", "color"})
public final class ColorConverter extends LogEventPatternConverter {

    private static final Map<String, AnsiElement> ELEMENTS = Map.of(
            "faint", AnsiStyle.FAINT,
            "red", AnsiColor.RED,
            "green", AnsiColor.GREEN,
            "yellow", AnsiColor.YELLOW,
            "blue", AnsiColor.BLUE,
            "magenta", AnsiColor.MAGENTA,
            "cyan", AnsiColor.CYAN
    );

    private static final Map<Integer, AnsiElement> LEVELS = Map.of(
            Level.FATAL.intLevel(), AnsiColor.RED,
            Level.ERROR.intLevel(), AnsiColor.RED,
            Level.WARN.intLevel(), AnsiColor.YELLOW,
            Level.DEBUG.intLevel(), AnsiColor.CYAN,
            Level.TRACE.intLevel(), AnsiColor.BLUE,
            Level.INFO.intLevel(), AnsiColor.GREEN
    );

    private final List<PatternFormatter> formatters;

    private final AnsiElement styling;

    private ColorConverter(List<PatternFormatter> formatters, AnsiElement styling) {
        super("style", "style");
        this.formatters = formatters;
        this.styling = styling;
    }

    /**
     * Creates a new instance of the class. Required by Log4J2.
     *
     * @param config  the configuration
     * @param options the options
     * @return a new instance, or {@code null} if the options are invalid
     */
    public static ColorConverter newInstance(Configuration config, String[] options) {
        if (options.length < 1) {
            LOGGER.error("Incorrect number of options on style. Expected at least 1, received {}", options.length);
            return null;
        }
        if (options[0] == null) {
            LOGGER.error("No pattern supplied on style");
            return null;
        }
        PatternParser parser = PatternLayout.createPatternParser(config);
        List<PatternFormatter> formatters = parser.parse(options[0]);
        AnsiElement element = (options.length != 1) ? ELEMENTS.get(options[1]) : null;
        return new ColorConverter(formatters, element);
    }

    @Override
    public boolean handlesThrowable() {
        for (PatternFormatter formatter : this.formatters) {
            if (formatter.handlesThrowable()) {
                return true;
            }
        }
        return super.handlesThrowable();
    }

    @Override
    public void format(LogEvent event, StringBuilder toAppendTo) {
        StringBuilder buf = new StringBuilder();
        for (PatternFormatter formatter : this.formatters) {
            formatter.format(event, buf);
        }
        if (buf.length() > 0) {
            AnsiElement element = this.styling;
            if (element == null) {
                // Assume highlighting
                element = LEVELS.get(event.getLevel().intLevel());
                element = (element != null) ? element : AnsiColor.GREEN;
            }
            appendAnsiString(toAppendTo, buf.toString(), element);
        }
    }

    private void appendAnsiString(StringBuilder toAppendTo, String in, AnsiElement element) {
        toAppendTo.append(AnsiOutput.toString(element, in));
    }
}