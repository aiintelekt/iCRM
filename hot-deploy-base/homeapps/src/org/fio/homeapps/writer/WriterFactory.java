package org.fio.homeapps.writer;

import org.fio.homeapps.writer.LogWriter;

/**
 * @author Sharif
 *
 */
public final class WriterFactory {

	private static final LogWriter LOG_WRITER = new LogWriter();
	
	public static LogWriter getLogWriter () {
		return LOG_WRITER;
	}
	
}

