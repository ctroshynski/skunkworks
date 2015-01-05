package com.brandNameSoftware.bikeTrainer.beans;

public class UserPrefs {
	private int maxHR;
	private int FTP;
	private boolean displayAsAbsolute = false;
	
	public int getMaxHR() {
		return maxHR;
	}
	public void setMaxHR(int maxHR) {
		this.maxHR = maxHR;
	}
	public int getFTP() {
		return FTP;
	}
	public void setFTP(int fTP) {
		FTP = fTP;
	}
	public boolean isDisplayAsAbsolute() {
		return displayAsAbsolute;
	}
	public void setDisplayAsAbsolute(boolean displayAsAbsolute) {
		this.displayAsAbsolute = displayAsAbsolute;
	}
}
