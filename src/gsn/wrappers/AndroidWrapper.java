package gsn.wrappers;

import gsn.beans.AddressBean;
import gsn.beans.DataField;
import gsn.beans.StreamElement;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import org.apache.log4j.Logger;

/**
 * This wrapper presents a MultiFormat protocol in which the data comes from the
 * system clock. Think about a sensor network which produces packets with
 * several different formats. In this example we have 3 different packets
 * produced by three different types of sensors. Here are the packet structures
 * : [temperature:double] , [light:double] , [temperature:double, light:double]
 * The first packet is for sensors which can only measure temperature while the
 * latter is for the sensors equipped with both temperature and light sensors.
 * 
 */
public class AndroidWrapper extends AbstractWrapper {
	private DataField[] collection;
	private final transient Logger logger = Logger
			.getLogger(AndroidWrapper.class);
	private int counter;
	private AddressBean params;
	private long rate = 1000;
	// New
	// ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

	private boolean[] availableSensors = new boolean[] { false, false, false,
			false, false, false, false, false, false, false, false, false,
			false };

	// ------------Acceleration-------------------------

	private double accelerationX_axis_incl_gravity = 0.0;
	private double accelerationY_axis_incl_gravity = 0.0;
	private double accelerationZ_axis_incl_gravity = 0.0;

	// ------------Gravity-------------------------
	private double gravityX_axis = 0.0;
	private double gravityY_axis = 0.0;
	private double gravityZ_axis = 0.0;

	// ------------Gyroscope-----------------------
	private double rotationX_axis = 0.0;
	private double rotationY_axis = 0.0;
	private double rotationZ_axis = 0.0;

	// ------------LinearAcceleration--------------
	private double accelerationX_axis_excl_gravity = 0.0;
	private double accelerationY_axis_excl_gravity = 0.0;
	private double accelerationZ_axis_excl_gravity = 0.0;

	// ------------RotationVector------------------
	private double rotationVectorX_axis = 0.0;
	private double rotationVectorY_axis = 0.0;
	private double rotationVectorZ_axis = 0.0;
	private double rotationVectorScalar = 0.0;

	// ------------MagneticField-------------------
	private double geomagneticFieldX_axis = 0.0;
	private double geomagneticFieldY_axis = 0.0;
	private double geomagneticFieldZ_axis = 0.0;

	// ------------Orientation---------------------
	private double azimuthX_axis = 0.0;
	private double pitchY_axis = 0.0;
	private double rollZ_axis = 0.0;

	// ------------Proximity-----------------------
	private double proximityDistance = 0.0;

	// ------------AmbientTemperature--------------
	private double ambientAirTemperature = 0.0;

	// ------------Light-----------------------
	private double light = 0.0;

	// ------------Pressure-----------------------
	private double ambientAirPressure = 0.0;

