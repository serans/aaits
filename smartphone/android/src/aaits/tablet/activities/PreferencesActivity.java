package aaits.tablet.activities;

import aaits.tablet.R;
import aaits.tablet.bluetooth.DeviceListActivity;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
    	txt.setText(settings.getString("server_url", "http://localhost"));
    }
    
    //Buttons
    public void onSaveClicked(View v){
    	TextView txt = (TextView) findViewById(R.id.server_name);
    	
    	Editor editor = settings.edit();
        editor.putString("server_url", txt.getText().toString());              
        editor.commit();
    	
    	goBack();
    }
    
    //Buttons
    public void onCancelClicked(View v){
    	goBack();
    }
    
    private void goBack(){
    	Intent goBack = new Intent(PreferencesActivity.this, MainActivity.class);
        startActivityForResult(goBack, 0);
    }
}