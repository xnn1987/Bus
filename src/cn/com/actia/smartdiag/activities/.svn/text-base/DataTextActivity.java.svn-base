package cn.com.actia.smartdiag.activities;

import java.util.ArrayList;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import cn.com.actia.businesslogic.NumericParameter;
import cn.com.actia.businesslogic.Parameter;
import cn.com.actia.businesslogic.ParameterUnit;
import cn.com.actia.smartdiag.R;
import cn.com.actia.smartdiag.constants.Constants;
import cn.com.actia.smartdiag.constants.FlashConstants;
import cn.com.actia.smartdiag.services.FlashService;
import cn.com.actia.smartdiag.services.FlashService.FlashBinder;
import cn.com.actia.smartdiag.tools.ShakeSensor;
import cn.com.actia.smartdiag.tools.Utils;
import cn.com.actia.smartdiag.tools.onShake;

public class DataTextActivity extends APPBaseActivity implements onShake {

	ViewPager mViewPager;
	RadioGroup mRadioGroup;
	ArrayList<RadioButton> mRadioButtons;
	ViewPagerAdapter mViewPagerAdapter;

	private ArrayList<Parameter> mItemBeans;
	boolean isServiceConnected;

	Handler mHandler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constants.NEED_TO_ACTIVATE:
				registDevices();
				break;
			case FlashConstants.MSG_CHECKED_COMPLETED:
				if (app.mDatas != null) {
					mFlashService.setmParameters(app.mDatas);
					app.mDatas = null;
				} else {
					switch (mVehicleState) {
					case FlashConstants.STATUE_ENGINE:
						mFlashService.setmParameters(Utils
								.getUsableParamListForValue(Utils
										.getEngineBundle()));
						break;
					case FlashConstants.STATUE_LIGHT:
						mFlashService.setmParameters(Utils
								.getUsableParamListForValue(Utils
										.getLightsBundle()));
						break;
					case FlashConstants.STATUE_OIL:
						mFlashService.setmParameters(Utils
								.getUsableParamListForValue(Utils
										.getOilBundle()));
						break;
					case FlashConstants.STATUE_STOP:
						mFlashService.setmParameters(Utils
								.getUsableParamListForValue(Utils
										.getSecurityBundle()));
						break;
					case FlashConstants.STATUE_WATER:
						mFlashService.setmParameters(Utils
								.getUsableParamListForValue(Utils
										.getWaterTemperatureBundle()));
						break;

					default:
						// TODO 写报错的提示，并把下面这句话去掉
						mFlashService.setmParameters(Utils
								.getUsableParamListForValue(Utils
										.getEngineBundle()));
						break;
					}
				}

				break;
			case FlashConstants.MSG_VALUE_CHANGE:
				if (null != mParameters) {
					ArrayList<Parameter> startParameters = mParameters;
					ArrayList<Parameter> cutedParameters = new ArrayList<Parameter>();
					int start = nowPage * Constants.PAGE_OF_ITEMS;
					int end = start + Constants.PAGE_OF_ITEMS - 1;
					for (int i = start; i <= end && i < startParameters.size(); i++) {
						cutedParameters.add(startParameters.get(i));
					}
					mFlashService.setmParameters(start, end, cutedParameters);
					isFirstChange = false;
				}
				cancelProgressDialog();
				ArrayList<Parameter> parameters = (ArrayList<Parameter>) msg.obj;
				mParameters = parameters;
				mFlashService.setAllParameters(mParameters);
				setmItemBeansValues(parameters);

				break;
			case FlashConstants.MSG_VALUE_PART_CHANGE:
				cancelProgressDialog();
				if (isFirstChange && null != mParameters) {
					ArrayList<Parameter> startParameters = mParameters;
					ArrayList<Parameter> cutedParameters = new ArrayList<Parameter>();
					int start = nowPage * Constants.PAGE_OF_ITEMS;
					int end = start + Constants.PAGE_OF_ITEMS - 1;
					for (int i = start; i <= end && i < startParameters.size(); i++) {
						cutedParameters.add(startParameters.get(i));
					}
					mFlashService.setmParameters(start, end, cutedParameters);
					isFirstChange = false;
				}
				System.out.println(msg.arg1 + "~~~~~" + msg.arg2);
				ArrayList<Parameter> partParameters = (ArrayList<Parameter>) msg.obj;
				setmItemBeansValues(msg.arg1, msg.arg2, partParameters);
				System.out.println("MSG_VALUE_PART_CHANGE");

