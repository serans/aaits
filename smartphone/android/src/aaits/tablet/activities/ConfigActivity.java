package aaits.tablet.activities;

import java.util.ArrayList;
import java.util.Arrays;

import aaits.tablet.R;
import aaits.tablet.models.SensorConfig;
import android.app.Activity;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

public class ConfigActivity extends Activity {

	private ListView sensorList;
	private ArrayAdapter<String> listAdapter;
	private SeekBar sampling_period;
	private TextView sampling_period_txt;
	private EditText device_name;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config_menu);
          
        // Find the ListView resource.   
        sensorList = (ListView) findViewById( R.id.sensors_list);  
        device_name = (EditText) findViewById(R.id.device_name);
    	sampling_period = (SeekBar) findViewById(R.id.sampling_period);
    	sampling_period_txt = (TextView) findViewById(R.id.sampling_period_txt);
    	
    	device_name.addTextChangedListener( new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				Log.i("after",s.toString());
				MainActivity.node.setName(s.toString());
			}
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,	int arg2, int arg3) {}
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,int arg3) {}
    	});
    	
    	sampling_period.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
    		@Override
			public void onStopTrackingTouch(SeekBar arg0) {}
			@Override
			public void onStartTrackingTouch(SeekBar arg0) {}
			@Override
			public void onProgressChanged(SeekBar s, int arg1, boolean arg2) {
				MainActivity.node.setSamplingPeriod(s.getProgress());
				String text = String.valueOf(s.getProgress());
				sampling_period_txt.setText(text);
			}
		});
    	
    }
    
    @Override
    public void onResume(){
    	super.onResume();
    	
    	device_name.setText(MainActivity.node.getName());
    	sampling_period.setProgress(MainActivity.node.getSamplingPeriod());
    	
    	listAdapter = new ArrayAdapter<String>(this, R.layout.sensor_list_item);  
        for(SensorConfig sc : MainActivity.node.getSensorConfigs()) {
        	listAdapter.add(sc.getListName());
        }
        
        sensorList.setAdapter( listAdapter );
        
        sensorList.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
		        Log.i("T",sensorList.getItemAtPosition(position).toString());
		        }
        });
    }
	
}
