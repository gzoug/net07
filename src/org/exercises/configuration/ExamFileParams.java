/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exercises.configuration;

/**
 *
 * @author George
 */
public class ExamFileParams {
   private String  filename;
   private String  checkFilename;
   private String  startingDate;
   private String  endingDate;
   private boolean encrypted;

   public ExamFileParams(){
       filename = "";
       checkFilename = "";
       startingDate = "";
       endingDate = "";
       encrypted = false;
   }
   
    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
    public String getCheckFilename() {
        return checkFilename;
    }

    public void setCheckFilename(String filename) {
        this.checkFilename = filename;
    }
    public boolean isEncrypted() {
        return encrypted;
    }

    public void setEncrypted(boolean encrypted) {
        this.encrypted = encrypted;
    }

    public String getEndingDate() {
        return endingDate;
    }

    public void setEndingDate(String endingDate) {
        this.endingDate = endingDate;
    }

    public String getStartingDate() {
        return startingDate;
    }

    public void setStartingDate(String startingDate) {
        this.startingDate = startingDate;
    }
   
    @Override
    public String toString() {
        String s = " Exercise: " + this.filename + " starts on " + this.startingDate + " and ends on " + this.endingDate;
        if (this.encrypted) {s+=" (encrypted)";}
        return s;
    }
   
}
