package cn.com.actia.smartdiag.activities;

import java.util.ArrayList;

import android.app.AlertDialog;
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
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import cn.com.actia.businesslogic.NumericParameter;
import cn.com.actia.businesslogic.Parameter;
import cn.com.actia.businesslogic.ParameterValue;
import cn.com.actia.smartdiag.R;
import cn.com.actia.smartdiag.constants.Constants;
import cn.com.actia.smartdiag.constants.FlashConstants;
import cn.com.actia.smartdiag.services.FlashService;
import cn.com.actia.smartdiag.services.FlashService.FlashBinder;
import cn.com.actia.smartdiag.tools.ShakeSensor;
import cn.com.actia.smartdiag.tools.Utils;
import cn.com.actia.smartdiag.tools.onShake;
import cn.com.actia.smartdiag.widget.DataPoint;
import cn.com.actia.smartdiag.widget.Histogram;
import cn.com.actia.smartdiag.widget.IGraphDataChangeListener;

public class DataHistogramActivity extends APPBaseActivity implements onShake {
	/** 圆盘 */
	public static final int KIND_HISTOGRAM = 31;

	/** 矩形框表示，默认表示方式 */
	public static final int KIND_NORMAL = 30;

	private ViewGroup mRadioGroup = null;

	private ArrayList<RadioButton> mRadioButtons;

	private int[] stateOfParameters;

	private ViewPager mViewPager;
	private ClockAadpter mClockAadpter;

	/** 当前的viewPage所对应的 index */
	public int nowPage = 0;
	ArrayList<Parameter> mParameters = null;
	public boolean isFirstChange = true;

	public int mVehicleState;
	FlashService mFlashService;
	ServiceConnection mServiceConnection = new ServiceConnection() {
		public void onServiceDisconnected(ComponentName name) {
			mFlashService.setNowIndex(nowPage);
			mFlashService.stopFlash();
		}

		public void onServiceConnected(ComponentName name, IBinder service) {
			mFlashService = ((FlashBinder) service).getFlashService();
			nowPage = mFlashService.getNowIndex();
			mFlashService.setmHandler(mHandler);
			mFlashService.startFlash();
			initDatas(mFlashService);
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
		}
	};

	private void initDatas(FlashService service) {
		mParameters = mFlashService.allParameters;
		init(mParameters);
		nowPage = mFlashService.getNowIndex();
		mViewPager.setCurrentItem(nowPage);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		initDatas(mFlashService);
		if (null != mAlertDiag) {
			mAlertDiag.dismiss();
		}
		super.onConfigurationChanged(newConfig);
	}

	public void init(ArrayList<Parameter> ps) {
		stateOfParameters = new int[ps.size()];
		for (int i = 0; i < ps.size(); i++) {
			Parameter p = mParameters.get(i);
			if (p instanceof NumericParameter) {
				NumericParameter numPara = (NumericParameter) p;
				if (numPara.hasMaxValue()) {
					stateOfParameters[i] = KIND_HISTOGRAM;
				} else {
					stateOfParameters[i] = KIND_NORMAL;
				}
			} else {
				stateOfParameters[i] = KIND_NORMAL;
			}
		}
		mClockAadpter = new ClockAadpter(this);
		mViewPager.setAdapter(mClockAadpter);
		if (mRadioButtons.size() > 0) {
			mRadioButtons.get(0).setChecked(true);
		}

	}

