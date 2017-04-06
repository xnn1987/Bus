package cn.com.actia.smartdiag.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import cn.com.actia.smartdiag.R;
import cn.com.actia.smartdiag.beans.DrivingManagerBean;
import cn.com.actia.smartdiag.constants.Constants;
import cn.com.actia.smartdiag.tools.Utils;

public class FlashDrivingManagerService extends Service implements Runnable {
	public static final int ERROR = 0;
	public static final int DATA = 1;

	public boolean isSurival;
	private long flashTimes = 1000;
	private FlashDrivingManagerBinder mFlashBinder;
	private Thread mflashThread;
	private Handler mHandler;
	private DrivingManagerBean mDrivingManagerBean;

	private int textStopTime;
	private SharedPreferences mPreferences;

	@Override
	public void onCreate() {
		super.onCreate();
		mPreferences = getSharedPreferences(Constants.SHAREPREFERECE_TAG_NAME,
				Context.MODE_PRIVATE);
		init();
	}

	@Override
	public IBinder onBind(Intent intent) {
		if (null == mFlashBinder) {
			mFlashBinder = new FlashDrivingManagerBinder();
		}
		try {
			textStopTime = Integer.parseInt(PreferenceManager
					.getDefaultSharedPreferences(
							FlashDrivingManagerService.this).getString(
							getResources().getString(
									R.string.key_TIME_TEXT_SHOW), "2")) * 1000;
		} catch (Exception e) {
			e.printStackTrace();
			textStopTime = 20000;
		}
		if (textStopTime > 10000) {
			textStopTime = 10000;
		} else if (textStopTime < 0) {
			textStopTime = 0;
		}
		return mFlashBinder;
	}

	public class FlashDrivingManagerBinder extends Binder {
		public FlashDrivingManagerService getFlashDrivingManagerService() {
			return FlashDrivingManagerService.this;
		}
	}

	public void init() {
		mflashThread = new Thread(this);
		isSurival = true;
		mFlashBinder = new FlashDrivingManagerBinder();
		mflashThread.start();
	}

	public void setHandler(Handler handler) {
		this.mHandler = handler;
	}

	public void stopFinish() {
		isSurival = false;
		mflashThread = null;
		mHandler = null;
		mDrivingManagerBean = null;
	}

	@Override
	public void onDestroy() {
		stopFinish();
		super.onDestroy();
	}

	private long timer = 0;

	private DrivingManagerBean nowShowingBean;

	public void run() {
		while (isSurival) {
			/* mHandler为空则不需要进行处理 */
			if (null != mHandler) {
				DrivingManagerBean tempBean;
				tempBean = Utils.getDrivingInfo(mHandler, mPreferences);
				if (null == tempBean) {// 如果为空则为发生了错误，故传递错误信息
					mHandler.sendEmptyMessage(0);
				}
				if (null != mDrivingManagerBean) {// 如果不为空，则与现有的版本进行比较
					if (null != nowShowingBean
							&& nowShowingBean.equals(tempBean)) {

					} else {
						mDrivingManagerBean = tempBean;
						long nowTime = System.currentTimeMillis();
						long k = nowTime - timer;

						if (nowTime - timer > textStopTime) {// 如果距离上次发送时间超过了2000ms则可以发送消息，否则让它消失在历史的长河中
							timer = nowTime;
							mHandler.sendMessage(mHandler.obtainMessage(DATA,
									mDrivingManagerBean));
							nowShowingBean = mDrivingManagerBean;
						} else {
							Utils.showTag("此数据未传递");
						}
					}
				} else {
					mDrivingManagerBean = tempBean;
				}
			}
			try {
				Thread.sleep(flashTimes);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
