/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exadmin.tools;

/**
 * $Id: ReportBuilder.java 290 2008-03-29 17:45:36Z gzoug $
 * @author George M. Zouganelis (gzoug@aueb.gr)
 */
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.FileFilter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.OutputKeys;

import javax.xml.transform.dom.DOMSource;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.exercises.util.XMLUtil;
import org.exercises.console.Console;
import org.exercises.configuration.Configuration;
import org.exercises.configuration.ExamFileParams;

public class ReportBuilder {

    static String AppName = "Net07 Report Builder v1.0";
    static Configuration conf;
    
    static {
        conf = Configuration.getInstance();
        
    }
    /** Creates a new instance of AnswerChecker */
    public ReportBuilder() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
       if(args.length != 1) {
          Help();
          return;
        }        
       Document d = buildSubmissionDocument(getSumbissionFiles(new File(args[0])));
       transformReport(d, new File("reportBuilder.html"));
       XMLUtil.writeDocument(d, new File("reportBuilder.xml"));       
        
    }
    
    public static void Help(){
        Console.out().println("Syntax: ReportBuilder <submission folder>");        
    }
    
    
    
    private static File[] getSumbissionFiles(File folder){
        File subFiles[] = null;
        
        if ((folder==null) || (!folder.exists()) || (!folder.isDirectory())) {
           Help();
           Console.out().println("Last error: Input folder does not exists, or it's not a directory");
           System.exit(1);            
        }
        
        FileFilter xmlFileFilter = new FileFilter() {
                public boolean accept(File file) {
                   Pattern p = Pattern.compile(".*\\.[Xx][Mm][Ll]$");
                   Matcher m = p.matcher(file.getName());
                   return (!file.isDirectory()) && (m.find());
                }
        };
        subFiles = folder.listFiles(xmlFileFilter);

        return subFiles;
    }
    
    
    // Join submission files into one DOM, append exercises.xml, and check correct answers
    private static Document buildSubmissionDocument(File subFiles[]){
       
       Document doc = XMLUtil.newDocument();
       Node root = doc.createElement("db");
       NamedNodeMap nmp = root.getAttributes();

       Attr dbDate = doc.createAttribute("date");
       dbDate.setNodeValue((new SimpleDateFormat("yyyyMMdd HHmmss").format(new Date(System.currentTimeMillis()))));              
       nmp.setNamedItem(dbDate);
       
       
       try { 
          for (File fAns : subFiles){
               FileInputStream fsAns = new FileInputStream(fAns);
               Document dAns = XMLUtil.parseDocument(fsAns);
               NodeList nl = XMLUtil.executeQuery(dAns, "//results");
               for (int i=0; i<nl.getLength(); i++){
                  Node nAns = nl.item(i);
                  root.appendChild(doc.importNode(nAns,true));                                 
               }
          }
          
          
       } catch (Exception e) {
          Console.out().println("Error loading sumbission files: " + e.getMessage()) ;
          System.exit(1);
       }       
       
       try {
           for (ExamFileParams exParam : conf.getExamFiles()){
               InputStream cis = ClassLoader.getSystemResourceAsStream(exParam.getCheckFilename());
               if (cis!=null) {
                   Document dCheck = XMLUtil.parseDocument(cis);
                   NodeList nl = XMLUtil.executeQuery(dCheck, "//exercise");
                   for (int i=0; i<nl.getLength(); i++) {
                     Node nCheck = nl.item(i);
                     root.appendChild(doc.importNode(nCheck,true));                      
                   }
               } else {
                   Console.out().println("Couldn't load " + exParam.getCheckFilename());
               }
           }
           
       } catch (Exception e) {
          Console.out().println("Error loading checkfiles: " + e.getMessage()) ;
          System.exit(1);           
       }
       
       doc.appendChild(root);
       
       // get Exercise Ids
       ArrayList<String> examIDs = XMLUtil.SelectNodeValues(doc, "/db/exercise/@id");
       for (String examID: examIDs){
           // get question ids for each exercise
           System.out.println("reporting for exID " + examID);
           ArrayList<String> qIDs = XMLUtil.SelectNodeValues(doc, "/db/exercise[@id='" + examID + "']/question/@id");
           for (String qID : qIDs) {
               // get the list of correct answerIDs for this question
               ArrayList<String> qCorrect = XMLUtil.SelectNodeValues(doc, "/db/exercise[@id='" + examID + "']/question[@id='"+qID+"']/answer[@correct='true']/@id");
               
               // get a list of submited answers for this question
               NodeList nlAns = XMLUtil.executeQuery(doc, "/db/results[@exercise-id='" + examID + "']/answer[@question-id='"  + qID + "']");
               for (int i=0; i<nlAns.getLength(); i++ ) {
                   Node nAns = nlAns.item(i);
                   NamedNodeMap nnm = nAns.getAttributes();
                   String answer = nnm.getNamedItem("answer-id").getNodeValue();
                   if (qCorrect.contains(answer)){
                       Attr correct = doc.createAttribute("correct");
                       correct.setNodeValue("true");
                       nnm.setNamedItem(correct);                     
                   }                   
               }
                       
           }

       }
               
       return doc;
    }
    
    public static void transformReport(Document db, File f){
        System.out.println("Building the final report");
        try {
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer(
                     new StreamSource(ClassLoader.getSystemResourceAsStream("org/exadmin/data/report-submits.xsl"))
                     );
            
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
    
            //StreamResult result = new StreamResult(new FileWriter(f));
            //TODO: Remove the above, as it does not exports UTF.
            StreamResult result = new StreamResult(new FileOutputStream(f));
            
            DOMSource source = new DOMSource(db);
            transformer.transform(source, result);
            
        } catch (Exception e) {
            Console.out().println(e.getMessage());
            e.printStackTrace();
            //logger.log("cannot create file " + ioe.getMessage());
        }

            
    }
    

}
