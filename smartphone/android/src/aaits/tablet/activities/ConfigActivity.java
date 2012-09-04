package aaits.tablet.activities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import aaits.tablet.R;
import aaits.tablet.models.SensorConfig;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.Toast;

public class ConfigActivity extends Activity {

	public static final int STATE_NONE=0;
	public static final int STATE_SENDING_CONFIG=1;
	
	private ListView sensorList;
	private ArrayAdapter<String> listAdapter;
	private SeekBar sampling_period;
	private TextView sampling_period_txt;
	private EditText device_name;
	
	public int state = STATE_NONE;
	public ProgressDialog progress;
	
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
    	MainActivity.getBTCommService().setHandler(dataHandler);
    	device_name.setText(MainActivity.node.getName());
    	sampling_period.setProgress(MainActivity.node.getSamplingPeriod());
    	
    	listAdapter = new ArrayAdapter<String>(this, R.layout.sensor_list_item);  
        for(SensorConfig sc : MainActivity.node.getSensorConfigs()) {
        	listAdapter.add(sc.getListName());
        }
        
        sensorList.setAdapter( listAdapter );
        
        sensorList.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
	        	Intent gotoEdit = new Intent(ConfigActivity.this, ConfigSensorActivity.class);
	        	gotoEdit.putExtra("sensorListName", sensorList.getItemAtPosition(position).toString());
	        	startActivity(gotoEdit);
		        }
        });
    }
	
    public void onAddSensorClick(View v){
    	Intent gotoEdit = new Intent(ConfigActivity.this, ConfigSensorActivity.class);
    	startActivity(gotoEdit);
    }
    

	public void onApllyClick(View v){
		state = STATE_SENDING_CONFIG;
		progress = ProgressDialog.show(ConfigActivity.this, "Updating config", "awaiting response...");
		MainActivity.getBTCommService().println("POST CONFIG.YML \n");
		MainActivity.getBTCommService().print(MainActivity.node.toYaml());
		byte [] eof = {0x04};
		MainActivity.getBTCommService().write(eof);
	}
	
	/**
	 * The Handler that gets information back from the BluetoothChatService
	 */
    private final Handler dataHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	switch (msg.what) {
        	case MainActivity.MESSAGE_READ:
        		
        		String str = (String) msg.obj;
        		Log.i("<-",str);
        		boolean EOT=false;
        		
        		//detect EOT
        		if(str.length()>0 && str.charAt(str.length()-1)==0x04) {
        			EOT=true;
        			if(str.length()==1) 
        				str="";
        			else
        				str=str.subSequence(0, str.length()-2).toString();
        		}
        		
        		if(EOT && state==STATE_SENDING_CONFIG) {
        			progress.dismiss();
        		}
        	}
        }
    };

}
