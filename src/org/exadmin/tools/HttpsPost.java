/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exadmin.tools;

import java.io.*;
import java.net.*;
import javax.net.ssl.*;
//import java.security.Security.*;
//import com.sun.net.ssl.*;
//import com.sun.*; 

//import javax.net.ssl.HttpsURLConnection;

        
public class HttpsPost {
       public static void main(String[] args) {
           String cookie=new String();
           String enc = "UTF-8";
           URL url = null;
           HttpURLConnection connection = null;
           try { 

             String query = "UserID=" + URLEncoder.encode("me@there.com",enc); 
             query += "&"; 
             query += "passwd=" + URLEncoder.encode("password",enc); 
             query += "&"; 
             query += "PreviousURL=" + URLEncoder.encode("",enc); 

             url = new URL("http://ikaros.dmst.aueb.gr/ws/lectures/net07/submittest.php"); 

             connection = (HttpURLConnection) url.openConnection();
             connection.setRequestMethod( "POST" ); 
             connection.setDoInput(true); 
             connection.setDoOutput(true);
             
             connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded"); 
             connection.setRequestProperty("Content-length",String.valueOf(query.length())); 
             connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; Net07 v1.0; Net07; DigExt)"); 

             // open up the output stream of the connection 
             DataOutputStream output = new DataOutputStream( connection.getOutputStream() ); 

             // write out the data 
             int queryLength = query.length(); 
             output.writeBytes( query ); 
             //output.close();

             System.out.println("Resp Code:"    + connection.getResponseCode()); 
             System.out.println("Resp Message:" + connection.getResponseMessage()); 

             // get ready to read the response from the cgi script 
             System.out.println("Reading response...");
             DataInputStream input = new DataInputStream( connection.getInputStream() ); 

             // read in each character until end-of-stream is detected 
             for( int c = input.read(); c != -1; c = input.read() ) 
	        System.out.print( (char)c ); 
             input.close(); 
          } catch(Exception e) {
              if (connection != null) { connection.disconnect(); }
             System.out.println( "Something bad just happened." ); 
             System.out.println( e ); 
             e.printStackTrace(); 
          }            
           
          
       }
}