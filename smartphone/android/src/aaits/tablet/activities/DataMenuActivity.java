package aaits.tablet.activities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import aaits.tablet.R;
import aaits.tablet.bluetooth.BluetoothCommService;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class DataMenuActivity extends Activity {
	
	public ProgressDialog progress;
	
	private static final int STATE_NONE = 0;
	private static final int STATE_DOWNLOADING = 1;
	private static final int STATE_DELETING = 2;
	
	File dataDir;
	BufferedWriter fileOutBuffer;
	
	Iterator<String> filesIterator;
	
	private int state = 0;
	
	/**
	 * Makes sure the data directory exists
	 */
	@Override
    public void onCreate(Bundle savedInstanceState) {
		dataDir = new File(Environment.getExternalStorageDirectory(),"aaits_data");
		dataDir.mkdir();
		super.onCreate(savedInstanceState);
        setContentView(R.layout.data_menu);
    }
	
	
	@Override
	public void onResume(){
		super.onResume();
		
		filesIterator = MainActivity.dataFiles.iterator();
		
        ((TextView) findViewById(R.id.device_name)).setText(MainActivity.mConnectedDeviceName);
        setTitle(getString(R.string.app_name)+":: connected to «"+MainActivity.mConnectedDeviceName+"»");
        MainActivity.getBTCommService().setHandler(dataHandler);
	}
	
	//Buttons
	public void onDownloadClick(View v) {
		if(dataDir.canWrite()) {
			state = STATE_DOWNLOADING;
			
			progress = ProgressDialog.show(DataMenuActivity.this, "Downloading data", "awaiting response...");
			
			downloadNextFile();
			
		} else {
			Toast.makeText(DataMenuActivity.this, "Can't write to SD card. Download cancelled", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void onConfigClick(View v) {
		startActivity(new Intent(DataMenuActivity.this, ConfigActivity.class));
	}
	
	/**
	 * Prepares to download the next file.
	 * @return
	 */
	private boolean downloadNextFile() {
		Log.i("DataMenu", "downloadNext start");
		if(fileOutBuffer!=null) {
			try {
				fileOutBuffer.close();
			} catch (IOException e1) {
				Log.i("DataMenu", "Error closing fileOutBuffer");
			}
		}
		
		if(filesIterator.hasNext()) {
			Log.i("DataMenu", "downloadNext has next");
			String filename = filesIterator.next();
			
			File outFile = new File(dataDir, MainActivity.node.getName()+"_"+filename);
		    FileWriter fileWriter;
			try {
				fileWriter = new FileWriter(outFile);
			} catch (IOException e) {
				Toast.makeText(DataMenuActivity.this, "Can't write to SD card. Download cancelled", Toast.LENGTH_SHORT).show();
				return false;
			}
		    fileOutBuffer = new BufferedWriter(fileWriter);
		    	
			progress.setMessage(filename);
			MainActivity.getBTCommService().println("GET "+filename+" ");
			return true;
		} else {
			return false;
		}
	}
	
	private boolean deleteNextFile() {
		if(filesIterator.hasNext()) {
			String filename = filesIterator.next();
			progress.setMessage(filename);
			MainActivity.getBTCommService().println("DELETE "+filename+" ");
			return true;
		} else {
			return false;
		}
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
        		boolean EOT=false;
        		
        		//detect EOT
        		if(str.length()>0 && str.charAt(str.length()-1)==0x04) {
        			EOT=true;
        			if(str.length()==1) 
        				str="";
        			else
        				str=str.subSequence(0, str.length()-2).toString();
        		}
        		
        		switch(state){ 
        		case STATE_DOWNLOADING:
        			
        			try {
						fileOutBuffer.write(str);
					} catch (IOException e) {
						state=STATE_NONE;
						Toast.makeText(DataMenuActivity.this, "Can't write to SD card. Download cancelled", Toast.LENGTH_SHORT).show();
						Log.i("DataMenu","can't write to SD!!");
					} catch (NullPointerException e) {
						state=STATE_NONE;
						Toast.makeText(DataMenuActivity.this, "Can't write to SD card. Download cancelled", Toast.LENGTH_SHORT).show();
						Log.i("DataMenu","fileOutBuffer not init");
					}
        			
        			if(EOT) {
        				if(!downloadNextFile()) {
        					state=STATE_DELETING; //STATE_DELETING
        					progress.setTitle("Removing data from device");
        					filesIterator = MainActivity.dataFiles.iterator();
        					deleteNextFile();
        				}
        			}
        			break;
        		case STATE_DELETING:
        			if(EOT) {
        				if(!deleteNextFile()) {
        					state=STATE_NONE; //STATE_DELETING
        					progress.dismiss();
        					Button b = (Button) findViewById(R.id.btn_download);
        					b.setClickable(false);
        				}
        			}
        			break;
        		}
        		
        	}
        }
    };

}
