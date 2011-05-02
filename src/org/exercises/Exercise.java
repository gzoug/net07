package org.exercises;

import org.exercises.submission.Submission;
import org.exercises.util.StringUtil;
import org.exercises.util.XMLUtil;
import org.exercises.util.EncryptEngine;
import org.exercises.util.Base64;

import org.exercises.configuration.Configuration;
import org.exercises.configuration.ConfigurationParameters;
import org.exercises.log.Logger;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.ArrayList;
import java.util.Random;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import org.exercises.configuration.ExamFileParams;
import org.exercises.console.Console;
import org.exercises.students.Student;

/**
 * 
 * @author Vassilios Karakoidas (bkarak@aueb.gr)
 * @author George Zouganelis (gzoug@aueb.gr)
 * 
 */
public class Exercise {
    private ExamFileParams exParams;
    private Date dateStart, dateEnd; // Starting/Ending Dates where this exercise is valid
    private int numOfQuestions; // max available questions and exercises to offer
    private String id;
    private String version;
    private String description;
    private ArrayList<Question> questions;
    
    private boolean loaded;

   
    //private ArrayList<BasicExercise> basics;
    //private Submission[] submissions;

    private void LoadExercise(Document d){
        questions = new ArrayList<Question>();

        id = new String();
        version = new String();
        description = new String();
        numOfQuestions = 0;

        dateStart = new Date();
        dateEnd = new Date();

        // BEGIN Parsing Exercise attributes
        NodeList nl = d.getElementsByTagName("exercise");
        Node root = nl.item(0);
        NamedNodeMap nnl = root.getAttributes();

        // Get Exercise ID
        id = nnl.getNamedItem("id").getNodeValue();

        // Get Exercise version
        version = nnl.getNamedItem("version").getNodeValue();

        // Get Max questions/exercises to offer
        numOfQuestions = Integer.parseInt(nnl.getNamedItem("mchoice").getNodeValue());

        // Parse exercise dates
        dateStart = StringUtil.parseDateString(exParams.getStartingDate(), "yyyyMMdd HHmmss", "yyyyMMdd");
        dateEnd = StringUtil.parseDateString(exParams.getEndingDate(), "yyyyMMdd HHmmss", "yyyyMMdd");

        // Get Description
        description = XMLUtil.selectNodeValue(d, "/exercise/description");
        description = StringUtil.purifyString(description);
            
        // A. parse questions
        NodeList nq = d.getElementsByTagName("question");
        for (int i = 0; i < nq.getLength(); i++) {
            boolean qisActive = true;
            
            // check if 'active' attribute forbids loading
            NamedNodeMap qAttrs = nq.item(0).getAttributes();
            if (qAttrs != null) {
                Node qActive = qAttrs.getNamedItem("active");
                if ((qActive != null) && "no".compareTo(qActive.getNodeValue())==0) {
                    qisActive = false;
                }
            }
            if (qisActive) {
                questions.add(new Question(nq.item(i)));
            }
        }
        loaded = true;

          
    }
    
    
    public Exercise(ExamFileParams examParams, InputStream is) {
        
        exParams = examParams;
        loaded = false;
        // parse the xml
        Logger logger = Logger.getInstance();
        logger.log("Parsing " + exParams.getFilename());
        Document d = null;
        if (!exParams.isEncrypted()) {
            d = XMLUtil.parseDocument(is);
        } else {
            try {
                String xmldata = new String();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                StringBuffer sb = new StringBuffer();
                while(br.ready()) {
                   sb.append(br.readLine());
                }
                EncryptEngine enc = EncryptEngine.getInstance();
                      
                byte[] decrypted = enc.decrypt(Base64.decode(sb.toString()) );
                xmldata = new String(decrypted,"UTF8");
                d = XMLUtil.parseDocument(xmldata);           
            } catch (IOException ioe) {
              
            }
            
        }
        if (d == null) {
            logger.log("Cannot parse XML Document " + exParams.getFilename());
            return;
        }
        LoadExercise(d);
    }
    

    // check if this set is DUE period
    public boolean isAvailable() {
        Date now = new Date(System.currentTimeMillis());
        boolean ret = false;
        try {
          ret =  loaded && now.after(dateStart) && now.before(dateEnd); 
          
          // Use this to avoid date-limits
          // ret |= (Configuration.getInstance().getUsername().compareTo("gzoug")==0);
          
        } catch (Exception e){
        }        
        return ret;
    }
    
