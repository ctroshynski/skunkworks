package com.brandNameSoftware.bikeTrainer;

import com.brandNameSoftware.bikeTrainer.utils.AnalyticsApplication;
import com.google.android.gms.analytics.GoogleAnalytics;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

public class AppPreferencesActivity extends PreferenceActivity {
	public static final String PREFERENCE_FTP_KEY = "prefftp";
	public static final String PREFERENCE_MAXHR_KEY = "prefmaxHR";
	public static final String PREFERENCE_DISPLAY_TYPE_KEY = "displayType";
	public static final String PREFERENCE_WARMUP_TIME_KEY = "prefwarmupTime";
	public static final String PREFERENCE_COOLDOWN_TIME_KEY = "prefcooldownTime";
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		//Get a Tracker (should auto-report)
    	((AnalyticsApplication) getApplication()).getTracker(AnalyticsApplication.TrackerName.APP_TRACKER);
    	
		super.onCreate(savedInstanceState);
		getFragmentManager().beginTransaction().replace(android.R.id.content,
                new FTPValueFragment()).commit();
	}
	
    public static class FTPValueFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener{
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);
            findPreference(PREFERENCE_FTP_KEY).setSummary(getPreferenceScreen().getSharedPreferences().getString(PREFERENCE_FTP_KEY, "") + " Watts");
            findPreference(PREFERENCE_MAXHR_KEY).setSummary(getPreferenceScreen().getSharedPreferences().getString(PREFERENCE_MAXHR_KEY, "") + " bpm");
            findPreference(PREFERENCE_WARMUP_TIME_KEY).setSummary(getPreferenceScreen().getSharedPreferences().getString(PREFERENCE_WARMUP_TIME_KEY, "") + " minutes");
            findPreference(PREFERENCE_COOLDOWN_TIME_KEY).setSummary(getPreferenceScreen().getSharedPreferences().getString(PREFERENCE_COOLDOWN_TIME_KEY, "") + " minutes");
        }
        
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        	if (key.equals(PREFERENCE_FTP_KEY)) {
                Preference pref = findPreference(key);
                // Set summary to be the user-description for the selected value
                pref.setSummary(sharedPreferences.getString(key, "") + " Watts");
            }
        	else if (key.equals(PREFERENCE_MAXHR_KEY)) {
                Preference pref = findPreference(key);
                // Set summary to be the user-description for the selected value
                pref.setSummary(sharedPreferences.getString(key, "") + " bpm");
            }
        	else if (key.equals(PREFERENCE_WARMUP_TIME_KEY) || key.equals(PREFERENCE_COOLDOWN_TIME_KEY)) {
                Preference pref = findPreference(key);
                // Set summary to be the user-description for the selected value
                pref.setSummary(sharedPreferences.getString(key, "") + " minutes");
            }
        }
        
        @Override
		public void onResume()
        {
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        /* (non-Javadoc)
         * @see android.app.Activity#onPause()
         */
        @Override
		public void onPause()
        {
            super.onPause();

            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }
    }

    
    @Override
    public void onStart()
    {
    	//Get an Analytics tracker to report app starts and uncaught exceptions etc.
    	GoogleAnalytics.getInstance(this).reportActivityStart(this);
    	super.onStart();
    }
    
    @Override
    public void onStop()
    {
    	//Stop the analytics tracking
    	GoogleAnalytics.getInstance(this).reportActivityStop(this);
    	super.onStop();
    }
}
