package org.exercises.students;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;

import org.exercises.log.Logger;

public class StudentList {
    private final static StudentList defaultInstance;
    //
    HashMap<String, Student> students;
    
    static {
        defaultInstance = new StudentList();
    }
    
    private StudentList() {
        Logger logger = Logger.getInstance();
        students = new HashMap<String, Student>();
        // read the input file
        try {
            InputStream is = ClassLoader.getSystemResourceAsStream("data/users.list");
            if(is == null) {
                logger.log("Cannot find students list");
                return;
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(is,"UTF8"));
            while(br.ready()) {
                String line = br.readLine();
                String[] a = line.split(",");
                students.put(a[1], new Student(a[1],a[0],a[2]));
            }
        } catch (IOException ioe) {
            logger.log("I/O error - " + ioe.toString());
        }
    }
    
    public Student get(String login) {
        if(students.containsKey(login)) {
            return students.get(login);
        }
        
        return Student.defaultStudent;
    }
    
    public Student getStudentByRegistry(String registry) {
        Collection<Student> cs = students.values();
        
        for (Student s : cs) {
            if(s.getRegistry().compareTo(registry) == 0) { return s; }
        }
        
        return null;
    }
    
    // static
    public static final StudentList getInstance() {
        return defaultInstance;
    }
}
