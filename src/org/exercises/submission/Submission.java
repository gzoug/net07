package org.exercises.submission;

import org.w3c.dom.Document;

/**
 * 
 * @author Vassilios Karakoidas (bkarak@aueb.gr)
 */
public interface Submission {
    // Constructor(String destination); // how to implement this?!!
    public boolean isActive();
    public boolean isCompleted(String sessionID, String userid);
    public boolean submit(Document xml, String sessionID, String userid);
    public String  getLastError();
}
