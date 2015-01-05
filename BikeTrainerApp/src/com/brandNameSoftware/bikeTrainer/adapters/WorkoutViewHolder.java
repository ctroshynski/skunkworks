package com.brandNameSoftware.bikeTrainer.adapters;

import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brandNameSoftware.bikeTrainer.R;

public class WorkoutViewHolder extends ViewHolder {

	TextView txtViewZone;
	TextView txtViewRepTime;
	TextView txtViewRestTime;
	TextView txtViewFTP;
	TextView txtViewReps;
	TextView txtViewHR;
	LinearLayout layoutWorkingDescription;
	ColorDrawable activeColor;
	
	public WorkoutViewHolder(View itemView) {
		super(itemView);
		txtViewZone = (TextView) itemView.findViewById(R.id.txtViewZone);
		txtViewRepTime = (TextView) itemView.findViewById(R.id.txtViewRepTime);
		txtViewRestTime = (TextView) itemView.findViewById(R.id.txtViewRestTime);
		txtViewFTP = (TextView) itemView.findViewById(R.id.txtViewFTP);
		txtViewReps = (TextView) itemView.findViewById(R.id.txtViewReps);
		txtViewHR = (TextView) itemView.findViewById(R.id.txtViewHR);
		layoutWorkingDescription = (LinearLayout) itemView.findViewById(R.id.layoutWorkoutDetails);
		activeColor = new ColorDrawable(itemView.getResources().getColor(R.color.accent_transparent));
	}

}
