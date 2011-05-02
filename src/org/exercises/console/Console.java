/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exercises.console;
import java.io.PrintStream;
import org.exercises.log.Logger;

/**
 *
 * @author George M. Zouganelis (gzoug@aueb.gr)
 */
public class Console {
    static String enc;
    static PrintStream stdout;
    static PrintStream stderr;
    static Console instance;
    
    
   
   static {
       // config default output encoding
       //  UTF8, 8859_7 CP1253, CP737
        String envEncoding = System.getenv("NET07_ENCODING");  // NET07_ENCODING overrides any output character encoding
        if (envEncoding==null) { envEncoding = ""; }
        if (envEncoding.length()>0) {
           enc = System.getenv("NET07_ENCODING");
        } else {
          if (System.getProperty("os.name").toUpperCase().indexOf("WINDOWS") != -1) {
            enc = "CP737"; 
          } else {
            enc = "UTF8";
          }
        }
        Logger.getInstance().log("Console encoding set to " +enc);
       try {           
           stdout = new PrintStream(System.out,true,enc);
           stderr = new PrintStream(System.err,true,enc);
       } catch (Exception e) {
           System.out.println(e.getMessage());
           e.printStackTrace();
           System.exit(1); 
       }

        instance = new Console();

   }
   
   Console(){
       
   }

   public static String getEncoding()  {
       return enc;
   }
   
   public Console getInstance(){
       return instance;
   }

   
   public static PrintStream out(){
       return stdout;
       //return System.out;
              
   } 
   public static PrintStream err(){
       return stderr;
       //return System.err;
   } 
  
}
