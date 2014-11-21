package com.hudl.myfirstapp;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.hudl.workout.datacontainer.WorkoutPrefs;

public class MainActivity extends ActionBarActivity {
	
	List<String> list;
	GridView grid;
	int selectedZone = 0;
	public final static String WORKOUT_PREFERENCES = "workoutPrefs";
	public final static String WORKOUT_CONSTRAINTS = "workoutConstraints";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        list=new ArrayList<String>();
        grid=(GridView) findViewById(R.id.zoneGrid);
        
        list.add("Zone 1 - less than 55% FTPw");
        list.add("Zone 2 - 55-74% FTPw");
        list.add("Zone 3 - 75-89% FTPw");
        list.add("Zone 4 - 90-104% FTPw");
        list.add("Zone 5 - 105-120% FTPw");
        list.add("Zone 6 - more than 120% FTPw");
        list.add("Zone 7 - SUPERHUMAN FTPw");
        
        ArrayAdapter<String> adp=new ArrayAdapter<String> (this,
        		android.R.layout.simple_dropdown_item_1line,list);
        grid.setAdapter(adp);
        
        grid.setOnItemClickListener(new OnItemClickListener() {
 
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			selectedZone = arg2 + 1;
				
			Toast.makeText(getBaseContext(), list.get(arg2),
				Toast.LENGTH_SHORT).show();
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    /** Called when the user clicks the Send button */
    public void btnStartWorkout(View view)
    {
    	WorkoutPrefs prefs = new WorkoutPrefs();

		EditText txtDesiredTime = (EditText) findViewById(R.id.editTxtTime);
		int workouttime = Integer.valueOf(txtDesiredTime.getText().toString());
    	
		prefs.setTime(workouttime * 60);
		prefs.setZone(selectedZone);
    	
    	
		
		//switch screens
		Intent intent = new Intent(this, DisplayWorkoutActivity.class);
		intent.putExtra(WORKOUT_PREFERENCES, prefs);
		startActivity(intent);		
    }
}


