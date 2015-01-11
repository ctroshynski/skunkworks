package com.brandNameSoftware.bikeTrainer;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.brandNameSoftware.bikeTrainer.adapters.WorkoutAdapter;
import com.brandNameSoftware.bikeTrainer.beans.UserPrefs;
import com.brandNameSoftware.bikeTrainer.utils.DisplayHelper;
import com.brandNameSoftware.workoutGenerator.WorkoutGenerator;
import com.brandNameSoftware.workoutGenerator.datacontainer.WorkoutConstraints;
import com.brandNameSoftware.workoutGenerator.datacontainer.WorkoutPrefs;
import com.brandNameSoftware.workoutGenerator.datacontainer.WorkoutSet;
import com.brandNameSoftware.workoutGenerator.utils.WorkoutMaths;

public class DisplayWorkoutActivity extends ActionBarActivity
{
	WorkoutAdapter workoutAdapter;
	ArrayList<WorkoutSet> workoutSets = null;
	HashMap<Integer, WorkoutConstraints> workoutConstraints = null;
	UserPrefs userPrefs = null;
	CountDownTimer totalWorkoutTimer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_workout);
	    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		readUserPrefs();
		
		Intent intent = getIntent();
		WorkoutPrefs workoutPrefs = (WorkoutPrefs) intent.getSerializableExtra(MainActivity.WORKOUT_PREFERENCES);
		this.workoutConstraints = (HashMap<Integer, WorkoutConstraints> ) intent.getSerializableExtra(MainActivity.WORKOUT_CONSTRAINTS);
		
		this.workoutSets = generateWorkout(workoutPrefs, workoutConstraints);
		
		TextView totalCounTextView = (TextView) findViewById(R.id.txtViewTotalCountdown);
		totalCounTextView.setText(Integer.toString(workoutPrefs.getTime()));

		
		

		
		RecyclerView listViewWorkoutSets = (RecyclerView) findViewById(R.id.listViewWorkoutSets);
		
		listViewWorkoutSets.setLayoutManager(new LinearLayoutManager(this));
		listViewWorkoutSets.setItemAnimator(new DefaultItemAnimator());
		
		workoutAdapter = new WorkoutAdapter(this, this.workoutSets, this.workoutConstraints, userPrefs);
		listViewWorkoutSets.setAdapter(workoutAdapter);
		
		
		
		this.totalWorkoutTimer = setupTimer(workoutSets);
		this.totalWorkoutTimer.start();
	}
	
	@Override
	protected void onResume()
	{		
		readUserPrefs();
		
		super.onResume();
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		
		this.totalWorkoutTimer.cancel();
	}
	
	private void readUserPrefs()
	{
		if(userPrefs == null)
		{
			userPrefs = new UserPrefs();
		}
		
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);

		userPrefs.setFTP(Integer.parseInt(settings.getString(AppPreferencesActivity.PREFERENCE_FTP_KEY, "0")));
		userPrefs.setMaxHR(Integer.parseInt(settings.getString(AppPreferencesActivity.PREFERENCE_MAXHR_KEY, "0")));
		userPrefs.setDisplayAsAbsolute(settings.getBoolean(AppPreferencesActivity.PREFERENCE_DISPLAY_TYPE_KEY, false));
		userPrefs.setWarmupTime(Integer.parseInt(settings.getString(AppPreferencesActivity.PREFERENCE_WARMUP_TIME_KEY, "0")));
		userPrefs.setCoolDownTime(Integer.parseInt(settings.getString(AppPreferencesActivity.PREFERENCE_COOLDOWN_TIME_KEY, "0")));
	}

	private CountDownTimer setupTimer(ArrayList<WorkoutSet> workoutSets)
	{
		int totalWorkoutTime = 0;
		
		for (WorkoutSet currentSet : workoutSets)
		{
			totalWorkoutTime += currentSet.getTotalSetTime();
		}
		
		WorkoutCountDownTimer totalWorkoutTimer = new WorkoutCountDownTimer(totalWorkoutTime * 1000, 1000, workoutSets);
		
		return totalWorkoutTimer;
	}
	
	private ArrayList<WorkoutSet> generateWorkout(WorkoutPrefs workoutPrefs, HashMap<Integer, WorkoutConstraints> workoutConstraints)
	{
		//workout algorithm only generates the main sets, so need to adjust for that.
		workoutPrefs.setTime(workoutPrefs.getTime() - (this.userPrefs.getWarmupTime() * 60) - (this.userPrefs.getCoolDownTime() * 60));
		WorkoutGenerator generator = new WorkoutGenerator(workoutConstraints, workoutPrefs);
		ArrayList<WorkoutSet> workoutSets = generator.generateMainSets();
		
		//add a dummy warmup set to the beginning
		WorkoutSet warmupSet = new WorkoutSet();
		warmupSet.setNumberOfReps(1);
		warmupSet.setRestTimePerRep(0);
		warmupSet.setTargetZone(0);
		warmupSet.setTimePerRep(this.userPrefs.getWarmupTime() * 60);
		//warmupSet.setTimePerRep(10);
		workoutSets.add(0, warmupSet);
		
		//add a dummy cooldown set to the beginning
		WorkoutSet cooldownSet = new WorkoutSet();
		cooldownSet.setNumberOfReps(1);
		cooldownSet.setRestTimePerRep(0);
		cooldownSet.setTargetZone(0);
		cooldownSet.setTimePerRep(this.userPrefs.getCoolDownTime() * 60);
		workoutSets.add(cooldownSet);
		
		int totalWorkoutTime = 0;
		for (WorkoutSet mainSet : workoutSets) 
		{
			System.out.println(mainSet);
			totalWorkoutTime += mainSet.getTotalSetTime();
		}
		
		System.out.println("total workout time " + totalWorkoutTime);
		return workoutSets;
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	Intent intent = new Intent();
    	intent.setClass(DisplayWorkoutActivity.this, AppPreferencesActivity.class);
        startActivityForResult(intent, 0); 
  
        return true;
    }
	
	public class WorkoutCountDownTimer extends CountDownTimer
	{
		ArrayList<WorkoutSet> mainSets = new ArrayList<WorkoutSet>();
		WorkoutSet currentSet = null;
		private Long timeLeftExcludingCurrentRep;
		private int currentSetIndex = 0;
		private boolean isWorkingSet = true;
		private int currentRepNum = 1;
		private Long currentRepTimeMillis = 0L;
		
		public WorkoutCountDownTimer(long millisInFuture, long countDownInterval, ArrayList<WorkoutSet> mainSets) {
			super(millisInFuture, countDownInterval);
			this.mainSets = mainSets;
			this.timeLeftExcludingCurrentRep = millisInFuture;
			currentSet = mainSets.get(0);
			currentRepTimeMillis = Long.valueOf(currentSet.getTimePerRep()) * 1000;
			TextView totalCounTextView = (TextView) findViewById(R.id.txtViewTotalCountdown);
			totalCounTextView.setText(WorkoutMaths.formatMillisAsTime(millisInFuture));
			TextView repCountTextView = (TextView) findViewById(R.id.txtViewRepCountdown);
			repCountTextView.setText(WorkoutMaths.formatMillisAsTime(currentSet.getTimePerRep()));
		}

		@Override
		public void onTick(long millisUntilFinished) {
			TextView totalCounTextView = (TextView) findViewById(R.id.txtViewTotalCountdown);
			totalCounTextView.setText(WorkoutMaths.formatMillisAsTime(millisUntilFinished));

			TextView repCountTextView = (TextView) findViewById(R.id.txtViewRepCountdown);
			Long remainingRepTimeMillis = currentRepTimeMillis - (timeLeftExcludingCurrentRep - Long.valueOf(millisUntilFinished));
			
			if(remainingRepTimeMillis <= 0)
			{
				boolean isSetIncremented = false;
				if(isWorkingSet)
				{
					//switch to the resting portion
					this.timeLeftExcludingCurrentRep -= this.currentSet.getTimePerRep() * 1000;
					remainingRepTimeMillis = Long.valueOf(currentSet.getRestTimePerRep()) * 1000;
					currentRepTimeMillis = Long.valueOf(currentSet.getRestTimePerRep()) * 1000;
					
					isWorkingSet = false;
				}
				else
				{
					this.timeLeftExcludingCurrentRep -= this.currentSet.getRestTimePerRep() * 1000;
					isWorkingSet = true;

					currentRepNum++;
					if(currentRepNum <= currentSet.getNumberOfReps())
					{
						//still working through this set's reps
						remainingRepTimeMillis = Long.valueOf(currentSet.getTimePerRep()) * 1000;
						currentRepTimeMillis = Long.valueOf(currentSet.getTimePerRep()) * 1000;
					}
					else
					{
						//switch to the next set because we're done with the reps
						currentSetIndex++;
						currentRepNum = 1;
						//just a safety precaution. This should never happen
						if(mainSets.size() > currentSetIndex)
						{
							currentSet = mainSets.get(currentSetIndex);
							remainingRepTimeMillis = Long.valueOf(currentSet.getTimePerRep()) * 1000;							
							currentRepTimeMillis = Long.valueOf(currentSet.getTimePerRep()) * 1000;
							isSetIncremented = true;
						}
					}
				}

				workoutAdapter.setWorkingRep(isWorkingSet);
				workoutAdapter.setActiveIndex(currentSetIndex);
				DisplayHelper.setActiveBackgroundColor((RecyclerView) findViewById(R.id.listViewWorkoutSets), currentSetIndex, isWorkingSet, isSetIncremented);
			}
			
			repCountTextView.setText(WorkoutMaths.formatMillisAsTime(remainingRepTimeMillis));
		}
		
		@Override
		public void onFinish() {
			TextView totalCounTextView = (TextView) findViewById(R.id.txtViewTotalCountdown);
			totalCounTextView.setText("Done!");
		    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		}

		public ArrayList<WorkoutSet> getMainSets() {
			return mainSets;
		}

		public void setMainSet(ArrayList<WorkoutSet> mainSets) {
			this.mainSets = mainSets;
		}
	}
}
