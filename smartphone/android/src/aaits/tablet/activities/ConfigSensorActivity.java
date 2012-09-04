package aaits.tablet.activities;

import java.util.List;
import java.util.Map.Entry;

import aaits.tablet.R;
import aaits.tablet.models.SensorConfig;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

public class ConfigSensorActivity extends Activity {
	
	String sensorListName=null;
	SensorConfig sensorConfig=null;
	
	EditText ref;
	EditText name;
	EditText units;
	EditText steps;
	EditText pin;
	EditText function;
	Spinner className;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config_sensor);
        if(getIntent().getExtras()!=null)
        	sensorListName = getIntent().getExtras().getString("sensorListName");
        ref = (EditText) findViewById(R.id.ref);
        name = (EditText) findViewById(R.id.name);
        units = (EditText) findViewById(R.id.units);
        steps = (EditText) findViewById(R.id.steps);
        pin = (EditText) findViewById(R.id.pinNumberText);
        function = (EditText) findViewById(R.id.transform);
        className = (Spinner) findViewById(R.id.className);
	}
	
	@Override
	public void onResume(){
		super.onResume();
		if(sensorConfig==null) {
			if(sensorListName==null) {
				List<SensorConfig> listS = MainActivity.node.getSensorConfigs();
				sensorConfig = new SensorConfig();
				sensorConfig.setInternal_id(listS.size()+1);
				listS.add(sensorConfig);
			} else {
				sensorConfig = MainActivity.node.findSensorByListName(sensorListName);
			}
		}
		
		ref.setText(sensorConfig.getRef());
		name.setText(sensorConfig.getName());
		units.setText(sensorConfig.getUnits());
 		steps.setText(String.valueOf(sensorConfig.getSteps()));
 		
 		ArrayAdapter <CharSequence> adapter =
 		    new ArrayAdapter <CharSequence> (this, android.R.layout.simple_spinner_item );
 		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
 		
 		int selectedItem=0;
 		for( Entry<String, String> s : SensorConfig.classes().entrySet()) {
 			if(s.getKey().equals(sensorConfig.getClassName())) {
 				selectedItem=adapter.getCount();
 			}
 			adapter.add(s.getValue());
 		}
 		className.setAdapter(adapter);
 		className.setSelection(selectedItem);
 		
		if(sensorConfig.getPin()!=null) {
			((RadioButton) findViewById(R.id.optionPinYes)).setChecked(true);
			pin.setText(String.valueOf(sensorConfig.getPin()));
		} else {
			((RadioButton) findViewById(R.id.optionPinYes)).setChecked(false);
			pin.setText(null);
		}
		
		if(sensorConfig.getTransform()!=null) {
			((RadioButton) findViewById(R.id.optionTransformYes)).setChecked(true);
			function.setText(String.valueOf(sensorConfig.getTransform()));
		} else {
			((RadioButton) findViewById(R.id.optionTransformNo)).setChecked(true);
			function.setText("");
		}
	}
	
	// BUTTON
	public void onDoneClicked(View v){
		Log.i("Selected:",className.getSelectedItem().toString());
		
		for(Entry<String,String> e : SensorConfig.classes().entrySet()) {
			if(e.getValue().equals(className.getSelectedItem().toString())) {
				sensorConfig.setClassName(e.getKey());
			}
		}
		
		sensorConfig.setRef(ref.getText().toString());
		sensorConfig.setName(name.getText().toString());
		sensorConfig.setUnits(units.getText().toString());
		sensorConfig.setSteps(Integer.parseInt(steps.getText().toString()));
		
		if(((RadioButton) findViewById(R.id.optionPinYes)).isChecked())
			sensorConfig.setPin(Integer.parseInt(pin.getText().toString()));
		else
			sensorConfig.setPin(null);
		
		if(((RadioButton) findViewById(R.id.optionTransformYes)).isChecked())
			sensorConfig.setTransform(function.getText().toString());
		else
			sensorConfig.setTransform(null);
		
		startActivity(new Intent(this, ConfigActivity.class));
	}
	
}