	// ------------Humidity-----------------------
	private double ambientRelativeHumidity = 0.0;
	// ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
	private boolean[] enabledSensors;
	private DataField[] sensorData;
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	public static final int SERVERPORT = 22005;
	private ServerSocket serverSocket;
	private ArrayList<Double> sensorDataCollection;
	private static PrintWriter out;
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	public boolean initialize() {
		// ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

		try {
			serverSocket = new ServerSocket(SERVERPORT);
			Socket client = serverSocket.accept();

			// If same client then DO follow client == null or client == client

			BufferedReader in = new BufferedReader(new InputStreamReader(
					client.getInputStream()));
			String sensorDataPacket = null;
			String[] sensorData = null;
			String metaData = null;

			enabledSensors = new boolean[] { false, false, false, false, false,
					false, false, false, false, false, false, false, false };
			sensorDataPacket = in.readLine();
			sensorData = sensorDataPacket.split(":");

			metaData = sensorData[0];
			enabledSensors = convertCharValuesToBoolean(metaData);
			collection = createDataFieldCollection(enabledSensors);

			if (sensorData.length > 1) {
				mapSensorData(sensorData, availableSensors);

				System.out.println(sensorDataPacket);
			}

			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
					client.getOutputStream())), true);
			out.println("1");
			System.out.println(metaData);

			
			
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

		// collection = createDataFieldCollection();
		setName("MultiFormatWrapper" + counter++);

		params = getActiveAddressBean();

		if (params.getPredicateValue("rate") != null) {
			rate = (long) Integer.parseInt(params.getPredicateValue("rate"));

			logger.info("Sampling rate set to "
					+ params.getPredicateValue("rate") + " msec.");
		}

		return true;
	}

	private boolean[] convertCharValuesToBoolean(String metaData) {

		for (int i = 0; i < metaData.length(); i++) {
			if (metaData.charAt(i) == '1') {
				enabledSensors[i + 1] = true;
			} else {
				enabledSensors[i + 1] = false;
			}
		}

		return enabledSensors;
	}

	private DataField[] createDataFieldCollection(boolean[] enabledSensors) {

		ArrayList<DataField> dataFieldsCollection = new ArrayList<DataField>();


		if (enabledSensors[1]) {

			DataField accelerationX_axis_incl_gravity = new DataField(
					"accelerationX_axis_incl_gravity", "double",
					"Acceleration force along the x axis (including gravity).");
			DataField accelerationY_axis_incl_gravity = new DataField(
					"accelerationY_axis_incl_gravity", "double",
					"Acceleration force along the y axis (including gravity).");
			DataField accelerationZ_axis_incl_gravity = new DataField(
					"accelerationZ_axis_incl_gravity", "double",
					"Acceleration force along the z axis (including gravity).");

			dataFieldsCollection.add(accelerationX_axis_incl_gravity);
			dataFieldsCollection.add(accelerationY_axis_incl_gravity);
			dataFieldsCollection.add(accelerationZ_axis_incl_gravity);
		}
		if (enabledSensors[2]) {

			DataField gravityX_axis = new DataField("gravityX_axis", "double",
					"Force of gravity along the x axis.");
			DataField gravityY_axis = new DataField("gravityY_axis", "double",
					"Force of gravity along the y axis.");
			DataField gravityZ_axis = new DataField("gravityZ_axis", "double",
					"Force of gravity along the z axis.");

			dataFieldsCollection.add(gravityX_axis);
			dataFieldsCollection.add(gravityY_axis);
			dataFieldsCollection.add(gravityZ_axis);

		}
		if (enabledSensors[3]) {

			DataField rotationX_axis = new DataField("rotationX_axis",
					"double",
					"Acceleration force along the x axis (excluding gravity).");
			DataField rotationY_axis = new DataField("rotationY_axis",
					"double",
					"Acceleration force along the y axis (excluding gravity).");
			DataField rotationZ_axis = new DataField("rotationZ_axis",
					"double",
					"Acceleration force along the z axis (excluding gravity).");

			dataFieldsCollection.add(rotationX_axis);
			dataFieldsCollection.add(rotationY_axis);
			dataFieldsCollection.add(rotationZ_axis);

		}
		if (enabledSensors[4]) {

			DataField accelerationX_axis_excl_gravity = new DataField(
					"accelerationX_axis_excl_gravity", "double",
					"Acceleration force along the x axis (excluding gravity).");
			DataField accelerationY_axis_excl_gravity = new DataField(
					"accelerationY_axis_excl_gravity", "double",
					"Acceleration force along the y axis (excluding gravity).");
			DataField accelerationZ_axis_excl_gravity = new DataField(
					"accelerationZ_axis_excl_gravity", "double",
					"Acceleration force along the z axis (excluding gravity).");

			dataFieldsCollection.add(accelerationX_axis_excl_gravity);
			dataFieldsCollection.add(accelerationY_axis_excl_gravity);
			dataFieldsCollection.add(accelerationZ_axis_excl_gravity);

		}
		if (enabledSensors[5]) {

			DataField rotationVectorX_axis = new DataField(
					"rotationVectorX_axis", "double",
					"Rotation vector component along the x axis (x * sin(Theta/2)).");
			DataField rotationVectorY_axis = new DataField(
					"rotationVectorY_axis", "double",
					"Rotation vector component along the y axis (y * sin(Theta/2)).");
			DataField rotationVectorZ_axis = new DataField(
					"rotationVectorZ_axis", "double",
					"Rotation vector component along the z axis (z * sin(Theta/2)).");
			DataField rotationVectorScalar = new DataField(
					"rotationVectorScalar", "double",
					"Scalar component of the rotation vector ((cos(Theta/2)).");

			dataFieldsCollection.add(rotationVectorX_axis);
			dataFieldsCollection.add(rotationVectorY_axis);
			dataFieldsCollection.add(rotationVectorZ_axis);
			dataFieldsCollection.add(rotationVectorScalar);

		}
		if (enabledSensors[6]) {

			DataField geomagneticFieldX_axis = new DataField(
					"geomagneticFieldX_axis", "double",
					"Geomagnetic field strength along the x axis.");
			DataField geomagneticFieldY_axis = new DataField(
					"geomagneticFieldY_axis", "double",
					"Geomagnetic field strength along the y axis.");
			DataField geomagneticFieldZ_axis = new DataField(
					"geomagneticFieldZ_axis", "double",
					"Geomagnetic field strength along the z axis.");

			dataFieldsCollection.add(geomagneticFieldX_axis);
			dataFieldsCollection.add(geomagneticFieldY_axis);
			dataFieldsCollection.add(geomagneticFieldZ_axis);

		}
		if (enabledSensors[7]) {

			DataField azimuthX_axis = new DataField("azimuthX_axis", "double",
					"Azimuth (angle around the z-axis).");
			DataField pitchY_axis = new DataField("pitchY_axis", "double",
					"Pitch (angle around the x-axis).");
			DataField rollZ_axis = new DataField("rollZ_axis", "double",
					"Roll (angle around the y-axis).");

			dataFieldsCollection.add(azimuthX_axis);
			dataFieldsCollection.add(pitchY_axis);
			dataFieldsCollection.add(rollZ_axis);

		}
		if (enabledSensors[8]) {

			DataField proximityDistance = new DataField("proximityDistance",
					"double", "proximityDistance");
			dataFieldsCollection.add(proximityDistance);

		}
		if (enabledSensors[9]) {

			DataField ambientAirTemperature = new DataField(
					"ambientAirTemperature", "double",
					"Ambient air temperature.");
			dataFieldsCollection.add(ambientAirTemperature);
		}
		if (enabledSensors[10]) {

			DataField light = new DataField("light", "double", "Illuminance");
			dataFieldsCollection.add(light);
		}
		if (enabledSensors[11]) {

			DataField ambientAirPressure = new DataField("ambientAirPressure",
					"double", "Ambient air pressure.");
			dataFieldsCollection.add(ambientAirPressure);
		}
		if (enabledSensors[12]) {

			DataField ambientRelativeHumidity = new DataField(
					"ambientRelativeHumidity", "double",
					"Ambient relative humidity.");
			dataFieldsCollection.add(ambientRelativeHumidity);
		}

		DataField[] arrayDataFieldsCollection = new DataField[3];

		sensorData = dataFieldsCollection.toArray(arrayDataFieldsCollection);
		return sensorData;
	}

	public void run() {



		while (isActive()) {
			try {
				// delay
				Thread.sleep(rate);
			} catch (InterruptedException e) {
				logger.error(e.getMessage(), e);
			}

			
			Socket client;
			try {
				client = serverSocket.accept();

				BufferedReader in = new BufferedReader(new InputStreamReader(
						client.getInputStream()));

				String sensorDataPacket = null;
				String[] sensorData = null;
				sensorDataPacket = in.readLine();
				sensorData = sensorDataPacket.split(":");

				mapSensorData(sensorData, enabledSensors);

			} catch (IOException e) {
				e.printStackTrace();
			}
		
		
			
			Double[] arraysensorDataCollection = new Double[0];
			Double[] ab;
			ab = sensorDataCollection.toArray(arraysensorDataCollection);
			
			postStreamElement(ab);

		}
	}

	private void mapSensorData(String[] sensorData, boolean[] enabledSensors) {

		int dataSegment = 1;
		resetDataFields();
		sensorDataCollection = new ArrayList<Double>();		
		if (enabledSensors[1]) {
			accelerationX_axis_incl_gravity = Float
					.parseFloat(sensorData[dataSegment++]);
			accelerationY_axis_incl_gravity = Float
					.parseFloat(sensorData[dataSegment++]);
			accelerationZ_axis_incl_gravity = Float
					.parseFloat(sensorData[dataSegment++]);

			sensorDataCollection.add(accelerationX_axis_incl_gravity);
			sensorDataCollection.add(accelerationY_axis_incl_gravity);
			sensorDataCollection.add(accelerationZ_axis_incl_gravity);
		}
		if (enabledSensors[2]) {
			gravityX_axis = Float.parseFloat(sensorData[dataSegment++]);
			gravityY_axis = Float.parseFloat(sensorData[dataSegment++]);
			gravityZ_axis = Float.parseFloat(sensorData[dataSegment++]);

			sensorDataCollection.add(gravityX_axis);
			sensorDataCollection.add(gravityY_axis);
			sensorDataCollection.add(gravityZ_axis);
		}
		if (enabledSensors[3]) {
			rotationX_axis = Float.parseFloat(sensorData[dataSegment++]);
			rotationY_axis = Float.parseFloat(sensorData[dataSegment++]);
			rotationZ_axis = Float.parseFloat(sensorData[dataSegment++]);

			sensorDataCollection.add(rotationX_axis);
			sensorDataCollection.add(rotationY_axis);
			sensorDataCollection.add(rotationZ_axis);

		}
		if (enabledSensors[4]) {
			accelerationX_axis_excl_gravity = Float
					.parseFloat(sensorData[dataSegment++]);
			accelerationY_axis_excl_gravity = Float
					.parseFloat(sensorData[dataSegment++]);
			accelerationZ_axis_excl_gravity = Float
					.parseFloat(sensorData[dataSegment++]);

			sensorDataCollection.add(accelerationX_axis_excl_gravity);
			sensorDataCollection.add(accelerationY_axis_excl_gravity);
			sensorDataCollection.add(accelerationZ_axis_excl_gravity);
		}
		if (enabledSensors[5]) {
			rotationVectorX_axis = Float.parseFloat(sensorData[dataSegment++]);
			rotationVectorY_axis = Float.parseFloat(sensorData[dataSegment++]);
			rotationVectorZ_axis = Float.parseFloat(sensorData[dataSegment++]);
			rotationVectorScalar = Float.parseFloat(sensorData[dataSegment++]);

			sensorDataCollection.add(rotationVectorX_axis);
			sensorDataCollection.add(rotationVectorY_axis);
			sensorDataCollection.add(rotationVectorZ_axis);
			sensorDataCollection.add(rotationVectorScalar);

		}
		if (enabledSensors[6]) {
			geomagneticFieldX_axis = Float
					.parseFloat(sensorData[dataSegment++]);
			geomagneticFieldY_axis = Float
					.parseFloat(sensorData[dataSegment++]);
			geomagneticFieldZ_axis = Float
					.parseFloat(sensorData[dataSegment++]);

			sensorDataCollection.add(geomagneticFieldX_axis);
			sensorDataCollection.add(geomagneticFieldY_axis);
			sensorDataCollection.add(geomagneticFieldZ_axis);
		}
		if (enabledSensors[7]) {
			azimuthX_axis = Float.parseFloat(sensorData[dataSegment++]);
			pitchY_axis = Float.parseFloat(sensorData[dataSegment++]);
			rollZ_axis = Float.parseFloat(sensorData[dataSegment++]);

			sensorDataCollection.add(azimuthX_axis);
			sensorDataCollection.add(pitchY_axis);
			sensorDataCollection.add(rollZ_axis);
		}
		if (enabledSensors[8]) {
			proximityDistance = Float.parseFloat(sensorData[dataSegment++]);
			sensorDataCollection.add(proximityDistance);
		}
		if (enabledSensors[9]) {
			ambientAirTemperature = Float.parseFloat(sensorData[dataSegment++]);
			sensorDataCollection.add(ambientAirTemperature);
		}
		if (enabledSensors[10]) {
			light = Float.parseFloat(sensorData[dataSegment++]);
			sensorDataCollection.add(light);
		}
		if (enabledSensors[11]) {
			ambientAirPressure = Float.parseFloat(sensorData[dataSegment++]);
			sensorDataCollection.add(ambientAirPressure);
		}
		if (enabledSensors[12]) {
			ambientRelativeHumidity = Float.parseFloat(sensorData[dataSegment]);
			sensorDataCollection.add(ambientRelativeHumidity);
		}
				
	}

	private void resetDataFields() {
		// ------------Accelerometer-------------------
		accelerationX_axis_incl_gravity = 0.0f;
		accelerationY_axis_incl_gravity = 0.0f;
		accelerationZ_axis_incl_gravity = 0.0f;

		// ------------Gravity-------------------------
		gravityX_axis = 0.0f;
		gravityY_axis = 0.0f;
		gravityZ_axis = 0.0f;

		// ------------Gyroscope-----------------------
		rotationX_axis = 0.0f;
		rotationY_axis = 0.0f;
		rotationZ_axis = 0.0f;

		// ------------LinearAcceleration--------------
		accelerationX_axis_excl_gravity = 0.0f;
		accelerationY_axis_excl_gravity = 0.0f;
		accelerationZ_axis_excl_gravity = 0.0f;

		// ------------RotationVector------------------
		rotationVectorX_axis = 0.0f;
		rotationVectorY_axis = 0.0f;
		rotationVectorZ_axis = 0.0f;
		rotationVectorScalar = 0.0f;

		// ------------MagneticField-------------------
		geomagneticFieldX_axis = 0.0f;
		geomagneticFieldY_axis = 0.0f;
		geomagneticFieldZ_axis = 0.0f;

		// ------------Orientation---------------------
		azimuthX_axis = 0.0f;
		pitchY_axis = 0.0f;
		rollZ_axis = 0.0f;

		// ------------Proximity-----------------------
		proximityDistance = 0.0f;

		// ------------AmbientTemperature--------------
		ambientAirTemperature = 0.0f;

		// ------------Light-----------------------
		light = 0.0f;

		// ------------Pressure-----------------------
		ambientAirPressure = 0.0f;

		// ------------Humidity-----------------------
		ambientRelativeHumidity = 0.0f;

	}

	public DataField[] getOutputFormat() {
		return collection;
	}

	public String getWrapperName() {
		return "MultiFormat Sample Wrapper";
	}

	public void dispose() {
		counter--;
	}
}
