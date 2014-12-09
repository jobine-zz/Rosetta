package com.mingzhao.rosetta;

import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class MainService extends Service implements SensorEventListener {
    private static final String TAG = "Main Service";
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private DevicePolicyManager mDevicePolicyManager;
    private ComponentName mAdminReceiver;

    @Override
    public void onCreate() {
        Log.d(TAG, "Service onCreate.");
        super.onCreate();

        this.mSensorManager = (SensorManager)this.getSystemService(android.content.Context.SENSOR_SERVICE);
        this.mSensor = this.mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        this.mDevicePolicyManager = (DevicePolicyManager)this.getSystemService(Context.DEVICE_POLICY_SERVICE);
        this.mAdminReceiver = new ComponentName(this, AdminReceiver.class);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Service onDestroy.");
        super.onDestroy();

        this.mSensorManager.unregisterListener(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service onStartCommand.");
        int res = super.onStartCommand(intent, flags, startId);
        this.mSensorManager.registerListener(this, this.mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "Service onBind.");
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float distance = sensorEvent.values[0];

        if (distance < this.mSensor.getMaximumRange()) {
            Toast.makeText(this, "proximity true", Toast.LENGTH_LONG).show();

            suspendDevice();
        } else {
            Toast.makeText(this, "proximity false", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void suspendDevice() {
        if (mDevicePolicyManager.isAdminActive(mAdminReceiver)) {
            Log.i(TAG, "Going to sleep now.");
            mDevicePolicyManager.lockNow();
        } else {
            Log.i(TAG, "Not an admin");
            Toast.makeText(this, R.string.device_admin_not_enabled, Toast.LENGTH_LONG).show();
        }
    }

    private void resumeDevice() {
    }
}