package org.exercises;

import java.util.ArrayList;
import java.util.LinkedList;


import java.io.InputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.exercises.configuration.*;
import org.exercises.submission.*;

import org.exercises.log.Logger;
import org.exercises.util.StringUtil;
import org.exercises.console.Console;


/**
 *
 * @author Vassilios Karakoidas (bkarak@aueb.gr)
 * @author George Zouganelis (gzoug@aueb.gr)
 */
public class Main {
    private final static String AppName = "Net07 v1.11-4";
    private final static boolean DEBUG = true;

    public static void main(String[] args) {

        // initialize the logger
        Logger logger = Logger.getInstance();

        // parse debugging parameters
        if(DEBUG) {
            if(args.length != 0) {
                for (String s : args) {
                    if(s.compareTo("--console") == 0) {
                        logger.setConsoleMode(true);
                    }
                    if(s.compareTo("--debug") == 0) {
                        logger.setDebugMode(true);
                    }
                }
            }
        }


        // Display initial data
        Console.out().println(AppName + " is loading...");
       
        // initialize the configuration
        Configuration conf = Configuration.getInstance();

               
        if (conf.get(ConfigurationParameters.REGISTRY_NUMBER).compareTo("unknown")==0) {
           Console.out().println("Δεν είστε καταχωρημένος στο σύστημα"); 
           Console.out().println("Παρακαλούμε επικοινωνήστε με τους διαχειριστές"); 
           System.exit(1);
        }
        
        if (!(conf.getSubmissionMethods().size()>0)) {
           Console.out().println("Ανεπαρκής αποθηκευτικός χώρος."); 
           Console.out().println("Παρακαλούμε επικοινωνήστε με τους διαχειριστές"); 
           System.exit(1);            
        }
        
        Console.out().println("Όνομα φοιτητή: " + conf.get(ConfigurationParameters.REAL_NAME) 
                           + " - " + conf.get(ConfigurationParameters.REGISTRY_NUMBER));
        Console.out().println(StringUtil.repString("* * * * * ",7));
        Console.out().println("Attention - Προσοχή:");
        Console.out().println("- An den blepete Ellhnika, symbouleuteite tis odhgies sto boh0htiko uliko, sthn enothta tou ma0hmatos");
        Console.out().println("- Αν θέλετε να διακόψετε την διαδικασία απαντήσεων, γράψτε quit");
        Console.out().println("- ΠΟΤΕ μην κλείνετε το παράθυρο του putty με το [x].");
        Console.out().println("- Συμβουλευτείτε το \"βοηθητικό υλικό\" για την σωστή χρήση του putty");
        Console.out().println(StringUtil.repString("* * * * * ",7));

               

        // initialize and load the exercise list
        ArrayList<Exercise> exercises = new ArrayList<Exercise>();
        LinkedList<ExamFileParams> examFiles = conf.getExamFiles();
        for(ExamFileParams ex : examFiles)  {
            InputStream is = ClassLoader.getSystemResourceAsStream(ex.getFilename());
            if(is == null) {
               logger.log("Cannot find resource " + ex.getFilename());
               continue;
            }
            
            Exercise e = null;
            logger.log("Loading exercises...");
            e = new Exercise(ex,is);
            
            if (e.isAvailable() && !e.isSubmitted()) {
               exercises.add(e);
            }
        }
        
        // check if we have at least one exercise to perform, else exit;
        if (exercises.isEmpty()) {
            Console.out().println("Δεν υπάρχουν διαθέσιμα πακέτα ασκήσεων.");
            System.exit(0);
        }

        // if more than 1 exercises, ask which to complete, instead of the following iteration
        int reqExercise = 1;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        if (exercises.size()>1) {
            while (true){
                Console.out().println("Διαθέσιμα πακέτα ασκήσεων");
                Console.out().println(StringUtil.repString("-",40));
                int counter = 1;
                for (Exercise e: exercises){
                   Console.out().print(counter + ": ");
                   Console.out().println(e.getDescription());
                   counter++;
                } 
                String ans = null;
                try{
                    Console.out().println(StringUtil.repString("-",40));
                    Console.out().print("Επιλέξτε πακέτο ασκήσεων (0 γιά έξοδο): ");
                    ans = br.readLine();
                    int ians = Integer.parseInt(ans);
                    if ((ians>0) && (ians<=exercises.size())) { 
                       reqExercise = ians;
                       break;
                    } else if (ians==0) { // exit
                         reqExercise = -1;
                         break;  
                    } else { 
                         Console.out().println("\nΔεν υπάρχει το πακέτο ασκήσεων που ζητήσατε.\n");                         
                    }
                    
                } catch (NumberFormatException nfe) {
                    Console.out().println("\nΔεν υπάρχει το πακέτο ασκήσεων που ζητήσατε.\n");
                    logger.log("Malformed input on exercise select - " + ans);
                } catch (IOException ioe) {
                    logger.log("IO error - " + ioe.toString());
                }
            }
        }
        
        // Perform the exercise
        if (reqExercise!=-1) {
           exercises.get(reqExercise-1).execute();
        }
    }
}
