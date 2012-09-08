package aaits.tablet.activities;

import aaits.tablet.R;
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
    
    TextView serverName;
    TextView dataDir;
    
    @Override 
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.preferences);
    }
    
    @Override 
    public void onResume() {
    	super.onResume();
    	setTitle(getString(R.string.app_name)+":: preferences");
    	
    	serverName = (TextView) findViewById(R.id.server_name);
    	dataDir    = (TextView) findViewById(R.id.data_dir);
    	
    	settings = getSharedPreferences(PREFERENCE_FILENAME,MODE_PRIVATE);
    	serverName.setText(settings.getString("server_url", "http://localhost"));
    	dataDir.setText(settings.getString("data_dir", "aaits_data"));
    }
    
    //Buttons
    public void onSaveClicked(View v){
    	
    	Editor editor = settings.edit();
        editor.putString("server_url", serverName.getText().toString());
        editor.putString("data_dir", dataDir.getText().toString());
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