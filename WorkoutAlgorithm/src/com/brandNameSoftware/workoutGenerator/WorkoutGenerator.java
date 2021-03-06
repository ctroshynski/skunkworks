package com.brandNameSoftware.workoutGenerator;

import java.util.ArrayList;
import java.util.HashMap;

import com.brandNameSoftware.workoutGenerator.datacontainer.WorkoutConstraints;
import com.brandNameSoftware.workoutGenerator.datacontainer.WorkoutPrefs;
import com.brandNameSoftware.workoutGenerator.datacontainer.WorkoutSet;
import com.brandNameSoftware.workoutGenerator.utils.WorkoutMaths;

public class WorkoutGenerator
{
	//inputs
	private HashMap<Integer, WorkoutConstraints> workoutConstraints;
	private WorkoutPrefs workoutPrefs;
	
	//generated data
	private static double bonusRoundPercentage = .15;
	private int secondsinTargetZone;
	private int secondsInBonusZone;
	private HashMap<Integer, Integer> totalTimePerZone = new HashMap<>();


	public WorkoutGenerator(HashMap<Integer, WorkoutConstraints> workoutConstraints, WorkoutPrefs workoutPrefs)
	{
		this.workoutConstraints = workoutConstraints;
		this.workoutPrefs = workoutPrefs;
	}

	public ArrayList<WorkoutSet> generateMainSets()
	{
		ArrayList<WorkoutSet> mainSets = new ArrayList<WorkoutSet>();

		this.secondsinTargetZone = (int) Math.round(this.workoutPrefs.getTime() * (1-bonusRoundPercentage));

		int numberofSets = WorkoutMaths.randInt(1, 10);
		
		//Generate the primary target set
		mainSets.addAll(generateWorkoutSets(numberofSets, this.secondsinTargetZone, this.workoutPrefs.getZone()));

		this.secondsInBonusZone = this.workoutPrefs.getTime() - this.secondsinTargetZone;
		int remainingSecondsInBonusZone = this.secondsInBonusZone + (this.secondsinTargetZone - this.totalTimePerZone.get(this.workoutPrefs.getZone()));
		
		//Loop through the various set types trying to fill in the workout. Not exactly efficient but it only needs to hit 5 numbers randomly
		while(totalTimePerZone.size() < 6 && remainingSecondsInBonusZone > 0)
		{
			//TODO: should allow the workout to have zone 1 if the user chooses so
			int randomZone = WorkoutMaths.randInt(2, 7);
			numberofSets = WorkoutMaths.randInt(1, 10);

			//we won't randomly generate a workout that already exists
			if(totalTimePerZone.get(randomZone) == null)
			{
				ArrayList<WorkoutSet> bonusSet = generateWorkoutSets(numberofSets, remainingSecondsInBonusZone, randomZone);
				mainSets.addAll(bonusSet);
				remainingSecondsInBonusZone -= totalTimePerZone.get(randomZone);
			}
			//TODO: I think I should allow the primary zone to happen multiple times.
			//add more time to the main set time. Goal is to reduce zone 7 sets
			/*else if(randomZone == this.workoutPrefs.getZone())
			{
				//Calculate the time allowed to add
				totalTimePerZone.get(randomZone)
			}*/
		}
		return mainSets;
	}
	
