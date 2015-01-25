package com.brandNameSoftware.bikeTrainer.utils;

import java.util.ArrayList;
import java.util.HashMap;

import com.brandNameSoftware.workoutGenerator.WorkoutGenerator;
import com.brandNameSoftware.workoutGenerator.datacontainer.WorkoutConstraints;
import com.brandNameSoftware.workoutGenerator.datacontainer.WorkoutPrefs;
import com.brandNameSoftware.workoutGenerator.datacontainer.WorkoutSet;

public class WorkoutHelper {
	public static ArrayList<WorkoutSet> generateWorkout(WorkoutPrefs workoutPrefs, HashMap<Integer, WorkoutConstraints> workoutConstraints, int wamupTimeSecs, int coolDownTimeSecs)
	{
		//workout algorithm only generates the main sets, so need to adjust for that.
		workoutPrefs.setTime(workoutPrefs.getTime() - wamupTimeSecs - coolDownTimeSecs);
		WorkoutGenerator generator = new WorkoutGenerator(workoutConstraints, workoutPrefs);
		ArrayList<WorkoutSet> workoutSets = generator.generateMainSets();
		
		//add a dummy warmup set to the beginning
		WorkoutSet warmupSet = new WorkoutSet();
		warmupSet.setNumberOfReps(1);
		warmupSet.setRestTimePerRep(0);
		warmupSet.setTargetZone(0);
		warmupSet.setTimePerRep(wamupTimeSecs);
		//warmupSet.setTimePerRep(10);
		workoutSets.add(0, warmupSet);
		
		//add a dummy cooldown set to the beginning
		WorkoutSet cooldownSet = new WorkoutSet();
		cooldownSet.setNumberOfReps(1);
		cooldownSet.setRestTimePerRep(0);
		cooldownSet.setTargetZone(0);
		cooldownSet.setTimePerRep(coolDownTimeSecs);
		workoutSets.add(cooldownSet);
		
		return workoutSets;
	}
	
	public static int calculateTotalWorkoutTimeSecs(ArrayList<WorkoutSet> workoutSets)
	{
		int totalWorkoutTime = 0;
		
		for (WorkoutSet currentSet : workoutSets)
		{
			totalWorkoutTime += currentSet.getTotalSetTime();
		}
		
		return totalWorkoutTime;
	}
}
