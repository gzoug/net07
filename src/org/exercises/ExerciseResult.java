package org.exercises;

public class ExerciseResult {
    String student;
    int exerciseId;
    int correct, answered, total;
    
    public ExerciseResult(String student,
                          int exerciseId) {
        this.student = student;
        this.exerciseId = exerciseId;
    }

    public int getAnswered() {
        return answered;
    }

    public void setAnswered(int answered) {
        this.answered = answered;
    }

    public int getCorrect() {
        return correct;
    }

    public void setCorrect(int correct) {
        this.correct = correct;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
    
    @Override
    public String toString() {
        return (" Set " + exerciseId + " : " + correct + "/" + total + ", ");
    }
}