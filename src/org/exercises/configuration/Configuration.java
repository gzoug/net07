package org.exercises.configuration;

import java.util.LinkedList;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Random;

import java.io.InputStream;

import org.exercises.util.XMLUtil;
import org.exercises.submission.*;

import org.exercises.students.Student;
import org.exercises.students.StudentList;
import org.exercises.log.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;

/**
 * 
 * @author Vassilios Karakoidas (bkarak@aueb.gr)
 * @author George M. Zouganelis (gzoug@aueb.gr)
 */
public class Configuration {
    private final static Configuration defaultInstance;
    //
    private HashMap<ConfigurationParameters,String> data;
    private LinkedList<ExamFileParams> examFiles;  // holds: <key>filename,<val>ExamFileParams/""
    private StudentList students;
    private Random rnd;
    private HashMap<SubmissionEnums,ArrayList<String>> submissionPaths; // Array holds the destination
    private ArrayList<Submission> submissionMethods;
    String username = "";


    
    static {
        defaultInstance = new Configuration();
    }
    
    private Configuration() {
        students = StudentList.getInstance();
        data = new HashMap<ConfigurationParameters,String>();
        examFiles = new LinkedList<ExamFileParams>();
        rnd = new Random(System.currentTimeMillis());
        submissionPaths = new HashMap<SubmissionEnums,ArrayList<String>>();
        submissionMethods = new ArrayList<Submission>();
                
        
        // TODO: initialize default parameters, make this one call 'whoami'
        username = System.getenv("USER");
        data.put(ConfigurationParameters.LOGIN, username);
        
        Student stu = students.get(username);
        
        String registry = stu.getRegistry();        
        data.put(ConfigurationParameters.REGISTRY_NUMBER, registry);
        
        String realName = stu.getName();
        data.put(ConfigurationParameters.REAL_NAME, realName);
        
        // read the XML file and populate the examFiles attribute
        InputStream cis = ClassLoader.getSystemResourceAsStream("data/configuration.xml");
        if(cis == null) {
            Logger.getInstance().exit("Could not find configuration.xml (EXITING)");
        }
        Document doc = XMLUtil.parseDocument(cis);
        if(doc == null) {
            Logger.getInstance().exit("Cannot parse configuration file");
        }
        
        // get the input examFiles (exercises)
        NodeList nl = doc.getElementsByTagName("file");
        for(int i = 0;i < nl.getLength();i++) {
            ExamFileParams ex = new ExamFileParams();
            

            //Node n = nl.item(i);
            NamedNodeMap nnm = nl.item(i).getAttributes();

            Node attrNode = null;
            attrNode = nnm.getNamedItem("name");
            if (attrNode!=null) { ex.setFilename( attrNode.getNodeValue() );}
            attrNode = nnm.getNamedItem("subname");
            if (attrNode!=null) { ex.setCheckFilename(attrNode.getNodeValue() );}
            attrNode = nnm.getNamedItem("start");
            if (attrNode!=null) { ex.setStartingDate(attrNode.getNodeValue()) ; }
            attrNode = nnm.getNamedItem("end");
            if (attrNode!=null) { ex.setEndingDate(attrNode.getNodeValue()) ; }
            attrNode = nnm.getNamedItem("encoded");
            if (attrNode!=null) { ex.setEncrypted( (attrNode.getNodeValue().compareTo("yes")==0) );}
            
            examFiles.add(ex);
        }
        
        // get the submission parameters
        // file input
        ArrayList<String> paths = new ArrayList<String>();
        String destPath;
        nl = doc.getElementsByTagName("file-writer");
        for (int i=0; i<nl.getLength(); i++){
            destPath = nl.item(i).getAttributes().getNamedItem("path").getNodeValue(); 
            paths.add(destPath);
            submissionMethods.add(new FileSubmission(destPath));
        }
        submissionPaths.put(SubmissionEnums.FILE, paths );
        
        ArrayList<String> urls = new ArrayList<String>();
        nl = doc.getElementsByTagName("url-writer");
        for (int i=0; i<nl.getLength(); i++){
            destPath = nl.item(i).getAttributes().getNamedItem("uri").getNodeValue(); 
            urls.add(destPath);
            submissionMethods.add(new HttpSubmission(destPath));
        }
        submissionPaths.put(SubmissionEnums.URL, urls );
        
        // keep only active submission methods
        int smSize = submissionMethods.size();
        for (int i=smSize; i>0; i--){
            if (!submissionMethods.get(i-1).isActive()){
                submissionMethods.remove(i-1);
            }
        }
        
    }
    
    // for debuging : if gzoug then show all exercises
    public String getUsername() {
        return username;
    }
    public String get(ConfigurationParameters cp) {
        if(data.containsKey(cp)) {
            return data.get(cp);
        }        
        return "NO VALUE";
    }
    
    public LinkedList<ExamFileParams> getExamFiles() {
        return examFiles;
    }
    
    public HashMap<SubmissionEnums,ArrayList<String>> getSubmissionPaths(){
        return submissionPaths;
    }
    
    public ArrayList<Submission> getSubmissionMethods(){
        return submissionMethods;
    }
    
    public Random getRandomGenerator() {
        return rnd;
    }

    public static Configuration getInstance() {
        return defaultInstance;
    }
}