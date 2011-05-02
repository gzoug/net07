/**
 * Create reporting for results
 * 
 * @author Vassilios Karakoidas (bkarak@aueb.gr)
 * 
 */

package org.exadmin.tools;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

import org.exercises.ExerciseResult;
import org.exercises.ExerciseResultComparator;
import org.exercises.util.StringUtil;
import org.exercises.util.XMLUtil;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class Report {
    public static void main(String[] args) {
        // create grade list
        if (args.length > 0 && args[0].compareTo("results") == 0) {
            HashMap<String, ArrayList<ExerciseResult>> results = new HashMap<String, ArrayList<ExerciseResult>>();
            
            // parse the files
            for(int i = 1; i < args.length;i++) {
                try {
                    BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(args[i])));
                    while(br.ready()) {
                        String l = br.readLine().trim();
                        String[] r = l.split(",");
                        
                        // i'm a little n00bish here ... but better safe than sorry
                        String registry = r[0];
                        String exerciseId = r[1];
                        String total = r[2];
                        String answered = r[3];
                        String correct = r[4];
                        
                        // set the results and correct
                        ArrayList<ExerciseResult> rl = results.get(registry);
                        if(rl == null) {
                            rl = new ArrayList<ExerciseResult>();
                            results.put(registry, rl);
                        }
                        ExerciseResult er = new ExerciseResult(registry, Integer.parseInt(exerciseId));
                        er.setCorrect(Integer.parseInt(correct));
                        er.setTotal(Integer.parseInt(total));
                        er.setAnswered(Integer.parseInt(answered));
                        
                        rl.add(er);
                    }
                    
                    br.close();
                } catch (IOException ioe) {
                    System.err.println("I/O error " + ioe.getMessage());
                }
            }
            
            // print the results
            
            Set<String> rn = results.keySet();
            
            for (String s : rn) {
                ArrayList<ExerciseResult> erl = results.get(s);
                ExerciseResult[] era = erl.toArray(new ExerciseResult[] {});
                Arrays.sort(era, new ExerciseResultComparator());
                
                // print out the information
                System.out.print(s);
                int correct = 0;
                int total = 0;
                for( ExerciseResult er : era ) {
                    System.out.print(er.toString());
                    correct+=er.getCorrect();
                    total+=er.getTotal();                    
                }
                double total_grade = (double)era.length*0.5;
                double grade = ((double)correct / (double)total)*total_grade;
                double final_grade = 2.5F;
                System.out.printf("Grade: %.2f / %.2f\n", grade, final_grade);
            }

            return;
        }
        // create submission list
        if (args.length > 0 && args[0].compareTo("submit") == 0) {
            try {
                File outf = new File("submit.txt");
                DataOutputStream dos = new DataOutputStream(new FileOutputStream(outf));                
                File f = new File(args[1]);
                if (!f.exists()) {
                    System.out.println(args[1] + " ... does not exist!");
                    return;
                }
                if (!f.isDirectory()) {
                    System.out.println(args[1] + " ... is not a directory!!");
                    return;
                }
                File[] ff = f.listFiles();
                for (File xml : ff) {
                    if (!xml.getName().endsWith(".xml")) { continue;}
                    System.out.println("Processing ... " + xml.getName());
                    Document d = XMLUtil.parseDocument(new FileInputStream(xml));
                    NodeList nl = d.getElementsByTagName("results");
                    Node n = nl.item(0);
                    NamedNodeMap nnm = n.getAttributes();
                    n = nnm.getNamedItem("student");
                    String registry = n.getNodeValue();
                    n = nnm.getNamedItem("received");
                    String date = StringUtil.parseDateString(n.getNodeValue(), "yyyyMMdd hhmmss").toString();
                    dos.writeBytes("AM" + registry + " -> " + date + "\n");
                }
                
                dos.close();

                return;
            } catch (IOException ioe) {
                System.out.println("Cannot open (" + ioe.toString() + ")");
            } catch (Exception e) {
                System.out.println("Generic error " + e.toString());
            }
        }
        // print help
        System.out.println("Bad arguments ... expected \"results\" or \"submit\"!\n");
        System.out.println("results <outputs-from-answer-checker>");
        System.out.println("submit <directory-with-xml>");
    }
}
