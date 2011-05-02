package org.exercises.log;

import java.util.Date;

public class LogEntry {
    Date date;
    String message;
    
    LogEntry(String message) {
        date = new Date();
        this.message = message;
    }
    
    public String getMessage() {
        return (date.toString() + " - " + message);
    }
    
    public String toString() {
        return getMessage();
    }
}
