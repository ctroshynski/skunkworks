package com.brandNameSoftware.bikeTrainer.adapters;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.brandNameSoftware.bikeTrainer.R;

public class WorkoutViewHolder extends ViewHolder {

	View itemView;
	TextView txtViewZone;
	TextView txtViewRepTime;
	TextView txtViewRestTime;
	TextView txtViewFTP;
	TextView txtViewReps;
	TextView txtViewHR;
	LinearLayout layoutWorkingDescription;
	RelativeLayout layoutRestDescription;
	int activeColor;
	int passiveColor;
	Drawable passiveDrawable;
	Drawable passiveTopBorderDrawable;
	
	public WorkoutViewHolder(View itemView) {
		super(itemView);
		this.itemView = itemView;
		txtViewZone = (TextView) itemView.findViewById(R.id.txtViewZone);
		txtViewRepTime = (TextView) itemView.findViewById(R.id.txtViewRepTime);
		txtViewRestTime = (TextView) itemView.findViewById(R.id.txtViewRestTime);
		txtViewFTP = (TextView) itemView.findViewById(R.id.txtViewFTP);
		txtViewReps = (TextView) itemView.findViewById(R.id.txtViewReps);
		txtViewHR = (TextView) itemView.findViewById(R.id.txtViewHR);
		layoutWorkingDescription = (LinearLayout) itemView.findViewById(R.id.layoutWorkoutDetails);
		layoutRestDescription = (RelativeLayout) itemView.findViewById(R.id.layoutRest);
		activeColor = itemView.getResources().getColor(R.color.accent_transparent);
		passiveColor = itemView.getResources().getColor(R.color.transparent);
		passiveDrawable = new ColorDrawable(itemView.getResources().getColor(R.color.transparent));
		passiveTopBorderDrawable = itemView.getResources().getDrawable(R.drawable.layout_top_border);
	}

}
