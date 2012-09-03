package aaits.tablet.activities;

import android.content.SharedPreferences;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import aaits.tablet.bluetooth.BluetoothCommService;
import aaits.tablet.bluetooth.DeviceListActivity;
import aaits.tablet.models.*;
import aaits.tablet.yaml.YamlInterpreter;
import aaits.tablet.yaml.YamlLine;

import aaits.tablet.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	// Debugging
    private static final String TAG = "BluetoothChat";
    private static final boolean D = true;

    // Communications state
    public static final int COMM_NONE             = 0;
    public static final int COMM_READING_FILEINFO = 1;
    public static final int COMM_READING_CONFIG   = 2;
    public static final int COMM_READING_DATAFILE = 3;
    public static final int COMM_SENDING_CONFIG   = 4;
    public static final int COMM_READING_UID      = 5;
    
    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    // Name of the connected device
    public static String mConnectedDeviceName = null;
    // Array adapter for the conversation thread
    private ArrayAdapter<String> mConversationArrayAdapter;
    // String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private static BluetoothCommService mBTCommService = null;
    
    public static int comm_state=0;

    public static NodeConfig node;
    public static List<String> dataFiles;
    private YamlInterpreter yi;
    
    private ProgressDialog progress;

	/**
     * Called when the activity is starting.
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	yi=new YamlInterpreter();
    	
    	super.onCreate(savedInstanceState);
    	dataFiles = new ArrayList<String>();
    	// Get local Bluetooth adapter
    	if(mBluetoothAdapter == null )
    		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
    	
        setContentView(R.layout.main);
    }
    
    /**
     * 
     */
    @Override
    public void onStart() {
        super.onStart();
        
        if(D) Log.e(TAG, "++ ON START ++");

        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        // Otherwise, setup the chat session
        } else {
            if (mBTCommService == null) setupBT();
        }
        
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        setTitle(getString(R.string.app_name)+":: main menu");
        if (mBTCommService != null) {
        	mBTCommService.setHandler(mHandler);
        }
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
      super.onConfigurationChanged(newConfig);
      setContentView(R.layout.main);
    }
    
    private void setupBT(){
        Log.d(TAG, "setupBT()");

        // Initialize the BluetoothChatService to perform bluetooth connections
        mBTCommService = new BluetoothCommService(this, mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");        
    }
    
 // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	switch (msg.what) {
            case MESSAGE_STATE_CHANGE:
            	
                if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                switch (msg.arg1) {
                case BluetoothCommService.STATE_CONNECTED:
                	progress.setMessage("reading file information...");
                	MainActivity.comm_state = COMM_READING_FILEINFO;
                	mBTCommService.println("GET FILEINFO ");
                    break;
                case BluetoothCommService.STATE_CONNECTING:
                    break;
                case BluetoothCommService.STATE_NONE:
                	progress.dismiss();
                    break;
                }
                break;
            case MESSAGE_WRITE:
                if(D) Log.i(TAG, "SENT:"+new String((byte []) msg.obj));
                break;
            case MESSAGE_READ:
            	
            	String str = (String) msg.obj;
        		boolean EOT=false;
        		
        		//detect EOT
        		if(str.length()>0 && str.charAt(str.length()-1)==0x04) {
        			EOT=true;
        			if(str.length()==1) 
        				str="";
        			else
        				str=str.substring(0, str.length()-2);
        		}
            	
            	switch(comm_state) {
            	
            	//ADD data files to list
            	case COMM_READING_FILEINFO:
	            	Pattern data_pattern = Pattern.compile("^[0-9]+\\.YML$");
	            	Matcher m = data_pattern.matcher(str);
	            	while(m.find()) {
	            		MainActivity.dataFiles.add(m.group());
	            	}
	            	break;

	            //interpretate node config 
            	case COMM_READING_CONFIG:
	            	try {
						yi.interpretateLine(str);
					} catch (ParseException e) {
						e.printStackTrace();
					}
	            	break;
	            	
            	case COMM_READING_UID:
            		
            		YamlLine yl = new YamlLine(str);
            		if(yl.getKey()!=null && yl.getKey().equals("device_uid")) {
            			
            			String number = yl.getValue();
            			node.setDeviceUid(Long.parseLong(number));
            			Log.i(TAG,"node uid:"+node.getDeviceUid());
            		}
            		break;
            	}
            	
            	if(EOT) {
            		switch (comm_state) {
            		case COMM_READING_FILEINFO:
            			Log.i(TAG,"EOT: fileinfo");
            			yi.reset();
            			mBTCommService.println("GET CONFIG.YML ");
            			comm_state = COMM_READING_CONFIG;
            			progress.setMessage("reading configuration...");
            			break;
            		case COMM_READING_CONFIG:
            			Log.i(TAG,"EOT: config");
            			MainActivity.node = yi.getNodeConfig();
            			yi.reset();
            			mBTCommService.println("GET device_uid ");
            			comm_state = COMM_READING_UID;
            			break;
            		case COMM_READING_UID:
            			Log.i(TAG,"EOT: uid");
            			progress.dismiss();
            			Intent myIntent = new Intent(MainActivity.this, DataMenuActivity.class);
            	        startActivityForResult(myIntent, 0);
            			break;
            		}
            	}
                break;
            case MESSAGE_DEVICE_NAME:
                // save the connected device's name
                MainActivity.mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                break;
            case MESSAGE_TOAST:
                Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                               Toast.LENGTH_SHORT).show();
                break;
            }
        }
    };
    
    /**
     * Receives result for selecting BT device 
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        case REQUEST_CONNECT_DEVICE:

            if (resultCode == Activity.RESULT_OK) {
                String address = data.getExtras()
                                     .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                mBTCommService.connect(device);
                
                progress = ProgressDialog.show(MainActivity.this, "Connecting", "awaiting response...");
                progress.setCancelable(true);
            }
            break;
        case REQUEST_ENABLE_BT:
            if (resultCode == Activity.RESULT_OK) {
                setupBT();
            } else {
                Log.d(TAG, "BT not enabled");
                Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
    
    //BUTTONS
    public void onSearchClicked(View v) {
    	Intent serverIntent = new Intent(this, DeviceListActivity.class);
        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
    }
    
    public void onSynchClicked(View v){
        //startActivity( new Intent(v.getContext(), ConfigActivity.class) );
    	File sdRootDir = Environment.getExternalStorageDirectory();
    	File dataDir = new File(sdRootDir,"aaits_data");
    	dataDir.mkdir();
    	BufferedWriter fileOutBuffer;
    	
    	File outFile = new File(dataDir, "android_test.txt");
	    FileWriter fileWriter;
		try {
			fileWriter = new FileWriter(outFile);
			fileOutBuffer = new BufferedWriter(fileWriter);
			fileOutBuffer.write("Hola mundo");
			fileOutBuffer.close();
		} catch (IOException e) {
			Toast.makeText(MainActivity.this, "Can't write to SD card. Download cancelled", Toast.LENGTH_SHORT).show();
		}
		
    }
    
    public void onPreferencesClicked(View v) {
        startActivity(new Intent(this, PreferencesActivity.class));
    }
    
    // GET SET
    public  static BluetoothCommService getBTCommService() {
    	return mBTCommService;
    }
    
}