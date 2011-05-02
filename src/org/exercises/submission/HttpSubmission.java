/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exercises.submission;

import org.exercises.util.XMLUtil;
import org.exercises.console.Console;
        
import org.w3c.dom.Document;
import java.util.HashMap;
import java.io.*;
import java.net.*;
import javax.net.ssl.*;
import org.exercises.util.Base64;
import org.exercises.log.Logger;
/**
 *
 * @author George
 */
public class HttpSubmission implements Submission {
       private String baseurl;
       private String lastError;
       private String HTTPStatus = "";
       private String HTTPResponse = "";
       private String HTTPResult = "";
       Logger logger;

           
       private boolean HttpPost(String action, HashMap<String,String> params){
           lastError = "";
           
           String enc = "UTF-8";
           URL oUrl = null;
           HTTPStatus = "";
           HTTPResponse = "";
           HTTPResult = "";
           
           HttpURLConnection connection = null;
           try { 
             // Build the query parameters
             StringBuffer query = new StringBuffer();
             query.append("action=" + URLEncoder.encode(action,enc));
             if (params!=null){
                 for (String s: params.keySet()){
                     query.append("&" + s + "=" + URLEncoder.encode(params.get(s), enc));
                 }
             }
            

             oUrl = new URL(this.baseurl); 

             connection = (HttpURLConnection) oUrl.openConnection();
             connection.setRequestMethod( "POST" ); 
             connection.setDoInput(true); 
             connection.setDoOutput(true);
             
             connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded"); 
             connection.setRequestProperty("Content-length",String.valueOf(query.length())); 
             connection.setRequestProperty("User-Agent", "Net07"); 

             // open up the output stream of the connection 
             DataOutputStream output = new DataOutputStream( connection.getOutputStream() ); 

             // write out the data 
             //int queryLength = query.length(); 
             output.writeBytes( query.toString() ); 
             //output.close();

             HTTPStatus = String.valueOf(connection.getResponseCode());
             HTTPResponse = connection.getResponseMessage();

             
             // get ready to read the response from the cgi script 
             DataInputStream input = new DataInputStream( connection.getInputStream() ); 

             // read in each character until end-of-stream is detected 
             StringBuffer sb = new StringBuffer();
             for( int c = input.read(); c != -1; c = input.read() ) 
	        sb.append( (char)c ); 
             output.close();
             input.close(); 
             connection.disconnect();
             
             HTTPResult = sb.toString();
             logger.log("HTTPPost Action: " + action);
             logger.log("HTTPStatus  :" + HTTPStatus);
             logger.log("HTTPResponse:" + HTTPResponse);
             logger.log("HTTPResult  :" + HTTPResult);
             return true;
          } catch(Exception e) {
              if (connection != null) { connection.disconnect(); }
              lastError = "HSubmission:Ex01";
              logger.log("Error while performing HttpPost");
              logger.log(e.toString());
             //e.printStackTrace(); 
          }  
          return false;
           
       }
    
    
    
    
    public HttpSubmission(String destination) {
        //baseurl = Configuration.getInstance().get(ConfigurationParameters.SUBMISSION_URL);
        baseurl = destination;
        logger = Logger.getInstance();

    }
    
    public boolean submit(Document xml, String sessionID, String userid) {
        String b64 = null;
        lastError = "";
        boolean result = false;
        try {
          b64 = Base64.encode(XMLUtil.getDocumentAsByteArray(xml));
        
          HashMap<String,String> params = new HashMap<String,String>();
          params.put("sessionid", sessionID);
          params.put("userid", userid);
          params.put("answers", b64);
          result = (
                 HttpPost("submit",params) &&
                (HTTPStatus.compareTo("200")==0) && 
                (HTTPResponse.compareTo("OK")==0) &&
                (HTTPResult.compareTo("0")==0)
               );
          
          if (!result) { 
              if (HTTPResult.compareTo("-1:20")==0) {
                 Console.out().println("\nΗ εξέταση για αυτό το σετ ασκήσεων έχει ήδη ολοκληρωθεί!");
                 result=true;
              } else {
                 lastError = "HSubmission:Ex02:" + HTTPStatus + " :" + HTTPResult ;    
              }
          }
          
        } catch (Exception e) {
          Console.out().println("\nΣφάλμα κατά την αποστολή των απαντήσεών σας: " + e.getMessage());
          lastError = "HSubmission:Ex03";
        }
        return result;
    }

    public boolean isCompleted(String sessionID, String userid) {
        lastError="";
        boolean result = false;
        HashMap<String,String> params = new HashMap<String,String>();
        params.put("sessionid", sessionID);
        params.put("userid", userid);
        result = (
                HttpPost("isCompleted",params) &&
                (HTTPStatus.compareTo("200")==0) && 
                (HTTPResponse.compareTo("OK")==0) &&
                (HTTPResult.compareTo("0")==0) 
               );
        if (!result) { lastError = "HSubmission:Ex04:" + HTTPStatus + " :" + HTTPResult ;}
        return result;
    }
    
    public boolean isActive(){
        boolean result = (
                HttpPost("ping",null) &&
                (HTTPStatus.compareTo("200")==0) && 
                (HTTPResponse.compareTo("OK")==0) &&
                (HTTPResult.compareTo("pong")==0) 
               );
        return result;
    }

    @Override
    public String toString() {
         return ("HTTP Submission, url: '" + baseurl + "'");
    }
    
    public String getLastError(){
        return lastError;
    }
    
}
