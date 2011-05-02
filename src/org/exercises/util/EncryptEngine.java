package org.exercises.util;

import java.io.ByteArrayOutputStream;

/**
 * Simple Encryption Engine
 * @author George M. Zouganelis
 * @version $Id:
 */
public class EncryptEngine {
    private byte[] internalBuffer;
    
    private static EncryptEngine instance;
    
    static {
        instance = new EncryptEngine();
    }
 
    public static EncryptEngine getInstance(){
        return instance;
    }
            
    public static void toByteArrayFile(byte[] inData, String filename){
        try {
            java.io.FileOutputStream b = new java.io.FileOutputStream(filename);
            b.write(inData);
            b.close();
        } catch (Exception e){
        }        
    }
    
     /**
     * Encrypt byte array into engine
     * @param inData binary data to be encoded
     */
    public  byte[] encrypt(byte[] inData){
    	ByteArrayOutputStream encoded = new ByteArrayOutputStream(inData.length);
        int c;
    	/* XOR each character with 170 (b10101010) and again with Not(possition+1) */
        for (int i = 0; i<inData.length; i++) {
            c = inData[i];
            
            // encrypt c
            
            encoded.write(c);
        }
        return encoded.toByteArray();        
    }
    
    
    
   
    /**
     * Decrypt encoded buffer.
     * @return decoded data as bytes[]
     */
    public  byte[] decrypt(byte[] inData){
        ByteArrayOutputStream decoded = new ByteArrayOutputStream(inData.length);
        int c;

        for (int i=0; i<inData.length; i++) {
            c = inData[i];
            
            // decript c
            
            decoded.write(c);
        }           
        return decoded.toByteArray();
    }
    

    
    /**
     * Simple Constructor
     */
    public EncryptEngine() {
    }


  
}
