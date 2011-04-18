package org.kofemann;

import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.status.ErrorStatus;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import java.util.Date;
import java.util.Map;

/**
 * Logback {@link Appender} to write into MongoDB.
 *
 * @author kofemann
 */
public class MongoDBAppender extends AppenderBase<LoggingEvent> {

    private Mongo _mongo;
    private String _dbHost = "localhost";
    private int _dbPort = 27017;
    private String _dbName = "logging";
    private DBCollection _collection;

    @Override
    public void start() {
        try {
            _mongo = new Mongo(_dbHost, _dbPort);
            DB db = _mongo.getDB(_dbName);
            _collection = db.getCollection(super.getName());
        } catch (Exception e) {
            addStatus(new ErrorStatus("Failed to initialize MondoDB", this, e));
            return;
        }
        super.start();
    }

    public void setDbHost(String dbHost) {
        _dbHost = dbHost;
    }

    public void setDbName(String dbName) {
        _dbName = dbName;
    }

    public void setDbPort(int dbPort) {
        _dbPort = dbPort;
    }


    @Override
    public void stop() {
        _mongo.close();
        super.stop();
    }

    @Override
    protected void append(LoggingEvent e) {

        BasicDBObjectBuilder objectBuilder = BasicDBObjectBuilder.start().
                add("timestamp", new Date(e.getTimeStamp())).
                add("message", e.getFormattedMessage()).
                add("level", e.getLevel().toString()).
                add("logger", e.getLoggerName()).
                add("thread", e.getThreadName());
        if(e.hasCallerData()) {
            StackTraceElement st = e.getCallerData()[0];
            String callerData = String.format("%s.%s:%d", st.getClassName(), st.getMethodName(), st.getLineNumber());
            objectBuilder.add("caller", callerData);
        }
        Map<String, String> mdc = e.getMdc();
        if(mdc != null && !mdc.isEmpty()) {
            objectBuilder.add("mdc", new BasicDBObject(mdc) );
        }
        _collection.insert(objectBuilder.get());
    }
}
