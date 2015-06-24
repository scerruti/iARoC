package org.jointheleague.nerdherd.iaroc;
/**************************************************************************
 * Simplified version 140512A by Erik  Super Happy Version
 * Vic's commit version 140904A
 * version 150122A AndroidStudio version
 * version 150225B AndroidStudio version
 **************************************************************************/
import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.IOIOConnectionManager;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOActivity;

import java.util.Locale;

import org.wintrisstech.irobot.ioio.IRobotCreateInterface;
import org.wintrisstech.irobot.ioio.SimpleIRobotCreate;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * This is the main activity of the iARoC 2015 application.
 *
 * <p>
 * This class assumes that there are 3 ultrasonic sensors attached to the
 * iRobot. An instance of the Dashboard class will display the readings of these
 * three sensors.
 *
 * <p>
 * There should be no need to modify this class. Modify Brain instead.
 *
 * @author Erik Colban
 *
 */
public class Dashboard extends IOIOActivity implements
        TextToSpeech.OnInitListener, SensorEventListener {

    private static final String LOGCAT_TAG = "iARoC";
    /**
     * Text view that contains all logged messages
     */
    private TextView mText;
    private ScrollView scroller;
    /**
     * A Brain instance
     */
    private Brain kalina;
    /**
     * TTS stuff
     */
    protected static final int MY_DATA_CHECK_CODE = 33;
    private TextToSpeech mTts;
    /**
     * Compass stuff
     */
    SensorManager sensorManager;
    private Sensor sensorAccelerometer;
    private Sensor sensorMagneticField;

    private float[] valuesAccelerometer;
    private float[] valuesMagneticField;

    private float[] matrixR;
    private float[] matrixI;
    private float[] matrixValues;
    private double azimuth;
    private double pitch;
    private double roll;
    public SeekBar slider;
    private TextView speedText;
    private Button drive;
    public int progress = 425;
    public CheckBox bumpBox;

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

        // Compass stuff
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorAccelerometer = sensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorMagneticField = sensorManager
                .getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        valuesAccelerometer = new float[3];
        valuesMagneticField = new float[3];

        matrixR = new float[9];
        matrixI = new float[9];
        matrixValues = new float[3];

        mText = (TextView) findViewById(R.id.text);
        scroller = (ScrollView) findViewById(R.id.scroller);
        slider = (SeekBar) findViewById(R.id.speedBar);
        speedText = (TextView) findViewById(R.id.speedText);

        slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Dashboard.this.progress = progress;
                speedText.setText(Integer.toString(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        drive = (Button) findViewById(R.id.driveButton);
        drive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)  {
                try {
                    kalina.driveDirect(
                            progress,
                            progress);
                    int time = 0;
                    while(!(kalina.isBumpLeft() || kalina.isBumpRight())) {
                        SystemClock.sleep(1);
                        time++;
                    }
                    kalina.driveDirect(0, 0);
                    log("S: " + progress + " T: " + time);
                } catch (ConnectionLostException e) {

                } catch (NullPointerException npe) {

                }
            }
        });
        bumpBox = (CheckBox) findViewById(R.id.bumpBox);
        log(getString(R.string.wait_ioio));

    }

    @Override
    public void onPause() {
        if (kalina != null) {
            log("Pausing");
        }
        sensorManager.unregisterListener(this, sensorAccelerometer);
        sensorManager.unregisterListener(this, sensorMagneticField);
        super.onPause();
    }

    @Override
    protected void onResume() {

        sensorManager.registerListener(this, sensorAccelerometer,
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorMagneticField,
                SensorManager.SENSOR_DELAY_NORMAL);
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

    @SuppressWarnings( "deprecation" )
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
            case Sensor.TYPE_ACCELEROMETER:
                System.arraycopy(event.values, 0, valuesAccelerometer, 0, valuesAccelerometer.length);
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                System.arraycopy(event.values, 0, valuesMagneticField, 0, valuesMagneticField.length);
                break;
        }

        boolean success = SensorManager.getRotationMatrix(matrixR, matrixI,
                valuesAccelerometer, valuesMagneticField);

        if (success) {
            SensorManager.getOrientation(matrixR, matrixValues);
            synchronized (this) {
                azimuth = Math.toDegrees(matrixValues[0]);
                pitch = Math.toDegrees(matrixValues[1]);
                roll = Math.toDegrees(matrixValues[2]);
            }
        }

    }

    /**
     * Gets the azimuth
     * @return the azimuth
     */
    public synchronized double getAzimuth() {
        return azimuth;
    }

    /**
     * Gets the pitch
     * @return the pitch
     */
    public synchronized double getPitch() {
        return pitch;
    }

    /**
     * Gets the roll
     * @return the roll
     */
    public synchronized double getRoll() {
        return roll;
    }

    @Override
    public IOIOLooper createIOIOLooper() {
        return new IOIOLooper() {

            public void setup(IOIO ioio) throws ConnectionLostException,
                    InterruptedException {
				/*
				 * When the setup() method is called the IOIO is connected.
				 */
                log(getString(R.string.ioio_connected));

				/*
				 * Establish communication between the android and the iRobot
				 * Create through the IOIO board.
				 */
                log(getString(R.string.wait_create));
                IRobotCreateInterface iRobotCreate = new SimpleIRobotCreate(
                        ioio);
                log(getString(R.string.create_connected));

				/*
				 * Get a Brain (built on the iRobot Create) and let it go... The
				 * ioio_ instance is passed to the constructor in case it is
				 * needed to establish connections to other peripherals, such as
				 * sensors that are not part of the iRobot Create.
				 */
                kalina = new Brain(ioio, iRobotCreate, Dashboard.this);
                kalina.initialize();

                DragRace dragrace = new DragRace(Dashboard.this);
                dragrace.runMission();
            }

            public void loop() throws ConnectionLostException,
                    InterruptedException {
                kalina.loop();
            }

            public void disconnected() {
                log(getString(R.string.ioio_disconnected));
            }

            public void incompatible() {
            }

            @Override
            public void incompatible(IOIO ioio) {
                log(getString(R.string.ioio_incompatible));
            }
        };
    }

    /**
     * Writes a message to the Dashboard instance.
     *
     * @param msg
     *            the message to write
     */
    public void log(final String msg) {
        Log.i(LOGCAT_TAG, msg);

        runOnUiThread(new Runnable() {

            public void run() {
                mText.append(msg);
                mText.append("\n");
                scroller.smoothScrollTo(0, mText.getBottom());
            }
        });
    }

    public Brain getBrain()
    {
        return kalina;
    }
}