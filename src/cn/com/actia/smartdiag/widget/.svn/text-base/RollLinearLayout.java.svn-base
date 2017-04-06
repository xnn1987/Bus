package cn.com.actia.smartdiag.widget;

import cn.com.actia.smartdiag.beans.RotateAnimation;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.widget.LinearLayout;

/**
 * @author Shaodw
 * @date:2012-11-26
 * @FileName:RollLinearLayout.java
 */
public class RollLinearLayout extends LinearLayout implements
		SensorEventListener {
	private SensorManager mSensorManager;
	private Sensor mSensor;
	private int mNowDegree;
	private int mOldDegree = 36000;
	private RotateAnimation mRotateAnimation;
	private long duration = 300;

	/**
	 * @param context
	 * @param attrs
	 */
	public RollLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		initSensorManager(context);
	}

	/**
	 * @param context
	 */
	public RollLinearLayout(Context context) {
		this(context, null);
	}

	private void initSensorManager(Context context) {
		initAnimation();
		mSensorManager = (SensorManager) context
				.getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mSensorManager.registerListener(this, mSensor,
				SensorManager.SENSOR_DELAY_UI);
	}

	private void initAnimation() {
		mRotateAnimation = new RotateAnimation(mOldDegree, mNowDegree,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		mRotateAnimation.setDuration(duration);
		mRotateAnimation.setFillEnabled(true);
		mRotateAnimation.setFillBefore(true);
		mRotateAnimation.setFillAfter(true);
	}

	private void startAnimation() {
		mRotateAnimation.setmFromDegrees(mOldDegree);
		mRotateAnimation.setmToDegrees(mNowDegree);
		startAnimation(mRotateAnimation);
		mOldDegree = mNowDegree;
	}

	@Override
	public void draw(Canvas canvas) {

		super.draw(canvas);
		Paint paint = new Paint();
		paint.setColor(Color.BLACK);
		paint.setTextSize(55);
		canvas.drawText("asdfas", 12, 50, paint);
	}

	public void onSensorChanged(SensorEvent event) {
		float x = event.values[0];
		float y = event.values[1];
		if (x > 5 && y > 5) {
			return;
		}
		if (x < -5 && y < -5) {
			return;
		}
		if (x > 5 && y < -5) {
			return;
		}
		if (x < -5 && y > 5) {
			return;
		}

		/* ÓÒ²àÌ§Æð */
		if (x > 5 && mOldDegree % 360 != 90) {
//			System.out.println("1");
			// if (mOldDegree % 360 == 0) {
			// mNowDegree = mOldDegree - 90;
			// } else {
			// mNowDegree = mOldDegree + 90;
			// }
			if (mOldDegree % 360 == 0) {
				mNowDegree = mOldDegree + 90;
			} else if (mOldDegree % 360 == 180) {
				mNowDegree = mOldDegree - 90;
			} else {
				mNowDegree = mOldDegree - 180;
			}
			startAnimation();
			return;

		}
		/* ×ó²àÌ§Æð */
		if (x < -5 && mOldDegree % 360 != 270) {
			System.out.println("2");
			if (mOldDegree % 360 == 0) {
				mNowDegree = mOldDegree - 90;
			} else if (mOldDegree % 360 == 180) {
				mNowDegree = mOldDegree + 90;
			} else {
				mNowDegree = mOldDegree - 180;
			}
			startAnimation();
			return;
		}
		/* ¶¥¶ËÌ§Æð */
		if (y > 5 && mOldDegree % 360 != 0) {
			System.out.println("3");
			if (mOldDegree % 360 == 90) {
				mNowDegree = mOldDegree - 90;
			} else if (mOldDegree % 360 == 270) {
				mNowDegree = mOldDegree + 90;
			} else {
				mNowDegree = mOldDegree - 180;
			}
			startAnimation();
			return;
		}

		/* µ×²¿Ì§Æð */
		if (y < -5 && mOldDegree % 360 != 180) {
			System.out.println("4");
			if (mOldDegree % 360 == 90) {
				mNowDegree = mOldDegree + 90;
			} else if (mOldDegree % 360 == 270) {
				mNowDegree = mOldDegree - 90;
			} else {
				mNowDegree = mOldDegree - 180;
			}
			startAnimation();
			return;
		}

	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

}
