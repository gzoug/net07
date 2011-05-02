package org.exercises.submission;

import java.io.File;


import org.exercises.console.Console;
import org.exercises.util.XMLUtil;
import org.w3c.dom.Document;

public class FileSubmission implements Submission {
    private String path;
    private String lastError;

    public FileSubmission(String destination) {
        path = destination;
    }

    public boolean submit(Document xml, String sessionID, String userid) {
        lastError="";
        try {
	        File file = new File(path + "/" + sessionID + "-" + userid + ".xml");
	        if(file.exists()) {
	        	lastError = "Η εξέταση για αυτό το σετ ασκήσεων έχει ολοκληρωθεί";
	            Console.out().println(lastError);
	            return false;
	        }
	        return XMLUtil.writeDocument(xml, file);
        } catch (Exception ex) {
        	lastError = ex.toString();
        	return false;
        }
    }

    public boolean isCompleted(String sessionID, String userid) {
        lastError="";
        File file = new File(path + "/" + sessionID + "-" + userid + ".xml");
        return file.exists();
    }
    
    public boolean isActive(){
        return true;
    }

    @Override 
    public String toString() {
        return ("File Submission, path: '" + path + "'");
    }
    
    public String getLastError(){
        return lastError;
    }
}
