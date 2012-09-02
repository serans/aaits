package aaits.tablet.activities;

import aaits.tablet.R;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


public class PreferencesActivity extends Activity {
    public static final String PREFERENCE_FILENAME = "aaits_preferences";
    private SharedPreferences settings;
    
    @Override 
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.preferences);
    }
    
    @Override 
    public void onResume() {
    	super.onResume();
    	setTitle(getString(R.string.app_name)+":: preferences");
    	TextView txt = (TextView) findViewById(R.id.server_name);
    	settings = getSharedPreferences(PREFERENCE_FILENAME,MODE_PRIVATE);
    	txt.setText(settings.getString("server_url", "http://localhost:9000"));
    }
    
    //Buttons
    public void onSaveClicked(View v){
    	
    }
    
    //Buttons
    public void onCancelClicked(View v){
    	
    }
}