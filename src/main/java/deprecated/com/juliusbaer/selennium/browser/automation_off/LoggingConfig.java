package deprecated.com.juliusbaer.selennium.browser.automation_off;

import java.util.logging.*;

public class LoggingConfig {
	static private Logger logger = null;

	static public Logger getLoggingConfig(String filename, String appName) {
		if (logger == null) {
			try {
				// Load a properties file from class path that way can't be
				// achieved with java.util.logging.config.file
				/*
				 * final LogManager logManager = LogManager.getLogManager(); try
				 * (final InputStream is =
				 * getClass().getResourceAsStream("/logging.properties")) {
				 * logManager.readConfiguration(is); }
				 */

				// Programmatic configuration
				System.setProperty("java.util.logging.SimpleFormatter.format",
						"%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS.%1$tL %4$-7s [%3$s] (%2$s) %5$s %6$s%n");

				final ConsoleHandler consoleHandler = new ConsoleHandler();
				consoleHandler.setLevel(Level.FINEST);
				consoleHandler.setFormatter(new SimpleFormatter());

				final FileHandler fileHandler = new FileHandler(filename,
						10000000, 20, true);
				fileHandler.setLevel(Level.FINEST);
				fileHandler.setFormatter(new SimpleFormatter());

				logger = Logger.getLogger(appName);
				logger.setLevel(Level.FINEST);
				logger.addHandler(consoleHandler);
				logger.addHandler(fileHandler);
			} catch (Exception e) {
				// The runtime won't show stack traces if the exception is
				// thrown
				e.printStackTrace();
			}

		}
		return logger;
	}
}