	private ArrayList<WorkoutSet> generateWorkoutSets(int numberOfSets, int maximumSecondsPerSet, int targetWorkZone)
	{
		ArrayList<WorkoutSet> targetZoneSets = new ArrayList<WorkoutSet>();
		Integer netTimeInZone = totalTimePerZone.get(targetWorkZone);
		
		if(netTimeInZone == null)
		{
			netTimeInZone = new Integer(0);
		}
		
		int remainingTimeInWorkout = maximumSecondsPerSet;
		WorkoutConstraints targetConstraints = this.workoutConstraints.get(targetWorkZone);
		
		while(numberOfSets > 0)
		{
			int numberOfReps = WorkoutMaths.randInt(targetConstraints.getMinReps(), targetConstraints.getMaxReps());
			//int upperBoundsOnWorkout = (targetConstraints.getMaxRepTime() * numberOfReps > remainingTimeInWorkout)? remainingTimeInWorkout : targetConstraints.getMaxRepTime();
			int upperBoundsOnWorkout = targetConstraints.getMaxRepTime();
			WorkoutSet currentWorkoutSet;
			if(numberOfSets > 1)
			{
				currentWorkoutSet = generateSingleZoneSet(numberOfReps, targetWorkZone, upperBoundsOnWorkout);
			}
			else
			{
				currentWorkoutSet = generateSingleZoneSet(targetWorkZone, upperBoundsOnWorkout);
			}
			
			while(currentWorkoutSet.getTotalSetTime() > maximumSecondsPerSet)
			{
				currentWorkoutSet.setNumberOfReps(currentWorkoutSet.getNumberOfReps() - 1);
				if(currentWorkoutSet.getNumberOfReps() <=0 )
					break;
			}
			remainingTimeInWorkout -= currentWorkoutSet.getTotalSetTime();
			//whoops, last set went over on time
			if(currentWorkoutSet.getNumberOfReps() <= 0)
			{
				break;
			}
			else if(remainingTimeInWorkout <= targetConstraints.getMinRepTime())
			{
				netTimeInZone += currentWorkoutSet.getTotalSetTime();
				targetZoneSets.add(currentWorkoutSet);
				numberOfSets = 0;
			}
			else if(targetConstraints.getMaxSetTimePerWorkout() > 0 && (netTimeInZone + currentWorkoutSet.getTotalSetTime()) >= targetConstraints.getMaxSetTimePerWorkout())
			{
				while((netTimeInZone + currentWorkoutSet.getTotalSetTime()) >= targetConstraints.getMaxSetTimePerWorkout())
				{
					currentWorkoutSet.setNumberOfReps(currentWorkoutSet.getNumberOfReps() - 1);
					if(currentWorkoutSet.getNumberOfReps() <=0 )
					{
						break;
					}
				}
				netTimeInZone += currentWorkoutSet.getTotalSetTime();
				targetZoneSets.add(currentWorkoutSet);
				numberOfSets = 0;
			}
			else
			{
				netTimeInZone += currentWorkoutSet.getTotalSetTime();
				maximumSecondsPerSet -= currentWorkoutSet.getTotalSetTime();
				targetZoneSets.add(currentWorkoutSet);
				numberOfSets--;
			}
		}

		totalTimePerZone.put(targetWorkZone, netTimeInZone);
		return targetZoneSets;
	}
	
	private ArrayList<WorkoutSet> generateWorkoutSets(int secondsForSet, int targetZone)
	{
		ArrayList<WorkoutSet> targetZoneSets = new ArrayList<WorkoutSet>();
		Integer netTimeInZone = totalTimePerZone.get(targetZone);
		
		if(netTimeInZone == null)
		{
			netTimeInZone = new Integer(0);
		}
		
		int remainingTimeInWorkout = secondsForSet;
		
		WorkoutConstraints targetConstraints = this.workoutConstraints.get(targetZone);
		boolean hitMaximumTimePerWorkout = false;
		while(!hitMaximumTimePerWorkout && remainingTimeInWorkout >= (targetConstraints.getMinRepTime() + targetConstraints.getRestRatio()*targetConstraints.getMinRepTime()))
		{
			int numberOfReps;
			
			numberOfReps = WorkoutMaths.randInt(targetConstraints.getMinReps(), targetConstraints.getMaxReps());
			
			int upperBoundsOnWorkout = (targetConstraints.getMaxRepTime() * numberOfReps > remainingTimeInWorkout)? remainingTimeInWorkout : targetConstraints.getMaxRepTime();
			WorkoutSet currentWorkoutSet = generateSingleZoneSet(numberOfReps, targetZone, upperBoundsOnWorkout);
			
			//last set might have gone over. Adjust constraints so that we guaranteee it will fall under. This is a shitty hack, there has to be a better way
			if(currentWorkoutSet.getTotalSetTime() > remainingTimeInWorkout)
			{
				int newUpperBoundsOnWorkout = upperBoundsOnWorkout/numberOfReps;
				//reduce the reps until our upper bound is higher than our lower bound
				while(newUpperBoundsOnWorkout < (targetConstraints.getMinRepTime() + targetConstraints.getRestRatio()*targetConstraints.getMinRepTime()))
				{
					numberOfReps--;
					newUpperBoundsOnWorkout = upperBoundsOnWorkout/numberOfReps;
				}
				currentWorkoutSet = generateSingleZoneSet(numberOfReps, targetZone, newUpperBoundsOnWorkout);
			}

			//only allow workout to be added if it didn't break stuff. Negative values are N/A
			if(targetConstraints.getMaxSetTimePerWorkout() < 0 || ((currentWorkoutSet.getTotalSetTime() + netTimeInZone) < targetConstraints.getMaxSetTimePerWorkout()))
			{
				netTimeInZone += currentWorkoutSet.getTotalSetTime();
				targetZoneSets.add(currentWorkoutSet);
				remainingTimeInWorkout -= currentWorkoutSet.getTotalSetTime();
				if(targetConstraints.getMaxSetTimePerWorkout() > 0)
				{
					hitMaximumTimePerWorkout = true;
				}
			}
		}
		
		totalTimePerZone.put(targetZone, netTimeInZone);
		return targetZoneSets;
	}
	
