package org.exercises.log;

import java.util.ArrayList;

public class Logger {
    private final static Logger logger;
    //
    private ArrayList<LogEntry> entries;
    private boolean console, debug;
    
    static {
        logger = new Logger();
    }
    
    private Logger() {
        console = false;
        debug = false;
        entries = new ArrayList<LogEntry>();
    }
    
    public void log(String message) {
        LogEntry l = new LogEntry(message); 
        entries.add(l);
        if(console) {
            System.out.println(l);
        }
    }
    
    public void print() {
        for( LogEntry l : entries ) {
            System.out.println(l);
        }
    }
    
    public void exit(String message) {
        log(message);
        if(debug) { print(); }
        System.exit(0);
    }
    
    public boolean getDebugMode() {
        return debug;
    }
    
    public boolean getConsoleMode() {
        return console;
    }
    
    public void setDebugMode(boolean b) {
        this.debug = b;
    }
    
    public void setConsoleMode(boolean b) {
        this.console = b;
    }
    
    // statics
    public static Logger getInstance() {
        return logger;
    }
}
