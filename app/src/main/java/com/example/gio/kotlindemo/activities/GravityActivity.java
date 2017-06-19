package com.example.gio.kotlindemo.activities;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.example.gio.kotlindemo.R;

/**
 * Copyright by Gio.
 * Created on 6/12/2017.
 */

public class GravityActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private ImageView imgGravity;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gravity);

        imgGravity = (ImageView) findViewById(R.id.imgGravity);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE_UNCALIBRATED);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float x = Math.round(sensorEvent.values[1]) * 10;
        float y = Math.round(sensorEvent.values[0]) * 10;
        imgGravity.setX(imgGravity.getX() + x);
        imgGravity.setY(imgGravity.getY() + y);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
