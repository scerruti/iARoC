package org.jointheleague.nerdherd.iaroc;

/**************************************************************************
 * Nerd Herd iARoC Base Code
 *
 * built from:
 * Simplified version 140512A by Erik  Super Happy Version
 **************************************************************************/

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.jointheleague.nerdherd.sensors.UltraSonicSensors;
import org.wintrisstech.irobot.ioio.IRobotCreateInterface;
import org.wintrisstech.irobot.ioio.SimpleIRobotCreate;

import java.util.Locale;

import ioio.lib.api.IOIO;
import ioio.lib.api.IOIO.VersionType;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOActivity;

/**
 * This is the main activity of the iRobot2012 application.
 * 
 * <p>
 * This class assumes that there are 3 ultrasonic sensors attached to the
 * iRobot. An instance of the DashboardOld class will display the readings of these
 * three sensors.
 * 
 * <p>
 * There should be no need to modify this class. Modify Robot instead.
 * 
 * @author Erik Colban
 * 
 */
public class DashboardOld extends IOIOActivity implements
		TextToSpeech.OnInitListener, SensorEventListener {

	/**
	 * Text view that contains all logged messages
	 */
	private TextView mText;
	private ScrollView scroller;
	/**
	 * A Robot instance
	 */
	private Robot mazeRunner;
	/**
	 * TTS stuff
	 */
	protected static final int MY_DATA_CHECK_CODE = 33;
	private TextToSpeech mTts;
	/**
	 * Compass stuff
	 */
	SensorManager sensorManager;
//	private Sensor sensorAccelerometer;
//	private Sensor sensorMagneticField;
	private Sensor rotationSensor;

//	private float[] valuesAccelerometer;
//	private float[] valuesMagneticField;
	private float[] rotationValues;
	
	private float[] matrixR;
//	private float[] matrixI;
//	private float[] matrixValues;
	private float[] orientationValues;
//	private double azimuth;
//	private double pitch;
//	private double roll;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*
		 * Since the android device is carried by the iRobot Create, we want to
		 * prevent a change of orientation, which would cause the activity to
		 * pause.
		 */
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		// getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.main);

		Intent checkIntent = new Intent();
		checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
		startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);

//		// Compass stuff
//		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
//		sensorAccelerometer = sensorManager
//				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//		sensorMagneticField = sensorManager
//				.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

//		valuesAccelerometer = new float[3];
//		valuesMagneticField = new float[3];
		
		// Rotation Vector
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
		rotationValues = new float[3];

//		matrixR = new float[9];
		matrixR = new float[16];
