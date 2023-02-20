package com.example.compass;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    ImageView dial_compass, n_sign;
    TextView direction_txt;

    private SensorManager mSensorManager;
    Sensor accelerometer;
    Sensor magnetometer;
    float azimuth = 0f;
    float azimuth_current = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dial_compass = findViewById(R.id.dial_compass);
        n_sign = findViewById(R.id.n_sign);
        direction_txt = findViewById(R.id.direction_txt);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

    }
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this,magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    float[] mGravity = new float[3];
    float[] mGeomagnetic = new float[3];
    public void onSensorChanged(SensorEvent event) {
        final float alpha = 0.9f;
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            //mGravity = event.values;
            mGravity[0] = alpha * mGravity[0] + (1 - alpha) * event.values[0];
            mGravity[1] = alpha * mGravity[1] + (1 - alpha) * event.values[1];
            mGravity[2] = alpha * mGravity[2] + (1 - alpha) * event.values[2];

        }
        if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            mGeomagnetic = event.values;


        }
        if(mGravity != null && mGeomagnetic != null){
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            if(success){
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                azimuth = (float)Math.toDegrees(orientation[0]);
                float azimuth_round = (azimuth+360)%360;
                int degree = (int)azimuth_round;
                direction_txt.setText(Integer.toString(degree) + '\u00B0' + " " + degreeConverter(degree));

                RotateAnimation animation = new RotateAnimation(azimuth_current,-azimuth,Animation.RELATIVE_TO_SELF,0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                RotateAnimation animation_N = new RotateAnimation(azimuth_current,-azimuth,Animation.RELATIVE_TO_SELF,0.5f, Animation.RELATIVE_TO_SELF, 3.75f);
                animation.setDuration(250);
                animation.setRepeatCount(0);
                animation.setFillAfter(true);

                animation_N.setDuration(250);
                animation_N.setRepeatCount(0);
                animation_N.setFillAfter(true);

                dial_compass.startAnimation(animation);
                n_sign.startAnimation(animation_N);

                azimuth_current = -azimuth;
            }
        }
    }
    public String degreeConverter(int windDegree){
        String direction = "";
        if(windDegree>=0 && windDegree<=23 || windDegree>=337 && windDegree<=360){
            direction = " North";
        }else if( windDegree>=24 && windDegree<=68){
            direction = " Northeast";
        }else if( windDegree>=69 && windDegree<=113){
            direction = " East";
        }else if( windDegree>=114 && windDegree<=158){
            direction = " Southeast";
        }else if( windDegree>=159 && windDegree<=203){
            direction = " South";
        }else if( windDegree>=204 && windDegree<=248){
            direction = " Southwest";
        }else if( windDegree>=249 && windDegree<=293){
            direction = " West";
        }else if( windDegree>=294 && windDegree<=336){
            direction = " Northwest";
        }
        return direction;
    }
}
