package com.brandNameSoftware.bikeTrainer.utils;

import java.util.HashMap;

import android.app.Application;

import com.brandNameSoftware.bikeTrainer.R;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;

public class AnalyticsApplication extends Application {
	 // The following line should be changed to include the correct property id.
    private static final String PROPERTY_ID = "UA-58924169-1";
    //Logging TAG
    private static final String TAG = "MyApp";
    public static int GENERAL_TRACKER = 0;

    public enum TrackerName
    {
        APP_TRACKER, // Tracker used only in this app.
        GLOBAL_TRACKER // Tracker used by all the apps from a company. eg: roll-up tracking.
    }

    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    public AnalyticsApplication()
    {
        super();
    }

    public synchronized Tracker getTracker(TrackerName trackerId)
    {
        if (!mTrackers.containsKey(trackerId)) {

            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            analytics.getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
            
            Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics.newTracker(PROPERTY_ID)
                    : analytics.newTracker(R.xml.global_tracker);
            mTrackers.put(trackerId, t);
            analytics.enableAutoActivityReports(this);
            t.enableAutoActivityTracking(true);
        }
        return mTrackers.get(trackerId);
    }
}
