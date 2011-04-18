package org.kofemann;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.LoggingEvent;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author kofemann
 */
public class MongoDBAppenderTest {
 
    MongoDBAppender _mongoDBAppender;
    @Before
    public void setUp() {
        _mongoDBAppender = new MongoDBAppender();
        _mongoDBAppender.setName("MongoDBAppenderTest");
        _mongoDBAppender.start();
    }

    @Test
    public void testLog() {
        LoggingEvent e = new LoggingEvent();
        e.setMessage("log message");
        e.setLevel(Level.DEBUG);
        e.setTimeStamp(System.currentTimeMillis());
        e.setLoggerName("my.logger");
        e.setCallerData(Thread.currentThread().getStackTrace());        
        _mongoDBAppender.append(e);
    }
}
