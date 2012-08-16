package test;

import java.util.Date;

import models.*;

import org.junit.*;

import utils.yaml.*;

import play.mvc.*;
import play.test.*;
import play.data.validation.Constraints;
import play.libs.F.*;

import static play.test.Helpers.*;
import static org.fest.assertions.Assertions.*;

public class CRUDTest {
	
	@Test
	public void createObjects(){
		 running(fakeApplication(inMemoryDatabase()), new Runnable() {
		       public void run() {
		    	    // NODECONFIG
		    	   	assertThat(NodeConfig.find.findRowCount()).isEqualTo(0);
		    	   
					NodeConfig nc = new NodeConfig();
					
					nc.setName("node0");
					nc.setDescription("description");
					nc.setSamplingPeriod(1000);
					nc.save();
					
					assertThat(NodeConfig.find.findRowCount()).isEqualTo(1);
					
					//SENSORCONFIG
					SensorConfig sc = new SensorConfig();
					sc.setIsSample(false);
					sc.setClassName("Analog");
					sc.setName("temp00");
					sc.setPin(13);
					sc.setSteps(2);
					sc.setNodeConfig(nc);
					nc.addSensorConfig(sc);
					nc.save();
					
					NodeConfig nc2 = new NodeConfig();
					nc2 = NodeConfig.find.byId(Long.valueOf(1));
					assertThat(nc2.getName()).isEqualTo("node0");
					assertThat(nc2.getSensorConfigs().size()).isEqualTo(1);
					
					//MEASUREMENTS
					Measurement m = new Measurement();
					m.setTimestamp(new Date());
					m.setValue(3.5f);
					
					/*
					 * Updating measurements?? memory usage??
					 */
					
		       }
		 });
	}
}
