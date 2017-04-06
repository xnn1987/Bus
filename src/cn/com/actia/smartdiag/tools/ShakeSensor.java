package cn.com.actia.smartdiag.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import cn.com.actia.smartdiag.constants.Constants;

public class ShakeSensor {
	public static final String IS_SHAKE_ENABLE = "is_shake_enable";

	private static Sensor mSensor;
	private static SensorManager mSensorManager;
	public static onShake mOnShake;

	private static boolean isShakeEnable;

	/** 若返回值是false表示LightSensor不可用,否則可用 */
	public static boolean init(Context context, onShake onShake) {
		mOnShake = onShake;
		
		mSensorManager = (SensorManager) context
				.getSystemService(Context.SENSOR_SERVICE);
		SharedPreferences mPreference;
		mPreference = context.getSharedPreferences(
				Constants.SHAREPREFERECE_SETTING, Context.MODE_PRIVATE);
		isShakeEnable = mPreference.getBoolean(IS_SHAKE_ENABLE, true);

		// 表示不支持感应器，已经将光感应器去掉
		if (null == mSensorManager) {
			isShakeEnable = false;
			return false;
		}
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		/* 表示不支持光感，已经将光感去掉 */
		if (null == mSensor) {
			isShakeEnable = false;
			return false;
		}
		if (isShakeEnable) {
			mSensorManager.registerListener(mSensorEventListener, mSensor,
					SensorManager.SENSOR_DELAY_UI);
		}
		return isShakeEnable;
	}

	public static void unregisterSensor() {
		if (null != mSensorManager) {
			mSensorManager.unregisterListener(mSensorEventListener, mSensor);
		}
	}

	private static SensorEventListener mSensorEventListener = new SensorEventListener() {

		public void onSensorChanged(SensorEvent event) {
			int sensorType = event.sensor.getType();

			// values[0]:X轴，values[0]：Y轴，values[0]：Z轴
			float[] values = event.values;

			if (sensorType == Sensor.TYPE_ACCELEROMETER) {
				/*
				 * 因为一般正常情况下，任意轴数值最大就在9.8~10之间，只有在你突然摇动手机的时候，瞬时加速度才会突然增大或减少。
				 * 所以，经过实际测试，只需监听任一轴的加速度大于14的时候，改变你需要的设置就OK了~~~
				 */
				if ((Math.abs(values[0]) > 14 || Math.abs(values[1]) > 14 || Math
						.abs(values[2]) > 14)) {
					if (null != mOnShake && isShakeEnable) {
						mOnShake.onShakeListener();
					}
				}
			}
		}

		public void onAccuracyChanged(Sensor sensor, int accuracy) {

		}
	};

}
