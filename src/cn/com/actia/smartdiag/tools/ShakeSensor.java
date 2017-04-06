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

	/** ������ֵ��false��ʾLightSensor������,��t���� */
	public static boolean init(Context context, onShake onShake) {
		mOnShake = onShake;
		
		mSensorManager = (SensorManager) context
				.getSystemService(Context.SENSOR_SERVICE);
		SharedPreferences mPreference;
		mPreference = context.getSharedPreferences(
				Constants.SHAREPREFERECE_SETTING, Context.MODE_PRIVATE);
		isShakeEnable = mPreference.getBoolean(IS_SHAKE_ENABLE, true);

		// ��ʾ��֧�ָ�Ӧ�����Ѿ������Ӧ��ȥ��
		if (null == mSensorManager) {
			isShakeEnable = false;
			return false;
		}
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		/* ��ʾ��֧�ֹ�У��Ѿ������ȥ�� */
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

			// values[0]:X�ᣬvalues[0]��Y�ᣬvalues[0]��Z��
			float[] values = event.values;

			if (sensorType == Sensor.TYPE_ACCELEROMETER) {
				/*
				 * ��Ϊһ����������£���������ֵ������9.8~10֮�䣬ֻ������ͻȻҡ���ֻ���ʱ��˲ʱ���ٶȲŻ�ͻȻ�������١�
				 * ���ԣ�����ʵ�ʲ��ԣ�ֻ�������һ��ļ��ٶȴ���14��ʱ�򣬸ı�����Ҫ�����þ�OK��~~~
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
