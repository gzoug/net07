package org.exercises;

import java.util.Comparator;

public class ExerciseResultComparator implements Comparator<ExerciseResult> {

    public int compare(ExerciseResult er_one, ExerciseResult er_two) {      
        return (er_one.exerciseId - er_two.exerciseId);
    }
}
