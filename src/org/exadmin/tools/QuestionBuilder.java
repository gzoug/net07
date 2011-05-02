/*
 * QuestionBuilder.java
 * (former name: XMLChecker.java)
 * 
 * Created on 8 ÌÜúïò 2007, 12:23 ðì
 * @author Vassilios Karakoidas (bkarak@aueb.gr)
 * @author George M. Zouganelis (gzoug@aueb.gr)
 *
 *
 * Internal utility only for exercise authors
 * Usage:
 * java -cp exercises.jar org.exercises.tools.QuestionBuilder %1 
 *   where %1 is the exercise.xml file to parse in order to generate the
 *         exercise-ids.xml and exercise-distribution.xml files
 */

package org.exadmin.tools;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;

import org.exercises.util.XMLUtil;
import org.exercises.util.StringUtil;

import java.io.FileInputStream;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.exercises.util.Base64;
import org.exercises.util.EncryptEngine;

public class QuestionBuilder {
    
    public static void main(String[] args) {
    
        if (  args.length!=1 ) {
            System.out.println("I need an 'exercise.xml' file as argument in order to generate ids and distribution files)");
            return;
        }
      
        try {
            long modifier = System.currentTimeMillis();
            
            System.out.println("Reading file " + args[0]);
            File file = new File(args[0]);
            if(!file.exists()) {
                System.out.println("Input file " + args[0] + " does not exist");
                return;
            }
            String filePath = file.getParent();
            if (filePath==null) filePath = ".";

            if (filePath.length()!=0) { filePath += File.separatorChar; }
            
            Pattern p = Pattern.compile("(.*)\\.(:?xml|XML)");
            Matcher m = p.matcher(file.getName());
            m.find();
            String name = m.group(1);
            System.out.println("Processing " + file.getName());
            
            FileInputStream fis = new FileInputStream(file);
            Document d = XMLUtil.parseDocument(fis);            
            NodeList nl = d.getElementsByTagName("question");
            
            int newQIDsCounter = 0;
            for(int i = 0;i < nl.getLength();i++) {
                Node n = nl.item(i);
                String crc = "";
                
                NamedNodeMap nnm = n.getAttributes();
                if(nnm.getNamedItem("id") != null) { continue; } // leave intact any existing IDs

                newQIDsCounter++;
                
                NodeList nnl = n.getChildNodes();
                for(int k = 0;k < nnl.getLength();k++) {
                    if(nnl.item(k).getNodeName().compareTo("text")==0) {
                        crc = StringUtil.generateId(nnl.item(k).getTextContent() + modifier);
                        modifier++;
                        break;
                    }
                }
                //
                Attr attr = d.createAttribute("id");
                attr.setValue(crc);
                
                nnm.setNamedItem(attr);
            }
            if (newQIDsCounter!=0){
                System.out.println(newQIDsCounter + " new question IDs generated.");
            }
            
            //
            nl = d.getElementsByTagName("answer");

            int newAIDsCounter = 0;
            for(int i = 0;i< nl.getLength();i++) {
                Node n = nl.item(i);

                NamedNodeMap nnm = n.getAttributes();
                if(nnm.getNamedItem("id") != null) { continue; }
                newAIDsCounter++;
                String crc = StringUtil.generateId(n.getTextContent());

                Attr attr = d.createAttribute("id");
                attr.setValue(crc);
                
                nnm.setNamedItem(attr);
            }
            if (newAIDsCounter!=0){
                System.out.println(newAIDsCounter + " new answer IDs generated.");
            }
            
            // Save the Q&A control file (including "correct" attribute)
            XMLUtil.writeDocument(d, new File(filePath + name + "-ids.xml"));
            
            // Strip "correct" attribute and save it for distribution
            for(int i = 0;i < nl.getLength();i++) {
                Node n = nl.item(i);
                
                NamedNodeMap nnm = n.getAttributes();
                for(int k = 0;k < nnm.getLength();k++) {
                    Node nn = nnm.item(k);
                    if(nn.getNodeName().compareTo("correct") == 0) {
                        nnm.removeNamedItem("correct");
                    }
                }
            }           
            XMLUtil.writeDocument(d, new File( filePath + name + "-distribution.xml"));
            
            // Save the encoded distribution file
            EncryptEngine enc = EncryptEngine.getInstance();
            byte[] encodedData = enc.encrypt(XMLUtil.getDocumentAsByteArray(d));
            EncryptEngine.toByteArrayFile(Base64.encode(encodedData).getBytes(), filePath + name + "-distribution.enc");
            
            System.out.print("Please copy " + filePath+name+"-distribution.[xml|enc] to src/data and ");
            System.out.println(filePath+name+"-ids.xml to src/org/exadmin/data");
            System.out.println("Finally, edit the src/data/configuration.xml configuration file");
            
            
            
                   
        } catch (Exception e) {
            System.out.println("ERROR: " + e.toString());
            e.printStackTrace();
            return;
        }
        
        
    }

}
