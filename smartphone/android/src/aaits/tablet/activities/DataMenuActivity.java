package aaits.tablet.activities;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import aaits.tablet.R;
import aaits.tablet.bluetooth.BluetoothCommService;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class DataMenuActivity extends Activity {
	
	private ProgressDialog progress;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.data_menu);
    }
	
	@Override
	public void onResume(){
		super.onResume();
        ((TextView) findViewById(R.id.device_name)).setText(MainActivity.mConnectedDeviceName);
        setTitle(getString(R.string.app_name)+":: connected to «"+MainActivity.mConnectedDeviceName+"»");
        MainActivity.getBTCommService().setHandler(dataHandler);
	}
	
	public void onDownloadClick(View v) {
		/*
		progress = ProgressDialog.show(DataMenuActivity.this, "Downloading Data", "");
		progress.setCancelable(true);
		*/
		/*
		for( String dataFile : MainActivity.dataFiles) {
			
		}*/
		
		Log.i("D","Downloading "+MainActivity.dataFiles.get(0));
		
		MainActivity.getBTCommService().println("GET "+MainActivity.dataFiles.get(0)+" ");
		
	}
	
	//The Handler that gets information back from the BluetoothChatService
    private final Handler dataHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	switch (msg.what) {
        	case MainActivity.MESSAGE_READ:
        		String str = (String) msg.obj;
        		Log.i("tst",str);
        		break;
        	}
        }
    };

}
