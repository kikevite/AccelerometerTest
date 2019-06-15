package vite.kike.accelerometertest;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final String LOG_TAG = "kike";
    // Sensor
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;

    // Vibracio
    private Vibrator v;

    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 500;

    @Override
    protected void onPause() {
        super.onPause();
        senSensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;
        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // get velocity of each sensor in m/s^2
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            long curTime = System.currentTimeMillis();
            int time = 100; // in ms
            // calculate velocity every 100ms
            if ((curTime - lastUpdate) > time) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;
                float speed = Math.abs(x + y + z - last_x - last_y - last_z)/ diffTime * time*time;
                if (speed > SHAKE_THRESHOLD) {
                    startVibration(time);
                } else {
                    stopVibration();
                }
                last_x = x;
                last_y = y;
                last_z = z;
            }
        }
    }

    private void stopVibration() {
        v.cancel();
    }

    private void startVibration(int time) {
        long[] pattern = {0, time, 0}; //start delay, duration, stop delay
        v.vibrate(pattern, 0);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }
}
