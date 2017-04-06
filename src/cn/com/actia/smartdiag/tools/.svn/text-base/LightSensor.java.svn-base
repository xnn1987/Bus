package cn.com.actia.smartdiag.tools;

import java.util.Calendar;

import cn.com.actia.smartdiag.constants.Constants;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.preference.PreferenceManager;

public class LightSensor {
	public static final String LIGHT_SENSOR_PREFERECE = "light_sensor_prefercec";
	public static final String IS_TIME_ENABLE = "is_time_enable";
	public static final String IS_LIGHT_ENABLE = "is_light_enable";
	public static final String SWITCH_TO_NIGHT = "switch_to_night";
	public static final String SWITCH_TO_DAY = "switch_to_day";
	public static final String SWITCH_LIGHT = "switch_light";

	public static final int STATE_DAY = 1;
	public static final int STATE_NIGHT = 2;
	public static final int STATE_UPTOSENSOR = 0;

	private static Sensor mSensor;
	private static SensorManager mSensorManager;

	private static boolean isLightEnable;
	private static boolean isTimeEnable;
	private static int mSwitchToNight;
	private static int mSwitchToDay;
	private static int mSwitchLight;

	private static boolean isShakeEnable;

	/** 若返回值是false表示LightSensor不可用,否則可用 */
	public static boolean init(Context context, onShake onShake) {
		mShakelistener = onShake;
		mSensorManager = (SensorManager) context
				.getSystemService(Context.SENSOR_SERVICE);
		SharedPreferences mPreference;
		mPreference = PreferenceManager.getDefaultSharedPreferences(context);
		isLightEnable = mPreference.getBoolean(IS_LIGHT_ENABLE, true);
		isTimeEnable = mPreference.getBoolean(IS_TIME_ENABLE, true);
		mSwitchToDay = Integer.parseInt(mPreference.getString(SWITCH_TO_DAY, "5"));
		mSwitchToNight = Integer.parseInt(mPreference.getString(SWITCH_TO_NIGHT, "17"));
		mSwitchLight = Integer.parseInt(mPreference.getString(SWITCH_LIGHT, "20"));
		isShakeEnable = mPreference.getBoolean(ShakeSensor.IS_SHAKE_ENABLE,
				true);

		// 表示不支持感应器，已经将光感应器去掉
		if (null == mSensorManager) {
			isLightEnable = false;
			return false;
		}
		// mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ALL);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
		System.out.println(mSensor);
		/* 表示不支持光感，已经将光感去掉 */
		if (null == mSensor) {
			isLightEnable = false;
			return false;
		}
		return true;
	}

	public static boolean reSet(Context context, onShake onShake) {
		return init(context, onShake);
	}

	static int state = STATE_DAY;
	static int timer = 0;
	static int postNum = 0;

	static onLightChange listener;
	static onShake mShakelistener;

	public static void removeLightListen() {
		listener = null;
		mSensorManager.unregisterListener(l, mSensor);
	}

	static SensorEventListener l = new SensorEventListener() {

		public void onSensorChanged(SensorEvent event) {
			int sensorType = event.sensor.getType();

			if (sensorType == Sensor.TYPE_ACCELEROMETER) {
				float[] values = event.values;
				System.out.println("Sensor.TYPE_ACCELEROMETER");
				/*
				 * 因为一般正常情况下，任意轴数值最大就在9.8~10之间，只有在你突然摇动手机的时候，瞬时加速度才会突然增大或减少。
				 * 所以，经过实际测试，只需监听任一轴的加速度大于14的时候，改变你需要的设置就OK了~~~
				 */
				if ((Math.abs(values[0]) > 14 || Math.abs(values[1]) > 14 || Math
						.abs(values[2]) > 14)) {
					if (null != mShakelistener && isShakeEnable) {
						mShakelistener.onShakeListener();
					}
				}
			}

			if (sensorType == Sensor.TYPE_LIGHT) {
				Utils.showTag("++++" + event.values[0]);
				if (event.values[0] >= mSwitchLight && state != STATE_DAY) {
					state = STATE_DAY;
					timer++;
					postNum++;
					new Handler().postDelayed(new Runnable() {
						public void run() {
							postNum--;
							if (postNum == 0 && timer != 0) {
								if (null != listener && isLightEnable
										&& isTimeEnable) {
									listener.onLightStateChange(state);
								}
								timer = 0;
							}

						}
					}, 2000);

				} else if (event.values[0] < mSwitchLight
						&& state != STATE_NIGHT && isLightEnable
						&& isTimeEnable) {
					Utils.showTag("~~~~" + event.values[0]);
					state = STATE_NIGHT;
					timer--;
					postNum++;
					new Handler().postDelayed(new Runnable() {
						public void run() {
							postNum--;
							if (postNum == 0 && timer != 0) {
								if (null != listener)
									listener.onLightStateChange(state);
								timer = 0;
							}

						}
					}, 2000);
				}

			}
		}

		public void onAccuracyChanged(Sensor sensor, int accuracy) {

		}
	};

	/**
	 * 用这个前必须要使用init（）
	 * 
	 * @throws Exception
	 */
	public static int getState(final onLightChange listener) {
		// 如果可用，则需要判断，如果不可以用，则直接返回默认值白天
		LightSensor.listener = listener;
		if (isTimeEnable) {
			Calendar calendar = Calendar.getInstance();
			int hour = calendar.get(Calendar.HOUR_OF_DAY);
			/* 如果大于转换到晚上的时间,则为晚上形式 */
			if (hour >= mSwitchToNight || hour < mSwitchToDay) {
				return STATE_NIGHT;
			} else {
				if (isLightEnable) {
					// 需要支持光感模式，如果手机不支持光感
					if (null == mSensor) {
						try {
							throw new Exception();
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						if (isShakeEnable) {
							mSensorManager.registerListener(l, mSensor,
									SensorManager.SENSOR_DELAY_FASTEST);
						} else {
							mSensorManager.registerListener(l, mSensor,
									SensorManager.SENSOR_DELAY_FASTEST);
						}
						return STATE_UPTOSENSOR;
					}
				} else {
					mSensorManager.registerListener(l, mSensor,
							SensorManager.SENSOR_DELAY_UI);
				}
			}
		} else {
			mSensorManager.registerListener(l, mSensor,
					SensorManager.SENSOR_DELAY_UI);
		}
		return STATE_DAY;
	}
}
