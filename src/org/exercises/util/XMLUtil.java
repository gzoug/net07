package org.exercises.util;

import java.io.FileOutputStream;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;

import org.xml.sax.SAXException;

import java.io.FileWriter;
import java.io.InputStream;
import java.io.IOException;
import java.io.File;

import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;

import javax.xml.transform.dom.DOMSource;

import javax.xml.transform.stream.StreamResult;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.StringWriter;
import java.io.ByteArrayOutputStream;

import org.exercises.log.Logger;
import org.xml.sax.InputSource;

/**
 * 
 * @author Vassilios Karakoidas (bkarak@aueb.gr)
 * @author George Zouganelis (gzoug@aueb.gr)
 */
public class XMLUtil {
    private static Logger logger;
    
    static {
        logger = Logger.getInstance();
    }
    public static byte[] getDocumentAsByteArray(Document d) {
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            //StringWriter sw = new StringWriter();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            StreamResult result = new StreamResult(bos);
            DOMSource source = new DOMSource(d);
            transformer.transform(source,result );                       
            return bos.toByteArray();
            
        } catch (TransformerConfigurationException tce) {
            logger.log("cannot write document " + tce.getMessage());
        } catch (TransformerException te) {
            logger.log("cannot write document " + te.getMessage());
        } catch (Exception e) {
            logger.log(e.getMessage());
        }
        
        return null;
    }
    
    public static boolean writeDocument(Document d, File f) {
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
    
            //StreamResult result = new StreamResult(new FileWriter(f));
            //TODO: Remove the above, as it does not exports UTF.
            StreamResult result = new StreamResult(new FileOutputStream(f));
            DOMSource source = new DOMSource(d);
            transformer.transform(source, result);
            
            return true;
        } catch (TransformerConfigurationException tce) {
            logger.log("cannot write document " + tce.getMessage());
        } catch (TransformerException te) {
            logger.log("cannot write document " + te.getMessage());
        } catch (IOException ioe) {
            logger.log("cannot create file " + ioe.getMessage());
        }
        return false;
    }
    
    /**
     * Parse an XML Document from a specified InputStream
     */
    public static Document parseDocument(InputStream is) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document d = db.parse(is);
            
                        
            return d;
        } catch (SAXException saxe) {
            logger.log("cannot parse document - " + saxe.toString());
        } catch (IOException ioe) {
            logger.log("cannot read data - " + ioe.toString());
        } catch (ParserConfigurationException pce) {
            logger.log("cannot configure DOM parser - " + pce.toString());
        }
        
        return null;
    }
        public static Document parseDocument(String xmldata) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();  
            InputSource is = new InputSource(new StringReader(xmldata));
            
            Document d = db.parse( is );            
            return d;
        } catch (SAXException saxe) {
            logger.log("cannot parse document - " + saxe.toString());
        } catch (IOException ioe) {
            logger.log("cannot read data - " + ioe.toString());
        } catch (ParserConfigurationException pce) {
            logger.log("cannot configure DOM parser - " + pce.toString());
        }
        
        return null;
    }
    
    public static Document newDocument() {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            return db.newDocument();
        } catch (ParserConfigurationException pce) {
            logger.log("Cannot create new Document (DOM) " + pce.toString());
            
            return null;
        }
    }

    /** 
     * Returns a NodeList using XPath Queries 
     */
    public static NodeList executeQuery(Document doc, String xpath) {
       try {
         XPathFactory factory = XPathFactory.newInstance();
         XPathExpression expr = factory.newXPath().compile(xpath);  
         Object result = expr.evaluate(doc, XPathConstants.NODESET);

         return  (NodeList) result;
       } catch (XPathExpressionException e ){
          logger.log("XPATH error " + e.getMessage());
          return null;
       }
    }

    /**
     * Select a node text value using XQuery
     */
    public static String selectNodeValue(Document doc, String xpath) {
        NodeList n = executeQuery(doc, xpath);    
        if (n != null) {
            if ( n.getLength() != 0 ) {  
              return n.item(0).getTextContent();
            }       
        }
        return null;
    }

    /**
     * Select the text value of multiple nodes using XQuery
     */
    public static ArrayList<String> SelectNodeValues(Document doc, String xpath){
        ArrayList<String> v = new ArrayList<String>();
        NodeList n = executeQuery(doc, xpath);
        if (n != null) {
            int len = n.getLength(); 
            for (int i = 0; i < len; i++){
               v.add(n.item(i).getTextContent());
            }        
        }
        return v;
    }
}
