package com.brandNameSoftware.bikeTrainer;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

public class AppPreferencesActivity extends PreferenceActivity {
	private static final String PREFERENCE_FTP_KEY = "prefftp";
	private static final String PREFERENCE_MAXHR_KEY = "prefmaxHR";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
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
        }
        
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        	if (key.equals(PREFERENCE_FTP_KEY)) {
                Preference ftpPref = findPreference(key);
                // Set summary to be the user-description for the selected value
                ftpPref.setSummary(sharedPreferences.getString(key, "") + " Watts");
            }
        	else if (key.equals(PREFERENCE_MAXHR_KEY)) {
                Preference hrPref = findPreference(key);
                // Set summary to be the user-description for the selected value
                hrPref.setSummary(sharedPreferences.getString(key, "") + " bpm");
            }
        }
        
        @Override
		public void onResume() {
            // TODO Auto-generated method stub
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        /* (non-Javadoc)
         * @see android.app.Activity#onPause()
         */
        @Override
		public void onPause() {
            // TODO Auto-generated method stub
            super.onPause();

            getPreferenceScreen().getSharedPreferences()
            .unregisterOnSharedPreferenceChangeListener(this);
        }
    }
}
