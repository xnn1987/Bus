package cn.com.actia.smartdiag.services;

import java.util.ArrayList;

import cn.com.actia.businesslogic.Parameter;
import cn.com.actia.smartdiag.constants.Constants;
import cn.com.actia.smartdiag.constants.FlashConstants;
import cn.com.actia.smartdiag.model.wapper.entity.GetParameterResponse;
import cn.com.actia.smartdiag.tools.GetVecicleStatuesParamsTools;
import cn.com.actia.smartdiag.tools.Utils;
import cn.com.actia.smartdiag.wrapper.DiagBusinessLogicWrapper;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

public class FlashService extends Service implements Runnable {
	SharedPreferences mPreferences;
	private FlashBinder mFlashBinder;
	private Handler mHandler;
	public static boolean isSurival = false;
	private long flashTimes = FlashConstants.FLASH_TIME;
	private ArrayList<Parameter> mParameters;

	public ArrayList<Parameter> allParameters;
	private ArrayList<Parameter> tempAllParameters;

	private int nowIndex = 0;

	Object objectLocked = new Object();

	private int mStart;
	private int mEnd;
	private Thread mThread;

	private int tempStart;
	private int tempEnd;
	private ArrayList<Parameter> mTempParameters;

	private boolean isPartFlash = false;

	@Override
	public void onCreate() {
		Utils.showTag("onCreate");
		super.onCreate();
		mPreferences = getSharedPreferences(Constants.SHAREPREFERECE_TAG_NAME,
				Context.MODE_PRIVATE);
		reset();
	}

	public void reset() {
		mFlashBinder = null;
		mHandler = null;
		isSurival = false;
		mParameters = null;
		mTempParameters = null;
		allParameters = null;
		tempAllParameters = null;

		mStart = 0;
		mEnd = 0;
		tempStart = 0;
		tempEnd = 0;
		setNowIndex(0);

		mThread = null;
	}

	public void setAllParameters(ArrayList<Parameter> params) {
		this.tempAllParameters = params;
	}

	public void setmParameters(ArrayList<Parameter> mParameters) {
		// if (null == mParameters || mParameters.size() == 0) {
		// if (mHandler != null) {
		// if (null != mHandler)
		// mHandler.sendEmptyMessage(FlashConstants.MSG_NODATA);
		// }
		// }
		synchronized (objectLocked) {
			this.mParameters = null;
			this.mTempParameters = mParameters;
			isPartFlash = false;
		}
	}

	public void setmParameters(int start, int end,
			ArrayList<Parameter> mParameters) {
		synchronized (objectLocked) {
			this.setNowIndex(start / Constants.PAGE_OF_ITEMS);
			this.mTempParameters = mParameters;
			this.tempStart = start;
			this.tempEnd = end;
			this.isPartFlash = true;
		}
	}

	private void tempToReal() {
		synchronized (objectLocked) {
			this.allParameters = tempAllParameters;
			this.mStart = tempStart;
			this.mEnd = tempEnd;
			this.mParameters = mTempParameters;
		}
	}

	public void setmHandler(Handler mHandler) {
		this.mHandler = mHandler;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		if (null == mFlashBinder) {
			mFlashBinder = new FlashBinder();
		}
		return mFlashBinder;
	}

	@Override
	public void onDestroy() {
		Utils.showTag("onDestroy");
		reset();
		super.onDestroy();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}

	public class FlashBinder extends Binder {
		public FlashService getFlashService() {
			return FlashService.this;
		}
	}

	public void startFlash() {
		if (null != mHandler) {
			if (null == mThread && !isSurival) {
				(mThread = new Thread(this)).start();
				isSurival = true;
			} else {

			}
		}
	}

	public void stopFlash() {
		isSurival = false;
		mThread = null;
		reset();
	}

	public void run() {
		if (null != mHandler)
			mHandler.sendEmptyMessage(FlashConstants.MSG_CHECKSUITABLE);
		Message message = Utils.checkSuitable(null, mPreferences);

		if (message.what == 0) {// ¼ì²é×´Ì¬³É¹¦
			isSurival = true;
			if (null != mHandler)
				mHandler.sendEmptyMessage(FlashConstants.MSG_CHECKED_COMPLETED);
		} else if (message.what == Constants.NEED_TO_ACTIVATE) {
			mHandler.sendEmptyMessage(Constants.NEED_TO_ACTIVATE);
		} else {
			isSurival = false;
			Message msg = mHandler.obtainMessage(FlashConstants.MSG_ERROR,
					message.obj);
			if (null != mHandler)
				mHandler.sendMessage(msg);
		}
		while (isSurival) {
			if (null != mParameters) {
				int size = mParameters.size();
				if (size != 0) {
					GetParameterResponse getParamsResp = DiagBusinessLogicWrapper
							.getParameter(mParameters);
					Utils.showTag("getParameters().size():" + size);
					if (getParamsResp.getCode() == 0) {
						ArrayList<Parameter> parameters = getParamsResp
								.getParameters();
						Utils.showTag("parameters.size():" + parameters.size());
						Message msg = new Message();
						if (isPartFlash) {
							msg.what = FlashConstants.MSG_VALUE_PART_CHANGE;
							msg.arg1 = mStart;
							msg.arg2 = mEnd;
						} else {
							msg.what = FlashConstants.MSG_VALUE_CHANGE;
						}
						ArrayList<Parameter> tempParameters = GetVecicleStatuesParamsTools
								.getComparedParameter(size, parameters.size(),
										parameters);
						ArrayList<Parameter> resultP = new ArrayList<Parameter>();
						if (null != mParameters && null != tempParameters) {
							for (Parameter p : mParameters) {
								for (Parameter temp : tempParameters) {
									if (temp.getName().equals(p.getName())) {
										resultP.add(temp);
									}
								}
							}
						}
						msg.obj = resultP;
						if (null != mHandler)
							mHandler.sendMessage(msg);
					} else {
						// mHandler.obtainMessage(APPViewPageExample.MSG_DATA,
						// "wokao");
						if (null != mHandler)
							mHandler.sendEmptyMessage(FlashConstants.MSG_NODATA);
					}
				} else {
					// mHandler.obtainMessage(APPViewPageExample.MSG_DATA,
					// "wokao");
					if (null != mHandler)
						mHandler.sendEmptyMessage(FlashConstants.MSG_NODATA);
				}
			}
			try {
				Thread.sleep(flashTimes);
				tempToReal();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	public int getNowIndex() {
		System.out.println("~~~~~~~~~~~~~~~~~~~~" + nowIndex);
		return nowIndex;
	}

	public void setNowIndex(int nowIndex) {
		System.out.println("++++++++++++++++++++" + nowIndex);
		this.nowIndex = nowIndex;
	}
}
