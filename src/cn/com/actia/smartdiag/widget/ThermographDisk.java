package cn.com.actia.smartdiag.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.widget.RelativeLayout;
import cn.com.actia.smartdiag.R;
import cn.com.actia.smartdiag.beans.RotateAnimation;
import cn.com.actia.smartdiag.beans.VehicleDataBean;
import cn.com.actia.smartdiag.constants.FlashConstants;

/*
 * Author: pancheng Email:gdpancheng@gmail.com
 * Created Date:2012-11-9
 * Copyright @ 2012 BU
 * Description: 转盘
 *
 * History:
 */
public class ThermographDisk extends RelativeLayout {
	public VehicleDataBean vehicleDataBean = null;

	private RotateAnimation anim = null;
	private Interpolator lir;
	private int rotateTime;
	private float toDegrees;

	private float oldDegrees;
	private View hand;
	private ThermographBack back;

	private int backID;
	private int handID;
	private RotateListen rotaListen;

	public ThermographDisk(Context context) {
		this(context, null);

	}

	public ThermographDisk(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ThermographDisk(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/** 初始化背景图片,必须要初始化了数据之后才能够初始化View */
	public void initDatas(float min, float max, String unit) {
		vehicleDataBean = new VehicleDataBean(min, max,unit);
		vehicleDataBean.unit = unit;
		setBackID(R.id.back);
		back.initDatas(min, max, unit);
	}

	/**
	 * 初始化动画 以及旋转时间等等
	 * 
	 * @author pancheng 2012-11-9 下午3:01:26
	 * @return void
	 */
	protected void init(float x, float y) {
		anim = new RotateAnimation(0, 0, Animation.RELATIVE_TO_SELF, x,
				Animation.RELATIVE_TO_SELF, y);
		anim.setAnimationListener(new Animation.AnimationListener() {
			public void onAnimationStart(Animation aim) {

			}

			public void onAnimationRepeat(Animation aim) {

			}

			public void onAnimationEnd(Animation aim) {
				if (rotaListen != null) {
					rotaListen.nowDegrees(anim.getmToDegrees());
				}
			}

		});
		lir = new AccelerateDecelerateInterpolator();
		rotateTime = FlashConstants.FLASH_TIME / 5 * 4;
		toDegrees = 0;
	}

	/**
	 * 设置变速器
	 * 
	 * @author pancheng 2012-11-9 下午3:02:00
	 * @param lir
	 * @return void
	 */
	public void setLir(Interpolator lir) {
		this.lir = lir;
	}

	/**
	 * 设置旋转时间
	 * 
	 * @author pancheng 2012-11-9 下午3:02:15
	 * @param rotateTime
	 * @return void
	 */
	public void setRotateTime(int rotateTime) {
		this.rotateTime = rotateTime;
	}

	/**
	 * 设置旋转到某个角度
	 * 
	 * @author pancheng 2012-11-9 下午3:02:26
	 * @param toDegrees
	 * @return void
	 */
	public void setToDegrees(float toDegrees) {
		this.toDegrees = toDegrees;
	}

	/**
	 * 设置中间指针View的ID
	 * 
	 * @author pancheng 2012-11-9 下午3:02:39
	 * @param handId
	 * @return void
	 */
	public void setHandId(int handId) {
		this.handID = handId;
		hand = findViewById(handId);
		float height = 0.5f;
		float width = 0.5f;
		init(width, height);
	}

	public void setBackID(int backID) {
		this.backID = backID;
		back = (ThermographBack) findViewById(backID);
	}

	/**
	 * 设置监听
	 * 
	 * @author pancheng 2012-11-9 下午3:02:57
	 * @param rotaListen
	 * @return void
	 */
	public void setRotaListen(RotateListen rotaListen) {
		this.rotaListen = rotaListen;
	}

	/**
	 * 开始动画
	 * 
	 * @author pancheng 2012-11-9 下午3:03:06
	 * @return void
	 */
	public void start() {
		if (handID == 0) {
			return;
		}
		anim.setInterpolator(lir);
		anim.setFillAfter(true);
		anim.setFillBefore(true);
		anim.setFillEnabled(true);
		anim.setDuration(rotateTime);
		anim.setmFromDegrees(oldDegrees);
		anim.setmToDegrees(toDegrees);
		hand.startAnimation(anim);

		oldDegrees = toDegrees;
	}

	public interface RotateListen {
		public void nowDegrees(float nowDegrees);
	}
}
