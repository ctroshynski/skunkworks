package com.hudl.workout;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

import com.hudl.workout.datacontainer.WorkoutConstraints;
import com.hudl.workout.datacontainer.WorkoutPrefs;
import com.hudl.workout.datacontainer.WorkoutSet;
import com.hudl.workout.utils.WorkoutMaths;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		HashMap<Integer, WorkoutConstraints> constraints = new HashMap<Integer, WorkoutConstraints>();
		try {
			constraints = ImportPrefs.ReadWorkoutconstraints(new FileInputStream("Config/WorkoutConfig"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		WorkoutPrefs prefs = ImportPrefs.ReadWorkoutPrefs();
		
		WorkoutGenerator generator = new WorkoutGenerator(constraints, prefs);
		ArrayList<WorkoutSet> mainSets = generator.generateMainSets();
		
		int totalWorkoutTime = 0;
		for (WorkoutSet mainSet : mainSets) 
		{
			System.out.println(mainSet.toString());
			totalWorkoutTime += mainSet.getTotalSetTime();
		}
		System.out.println("Total workout time is: " + totalWorkoutTime/60 + " minutes");
	}

}