    // check if current user has already submited answers for this set, using any submission method
    public boolean isSubmitted() {
        Configuration cfg = Configuration.getInstance();
        for(Submission s : cfg.getSubmissionMethods()) {
            if(s.isCompleted(this.id, cfg.get(ConfigurationParameters.LOGIN) )) { return true; }            
        }
        
        return false;
    }
    
    /**
     * Execute a specified exercise
     */
    public void execute() {
        //
        Configuration cfg = Configuration.getInstance();
        
        // Check if exists
        // TODO: what are we doing here?! 
        //for(Submission ss : submissions) {
        //    ss.isCompleted(this.id, cfg.get(ConfigurationParameters.LOGIN));
        // }
        
        // print out the description
        Console.out().println(description);
        
        // create the document
        Document answerDocument = XMLUtil.newDocument();
        
        // write the resultsNode Node
        Node resultsNode = answerDocument.createElement("results");
        
        // exercise id
        Attr exerciseId = answerDocument.createAttribute("exercise-id");
        exerciseId.setNodeValue(id);
        
        // received
        Attr received = answerDocument.createAttribute("received");
        
        Date current = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HHmmss");
        received.setNodeValue(sdf.format(current));
        
        // student
        Attr student = answerDocument.createAttribute("student");
        student.setNodeValue(cfg.get(ConfigurationParameters.REGISTRY_NUMBER));
        
        // real name
        Attr realName = answerDocument.createAttribute("full-name");
        realName.setNodeValue(cfg.get(ConfigurationParameters.REAL_NAME));
        
        // add the attributes
        NamedNodeMap nnm = resultsNode.getAttributes();
        
        nnm.setNamedItem(exerciseId);
        nnm.setNamedItem(received);
        nnm.setNamedItem(student);
        nnm.setNamedItem(realName);
        
        // add the resultsNode node
        answerDocument.appendChild(resultsNode);
        
        // execute multiple choice
        // TODO: is this random enough?
        Random rnd = cfg.getRandomGenerator();
        
        Collections.shuffle(questions, rnd);
        Collections.shuffle(questions, rnd);
        Collections.shuffle(questions, rnd);
        
        for(int i = 0; i < numOfQuestions;i++) {
            resultsNode.appendChild(questions.get(i).execute(i+1,answerDocument));
        }        
        
        // execute basic exercise
        // TODO: not implemented yet
        
        String sContinue = "";
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        
        while (true){
            if (numOfQuestions>0) {
                Console.out().print("\nΝα αποσταλούν οι απαντήσεις σας; (yes/no) ");
            } else {
                Console.out().print("\nΘέλετε να ολοκληρωθεί η διαδικασία; (yes/no) ");
            }
            try {
                sContinue = br.readLine();
            } catch (IOException e) {
                continue;
            }
            if ("yes".compareTo(sContinue) == 0){
                 // submited
                Attr submited = answerDocument.createAttribute("submited");
                current = new Date(System.currentTimeMillis());
                submited.setNodeValue(sdf.format(current));
                nnm.setNamedItem(submited);

                // store the document
                // todo: count succesfull submissions
                for( Submission sm : cfg.getSubmissionMethods() ) {
                    if(!sm.submit(answerDocument, this.id, cfg.get(ConfigurationParameters.LOGIN))) {
                        Logger.getInstance().log("Cannot execute " + sm.toString());
                        Console.out().println(sm.getLastError());
                        sContinue = "no";
                        break;
                    }
                }
                
                if("yes".compareTo(sContinue) == 0) {
                    if (numOfQuestions>0) {
                       Console.out().println("\nΟι απαντήσεις σας καταχωρήθηκαν με επιτυχία!!");
                    } else {
                       Console.out().println("\nΗ διαδικασία ολοκληρώθηκε με επιτυχία!!");
                    }
                    return;
                }
            }
            if ("no".compareTo(sContinue) == 0) {
                if (numOfQuestions>0) {
                    Console.out().println("\nΟι απαντήσεις σας ΔΕΝ ΚΑΤΑΧΩΡΗΘΗΚΑΝ, θα πρέπει να επαναλάβετε την διαδικασία.");
                } else {                    
                    Console.out().println("\nΗ ΔΙΑΔΙΚΑΣΙΑ ΔΕΝ ΟΛΟΚΛΗΡΩΘΗΚΕ, θα πρέπει να επαναληφθεί.");
                }
                return;
            }
            Console.out().println("Απαντήστε πληκτρολογόντας \"yes\" ή \"no\"");
        }
    }    
    
    public String getDescription(){
        return description;
    }
    
    public String getID(){
        return id;
    }  
    
    /*
     private String getSubmissionCode() {
        return Configuration.getInstance().get(ConfigurationParameters.LOGIN) + "-" + id;
    }
     * */
}