				break;
			case FlashConstants.MSG_NODATA:
				cancelProgressDialog();
				if (isServiceConnected) {
					unbindService(mServiceConnection);
					isServiceConnected = false;
				}
				DataTextActivity.this.showMessageDialog(DataTextActivity.this,
						R.string.app_prompt, R.string.tips_no_message,
						Constants.STYLE_EXIT_ACTIVITY);
				break;
			case FlashConstants.MSG_ERROR:
				cancelProgressDialog();

				String content = Utils.getStringResourse(DataTextActivity.this,
						R.string.tips_no_message);
				content = msg.obj.toString();
				DataTextActivity.this.showMessageDialog(DataTextActivity.this,
						R.string.app_prompt, content,
						Constants.STYLE_EXIT_ACTIVITY);
				break;
			case FlashConstants.MSG_CHECKSUITABLE:
				break;
			default:

				break;
			}

		};
	};

	/** 当前的viewPage所对应的 index */
	public int nowPage = 0;
	ArrayList<Parameter> mParameters;
	public boolean isFirstChange = true;

	public int mVehicleState;
	FlashService mFlashService;
	ServiceConnection mServiceConnection = new ServiceConnection() {
		public void onServiceDisconnected(ComponentName name) {
			mFlashService.setNowIndex(nowPage);
			mFlashService.stopFlash();
			isServiceConnected = false;
		}

		public void onServiceConnected(ComponentName name, IBinder service) {
			mFlashService = ((FlashBinder) service).getFlashService();
			mFlashService.setmHandler(mHandler);
			mFlashService.startFlash();
			isServiceConnected = true;
			setmItemBeansValues(mFlashService.allParameters);
			nowPage = mFlashService.getNowIndex();
			mViewPager.setCurrentItem(mFlashService.getNowIndex());
			if(null!=mFlashService.allParameters){
				mParameters = mFlashService.allParameters;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.flipper_item_layout);
		if (getIntent().hasExtra(FlashConstants.VEHICLESTATE)) {
			mVehicleState = getIntent().getIntExtra(
					FlashConstants.VEHICLESTATE, FlashConstants.STATUE_ENGINE);
		}
		initWidget();
	}

	public void initWidget() {
		mViewPager = (ViewPager) findViewById(R.id.flipper_item_viewpage);
		// findViewById(R.id.baseView).setOnClickListener(itemClick);
		mRadioGroup = (RadioGroup) findViewById(R.id.flipper_item_radiogroup);
		mViewPager
				.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
					public void onPageSelected(int index) {
						mRadioButtons.get(index).setChecked(true);
						nowPage = index;
						System.out.println(index);
						isFirstChange = true;
						if (null != mParameters) {
							ArrayList<Parameter> startParameters = mParameters;
							ArrayList<Parameter> cutedParameters = new ArrayList<Parameter>();
							int start = nowPage * Constants.PAGE_OF_ITEMS;
							int end = start + Constants.PAGE_OF_ITEMS - 1;
							for (int i = start; i <= end
									&& i < startParameters.size(); i++) {
								cutedParameters.add(startParameters.get(i));
							}
							mFlashService.setmParameters(start, end,
									cutedParameters);
							isFirstChange = false;
						}
					}

					public void onPageScrolled(int arg0, float arg1, int arg2) {

					}

					public void onPageScrollStateChanged(int arg0) {

					}
				});

	}

	public synchronized void setmItemBeansValues(int start, int end,
			ArrayList<Parameter> items) {
		if (null == mItemBeans) {
			setmItemBeans(items);
			return;
		}
		for (int i = start; i < mItemBeans.size() && i <= end; i++) {
			mItemBeans.get(i).setParameterValue(
					items.get(i - start).getParameterValue());
		}
		mViewPagerAdapter.notifyDataSetChanged();
	}

	public synchronized void setmItemBeansValues(ArrayList<Parameter> items) {
		if (null == mItemBeans) {
			setmItemBeans(items);
			return;
		}
		for (int i = 0; i < items.size(); i++) {
			mItemBeans.get(i).setParameterValue(
					items.get(i).getParameterValue());
		}
		mViewPagerAdapter.notifyDataSetChanged();
	}

	public synchronized void setmItemBeans(ArrayList<Parameter> items) {
		if (null == items)
			return;
		this.mItemBeans = items;
		mViewPagerAdapter = new ViewPagerAdapter(this);
		mViewPager.setAdapter(mViewPagerAdapter);
		mViewPagerAdapter.notifyDataSetChanged();
	}

	// TODO 这个要改的
	OnClickListener itemClick = new OnClickListener() {
		public void onClick(View v) {
			Message msg = new Message();
			msg.what = -1;
			msg.obj = mItemBeans;
			// DataClockActivity.mHandler.sendMessage(msg);
			if (null != mFlashService.allParameters) {
				startActivity(new Intent(DataTextActivity.this,
						DataClockActivity.class));
				if (!isServiceConnected) {
					unbindService(mServiceConnection);
					isServiceConnected = true;
				}
				DataTextActivity.this.finish();
			} else {
				showToast(R.string.app_waiting_for_complete);
			}
		}
	};

	public class ViewPagerAdapter extends PagerAdapter {
		ArrayList<View> mViews;
		LayoutInflater mInflater;
		Resources mResource;
		int size;

		public ViewPagerAdapter(Context context) {
			mViews = new ArrayList<View>();
			mInflater = LayoutInflater.from(context);
			mRadioButtons = new ArrayList<RadioButton>();
			mResource = context.getResources();
			// 计数器
			if (mItemBeans.size() % Constants.PAGE_OF_ITEMS == 0)
				size = mItemBeans.size() / Constants.PAGE_OF_ITEMS;
			else {
				size = mItemBeans.size() / Constants.PAGE_OF_ITEMS + 1;
			}

			for (int i = 0; i < size; i++) {
				View group = mInflater.inflate(R.layout.contain_item_layout,
						null);
				LinearLayout llgroup = (LinearLayout) group
						.findViewById(R.id.linearlayout);
				int start = i * Constants.PAGE_OF_ITEMS;
				int end = start + Constants.PAGE_OF_ITEMS > mItemBeans.size() ? mItemBeans
						.size() : start + Constants.PAGE_OF_ITEMS;
				for (int j = start; j < end; j++) {
					Parameter b = mItemBeans.get(j);
					View itemView = mInflater.inflate(R.layout.item_layout,
							null);
					StringBuffer sb = new StringBuffer(b.getDisplayName());

					System.out.println("B:" + b);

					if (b instanceof NumericParameter) {
						ParameterUnit UnitB = ((NumericParameter) b)
								.getParameterUnit();
						if (null != UnitB) {
							sb.append("(").append(UnitB.getLabel()).append(")");
						}
					}
					((TextView) itemView.findViewById(R.id.item_name_tv))
							.setText(sb.toString());
					((TextView) itemView.findViewById(R.id.item_name_tv))
							.setSelected(true);

					((TextView) itemView.findViewById(R.id.item_value_tv))
							.setText(b.getParameterValue().getValue() + "");
					itemView.setTag(j + "");
					itemView.setOnClickListener(itemClick);
					llgroup.addView(itemView);
					group.setOnClickListener(itemClick);
				}
				RadioButton rb = (RadioButton) getLayoutInflater().inflate(
						R.layout.radio_btn, null);
				int size = getResources().getDimensionPixelOffset(
						R.dimen.radio_size);
				RadioGroup.LayoutParams rbL = new RadioGroup.LayoutParams(size,
						size);
				final Integer index = i;
				rb.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						mViewPager.setCurrentItem(index);
						nowPage = index;
						isFirstChange = true;
						if (null != mParameters) {
							ArrayList<Parameter> startParameters = mParameters;
							ArrayList<Parameter> cutedParameters = new ArrayList<Parameter>();
							int start = nowPage * Constants.PAGE_OF_ITEMS;
							int end = start + Constants.PAGE_OF_ITEMS - 1;
							for (int i = start; i <= end
									&& i < startParameters.size(); i++) {
								cutedParameters.add(startParameters.get(i));
							}
							mFlashService.setmParameters(start, end,
									cutedParameters);
							isFirstChange = false;
						}
					}
				});
				mRadioButtons.add(rb);
				mRadioGroup.addView(rb, rbL);
				mViews.add(group);
				if (mRadioButtons.size() > 0) {
					mRadioButtons.get(0).setChecked(true);
				}
			}
		}

		@Override
		public void notifyDataSetChanged() {
			for (int j = 0; j < mViews.size(); j++) {
				View viewParent = mViews.get(j);
				LinearLayout ll = (LinearLayout) viewParent
						.findViewById(R.id.linearlayout);
				for (int i = 0; i < ll.getChildCount(); i++) {
					int m = Integer.parseInt(ll.getChildAt(i).getTag()
							.toString());
					((TextView) ll.getChildAt(i).findViewById(
							R.id.item_value_tv)).setText(mItemBeans.get(m)
							.getParameterValue().getValue().toString());
				}
			}
			super.notifyDataSetChanged();
		}

		public void notifyDataSetChanged(int nowPage) {
			LinearLayout ll = ((LinearLayout) mViews.get(nowPage));
			for (int i = 0; i < ll.getChildCount(); i++) {
				int m = Integer.parseInt(ll.getChildAt(i).getTag().toString());
				((TextView) ll.getChildAt(i).findViewById(R.id.item_value_tv))
						.setText(mItemBeans.get(m).getParameterValue()
								.getValue().toString());
			}
			super.notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return mViews.size();
		}

		@Override
		public boolean isViewFromObject(View v, Object obj) {
			return v == obj;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			((ViewPager) container).addView(mViews.get(position));
			return mViews.get(position);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView(mViews.get(position));
		}

	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		cancelProgressDialog();
		if (isServiceConnected) {
			unbindService(mServiceConnection);
			isServiceConnected = false;
		}
		ShakeSensor.unregisterSensor();
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		// boolean isShakeEnable = ShakeSensor.init(this, this);
		// if (isShakeEnable) {
		//
		// } else {
		// showTag("加速度传感器不能用");
		// }
		super.onResume();
	}

	@Override
	protected void onStart() {
		if (!isServiceConnected) {
			startService(new Intent(DataTextActivity.this, FlashService.class));
			bindService(new Intent(DataTextActivity.this, FlashService.class),
					mServiceConnection, Context.BIND_AUTO_CREATE);
			isServiceConnected = true;
		}
		if (FlashService.isSurival) {

		} else {
			this.showPrograssDialog(this,
					getResources().getString(R.string.app_prompt),
					getResources().getString(R.string.fast_scan_is_scan_3));
		}
		super.onStart();
	}

	@Override
	public void finish() {
		try {
			stopService(new Intent(DataTextActivity.this, FlashService.class));
		} catch (Exception e) {
		}
		super.finish();
	}

	@Override
	public void onBackPressed() {
		DataClockActivity.mClock_Status = null;
		ShakeSensor.unregisterSensor();
		super.onBackPressed();
	}

	public void onShakeListener() {
		Message msg = new Message();
		msg.what = -1;
		msg.obj = mItemBeans;
		// DataClockActivity.mHandler.sendMessage(msg);
		if (null != mFlashService && null != mFlashService.allParameters) {
			startActivity(new Intent(DataTextActivity.this,
					DataClockActivity.class));
			if (!isServiceConnected) {
				unbindService(mServiceConnection);
				isServiceConnected = true;
			}
			DataTextActivity.this.finish();
		} else {
			showToast(R.string.app_waiting_for_complete);
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.parameter_memu_next:
			Message msg = new Message();
			msg.what = -1;
			msg.obj = mItemBeans;
			// DataClockActivity.mHandler.sendMessage(msg);
			if (null != mFlashService.allParameters) {
				startActivity(new Intent(DataTextActivity.this,
						DataClockActivity.class));
				if (!isServiceConnected) {
					unbindService(mServiceConnection);
					isServiceConnected = true;
				}
				DataTextActivity.this.finish();
			} else {
				showToast(R.string.app_waiting_for_complete);
			}
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.parameter_menu, menu);
		return true;
	}
}