//		matrixI = new float[9];
//		matrixValues = new float[3];
		orientationValues = new float[3];

		mText = (TextView) findViewById(R.id.text);
		scroller = (ScrollView) findViewById(R.id.scroller);
		log(getString(R.string.wait_ioio));

	}

	@Override
	public void onPause() {
		if (mazeRunner != null) {
			log("Pausing");
		}
//		sensorManager.unregisterListener(this, sensorAccelerometer);
//		sensorManager.unregisterListener(this, sensorMagneticField);
		super.onPause();
	}

	@Override
	protected void onResume() {

//		sensorManager.registerListener(this, sensorAccelerometer,
//				SensorManager.SENSOR_DELAY_NORMAL);
//		sensorManager.registerListener(this, sensorMagneticField,
//				SensorManager.SENSOR_DELAY_NORMAL);
		super.onResume();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == MY_DATA_CHECK_CODE) {
			if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
				// success, create the TTS instance
				mTts = new TextToSpeech(this, this);
			} else {
				// missing data, install it
				Intent installIntent = new Intent();
				installIntent
						.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
				startActivity(installIntent);
			}
		}
	}

	public void onInit(int arg0) {
	}

	public void speak(String stuffToSay) {
		mTts.setLanguage(Locale.US);
		if (!mTts.isSpeaking()) {
			mTts.speak(stuffToSay, TextToSpeech.QUEUE_FLUSH, null);
		}
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		switch (event.sensor.getType()) {
//		case Sensor.TYPE_ACCELEROMETER:
//			for (int i = 0; i < 3; i++) {
//				valuesAccelerometer[i] = event.values[i];
//			}
//			break;
//		case Sensor.TYPE_MAGNETIC_FIELD:
//			for (int i = 0; i < 3; i++) {
//				valuesMagneticField[i] = event.values[i];
//			}
//			break;
		case Sensor.TYPE_ROTATION_VECTOR:
	        SensorManager.getRotationMatrixFromVector(matrixR, event.values);
	        SensorManager.remapCoordinateSystem(matrixR, SensorManager.AXIS_X, SensorManager.AXIS_Z, matrixR);
	        SensorManager.getOrientation(matrixR, orientationValues);
	        orientationValues[0]=(float) Math.toDegrees(orientationValues[0]);
	        orientationValues[1]=(float) Math.toDegrees(orientationValues[1]);
	        orientationValues[2]=(float) Math.toDegrees(orientationValues[2]);
	        break;
		}

//		boolean success = SensorManager.getRotationMatrix(matrixR, matrixI,
//				valuesAccelerometer, valuesMagneticField);
//
//		if (success) {
//			SensorManager.getOrientation(matrixR, matrixValues);
//			synchronized (this) {
//				azimuth = Math.toDegrees(matrixValues[0]);
//				pitch = Math.toDegrees(matrixValues[1]);
//				roll = Math.toDegrees(matrixValues[2]);
//			}
//		}

	}

	/**
	 * Gets the azimuth
	 * 
	 * @return the azimuth
	 */
	public synchronized double getAzimuth() {
		return orientationValues[0];
	}

	/**
	 * Gets the pitch
	 * 
	 * @return the pitch
	 */
	public synchronized double getPitch() {
		return orientationValues[0];
	}

	/**
	 * Gets the roll
	 * 
	 * @return the roll
	 */
	public synchronized double getRoll() {
		return orientationValues[0];
	}

	class Looper extends BaseIOIOLooper {

        private UltraSonicSensors uss;

        @Override
		public void setup() throws ConnectionLostException,
				InterruptedException {
			/*
			 * When the setup() method is called the IOIO is connected.
			 */
			showVersions(ioio_, "IOIO connected!");
			log(getString(R.string.ioio_connected));

			/*
			 * Establish communication between the android and the iRobot Create
			 * through the IOIO board.
			 */
			log(getString(R.string.wait_create));
			IRobotCreateInterface iRobotCreate = new SimpleIRobotCreate(ioio_);
			log(getString(R.string.create_connected));

			/*
			 * Get a Robot (built on the iRobot Create) and let it go... The
			 * ioio_ instance is passed to the constructor in case it is needed
			 * to establish connections to other peripherals, such as sensors
			 * that are not part of the iRobot Create.
			 */
			mazeRunner = new Robot(ioio_, iRobotCreate, DashboardOld.this);
			mazeRunner.initialize();
            log("BEFORE USS");
            Thread.sleep(1000, 0);
            try {
                uss = new UltraSonicSensors(this.ioio_);
            } catch (Exception e) {
                log(e.getMessage());
                Thread.sleep(2000, 0);
            }
            log("AFTER USS");
            Thread.sleep(1000, 0);
		}

		@Override
		public void loop() throws ConnectionLostException, InterruptedException {
			mazeRunner.loop(uss);

		}

		@Override
		public void disconnected() {
            uss.closeConnection();
            log(getString(R.string.ioio_disconnected));
		}

		@Override
		public void incompatible() {
			showVersions(ioio_, "Incompatible firmware version!");
		}
	}

	/**
	 * A method to create our IOIO thread.
	 * 
	 * @see ioio.lib.util.AbstractIOIOActivity#createIOIOThread()
	 */
	@Override
	protected IOIOLooper createIOIOLooper() {
		return new Looper();
	}

	/**
	 * Writes a message to the DashboardOld instance.
	 * 
	 * @param msg
	 *            the message to write
	 */
	public void log(final String msg) {
		runOnUiThread(new Runnable() {

			public void run() {
				mText.append(msg);
				mText.append("\n");
				scroller.smoothScrollTo(0, mText.getBottom());
			}
		});
	}

	private void showVersions(IOIO ioio, String title) {
		toast(String.format("%s\n" + "IOIOLib: %s\n"
						+ "Application firmware: %s\n" + "Bootloader firmware: %s\n"
						+ "Hardware: %s", title,
				ioio.getImplVersion(VersionType.IOIOLIB_VER),
				ioio.getImplVersion(VersionType.APP_FIRMWARE_VER),
				ioio.getImplVersion(VersionType.BOOTLOADER_VER),
				ioio.getImplVersion(VersionType.HARDWARE_VER)));
	}

	private void toast(final String message) {
		final Context context = this;
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(context, message, Toast.LENGTH_LONG).show();
			}
		});
	}

}
