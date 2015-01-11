package com.brandNameSoftware.bikeTrainer.utils;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.brandNameSoftware.bikeTrainer.R;
import com.brandNameSoftware.bikeTrainer.beans.UserPrefs;

public class DisplayHelper {

	public static String getHRUserPrefDisplayRange(int minHRPercentage, int maxHRPercentage, UserPrefs userPrefs)
	{
		String hrDisplay = "";
		if(minHRPercentage < 0 || maxHRPercentage < 0)
		{
			hrDisplay = "MAX";
		}
		else if(userPrefs.isDisplayAsAbsolute())
		{
			int minHR = (int)((minHRPercentage/100f) * userPrefs.getMaxHR());
			int maxHR = (int)((maxHRPercentage/100f) * userPrefs.getMaxHR());
			hrDisplay = minHR + "-" + maxHR + " bpm";
		}
		else
		{
			hrDisplay = minHRPercentage + "-" + maxHRPercentage + "% Max HR";
		}
		
		return hrDisplay;
	}
	
	public static String getHRPercentageDisplayRange(int minHRPercentage, int maxHRPercentage)
	{
		String hrDisplay = "";
		if(minHRPercentage < 0 || maxHRPercentage < 0)
		{
			hrDisplay = "MAX";
		}
		else
		{
			hrDisplay = minHRPercentage + "-" + maxHRPercentage + "% Max HR";;
		}
		
		return hrDisplay;
	}
	
	public static String getFTPUserPrefDisplayRange(int minFTPPercentage, int maxFTPPercentage, UserPrefs userPrefs)
	{
		String ftpDisplay = "";
		if(userPrefs.isDisplayAsAbsolute())
		{
			int minPower = (int)((minFTPPercentage/100f) * userPrefs.getFTP());
			int maxPower = (int)((maxFTPPercentage/100f) * userPrefs.getFTP());
			ftpDisplay = minPower + "-" + maxPower + " Watts";
		}
		else
		{
			ftpDisplay = minFTPPercentage + "-" + maxFTPPercentage + "%";
		}
		
		return ftpDisplay;
	}
	
	public static String getFTPPercentageDisplayRange(int minFTPPercentage, int maxFTPPercentage)
	{
		String ftpDisplay = "";
		
		ftpDisplay = minFTPPercentage + "-" + maxFTPPercentage + "% FTP";
		
		return ftpDisplay;
	}
	
	public static void setActiveBackgroundColor(View context, int currentSetIndex, boolean isWorkingSet, boolean wasSetIncremented)
	{
		RecyclerView recyclerView = (RecyclerView)context.findViewById(R.id.listViewWorkoutSets);
		
		if(recyclerView != null)
		{
			LinearLayoutManager layoutManager = (LinearLayoutManager)recyclerView.getLayoutManager();
			CardView currentCard = null;
			
			if(layoutManager.findFirstVisibleItemPosition() <= currentSetIndex && layoutManager.findLastVisibleItemPosition() >= currentSetIndex)
			{
				//if the card isn't visible, then we can't find it in the layout manager
				currentCard = (CardView)layoutManager.findViewByPosition(currentSetIndex);
				
				ViewGroup currentLayout = null;
				ViewGroup previousLayout = null;
				Drawable previousBackground = null;
				
				if(isWorkingSet)
				{
					currentLayout = (ViewGroup)currentCard.findViewById(R.id.layoutWorkoutDetails);
					
					if(wasSetIncremented)
					{
						//have to get the previous card if we moved sets
						currentCard = (CardView)recyclerView.getLayoutManager().findViewByPosition(currentSetIndex - 1);
					}
					
					if(currentCard != null)
					{
						previousLayout = (ViewGroup)currentCard.findViewById(R.id.layoutRest);
					}
					previousBackground = context.getResources().getDrawable(R.drawable.layout_top_border);
				}
				else
				{
					currentLayout = (ViewGroup)currentCard.findViewById(R.id.layoutRest);
					previousLayout = (ViewGroup)currentCard.findViewById(R.id.layoutWorkoutDetails);
					previousBackground = new ColorDrawable(context.getResources().getColor(R.color.transparent));
				}
				
				if(currentLayout != null)
				{
					currentLayout.setBackgroundColor(context.getResources().getColor(R.color.accent_transparent));
				}
				if(previousLayout != null)
				{
					previousLayout.setBackground(previousBackground);
				}
			}
		}
	}
}
