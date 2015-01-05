package com.brandNameSoftware.bikeTrainer.adapters;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.brandNameSoftware.bikeTrainer.R;
import com.brandNameSoftware.bikeTrainer.beans.UserPrefs;
import com.brandNameSoftware.bikeTrainer.utils.DisplayHelper;
import com.brandNameSoftware.workoutGenerator.datacontainer.WorkoutConstraints;
import com.brandNameSoftware.workoutGenerator.datacontainer.WorkoutSet;
import com.brandNameSoftware.workoutGenerator.utils.WorkoutMaths;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutViewHolder> {

	private ArrayList<WorkoutSet> workoutSets = new ArrayList<WorkoutSet>();
	private Context context;
	private HashMap<Integer, WorkoutConstraints> workoutConstraints;
	private int activeIndex = 0;
	private boolean isWorkingRep = true;
	private UserPrefs userPrefs;
	
	public WorkoutAdapter(Context context, ArrayList<WorkoutSet> workoutSets, HashMap<Integer, WorkoutConstraints> workoutConstraints, UserPrefs userPrefs) {
		this.context = context;
		this.workoutSets = workoutSets;
		this.workoutConstraints = workoutConstraints;
		this.userPrefs = userPrefs;
	}

	@Override
	public int getItemCount() {
		return this.workoutSets.size();
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void onBindViewHolder(WorkoutViewHolder viewHolder, int position) {
		viewHolder.txtViewZone.setText(Integer.toString(workoutSets.get(position).getTargetZone()));
		viewHolder.txtViewRepTime.setText(WorkoutMaths.formatMillisAsTime(workoutSets.get(position).getTimePerRep() * 1000));
		viewHolder.txtViewRestTime.setText(WorkoutMaths.formatMillisAsTime(workoutSets.get(position).getRestTimePerRep() * 1000));
	
		WorkoutConstraints currentConstraint = workoutConstraints.get(workoutSets.get(position).getTargetZone());
		viewHolder.txtViewFTP.setText(DisplayHelper.getFTPUserPrefDisplayRange(currentConstraint.getMinPower(), currentConstraint.getMaxPower(), this.userPrefs));
		viewHolder.txtViewReps.setText(Integer.toString(workoutSets.get(position).getNumberOfReps()));
		viewHolder.txtViewHR.setText(DisplayHelper.getHRUserPrefDisplayRange(currentConstraint.getMinHR(), currentConstraint.getMaxHR(), this.userPrefs));
		
		//this is the only way I could figure out how to paint the background of the FIRST working rep. Everything else is handled in the DisplayWorkoutActivity
		if(position == 0 && isWorkingRep && activeIndex == 0)
		{
			viewHolder.layoutWorkingDescription.setBackground(viewHolder.activeColor);
		}
	}
	
	@Override
	public WorkoutViewHolder onCreateViewHolder(ViewGroup viewGroup, int arg1) {
		View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_workout_set, viewGroup, false);
        return new WorkoutViewHolder(v);
	}

	public int getActiveIndex() {
		return activeIndex;
	}

	public void setActiveIndex(int activeIndex) {
		this.activeIndex = activeIndex;
	}

	public boolean isWorkingRep() {
		return isWorkingRep;
	}

	public void setWorkingRep(boolean isWorkingRep) {
		this.isWorkingRep = isWorkingRep;
	}
}
