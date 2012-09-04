package aaits.tablet.comm;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

public class UploadFileTask extends AsyncTask<File, Integer, Integer>{

	private ProgressDialog progress;
	private Activity activity;
	private String serverUrl;
	private String progressTitle = "Uploading data";
	private String progressText  = "awaiting response...";
	
	private onCompleteI onComplete;
	
	public interface onCompleteI {
		public void execute();
	}
	
	@Override
	protected void onPreExecute(){
		progress = ProgressDialog.show(activity, progressTitle, progressText);
	}
	
	
	@Override
	protected Integer doInBackground(File... files) {
		Log.i("UploadFileTask","starting");
		
		for(File f : files ) {
			sendFile(f);
			f.delete();
		}
		return null;
	}
	
	@Override
	protected void onPostExecute(Integer result) {
        progress.dismiss();
        if(onComplete!=null) onComplete.execute();
    }
	
	@Override
	protected void onProgressUpdate(Integer... pros) {
		progress.setTitle(progressTitle);
		progress.setMessage(progressText);
	}
	
	private void sendFile(File f) {
		Log.i("UploadFileTask", "uploading "+f.getName());
    	progressText="uploading "+f.getName();
    	String urlServer = serverUrl+"/data/upload/yaml";
    	
    	HttpURLConnection connection = null;
    	DataOutputStream outputStream = null;
    	DataInputStream inputStream = null;

    	String lineEnd = "\r\n";
    	String twoHyphens = "--";
    	String boundary =  "*****";

    	int bytesRead, bytesAvailable, bufferSize;
    	byte[] buffer;
    	int maxBufferSize = 1*1024*1024;

    	try
    	{
    		FileInputStream fileInputStream = new FileInputStream(f);
    		URL url = new URL(urlServer);
    		connection = (HttpURLConnection) url.openConnection();

	    	// Allow Inputs & Outputs
	    	connection.setDoInput(true);
	    	connection.setDoOutput(true);
	    	connection.setUseCaches(false);

	    	// Enable POST method
	    	connection.setRequestMethod("POST");
	
	    	connection.setRequestProperty("Connection", "Keep-Alive");
	    	connection.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
	
	    	outputStream = new DataOutputStream( connection.getOutputStream() );
	    	outputStream.writeBytes(twoHyphens + boundary + lineEnd);
	    	outputStream.writeBytes("Content-Disposition: form-data; name=\"Data File\";filename=\"data.YML\"" + lineEnd);
	    	outputStream.writeBytes(lineEnd);
	
	    	bytesAvailable = fileInputStream.available();
	    	bufferSize = Math.min(bytesAvailable, maxBufferSize);
	    	buffer = new byte[bufferSize];
	
	    	// Read file
	    	bytesRead = fileInputStream.read(buffer, 0, bufferSize);
	
	    	while (bytesRead > 0) {
		    	outputStream.write(buffer, 0, bufferSize);
		    	bytesAvailable = fileInputStream.available();
		    	bufferSize = Math.min(bytesAvailable, maxBufferSize);
		    	bytesRead = fileInputStream.read(buffer, 0, bufferSize);
	    	}
	
	    	outputStream.writeBytes(lineEnd);
	    	outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
	
	    	// Responses from the server (code and message)
	    	int serverResponseCode = connection.getResponseCode();
	    	String serverResponseMessage = connection.getResponseMessage();
	
	    	fileInputStream.close();
	    	outputStream.flush();
	    	outputStream.close();
	    	}
    	catch (Exception ex) {
	    	Log.i("UploadFileTask",ex.toString());
	    }
    }

	public void setActivity(Activity a) {
		activity = a;
	}
	
	public void setProgressDialog(ProgressDialog pd) {
		progress = pd;
	}
	
	public void setServerUrl(String s) {
		serverUrl = s;
	}
	
	public void setOnComplete(onCompleteI callback) {
		onComplete = callback;
	}
}