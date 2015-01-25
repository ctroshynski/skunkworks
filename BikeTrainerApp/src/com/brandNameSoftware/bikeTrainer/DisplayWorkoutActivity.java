package com.brandNameSoftware.bikeTrainer;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.brandNameSoftware.bikeTrainer.adapters.WorkoutAdapter;
import com.brandNameSoftware.bikeTrainer.beans.UserPrefs;
import com.brandNameSoftware.bikeTrainer.utils.DisplayHelper;
import com.brandNameSoftware.bikeTrainer.utils.WorkoutHelper;
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
	WorkoutCountDownTimer totalWorkoutTimer;
	ImageButton playPauseButton;
	boolean isWorkoutPlaying = true;
	long remainingTimeInWorkoutMillis = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_workout);
	    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		readUserPrefs();
		
		Intent intent = getIntent();
		WorkoutPrefs workoutPrefs = (WorkoutPrefs) intent.getSerializableExtra(MainActivity.WORKOUT_PREFERENCES);
		this.workoutConstraints = (HashMap<Integer, WorkoutConstraints> ) intent.getSerializableExtra(MainActivity.WORKOUT_CONSTRAINTS);
		
		this.workoutSets = WorkoutHelper.generateWorkout(workoutPrefs, workoutConstraints, (this.userPrefs.getWarmupTime() * 60), (this.userPrefs.getCoolDownTime() * 60));
		
		TextView totalCounTextView = (TextView) findViewById(R.id.txtViewTotalCountdown);
		totalCounTextView.setText(Integer.toString(workoutPrefs.getTime()));
		
		addListenerOnPlayPauseButton();
		
		setupAndStartTimer(WorkoutHelper.calculateTotalWorkoutTimeSecs(workoutSets)*1000, true);
	}
	
	@Override
	protected void onResume()
	{		
		readUserPrefs();
		RecyclerView listViewWorkoutSets = (RecyclerView) findViewById(R.id.listViewWorkoutSets);
		
		listViewWorkoutSets.setLayoutManager(new LinearLayoutManager(this));
		listViewWorkoutSets.setItemAnimator(new DefaultItemAnimator());
		
		workoutAdapter = new WorkoutAdapter(this, this.workoutSets, this.workoutConstraints, userPrefs);
		listViewWorkoutSets.setAdapter(workoutAdapter);
		
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

		userPrefs.setFTP(Integer.parseInt(settings.getString(AppPreferencesActivity.PREFERENCE_FTP_KEY, "200")));
		userPrefs.setMaxHR(Integer.parseInt(settings.getString(AppPreferencesActivity.PREFERENCE_MAXHR_KEY, "190")));
		userPrefs.setDisplayAsAbsolute(settings.getBoolean(AppPreferencesActivity.PREFERENCE_DISPLAY_TYPE_KEY, false));
		userPrefs.setWarmupTime(Integer.parseInt(settings.getString(AppPreferencesActivity.PREFERENCE_WARMUP_TIME_KEY, "10")));
		userPrefs.setCoolDownTime(Integer.parseInt(settings.getString(AppPreferencesActivity.PREFERENCE_COOLDOWN_TIME_KEY, "5")));
	}

	private void setupAndStartTimer(long workoutTimeMillis, boolean isStartingNew)
	{
		if(isStartingNew)
		{
			this.totalWorkoutTimer = new WorkoutCountDownTimer(workoutTimeMillis, 1000, workoutSets, null);
		}
		else
		{
			this.totalWorkoutTimer = new WorkoutCountDownTimer(workoutTimeMillis, 1000, workoutSets, this.totalWorkoutTimer);
		}
		remainingTimeInWorkoutMillis = workoutTimeMillis;
		this.totalWorkoutTimer.start();
	}
	
	public void addListenerOnPlayPauseButton()
	{
		playPauseButton = (ImageButton) findViewById(R.id.imgBtnPlayPause);
 
		playPauseButton.setOnClickListener(new OnClickListener() {
 
			@Override
			public void onClick(View arg0)
			{
				Drawable buttonDrawable;
				if(isWorkoutPlaying)
				{
					buttonDrawable = getResources().getDrawable(R.drawable.play);
					totalWorkoutTimer.cancel();
				}
				else
				{
					buttonDrawable = getResources().getDrawable(R.drawable.pause);
					setupAndStartTimer(remainingTimeInWorkoutMillis, false);
				}
				playPauseButton.setImageDrawable(buttonDrawable); 
				isWorkoutPlaying = !isWorkoutPlaying;
			}
 
		});
 
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
		
		public WorkoutCountDownTimer(long millisInFuture, long countDownInterval, ArrayList<WorkoutSet> mainSets, WorkoutCountDownTimer cloneTimer)
		{
			super(millisInFuture, countDownInterval);
			if(cloneTimer == null)
			{
				this.mainSets = mainSets;
				this.timeLeftExcludingCurrentRep = millisInFuture;
			
				this.currentSet = mainSets.get(0);
				this.currentRepTimeMillis = Long.valueOf(currentSet.getTimePerRep()) * 1000;
				TextView totalCounTextView = (TextView) findViewById(R.id.txtViewTotalCountdown);
				totalCounTextView.setText(WorkoutMaths.formatMillisAsTime(millisInFuture));
				TextView repCountTextView = (TextView) findViewById(R.id.txtViewRepCountdown);
				repCountTextView.setText(WorkoutMaths.formatMillisAsTime(currentSet.getTimePerRep()));
			}
			else
			{
				this.setMainSet(cloneTimer.getMainSets());
				this.setCurrentSet(cloneTimer.getCurrentSet());
				this.setTimeLeftExcludingCurrentRep(cloneTimer.getTimeLeftExcludingCurrentRep());
				this.setCurrentSetIndex(cloneTimer.getCurrentSetIndex());
				this.setWorkingSet(cloneTimer.isWorkingSet());
				this.setCurrentRepNum(cloneTimer.getCurrentRepNum());
				this.setCurrentRepTimeMillis(cloneTimer.getCurrentRepTimeMillis());
			}
		}

		@Override
		public void onTick(long millisUntilFinished) {
			remainingTimeInWorkoutMillis = millisUntilFinished;
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
			TextView repCountTextView = (TextView) findViewById(R.id.txtViewRepCountdown);
			totalCounTextView.setText("Done!");
			repCountTextView.setText("Done!");
		    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		}

		public ArrayList<WorkoutSet> getMainSets() {
			return mainSets;
		}

		public void setMainSet(ArrayList<WorkoutSet> mainSets) {
			this.mainSets = mainSets;
		}

		public WorkoutSet getCurrentSet() {
			return currentSet;
		}

		public void setCurrentSet(WorkoutSet currentSet) {
			this.currentSet = currentSet;
		}

		public Long getTimeLeftExcludingCurrentRep() {
			return timeLeftExcludingCurrentRep;
		}

		public void setTimeLeftExcludingCurrentRep(Long timeLeftExcludingCurrentRep) {
			this.timeLeftExcludingCurrentRep = timeLeftExcludingCurrentRep;
		}

		public int getCurrentSetIndex() {
			return currentSetIndex;
		}

		public void setCurrentSetIndex(int currentSetIndex) {
			this.currentSetIndex = currentSetIndex;
		}

		public boolean isWorkingSet() {
			return isWorkingSet;
		}

		public void setWorkingSet(boolean isWorkingSet) {
			this.isWorkingSet = isWorkingSet;
		}

		public int getCurrentRepNum() {
			return currentRepNum;
		}

		public void setCurrentRepNum(int currentRepNum) {
			this.currentRepNum = currentRepNum;
		}

		public Long getCurrentRepTimeMillis() {
			return currentRepTimeMillis;
		}

		public void setCurrentRepTimeMillis(Long currentRepTimeMillis) {
			this.currentRepTimeMillis = currentRepTimeMillis;
		}

		public void setMainSets(ArrayList<WorkoutSet> mainSets) {
			this.mainSets = mainSets;
		}
	}
}
