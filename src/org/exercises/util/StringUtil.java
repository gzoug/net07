/*
 * StringUtil.java
 */

package org.exercises.util;

import java.util.Date;

import java.text.SimpleDateFormat;
import java.text.ParsePosition;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.exercises.log.Logger;

/**
 * 
 * @author George Zouganelis (gzoug@aueb.gr)
 * @author Vassilios Karakoidas (bkarak@aueb.gr)
 */
public class StringUtil {

    /**
     * parse a string into a Date object, based on the format parameter
     */
    public static Date parseDateString(String inDate, String ... formats) {
        Date d = null;
        for (int i=0; i<formats.length; i++){
            String format = formats[i];
            try {
                // Logger.getInstance().log(" Parsing date " + inDate + " using format " + formats[i]);
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                ParsePosition pos = new ParsePosition(0);
                d =  sdf.parse(inDate, pos);
                if ( d != null) { 
                    break; 
                } else {
                    Logger.getInstance().log("  Can not parse date " + inDate + " using format " + formats[i]);
                }
            } catch (NullPointerException pe) {
                Logger.getInstance().log("  Can not parse date " + inDate + " using format " + formats[i]);
            }
        }
        //Logger.getInstance().log(" Returning parsed date: " + d);
        return d;
    }

    public static String repString(String template, int count){
        StringBuilder sb = new StringBuilder();
        for (int i=0;i<count;i++){
            sb.append(template);
        }
        return sb.toString();
    }
    
    public static String purifyString(String s){
        return s.replaceAll("\\s", " ");        
    }
    
    public static String generateId(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("md5");
            md.reset();
            md.update(text.getBytes());

            StringBuffer hex = new StringBuffer();
            byte digest[] = md.digest();
            String tmpStr;
            for (int i=0; i<digest.length; i++){
               tmpStr = Integer.toHexString(0xFF & digest[i]);
               if (tmpStr.length()==1) {hex.append('0');} 
               hex.append(tmpStr);
            }
            return hex.toString();

        } catch (NoSuchAlgorithmException nsae) {            
            Logger.getInstance().log("Cannot initialize md5 algorithm");
            
            return "FAILED: md5";
        }
    }
}
