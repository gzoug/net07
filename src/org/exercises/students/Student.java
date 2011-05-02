package org.exercises.students;

/**
 * 
 * @author Vassilios Karakoidas (bkarak@aueb.gr)
 * 
 */
public class Student {
    final static Student defaultStudent;
    //
    String login;
    String registry;
    String name;
    
    static {
        defaultStudent = new Student("unknown","unknown","unknown");
    }
    
    Student(String login,
            String registry,
            String name) {
        this.login = login;
        this.registry = registry;
        this.name = name;
    }
    
    public String getLogin() {
        return login;
    }
    
    public String getName() {
        return name;
    }
    
    public String getRegistry() {
        return registry;
    }
    
    @Override
    public String toString() {
        return (registry + " - " + name);
    }
}
