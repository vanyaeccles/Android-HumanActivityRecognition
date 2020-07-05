package com.fitreo.wisdmtestapp;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;


/**
 * Created by vanya on 20-Apr-18.
 *
 * This class maintains access to the device accelerometer and stores the values in a limited size queue
 *
 */

public class AccelerometerHandler implements SensorEventListener {

    Context context;

    private int QUEUE_SIZE = 200;
    private int ACC_SAMPLING_DELAY = 50000; // wisdm sampling rate is 20Hz

    private final SensorManager mSensorManager;
    private final Sensor mAccelerometer;
    public LimitedSizeQueue<Float> dataQueueX;
    public LimitedSizeQueue<Float> dataQueueY;
    public LimitedSizeQueue<Float> dataQueueZ;



    public AccelerometerHandler(Context context) {
        this.context = context;
        mSensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor( Sensor.TYPE_ACCELEROMETER );
        mSensorManager.registerListener( this, mAccelerometer, ACC_SAMPLING_DELAY);
        dataQueueX = new LimitedSizeQueue<>(QUEUE_SIZE);
        dataQueueY = new LimitedSizeQueue<>(QUEUE_SIZE);
        dataQueueZ = new LimitedSizeQueue<>(QUEUE_SIZE);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void onSensorChanged(SensorEvent event) {
        // add the three accelerometer values to the queues


        dataQueueX.add(event.values[0]);
        dataQueueY.add(event.values[1]);
        dataQueueZ.add(event.values[2]);

    }

}
