package org.exercises;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.text.SimpleDateFormat;

import org.exercises.configuration.Configuration;
import org.exercises.log.Logger;
import org.exercises.util.StringUtil;
import org.exercises.console.Console;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 * @author Vassilios Karakoidas (bkarak@aueb.gr)
 *
 */
public class Question {
    private final static Logger logger;
    //
    String id;
    String text;
    int difficulty;
    int grade;
    ArrayList<Answer> answers;
    
    static {
        logger = Logger.getInstance();
    }
    
    public Question(Node n) {
        answers = new ArrayList<Answer>();
        
        // get the attributes
        NamedNodeMap nnm = n.getAttributes();
        for(int i = 0;i < nnm.getLength();i++) {
            Node node = nnm.item(i);
            if(node.getNodeName().compareTo("id") == 0) {
                id = node.getNodeValue();
                continue;
            }
            if(node.getNodeName().compareTo("difficulty") == 0) {
                difficulty = Integer.parseInt(node.getNodeValue());
                continue;
            }
            if(node.getNodeName().compareTo("grade") == 0) {
                grade = Integer.parseInt(node.getNodeValue());
            }
        }
        
        // get the child nodes
        NodeList nl = n.getChildNodes();
        for(int i = 0;i < nl.getLength();i++) {
            Node node = nl.item(i);
            if(node.getNodeName().compareTo("text") == 0) {
                text = StringUtil.purifyString(node.getTextContent());
                continue;
            }
            if(node.getNodeName().compareTo("answer") == 0) {
                String aId = "";
                boolean correct = false;                
                
                NamedNodeMap answerMap = node.getAttributes();
                for(int k = 0;k < answerMap.getLength();k++) {
                    Node anode = answerMap.item(k);
                    if(anode.getNodeName().compareTo("id") == 0) {
                        aId = anode.getNodeValue();
                        continue;
                    }
                    if(anode.getNodeName().compareTo("correct") == 0) {
                        correct = Boolean.parseBoolean(anode.getNodeValue());
                        continue;
                    }                    
                }
                
                String anstext = "";
                try {
                	anstext = node.getChildNodes().item(0).getTextContent();
                    anstext = StringUtil.purifyString(anstext);
                	answers.add(new Answer(aId, anstext, correct));                
                } catch (Exception ex) {
                	Console.out().println("\n*** Error parsing answer " + aId + " for Question ID " + id + "\n");
                    logger.log("Error parsing answer " + aId + " for Question ID " + id);                    
                }
                

            }
        }
    }
    public static void help(){
    	
        Console.out().println();
        Console.out().println(StringUtil.repString("?????",8));
        Console.out().println("Πληκτρολογήστε τον αριθμό της απάντησης ή ");
        Console.out().println("quit για να τερματήσετε την διαδικασία χωρίς να αποστείλετε τις απαντήσεις σας ή ");
        Console.out().println("show για να επανεμφανίσετε την τελευταία ερώτηση ή ");
        Console.out().println("help για να δείτε ξανά αυτές τις πληροφορίες.");
        Console.out().println(StringUtil.repString("?????",8));
    }
    private static void printWrongAnswer(){
        Console.out().println();
        Console.out().println(StringUtil.repString("!!!!!",8));
        Console.out().println("Η απάντηση που δώσατε δεν είναι διαθέσιμη, δοκιμάστε ξανά.");
        Console.out().println(StringUtil.repString("!!!!!",8));        
    }
    
    public Node execute(int qNumber, Document d) {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        Node r = d.createElement("answer");
        
        Attr questionId = d.createAttribute("question-id");
        questionId.setNodeValue(id);

        Attr answerId = d.createAttribute("answer-id");
        
        // duration (for statistics)
        Attr duration  = d.createAttribute("duration");      
        
        
        long tmpTimestamp = System.currentTimeMillis();
      
        
        if(answers.size() > 2) {
            Collections.shuffle(answers, Configuration.getInstance().getRandomGenerator());
        }
        
        while(true) {
            Console.out().println();
            Console.out().printf("%dη ερώτηση: %s\n",qNumber,text);
            Console.out().println();
            
            for( int i = 0;i < answers.size();i++ ) {
                Console.out().println((i + 1) + ". " + answers.get(i).text);
            }
            Console.out().print("\nΑπάντηση (ή help για βοήθεια): ");
            
            try {
                String a = br.readLine();
                if (a.equalsIgnoreCase("help")){
                    help();
                } else if (a.equalsIgnoreCase("show")) {
                    continue;
                } else if (a.equalsIgnoreCase("quit")) {
                    Console.out().println("\n\nΟι απαντήσεις σας ΔΕΝ αποθηκεύτηκαν. Θα πρέπει να επαναλάβετε την διαδικασία.");
                    System.exit(0);
                } else {
                    int i = Integer.parseInt(a);
                    if(i > 0 && i <= answers.size()) {
                        answerId.setNodeValue(answers.get(i - 1).id);
                        break;
                    } else {
                        printWrongAnswer();
                    }
                }
            } catch (NumberFormatException nfe) {
                printWrongAnswer();
                logger.log("Malformed input on question - " + id);
            } catch (IOException ioe) {
                logger.log("IO error - " + ioe.toString());
            }
        }

        // duration (for statistics)
        duration.setNodeValue(Long.toString(System.currentTimeMillis() - tmpTimestamp));
        
        NamedNodeMap nnm = r.getAttributes();      
        nnm.setNamedItem(questionId);
        nnm.setNamedItem(answerId);
        nnm.setNamedItem(duration);
        
        return r;
    }
}