	Handler mHandler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			cancelProgressDialog();
			switch (msg.what) {
			case Constants.NEED_TO_ACTIVATE:
				registDevices();
				break;
			case FlashConstants.MSG_CHECKED_COMPLETED:
				switch (mVehicleState) {
				case FlashConstants.STATUE_ENGINE:
					mFlashService
							.setmParameters(Utils
									.getUsableParamListForValue(Utils
											.getEngineBundle()));
					break;
				case FlashConstants.STATUE_LIGHT:
					mFlashService
							.setmParameters(Utils
									.getUsableParamListForValue(Utils
											.getLightsBundle()));
					break;
				case FlashConstants.STATUE_OIL:
					mFlashService.setmParameters(Utils
							.getUsableParamListForValue(Utils.getOilBundle()));
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
					mFlashService
							.setmParameters(Utils
									.getUsableParamListForValue(Utils
											.getEngineBundle()));
					break;
				}

				break;
			case FlashConstants.MSG_VALUE_CHANGE:
				// if (null != mParameters) {
				// ArrayList<Parameter> startParameters = mParameters;
				// ArrayList<Parameter> cutedParameters = new
				// ArrayList<Parameter>();
				// int start = nowPage * Constants.PAGE_OF_ITEMS;
				// int end = start + Constants.PAGE_OF_ITEMS - 1;
				// for (int i = start; i <= end && i < startParameters.size();
				// i++) {
				// cutedParameters.add(startParameters.get(i));
				// }
				// mFlashService.setmParameters(start, end, cutedParameters);
				// isFirstChange = false;
				// }
				// ArrayList<Parameter> ps = (ArrayList<Parameter>) msg.obj;
				// if (null != ps) {
				// mParameters = ps;
				// init(ps);
				// }
				break;
			case FlashConstants.MSG_VALUE_PART_CHANGE:
				if (null != mParameters) {
					if (isFirstChange) {
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
					ArrayList<Parameter> partParameters = (ArrayList<Parameter>) msg.obj;
					for (int i = msg.arg1; i <= msg.arg2
							&& i < mParameters.size(); i++) {
						mParameters.remove(i);
						mParameters.add(i, partParameters.get(i - msg.arg1));
					}
					mClockAadpter.notifyDataSetChanged();
				}
				break;
			default:
				break;
			}

		};
	};

	// static Handler mHandler = new Handler() {
	//
	// @SuppressWarnings("unchecked")
	// public void handleMessage(Message msg) {
	// switch (msg.what) {
	// case 0:
	// int start = msg.arg1;
	// int end = msg.arg2;
	// if (null != msg.obj) {
	// ArrayList<Parameter> ps = (ArrayList<Parameter>) msg.obj;
	// for (int i = start; i <= end; i++) {
	// parameters.remove(i);
	// parameters.add(i, ps.get(i - start));
	// }
	// mClockAadpter.notifyDataSetChanged();
	// }
	// break;
	// case -1:
	// ArrayList<Parameter> parameters = (ArrayList<Parameter>) msg.obj;
	// init(parameters);
	// break;
	// default:
	// break;
	//
	// }
	// };
	// };

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.flipper_item_layout);
		if (getIntent().hasExtra(FlashConstants.VEHICLESTATE)) {
			mVehicleState = getIntent().getIntExtra(
					FlashConstants.VEHICLESTATE, FlashConstants.STATUE_ENGINE);
		}
		mRadioGroup = (ViewGroup) findViewById(R.id.flipper_item_radiogroup);
		mViewPager = (ViewPager) findViewById(R.id.flipper_item_viewpage);
		mViewPager
				.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
					public void onPageSelected(int index) {
						mRadioButtons.get(index).setChecked(true);
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

					public void onPageScrolled(int arg0, float arg1, int arg2) {

					}

					public void onPageScrollStateChanged(int arg0) {

					}
				});
		startService(new Intent(DataHistogramActivity.this, FlashService.class));
		bindService(new Intent(DataHistogramActivity.this, FlashService.class),
				mServiceConnection, Context.BIND_AUTO_CREATE);
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

	class ClockAadpter extends PagerAdapter {

		ArrayList<View> mViews;
		LayoutInflater mInflater;
		Resources mResource;

		private ArrayList<View> allViews;
		int size;

		public ClockAadpter(Context context) {
			mViews = new ArrayList<View>();
			allViews = new ArrayList<View>();
			mInflater = LayoutInflater.from(context);
			mRadioButtons = new ArrayList<RadioButton>();
			mRadioGroup.removeAllViews();
			mResource = context.getResources();
			// 计数器
			if (mParameters.size() % Constants.PAGE_OF_ITEMS == 0)
				size = mParameters.size() / Constants.PAGE_OF_ITEMS;
			else {
				size = mParameters.size() / Constants.PAGE_OF_ITEMS + 1;
			}

			for (int i = 0; i < size; i++) {
				LinearLayout group = (LinearLayout) mInflater.inflate(
						R.layout.clock_item_layout, null);
				int start = i * Constants.PAGE_OF_ITEMS;
				int end = start + Constants.PAGE_OF_ITEMS > mParameters.size() ? mParameters
						.size() : start + Constants.PAGE_OF_ITEMS;

				int[] ids = new int[] { R.id.clock_item_llayout1,
						R.id.clock_item_llayout2, R.id.clock_item_llayout3,
						R.id.clock_item_llayout4, R.id.clock_item_llayout5,
						R.id.clock_item_llayout6 };
				for (int j = start; j < end; j++) {
					LinearLayout linearLayout = (LinearLayout) group
							.findViewById(ids[j - start]);
					View v = null;
					switch (stateOfParameters[j]) {
					case KIND_NORMAL:
						View llview = mInflater.inflate(
								R.layout.widget_view_normal, null);
						TextView name = (TextView) llview
								.findViewById(R.id.tv_name);
						name.setText(mParameters.get(j).getDisplayName());
						name.setSelected(true);
						v = llview;
						break;

					case KIND_HISTOGRAM:
						View histogramView = mInflater.inflate(
								R.layout.widget_view_histogram, null);
						final Histogram histogram = (Histogram) histogramView
								.findViewById(R.id.histogram);
						histogram
								.setOnLongClickListener(new OnLongClickListener() {

									public boolean onLongClick(View v) {
										showBigHistogram(histogram);
										return false;
									}
								});
						histogram.setOnClickListener(itemClick);

						NumericParameter numP = (NumericParameter) mParameters
								.get(j);

						histogram.initData(numP.getMaxValue().getValue(), numP
								.getMinValue().getValue(), null == numP
								.getParameterUnit() ? "" : numP
								.getParameterUnit().getLabel());

						TextView thername = (TextView) histogramView
								.findViewById(R.id.tv_name);
						thername.setText(mParameters.get(j).getDisplayName());
						thername.setSelected(true);
						v = histogramView;
						break;

					default:
						break;
					}
					if (v == null) {
						View llview = mInflater.inflate(
								R.layout.widget_view_normal, null);
						TextView name = (TextView) llview
								.findViewById(R.id.tv_name);
						name.setText(mParameters.get(j).getDisplayName());
						name.setSelected(true);
						v = llview;
					}
					linearLayout.addView(v);
					allViews.add(v);
					v.setOnClickListener(itemClick);
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
						nowPage = index;
						mViewPager.setCurrentItem(index);
					}
				});
				mRadioButtons.add(rb);
				mRadioGroup.addView(rb, rbL);
				mViews.add(group);
			}
		}

		OnClickListener itemClick = new OnClickListener() {
			public void onClick(View v) {

				Message msg = new Message();
				msg.what = -1;
				msg.obj = mParameters;
				startActivity(new Intent(DataHistogramActivity.this,
						DataTextActivity.class));
				DataHistogramActivity.this.finish();
				APPViewPageActivity.form_status = 2;
			}
		};
		float degree = 100;

		int k = 0;

		@Override
		public void notifyDataSetChanged() {
			k++;
			for (int j = 0; j < allViews.size(); j++) {
				ParameterValue value = mParameters.get(j).getParameterValue();
				if (null == value) {
					super.notifyDataSetChanged();
					continue;
				}
				switch (stateOfParameters[j]) {
				case KIND_NORMAL:
					TextView textView = (TextView) allViews.get(j)
							.findViewById(R.id.tv_value);
					textView.setText(value.getValue().toString());
					break;

				case KIND_HISTOGRAM:
					NumericParameter p = (NumericParameter) mParameters.get(j);
					float max = 0;
					float min = 0;
					if (p.hasMaxValue()) {
						max = p.getMaxValue().getValue();
						min = p.getMinValue().getValue();
					}

					Histogram histogram = (Histogram) allViews.get(j)
							.findViewById(R.id.histogram);
					DataPoint date = new DataPoint();
					date.hen += k;
					date.shu += Math.abs(Float.parseFloat(p.getParameterValue()
							.getValue().toString())
							/ (max - min));
					date.shu = Float.parseFloat(p.getParameterValue()
							.getValue().toString());

					histogram.addDatas(date);
					histogram.invalidate();
					if (date.hen >= 10) {
						histogram.setO_t(date.hen - 10);
					}
					break;
				default:
					break;
				}
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
	protected void onDestroy() {
		unbindService(mServiceConnection);
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		DataClockActivity.mClock_Status = null;
		ShakeSensor.unregisterSensor();
	}

	@Override
	public void finish() {
		try {
			stopService(new Intent(DataHistogramActivity.this,
					FlashService.class));
		} catch (Exception e) {
		}
		super.finish();
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.parameter_memu_next:
			Message msg = new Message();
			msg.what = -1;
			msg.obj = mParameters;
			startActivity(new Intent(DataHistogramActivity.this,
					DataTextActivity.class));
			DataHistogramActivity.this.finish();
			APPViewPageActivity.form_status = 2;
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.parameter_menu, menu);
		return true;
	}

	AlertDialog mAlertDiag;

	public void showBigHistogram(Histogram histogram) {
		final Histogram big_histogram = (Histogram) LayoutInflater.from(this)
				.inflate(R.layout.big_histogram, null);
		big_histogram.setLittle(false);
		histogram.setGdcl(new IGraphDataChangeListener() {

			public void onSetOT(double ot) {
				big_histogram.setO_t(ot);
				big_histogram.postInvalidate();
			}

			public void onInitData(ArrayList<DataPoint> datas, float max,
					float min, String unit) {
				big_histogram.initData(max, min, unit);
				big_histogram.setmDatas(datas);
				big_histogram.postInvalidate();
			}

			public void onAddData(DataPoint dp) {
				big_histogram.addDatas(dp);
				big_histogram.postInvalidate();
			}
		});

		LinearLayout groupView = new LinearLayout(this);
		int height = getWindowManager().getDefaultDisplay().getHeight();
		int width = getWindowManager().getDefaultDisplay().getWidth();
		int Size = (int) ((height > width ? width : height) * 0.8);
		LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(Size, Size);
		groupView.addView(big_histogram, ll);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		// builder.setView(groupView);
		AlertDialog alertDiag = builder.create();
		mAlertDiag = alertDiag;
		alertDiag.show();
		alertDiag.setContentView(groupView);
	}

	public void onShakeListener() {
		Message msg = new Message();
		msg.what = -1;
		msg.obj = mParameters;
		startActivity(new Intent(DataHistogramActivity.this,
				DataTextActivity.class));
		DataHistogramActivity.this.finish();
		APPViewPageActivity.form_status = 2;
	}
}
