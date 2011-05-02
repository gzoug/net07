/*
 * AnswerChecker.java
 *
 * Created on 8 ÌÜúïò 2007, 12:23 ðì
 *
 * Internal Utility for Grading submited answers
 * Usage:
 * java -cp exercises.jar org.exercises.tools.AnswerChecker %1 %2
 *   where %1 is the exercise-ids.xml file to check against
 *         %2 can be a folder containing submited answer files 
 *            or a single answer file
 */

package org.exadmin.tools;

/**
 *
 * @author "George M. Zouganelis (gzoug@aueb.gr)"
 */

import java.io.FileInputStream;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import org.w3c.dom.Document;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.exercises.util.XMLUtil;
import org.exercises.util.StringUtil;
import org.exercises.console.Console;

import java.util.Date;

public class AnswerCheckerOld {
    static String AppName = "Net07 Answer checker v1.0";
    
    /** Creates a new instance of AnswerChecker */
    public AnswerCheckerOld() {
    }
    
    public static void CheckAnswers(Document ex, Document ans){
        //ArrayList<String> errors = new ArrayList<String>();
        String exID = XMLUtil.selectNodeValue(ex,"/exercise/@id");
        int exMchoice = Integer.parseInt(XMLUtil.selectNodeValue(ex,"/exercise/@mchoice"));
        //int exBasics = Integer.parseInt(XMLUtil.selectNodeValue(ex,"/exercise/@basics"));
        Date exStart = StringUtil.parseDateString(XMLUtil.selectNodeValue(ex,"/exercise/@start"),"yyyyMMdd hhmmss");
        Date exEnd = StringUtil.parseDateString(XMLUtil.selectNodeValue(ex,"/exercise/@end"),"yyyyMMdd hhmmss");
        
        String ansID = XMLUtil.selectNodeValue(ans,"/results/@exercise-id");
        String ansStudent = XMLUtil.selectNodeValue(ans,"/results/@student");
        //String ansFullName = XMLUtil.selectNodeValue(ans,"/results/@fullname");
        Date  ansReceived = StringUtil.parseDateString(XMLUtil.selectNodeValue(ans,"/results/@received"),"yyyyMMdd hhmmss");
                             
        if (exID.compareTo(ansID)!=0) {
            System.err.println("Answers (ID=" + ansID + ") are not for this Exercise (ID=" + exID + ").");
            return;
        }
        if (!(ansReceived.before(exEnd) && ansReceived.after(exStart))) {
            System.err.println("Answers are received beyond acceptable date limits");
            return;
        }

        ArrayList<String> qIDs = XMLUtil.SelectNodeValues(ans,"/results//answer/@question-id");
        if (qIDs.size() != exMchoice){
            System.err.println("Wrong count of submited answers. Expected " + exMchoice + ", received "+qIDs.size());
            return;
        }

        int correctAnswers = 0;
        int checkedQuestions = 0;
        for (int i=0; i<qIDs.size(); i++){
            //String correctAnswerID =  XMLUtil.SelectNodeValue(ex,"/exercise//question[@id=\""+qIDs.get(i)+"\"]/answer[@correct=\"true\"]/@id");
            ArrayList<String> correctAnswerID =  XMLUtil.SelectNodeValues(ex,"/exercise//question[@id=\""+qIDs.get(i)+"\"]/answer[@correct=\"true\"]/@id");
            
            String AnsweredID = XMLUtil.selectNodeValue(ans,"/results//answer[@question-id=\"" + qIDs.get(i) + "\"]/@answer-id"); 
            
            if (correctAnswerID==null){
                System.err.println("Error finding correct answer for question id " + qIDs.get(i) + " or question not found");
                continue;
            }
            if (AnsweredID==null){
                System.err.println("Error getting given answer for question id " + qIDs.get(i));
                continue;
            }
            checkedQuestions++;
            //if (correctAnswerID.compareToIgnoreCase(AnsweredID)==0) { correctAnswers++; }
            if (correctAnswerID.contains(AnsweredID)) { correctAnswers++; }            
        }
        
        Console.out().printf("%s,%s,%d,%d,%d\n",ansStudent,exID,exMchoice,checkedQuestions,correctAnswers);
        
        return;    
    }
    
    public static void Help(){
        Console.out().println("Wrong arguments.");
        Console.out().println("I need <exercise-ids.xml> < answers file | directory >");
    }
    public static void main(String[] args){
        Console.out().println(AppName);
        Document dEx = null;

        // Check for Parameters
        if(args.length < 2) {
          Help();
          return;
        }
        
        try {
            // Read Exercise
            File fEx = new File(args[0]);
            if( (!fEx.exists()) || fEx.isDirectory()  ) {
                Help();
                Console.out().println("Last error: Input file " + args[0] + " does not exist, or it's a Directory");
                return;
            }
            FileInputStream fsEx = new FileInputStream(fEx);
            dEx = XMLUtil.parseDocument(fsEx);

            // Read answers
            File dirAns = new File(args[1]);
            if ( !dirAns.exists() ) {
                Help();
                Console.out().println("Last error : File or Directory " + args[1] + " does not exist");
                return;
            }
         
            FileFilter xmlFileFilter = new FileFilter() {
                public boolean accept(File file) {
                   Pattern p = Pattern.compile(".*\\.[Xx][Mm][Ll]$");
                   Matcher m = p.matcher(file.getName());
                   return (!file.isDirectory()) && (m.find());
                }};
            
            File flistAns[] = null;
            if (dirAns.isDirectory()) {
              flistAns = dirAns.listFiles(xmlFileFilter);
            }
            else{
              flistAns = new File[1];
              flistAns[0] = dirAns;              
            }
            for (File fAns : flistAns){
               FileInputStream fsAns = new FileInputStream(fAns);
               Document dAns = XMLUtil.parseDocument(fsAns);
               
               System.err.println("Checking " + fAns.getName());
               CheckAnswers(dEx,dAns);
               
            }

            
            
            
        }
        catch (Exception e) {
            Console.out().println("ERROR: " + e.toString());
            return;                
        }
        
        

    }
}