	private WorkoutSet generateSingleZoneSet(int targetZoneNumber, int maxTimePerSet)
	{
		WorkoutSet currentWorkoutSet = new WorkoutSet();
		int timePerRep, restTimePerRep;
		
		WorkoutConstraints targetConstraints = this.workoutConstraints.get(targetZoneNumber);
		
		timePerRep = WorkoutMaths.randInt(targetConstraints.getMinRepTime(), maxTimePerSet);
		restTimePerRep = (int) Math.round(timePerRep * targetConstraints.getRestRatio());
		
		int numberOfReps = 0;
		
		while((numberOfReps * (timePerRep + restTimePerRep)) < maxTimePerSet)
		{
			numberOfReps++;
		}
		
		currentWorkoutSet.setTargetZone(targetZoneNumber);
		currentWorkoutSet.setNumberOfReps(numberOfReps);
		currentWorkoutSet.setRestTimePerRep(restTimePerRep);
		currentWorkoutSet.setTimePerRep(timePerRep);
		
		return currentWorkoutSet;
	}
	
	private WorkoutSet generateSingleZoneSet(int numberOfReps, int targetZoneNumber, int maxTimePerSet)
	{
		WorkoutSet currentWorkoutSet = new WorkoutSet();
		int timePerRep, restTimePerRep;
		
		WorkoutConstraints targetConstraints = this.workoutConstraints.get(targetZoneNumber);
		
		timePerRep = WorkoutMaths.randInt(targetConstraints.getMinRepTime(), maxTimePerSet);
		restTimePerRep = (int) Math.round(timePerRep * targetConstraints.getRestRatio());
		
		currentWorkoutSet.setTargetZone(targetZoneNumber);
		currentWorkoutSet.setNumberOfReps(numberOfReps);
		currentWorkoutSet.setRestTimePerRep(restTimePerRep);
		currentWorkoutSet.setTimePerRep(timePerRep);
		
		return currentWorkoutSet;
	}
	
	public HashMap<Integer, WorkoutConstraints> getWorkoutConstraints() {
		return workoutConstraints;
	}

	public void setWorkoutConstraints(HashMap<Integer, WorkoutConstraints> workoutConstraints) {
		this.workoutConstraints = workoutConstraints;
	}
	
	public WorkoutPrefs getWorkoutPrefs() {
		return workoutPrefs;
	}

	public void setWorkoutPrefs(WorkoutPrefs workoutPrefs) {
		this.workoutPrefs = workoutPrefs;
	}

	public static double getBonusRoundPercentage() {
		return bonusRoundPercentage;
	}

	public static void setBonusRoundPercentage(double bonusRoundPercentage) {
		WorkoutGenerator.bonusRoundPercentage = bonusRoundPercentage;
	}

	public int getMinutesInTargetZone() {
		return secondsinTargetZone;
	}

	public void setMinutesInTargetZone(int minutesInTargetZone) {
		this.secondsinTargetZone = minutesInTargetZone;
	}
}
