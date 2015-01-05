package com.brandNameSoftware.bikeTrainer.utils;

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
			ftpDisplay = minFTPPercentage + "-" + maxFTPPercentage;
		}
		
		return ftpDisplay;
	}
	
	public static String getFTPPercentageDisplayRange(int minFTPPercentage, int maxFTPPercentage)
	{
		String ftpDisplay = "";
		
		ftpDisplay = minFTPPercentage + "-" + maxFTPPercentage + "% FTP";
		
		return ftpDisplay;
	}
}
