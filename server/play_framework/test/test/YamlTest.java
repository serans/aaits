package test;

import java.io.FileNotFoundException;
import java.text.ParseException;

import models.NodeConfig;
import models.SensorConfig;

import org.junit.*;

import utils.yaml.*;

import play.Play;
import play.mvc.*;
import play.test.*;
import play.libs.F.*;

import static play.test.Helpers.*;
import static org.fest.assertions.Assertions.*;

public class YamlTest {

	@Test
	public void testYamlInterpreter() throws FileNotFoundException,
			ParseException {
		running(fakeApplication(inMemoryDatabase()), new Runnable() {
			public void run() {
				String url = Play.application().path()
						+ "/test/test_files/data_file.yml";
				YamlInterpreter yi = new YamlInterpreter();
				try {
					NodeConfig nc = yi.readDataFile(url);
					assertThat(nc.deviceUid).isEqualTo(1);
					assertThat(nc.name).isEqualTo("buoy_test");
					assertThat(nc.samplingPeriod).isEqualTo(1000);
					assertThat(nc.sensorConfigs.size()).isEqualTo(2);
					
					SensorConfig sc = nc.sensorConfigs.get(0);
					assertThat(sc.internal_id).isEqualTo(0);
					assertThat(sc.className).isEqualTo("Analog");
					assertThat(sc.name).isEqualTo("TempS");
					
					assertThat(sc.measurements.size()).isEqualTo(12);
					assertThat(sc.measurements.get(0).value).isEqualTo(434.0f);
					
				} catch (Exception e) {
				}
			}
		});
	}
	
	@Test
	public void readAndSave() throws FileNotFoundException,
			ParseException {
		running(fakeApplication(inMemoryDatabase()), new Runnable() {
			public void run() {
				String url = Play.application().path()
						+ "/test/test_files/data_file.yml";
				YamlInterpreter yi = new YamlInterpreter();
				try {
					NodeConfig nc_ini = yi.readDataFile(url);
					nc_ini.save();
					NodeConfig nc = NodeConfig.find.byId(1l);
					assertThat(nc.deviceUid).isEqualTo(1);
					assertThat(nc.name).isEqualTo("buoy_test");
					assertThat(nc.samplingPeriod).isEqualTo(1000);
					assertThat(nc.sensorConfigs.size()).isEqualTo(2);
					
					SensorConfig sc = nc.sensorConfigs.get(0);
					assertThat(sc.internal_id).isEqualTo(0);
					assertThat(sc.className).isEqualTo("Analog");
					assertThat(sc.name).isEqualTo("TempS");
					
					assertThat(sc.measurements.size()).isEqualTo(12);
					assertThat(sc.measurements.get(0).value).isEqualTo(434.0f);
					
				} catch (Exception e) {
				}
			}
		});
	}
	
}