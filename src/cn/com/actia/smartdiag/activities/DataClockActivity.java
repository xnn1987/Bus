package cn.com.actia.smartdiag.activities;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import cn.com.actia.businesslogic.BooleanParameter;
import cn.com.actia.businesslogic.BooleanParameterValue;
import cn.com.actia.businesslogic.Degree;
import cn.com.actia.businesslogic.Distance;
import cn.com.actia.businesslogic.NumericParameter;
import cn.com.actia.businesslogic.Parameter;
import cn.com.actia.businesslogic.ParameterUnit;
import cn.com.actia.businesslogic.ParameterValue;
import cn.com.actia.businesslogic.Percent;
import cn.com.actia.businesslogic.Pressure;
import cn.com.actia.businesslogic.Speed;
import cn.com.actia.businesslogic.TextParameter;
import cn.com.actia.businesslogic.TextParameterValue;
import cn.com.actia.businesslogic.Time;
import cn.com.actia.businesslogic.Voltage;
import cn.com.actia.smartdiag.R;
import cn.com.actia.smartdiag.constants.Constants;
import cn.com.actia.smartdiag.constants.FlashConstants;
import cn.com.actia.smartdiag.range.RangeBean;
import cn.com.actia.smartdiag.services.FlashService;
import cn.com.actia.smartdiag.services.FlashService.FlashBinder;
import cn.com.actia.smartdiag.tools.LightSensor;
import cn.com.actia.smartdiag.tools.Utils;
import cn.com.actia.smartdiag.tools.onLightChange;
import cn.com.actia.smartdiag.tools.onShake;
import cn.com.actia.smartdiag.widget.Disk;
import cn.com.actia.smartdiag.widget.DiskBack;
import cn.com.actia.smartdiag.widget.E_Disk;
import cn.com.actia.smartdiag.widget.E_Odometer;
import cn.com.actia.smartdiag.widget.E_Piezometer;
import cn.com.actia.smartdiag.widget.E_Thermograph;
import cn.com.actia.smartdiag.widget.MyRadioButton;
import cn.com.actia.smartdiag.widget.PiezometerBack;
import cn.com.actia.smartdiag.widget.PiezometerDisk;
import cn.com.actia.smartdiag.widget.SwitchBack;
import cn.com.actia.smartdiag.widget.ThermographBack;
import cn.com.actia.smartdiag.widget.ThermographDisk;
import cn.com.actia.smartdiag.widget.TimeNum;
import cn.com.actia.smartdiag.widget.TwoSwitch;

@SuppressLint("HandlerLeak")
public class DataClockActivity extends APPBaseActivity implements
		onLightChange, onShake {
	/** Բ�� */
	public static final int KIND_DISK = 11;
	/** �¶ȼ� */
	public static final int KIND_THERMOGRAPH = 12;
	/** ��̱� */
	public static final int KIND_ODOMETER = 13;
	/** ѹ���� */
	public static final int KIND_PIEZOMETER = 14;
	/** ��� */
	public static final int KIND_STOPWATCH = 15;

	/** ���أ�1��״̬ */
	public static final int KIND_SWITCH = 16;
	/** ���أ�1��״̬ */
	public static final int KIND_SWITCH_1 = 161;
	/** ���أ�2��״̬ */
	public static final int KIND_SWITCH_2 = 162;
	/** ���أ�3��״̬ */
	public static final int KIND_SWITCH_3 = 163;
	/** ���أ�4��״̬ */
	public static final int KIND_SWITCH_4 = 164;
	/** ���أ�5��״̬ */
	public static final int KIND_SWITCH_5 = 165;

	/** ���ο��ʾ��Ĭ�ϱ�ʾ��ʽ */
	public static final int KIND_NORMAL = 10;

	private ViewGroup mRadioGroup = null;

	private ArrayList<RadioButton> mRadioButtons;

	// private static ArrayList<Parameter> parameters;
	/** 0��ʾʹ��Ĭ�ϵ���ʽ,1��ʾʹ��clock */
	private int[] stateOfParameters;

	public static final int S_DIGITAL = 1;
	public static final int S_NORMAL = 0;
	public static int[] mClock_Status;

	private ViewPager mViewPager;
	private ClockAadpter mClockAadpter;

	/** ��ǰ��viewPage���Ӧ�� index */
	public int nowPage = 0;
	ArrayList<Parameter> mParameters = null;
	public boolean isFirstChange = true;

	private static int mDaily_state = 1;

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
		if (null != mParameters)
			init(mParameters);
		nowPage = mFlashService.getNowIndex();
		mViewPager.setCurrentItem(nowPage);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		initDatas(mFlashService);
		super.onConfigurationChanged(newConfig);
	}

	public void init(ArrayList<Parameter> ps) {
		stateOfParameters = new int[ps.size()];
		if (null == mClock_Status) {
			mClock_Status = new int[ps.size()];
			// �������ʱֻ֧�ֵ�����ͼ
			
			mClock_Status[KIND_ODOMETER] = S_DIGITAL;
		}
		for (int i = 0; i < ps.size(); i++) {
			Parameter p = mParameters.get(i);
			if (p instanceof NumericParameter) {
				NumericParameter numPara = (NumericParameter) p;
				ParameterUnit parameterUnit = numPara.getParameterUnit();
				Utils.showTag("num:	"
						+ i
						+ (null == numPara.getParameterUnit() ? "" : numPara
								.getParameterUnit().getLabel()));
				if (parameterUnit instanceof Pressure) {
					stateOfParameters[i] = KIND_PIEZOMETER;
				} else if (parameterUnit instanceof Speed) {// �ٶ�
					stateOfParameters[i] = KIND_DISK;
				} else if (parameterUnit instanceof Time) {// ʱ��
					stateOfParameters[i] = KIND_STOPWATCH;
				} else if (parameterUnit instanceof Voltage) {// ��ѹ
					stateOfParameters[i] = KIND_DISK;
				} else if (parameterUnit instanceof Percent) {// �ٷ���
					stateOfParameters[i] = KIND_DISK;
				} else if (parameterUnit instanceof Distance) {// ����
					stateOfParameters[i] = KIND_ODOMETER;
				} else if (parameterUnit instanceof Degree) {// �¶�
					stateOfParameters[i] = KIND_THERMOGRAPH;
				} else if (numPara.hasMaxValue()) {
					stateOfParameters[i] = KIND_DISK;// ��ͨ
				} else {
					stateOfParameters[i] = KIND_NORMAL;
				}
			} else if (p instanceof BooleanParameter) {// �����ذ�ť
				stateOfParameters[i] = KIND_SWITCH_1;
			} else if (p instanceof TextParameter) {// ����Ŀѡ��ť
				TextParameter textParameter = (TextParameter) p;
				int size = textParameter.getPossibleParameterValues().size();
				switch (size) {
				case 2:
					stateOfParameters[i] = KIND_SWITCH_2;
					break;
				case 3:
					stateOfParameters[i] = KIND_SWITCH_3;
					break;
				case 4:
					stateOfParameters[i] = KIND_SWITCH_4;
					break;
				case 5:
					stateOfParameters[i] = KIND_SWITCH_5;
					break;
				case 6:
					stateOfParameters[i] = KIND_SWITCH_5;
					break;
				default:
					stateOfParameters[i] = KIND_NORMAL;
					break;
				}
			} else {
				stateOfParameters[i] = KIND_NORMAL;
			}
		}
		mClockAadpter = new ClockAadpter(this);
		mViewPager.setAdapter(mClockAadpter);
		mViewPager.setCurrentItem(nowPage);
		if (mRadioButtons.size() > 0) {
			// mRadioButtons.get(0).setChecked(true);
			if (mRadioButtons.size() > nowPage) {
				mRadioButtons.get(nowPage).setChecked(true);
			} else {
				mRadioButtons.get(0).setChecked(true);
			}
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
					// TODO д�������ʾ������������仰ȥ��
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
		// TODO showMessageDialog((DataClockActivity.this, R.string.app_prompt,
		// R.string.app_scaning);
		startService(new Intent(DataClockActivity.this, FlashService.class));
		bindService(new Intent(DataClockActivity.this, FlashService.class),
				mServiceConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onResume() {
		LightSensor.init(this, this);

		// boolean isShakeEnable = ShakeSensor.init(this, this);
		// if (isShakeEnable) {
		//
		// } else {
		// showTag("���ٶȴ�����������");
		// }

		int tempState = LightSensor.getState(DataClockActivity.this);

		if (tempState != 0) {
			mDaily_state = tempState;
		}
		if (mDaily_state == LightSensor.STATE_DAY) {
			Constants.COLOR = Color.WHITE;
		} else if (mDaily_state == LightSensor.STATE_NIGHT) {
			Constants.COLOR = Color.RED;
		} else {
			Constants.COLOR = Color.WHITE;
		}
		super.onResume();
	}

	public void showPopWindow(View v, final int index) {
		Builder builder = new Builder(DataClockActivity.this);
		final AlertDialog dialog = builder.create();
		dialog.setTitle(mParameters.get(index).getDisplayName());
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(true);
		final View view = getLayoutInflater().inflate(
				R.layout.widget_pop_up_menu, null);

		int nowStatue = mClock_Status[index];
		int nowKind = stateOfParameters[index];
		boolean isPopupCanDispaly = false;
		switch (nowKind) {
		case KIND_DISK:
			isPopupCanDispaly = true;
			break;
		case KIND_ODOMETER:
			// isPopupCanDispaly = true;
			break;
		case KIND_PIEZOMETER:
			isPopupCanDispaly = true;
			break;
		case KIND_STOPWATCH:

			break;
		case KIND_THERMOGRAPH:
			isPopupCanDispaly = true;
			break;

		default:
			isPopupCanDispaly = false;
			break;
		}
		TextView tv_normal = null;
		TextView tv_digital = null;

		OnClickListener cl = null;
		if (isPopupCanDispaly) {
			cl = new OnClickListener() {
				public void onClick(View v) {
					dialog.dismiss();
					switch (v.getId()) {
					case R.id.pop_tv_digital:
						mClock_Status[index] = S_DIGITAL;
						break;

					default:
						mClock_Status[index] = S_NORMAL;
						break;
					}
					init(mParameters);
				}
			};
			switch (nowStatue) {
			case S_DIGITAL:// �������������ģʽ
				tv_normal = (TextView) view.findViewById(R.id.pop_tv_normal);
				tv_digital = (TextView) view.findViewById(R.id.pop_tv_digital);
				tv_digital.setEnabled(false);
				tv_normal.setEnabled(true);
				break;
			default:
				tv_normal = (TextView) view.findViewById(R.id.pop_tv_normal);
				tv_digital = (TextView) view.findViewById(R.id.pop_tv_digital);
				tv_normal.setEnabled(false);
				tv_digital.setEnabled(true);
				break;
			}
			tv_normal.setOnClickListener(cl);
			tv_digital.setOnClickListener(cl);
		} else {
			showToast(R.string.popup_nopopup);
			return;
		}
		dialog.setView(view);
		dialog.show();

		// dialog.setContentView(view, new
		// LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		// myPopupWindow.setFocusable(true);
		// myPopupWindow.setOutsideTouchable(true);
		// myPopupWindow.setContentView(view);
		// myPopupWindow.showAsDropDown(v, 0, v.getHeight() * -1);
	}

	class ClockAadpter extends PagerAdapter {

		ArrayList<View> mViews;
		LayoutInflater mInflater;
		Resources mResource;

		private ArrayList<View> allViews;
		int size;

		public View initNormal(int index) {
			if (mClock_Status[index] != 0) {
				// NULL ����ʵ�ֲ�ͬ�ı��̰汾�����ӵ�or��ͨ�ģ���
			}

			View llview = mInflater.inflate(R.layout.widget_view_normal, null);
			if (mDaily_state == LightSensor.STATE_DAY) {
				((ImageView) llview.findViewById(R.id.back))
						.setImageResource(R.drawable.widget_view_normal);
			} else {
				((ImageView) llview.findViewById(R.id.back))
						.setImageResource(R.drawable.widget_view_normal_night);
			}
			/*
			 * TextView value = (TextView) llview .findViewById(R.id.tv_value);
			 */
			TextView name = (TextView) llview.findViewById(R.id.tv_name);
			name.setText(mParameters.get(index).getDisplayName());
			name.setSelected(true);
			return llview;
		}

		public View initDisk(int index) {
			if (mClock_Status[index] != 0) {
				View view = mInflater
						.inflate(R.layout.widget_view_disk_e, null);
				TextView nameTv = (TextView) view.findViewById(R.id.tv_name);
				NumericParameter parameter = (NumericParameter) mParameters
						.get(index);
				nameTv.setText(parameter.getDisplayName());
				nameTv.setSelected(true);
				E_Disk e_Disk = (E_Disk) view.findViewById(R.id.back);
				if (mDaily_state == LightSensor.STATE_DAY) {
					e_Disk.setImageResource(R.drawable.e_widget_view_disk);
				} else {
					e_Disk.setImageResource(R.drawable.e_widget_view_disk_night);
				}

				RangeBean b = null;
				if (Constants.isLastOBDVEHICLE) {
					b = Utils.getRangeBean(parameter.getName());
				} else {
					b = Utils.getRangeBean(parameter.getParameterCategorySet());
				}
				if (null == b) {
					e_Disk.init(mDaily_state, parameter.getMaxValue()
							.getValue(), parameter.getMinValue().getValue(),
							Float.parseFloat(parameter.getParameterValue()
									.getValue().toString()));
				} else {
					e_Disk.init(
							mDaily_state,
							b.getMax(),
							b.getMin(),
							Float.parseFloat(parameter.getParameterValue()
									.getValue().toString()));
				}

				e_Disk.unit = null == parameter.getParameterUnit() ? ""
						: parameter.getParameterUnit().getLabel();
				return view;
			}
			View view = mInflater.inflate(R.layout.widget_view_disk, null);
			Disk c = (Disk) view.findViewById(R.id.clock_id);

			DiskBack backView = (DiskBack) view.findViewById(R.id.clockback);
			if (mDaily_state == LightSensor.STATE_DAY) {
				backView.setImageResource(R.drawable.widget_view_disk);
			} else {
				backView.setImageResource(R.drawable.widget_view_disk_night);
			}

			TextView nameTv = (TextView) view.findViewById(R.id.tv_name);
			NumericParameter parameter = (NumericParameter) mParameters
					.get(index);
			try {
				backView.setNowValue(Float.parseFloat(parameter
						.getParameterValue().getValue().toString()));
			} catch (Exception e) {
				showTag("�ַ�ת������");
			}
			nameTv.setText(parameter.getDisplayName());
			nameTv.setSelected(true);
			c.setHandId(R.id.second_hands);
			RangeBean b = null;
			if (Constants.isLastOBDVEHICLE) {
				b = Utils.getRangeBean(parameter.getName());
			} else {
				b = Utils.getRangeBean(parameter.getParameterCategorySet());
			}
			if (null == b) {
				c.initDatas(parameter.getMinValue().getValue(), parameter
						.getMaxValue().getValue(), null == parameter
						.getParameterUnit() ? "" : parameter.getParameterUnit()
						.getLabel());
			} else {
				c.initDatas(b.getMin(), b.getMax(), null == parameter
						.getParameterUnit() ? "" : parameter.getParameterUnit()
						.getLabel());
			}
			backView.invalidate();
			return view;
		}

		public View initOdometer(int index) {
			// TODO �����ʱֻ֧�ֵ�����ͼ
			if (mClock_Status[index] != 0) {
				View view = mInflater.inflate(R.layout.widget_view_odometer_e,
						null);
				TextView nameTv = (TextView) view.findViewById(R.id.tv_name);
				NumericParameter parameter = (NumericParameter) mParameters
						.get(index);
				nameTv.setText(parameter.getDisplayName());
				nameTv.setSelected(true);
				E_Odometer e_Odometer = (E_Odometer) view
						.findViewById(R.id.back);
				if (mDaily_state == LightSensor.STATE_DAY) {
					e_Odometer
							.setImageResource(R.drawable.widget_view_odometer);
				} else {
					e_Odometer
							.setImageResource(R.drawable.widget_view_odometer_night);
				}

				e_Odometer.number = Float.parseFloat(parameter
						.getParameterValue().getValue().toString());
				e_Odometer.unit = (null == parameter.getParameterUnit() ? ""
						: parameter.getParameterUnit().getLabel());
				return view;
			}
			View odoview = mInflater.inflate(R.layout.widget_view_odometer,
					null);
			TextView odoname = (TextView) odoview.findViewById(R.id.tv_name);
			odoname.setText(mParameters.get(index).getDisplayName());
			odoname.setSelected(true);
			return odoview;
		}

		public View initPiezoMeter(int index) {
			if (null != mClock_Status && mClock_Status[index] != 0) {
				View view = mInflater.inflate(
						R.layout.widget_view_piezometer_e, null);
				TextView nameTv = (TextView) view.findViewById(R.id.tv_name);
				NumericParameter parameter = (NumericParameter) mParameters
						.get(index);
				nameTv.setText(parameter.getDisplayName());
				nameTv.setSelected(true);
				E_Piezometer e_Piezometer = (E_Piezometer) view
						.findViewById(R.id.back);
				if (mDaily_state == LightSensor.STATE_DAY) {
					e_Piezometer
							.setImageResource(R.drawable.e_widget_view_piezometer);
				} else {
					e_Piezometer
							.setImageResource(R.drawable.e_widget_view_piezometer_night);
				}

				e_Piezometer.setNumber(Float.parseFloat(parameter
						.getParameterValue().getValue().toString()));
				e_Piezometer.unit = (null == parameter.getParameterUnit() ? ""
						: parameter.getParameterUnit().getLabel());
				return view;
			}
			View pizeoView = mInflater.inflate(R.layout.widget_view_piezometer,
					null);
			TextView pizeoname = (TextView) pizeoView
					.findViewById(R.id.tv_name);
			pizeoname.setText(mParameters.get(index).getDisplayName());
			pizeoname.setSelected(true);

			PiezometerDisk piezoc = (PiezometerDisk) pizeoView
					.findViewById(R.id.clock_id);

			PiezometerBack piezobackView = (PiezometerBack) pizeoView
					.findViewById(R.id.back);

			if (mDaily_state == LightSensor.STATE_DAY) {
				piezobackView
						.setImageResource(R.drawable.widget_view_piezometer);
			} else {
				piezobackView
						.setImageResource(R.drawable.widget_view_piezometer_night);
			}

			NumericParameter piezoparameter = (NumericParameter) mParameters
					.get(index);
			try {
				piezobackView.setNowValue(Float.parseFloat(piezoparameter
						.getParameterValue().getValue().toString()));
			} catch (Exception e) {
				showTag("�ַ�ת������");
			}
			piezoc.setHandId(R.id.second_hands);
			RangeBean b = null;
			if (Constants.isLastOBDVEHICLE) {
				b = Utils.getRangeBean(piezoparameter.getName());
			} else {
				b = Utils
						.getRangeBean(piezoparameter.getParameterCategorySet());
			}
			if (null == b) {
				piezoc.initDatas(piezoparameter.getMinValue().getValue(),
						piezoparameter.getMaxValue().getValue(),
						null == piezoparameter.getParameterUnit() ? ""
								: piezoparameter.getParameterUnit().getLabel());
			} else {
				piezoc.initDatas(b.getMin(), b.getMax(), null == piezoparameter
						.getParameterUnit() ? "" : piezoparameter
						.getParameterUnit().getLabel());
			}
			piezobackView.invalidate();
			return pizeoView;
		}

		public View initStopWatch(int index) {
			if (mClock_Status[index] != 0) {
				// NULL ����ʵ�ֲ�ͬ�ı��̰汾�����ӵ�or��ͨ�ģ���
				View stopView = mInflater.inflate(
						R.layout.widget_view_stopwatch_e, null);

				if (mDaily_state == LightSensor.STATE_DAY) {
					((ImageView) stopView.findViewById(R.id.back))
							.setImageResource(R.drawable.widget_view_time_e);
				} else {
					((ImageView) stopView.findViewById(R.id.back))
							.setImageResource(R.drawable.widget_view_time_e_night);
				}
				TextView sotpname = (TextView) stopView
						.findViewById(R.id.tv_name);
				sotpname.setText(mParameters.get(index).getDisplayName());
				sotpname.setSelected(true);
			}
			View stopView = mInflater.inflate(R.layout.widget_view_stopwatch_e,
					null);
			if (mDaily_state == LightSensor.STATE_DAY) {
				((ImageView) stopView.findViewById(R.id.back))
						.setImageResource(R.drawable.widget_view_time_e);
			} else {
				((ImageView) stopView.findViewById(R.id.back))
						.setImageResource(R.drawable.widget_view_time_e_night);
			}
			TextView sotpname = (TextView) stopView.findViewById(R.id.tv_name);
			sotpname.setText(mParameters.get(index).getDisplayName());
			sotpname.setSelected(true);
			return stopView;
		}

		public View initSwitch_1(int index) {
			if (mClock_Status[index] != 0) {
				// NULL ����ʵ�ֲ�ͬ�ı��̰汾�����ӵ�or��ͨ�ģ���
			}
			View switch1view = mInflater.inflate(R.layout.widget_view_switch_1,
					null);

			TextView switch1name = (TextView) switch1view
					.findViewById(R.id.tv_name);
			switch1name.setText(mParameters.get(index).getDisplayName());
			switch1name.setSelected(true);
			return switch1view;
		}

		public View initSwitch_2(int index) {
			if (mClock_Status[index] != 0) {
				// NULL ����ʵ�ֲ�ͬ�ı��̰汾�����ӵ�or��ͨ�ģ���
			}
			showTag("KIND_SWITCH_2");
			View switch2view = mInflater.inflate(R.layout.widget_view_switch_2,
					null);
			TextView switch2name = (TextView) switch2view
					.findViewById(R.id.tv_name);
			TwoSwitch switch2Swtich = (TwoSwitch) switch2view
					.findViewById(R.id.back);

			TextParameter textParameter = (TextParameter) mParameters
					.get(index);
			ArrayList<TextParameterValue> values = textParameter
					.getPossibleParameterValues();

			switch2Swtich.setShowingText1(values.get(0).getValue());
			switch2Swtich.setShowingText2(values.get(1).getValue());

			switch2name.setText(mParameters.get(index).getDisplayName());
			return switch2view;
		}

		public View initSwitch(int index) {
			if (mClock_Status[index] != 0) {
				// NULL ����ʵ�ֲ�ͬ�ı��̰汾�����ӵ�or��ͨ�ģ���
			}
			View switch3view = mInflater.inflate(R.layout.widget_view_switch_3,
					null);
			SwitchBack back = (SwitchBack) switch3view.findViewById(R.id.back);
			TextParameter p = (TextParameter) mParameters.get(index);
			ArrayList<String> values = new ArrayList<String>();
			p.getPossibleParameterValues();
			for (TextParameterValue pv : p.getPossibleParameterValues()) {
				values.add(pv.getValue());
			}
			back.init(values, mDaily_state);
			back.notify(p.getParameterValue().getValue().toString());

			TextView switch3name = (TextView) switch3view
					.findViewById(R.id.tv_name);
			switch3name.setText(mParameters.get(index).getDisplayName());
			return switch3view;
		}

		public View initThermograph(int index) {
			if (mClock_Status[index] != 0) {
				// TODO ����ʵ�ֲ�ͬ�ı��̰汾�����ӵ�or��ͨ�ģ���
				View therView = mInflater.inflate(
						R.layout.widget_view_thermograph_e, null);
				NumericParameter therparameter = (NumericParameter) mParameters
						.get(index);
				float max = therparameter.getMaxValue().getValue();
				float min = therparameter.getMinValue().getValue();
				float value = Float.parseFloat(therparameter
						.getParameterValue().getValue().toString());
				E_Thermograph back = (E_Thermograph) therView
						.findViewById(R.id.back);
				back.init(mDaily_state, max, min, value);
				return therView;
			}
			View therView = mInflater.inflate(R.layout.widget_view_thermograph,
					null);
			TextView thername = (TextView) therView.findViewById(R.id.tv_name);
			thername.setText(mParameters.get(index).getDisplayName());
			thername.setSelected(true);

			ThermographDisk therc = (ThermographDisk) therView
					.findViewById(R.id.clock_id);

			ThermographBack thercbackView = (ThermographBack) therView
					.findViewById(R.id.back);

			if (mDaily_state == LightSensor.STATE_DAY) {
				thercbackView
						.setImageResource(R.drawable.widget_view_thermograph);
			} else {
				thercbackView
						.setImageResource(R.drawable.widget_view_thermograph_night);
			}

			NumericParameter therparameter = (NumericParameter) mParameters
					.get(index);
			try {
				thercbackView.setNowValue(Float.parseFloat(therparameter
						.getParameterValue().getValue().toString()));
			} catch (Exception e) {
				showTag("�ַ�ת������");
			}
			therc.setHandId(R.id.second_hands);
			RangeBean b = null;
			if (Constants.isLastOBDVEHICLE) {
				b = Utils.getRangeBean(therparameter.getName());
			} else {
				b = Utils.getRangeBean(therparameter.getParameterCategorySet());
			}
			if (null == b) {
				therc.initDatas(therparameter.getMinValue().getValue(),
						therparameter.getMaxValue().getValue(),
						null == therparameter.getParameterUnit() ? ""
								: therparameter.getParameterUnit().getLabel());
			} else {
				therc.initDatas(b.getMin(), b.getMax(), null == therparameter
						.getParameterUnit() ? "" : therparameter
						.getParameterUnit().getLabel());
			}

			thercbackView.invalidate();
			return therView;
		}

		public void notifyNormal(int index) {
			ParameterValue value = mParameters.get(index).getParameterValue();
			if (null != value) {
				TextView textView = (TextView) allViews.get(index)
						.findViewById(R.id.tv_value);
				textView.setText(value.getValue().toString());
				if (mDaily_state == LightSensor.STATE_DAY) {
					((ImageView) allViews.get(index).findViewById(R.id.back))
							.setImageResource(R.drawable.widget_view_normal);
				} else {
					((ImageView) allViews.get(index).findViewById(R.id.back))
							.setImageResource(R.drawable.widget_view_normal_night);
				}
			}
		}

		public void notifyDisk(int index) {
			if (mClock_Status[index] != 0) {
				View view = allViews.get(index);
				E_Disk e_disk = (E_Disk) view.findViewById(R.id.back);
				if (mDaily_state == LightSensor.STATE_DAY) {
					e_disk.setImageResource(R.drawable.e_widget_view_disk);
				} else {
					e_disk.setImageResource(R.drawable.e_widget_view_disk_night);
				}

				Parameter parameter = (Parameter) mParameters.get(index);
				e_disk.notify(
						mDaily_state,
						Float.parseFloat(parameter.getParameterValue()
								.getValue().toString()));
				return;
			}
			View view = allViews.get(index);
			Disk clockView = (Disk) view.findViewById(R.id.clock_id);
			DiskBack back = (DiskBack) view.findViewById(R.id.clockback);

			if (mDaily_state == LightSensor.STATE_DAY) {
				back.setImageResource(R.drawable.widget_view_disk);
			} else {
				back.setImageResource(R.drawable.widget_view_disk_night);
			}

			NumericParameter numeric = (NumericParameter) mParameters
					.get(index);
			try {
				back.setNowValue(Float.parseFloat(numeric.getParameterValue()
						.getValue().toString()));
			} catch (Exception e) {
				showTag("�ַ�ת������");
			} // DiskBack back = (DiskBack) view
				// .findViewById(R.id.clockback);
			int ratio = clockView.vehicleDataBean.ratio;
			float max = clockView.vehicleDataBean.disPlayMax * ratio;
			float min = clockView.vehicleDataBean.disPlayMin * ratio;
			float nowvalue = (Float) (numeric.getParameterValue().getValue());
			clockView.setToDegrees(getRollDegree(nowvalue, min, max));
			clockView.start();
		}

		public float getRollDegree(float now, float min, float max) {
			if (now > max) {
				now = max;
				showToast("");// ��ݳ�����Χ����
			}
			if (now < min) {
				now = min;// ��ݳ�����Χ����
				showToast("");
			}
			return (now - min) / (max - min) * (360 - 120) - (30 + 90);
		}

		public void notifyOdometer(int index) {
			// TODO �����ʱֻ֧�ֵ�����ͼ
			if (mClock_Status[index] != 0) {
				View view = allViews.get(index);

				E_Odometer e_Odometer = (E_Odometer) view
						.findViewById(R.id.back);
				if (mDaily_state == LightSensor.STATE_DAY) {
					e_Odometer
							.setImageResource(R.drawable.widget_view_odometer);
				} else {
					e_Odometer
							.setImageResource(R.drawable.widget_view_odometer_night);
				}

				Parameter parameter = (Parameter) mParameters.get(index);
				e_Odometer.number = Float.parseFloat(parameter
						.getParameterValue().getValue().toString());
				view.invalidate();
				return;
			}
		}

		public void notifyPiezoMeter(int index) {
			if (mClock_Status[index] != 0) {
				View view = allViews.get(index);
				E_Piezometer e_Piezometer = (E_Piezometer) view
						.findViewById(R.id.back);
				if (mDaily_state == LightSensor.STATE_DAY) {
					e_Piezometer
							.setImageResource(R.drawable.e_widget_view_piezometer);
				} else {
					e_Piezometer
							.setImageResource(R.drawable.e_widget_view_piezometer_night);
				}
				Parameter parameter = (Parameter) mParameters.get(index);
				e_Piezometer.setNumber(Float.parseFloat(parameter
						.getParameterValue().getValue().toString()));
				view.invalidate();
				return;
			}
			View pizeoView = allViews.get(index);
			PiezometerDisk piezoc = (PiezometerDisk) pizeoView
					.findViewById(R.id.clock_id);
			PiezometerBack back = (PiezometerBack) pizeoView
					.findViewById(R.id.back);

			if (mDaily_state == LightSensor.STATE_DAY) {
				back.setImageResource(R.drawable.widget_view_piezometer);
			} else {
				back.setImageResource(R.drawable.widget_view_piezometer_night);
			}

			NumericParameter pizeonumeric = (NumericParameter) mParameters
					.get(index);
			try {
				back.setNowValue(Float.parseFloat(pizeonumeric
						.getParameterValue().getValue().toString()));
			} catch (Exception e) {
				showTag("�ַ�ת������");
			}
			int pizeoratio = piezoc.vehicleDataBean.ratio;
			float pizeomax = piezoc.vehicleDataBean.disPlayMax * pizeoratio;
			float pizeomin = piezoc.vehicleDataBean.disPlayMin * pizeoratio;
			float pizeonowvalue = (Float) (pizeonumeric.getParameterValue()
					.getValue());
			piezoc.setToDegrees(getRollDegree(pizeonowvalue, pizeomin, pizeomax));

			piezoc.start();
		}

		public void notifyStopWatch(int index) {
			View sotpview = allViews.get(index);
			TimeNum timeNum = (TimeNum) sotpview.findViewById(R.id.back);
			NumericParameter stopnumeric = (NumericParameter) mParameters
					.get(index);
			timeNum.setTime((long) Float.parseFloat(stopnumeric
					.getParameterValue().getValue().toString()));

			if (mDaily_state == LightSensor.STATE_DAY) {
				timeNum.setImageResource(R.drawable.widget_view_time_e);
			} else {
				timeNum.setImageResource(R.drawable.widget_view_time_e_night);
			}
		}

		/** û�к�ҹ�Ͱ�����л� */
		public void notifySwitch_1(int index) {
			View switch1 = allViews.get(index);

			// if (mDaily_state == LightSensor.STATE_DAY) {
			// ((ImageView) switch1.findViewById(R.id.back))
			// .setImageResource(R.drawable.widget_view_s);
			// } else {
			// ((ImageView) switch1.findViewById(R.id.back))
			// .setImageResource(R.drawable.widget_view_odometer);
			// }

			MyRadioButton switch1back = (MyRadioButton) switch1
					.findViewById(R.id.back);
			BooleanParameter switch1boolean = (BooleanParameter) mParameters
					.get(index);
			BooleanParameterValue booleanvalue = (BooleanParameterValue) switch1boolean
					.getParameterValue();
			String strTrue = booleanvalue.getPossibleValue1();
			if (switch1boolean.getParameterValue().getValue().equals(strTrue)) {
				switch1back.setChecked(true);
			} else {
				switch1back.setChecked(false);
			}
			switch1back.invalidate();
		}

		/** û�к�ҹ�Ͱ�����л� */
		public void notifySwitch_2(int index) {
			View switch2 = allViews.get(index);
			TwoSwitch switch2TwoSwitch = (TwoSwitch) switch2
					.findViewById(R.id.back);

			// if (mDaily_state == LightSensor.STATE_DAY) {
			// ((ImageView) switch2.findViewById(R.id.back))
			// .setImageResource(R.drawable.widget_view_sw);
			// } else {
			// ((ImageView) switch2.findViewById(R.id.back))
			// .setImageResource(R.drawable.widget_view_odometer);
			// }

			switch2TwoSwitch.setText(mParameters.get(index).getParameterValue()
					.getValue().toString());
		}

		public void notifySwitch(int index) {
			SwitchBack back = (SwitchBack) allViews.get(index).findViewById(
					R.id.back);
			Parameter parameter = mParameters.get(index);
			back.initChange(mDaily_state);
			back.notify(parameter.getParameterValue().getValue().toString());
		}

		public void notifyThermograph(int index) {

			if (mClock_Status[index] != 0) {
				// TODO ����ʵ�ֲ�ͬ�ı��̰汾�����ӵ�or��ͨ�ģ���
				View therview = allViews.get(index);
				NumericParameter therparameter = (NumericParameter) mParameters
						.get(index);
				float value = Float.parseFloat(therparameter
						.getParameterValue().getValue().toString());
				E_Thermograph back = (E_Thermograph) therview
						.findViewById(R.id.back);
				back.notify(value, mDaily_state);
				return;
			}
			View therview = allViews.get(index);
			ThermographDisk thermographDisk = (ThermographDisk) therview
					.findViewById(R.id.clock_id);

			ThermographBack back = (ThermographBack) therview
					.findViewById(R.id.back);

			if (mDaily_state == LightSensor.STATE_DAY) {
				back.setImageResource(R.drawable.widget_view_thermograph);
			} else {
				back.setImageResource(R.drawable.widget_view_thermograph_night);
			}

			NumericParameter thernumeric = (NumericParameter) mParameters
					.get(index);
			try {
				back.setNowValue(Float.parseFloat(thernumeric
						.getParameterValue().getValue().toString()));
			} catch (Exception e) {
				showTag("�ַ�ת������");
			}
			int therratio = thermographDisk.vehicleDataBean.ratio;
			float thermax = thermographDisk.vehicleDataBean.disPlayMax
					* therratio;
			float thermin = thermographDisk.vehicleDataBean.disPlayMin
					* therratio;
			float thernowvalue = (Float) (thernumeric.getParameterValue()
					.getValue());
			thermographDisk.setToDegrees(getRollDegree(thernowvalue, thermin,
					thermax));
			thermographDisk.start();

		}

		public ClockAadpter(Context context) {
			int tempState = LightSensor.getState(DataClockActivity.this);
			if (tempState != 0) {
				mDaily_state = tempState;
			}
			if (mDaily_state == LightSensor.STATE_DAY) {
				Constants.COLOR = Color.WHITE;
			} else {
				Constants.COLOR = Color.RED;
			}
			mViews = new ArrayList<View>();
			allViews = new ArrayList<View>();
			mInflater = LayoutInflater.from(context);
			mRadioButtons = new ArrayList<RadioButton>();
			mRadioGroup.removeAllViews();
			mResource = context.getResources();
			// ������
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
						v = initNormal(j);
						break;
					case KIND_DISK:
						v = initDisk(j);
						break;
					case KIND_ODOMETER:
						v = initOdometer(j);
						break;
					case KIND_PIEZOMETER:
						v = initPiezoMeter(j);
						break;
					case KIND_STOPWATCH:
						v = initStopWatch(j);
						break;
					case KIND_SWITCH_1:
						v = initSwitch_1(j);
						break;
					case KIND_SWITCH_2:
						v = initSwitch_2(j);
						break;
					case KIND_SWITCH_3:
						v = initSwitch(j);
						break;
					case KIND_SWITCH_4:
						v = initSwitch(j);
						break;
					case KIND_SWITCH_5:
						v = initSwitch(j);
						break;
					case KIND_THERMOGRAPH:
						v = initThermograph(j);
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
						v = llview;
					}
					linearLayout.addView(v);
					allViews.add(v);
					/* ÿһ��С����ĵ���¼� */
					v.setOnLongClickListener(longItemClick);
					v.setOnClickListener(itemClick);
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
				group.setOnClickListener(itemClick);
			}
		}

		OnClickListener itemClick = new OnClickListener() {
			public void onClick(View v) {

				// DataHistogramActivity.mHandler.sendMessage(msg);
				startActivity(new Intent(DataClockActivity.this,
						DataHistogramActivity.class));
				DataClockActivity.this.finish();
				APPViewPageActivity.form_status = 2;
			}
		};

		OnLongClickListener longItemClick = new OnLongClickListener() {

			public boolean onLongClick(View v) {
				showPopWindow(v, mClockAadpter.allViews.indexOf(v));
				return true;
			}

		};
		float degree = 100;

		@Override
		public void notifyDataSetChanged() {
			if (null == mClock_Status) {
				return;
			}
			int tempState = LightSensor.getState(DataClockActivity.this);
			if (tempState != 0) {
				mDaily_state = tempState;
			}
			if (mDaily_state == LightSensor.STATE_DAY) {
				Constants.COLOR = Color.WHITE;
			} else {
				Constants.COLOR = Color.RED;
			}
			for (int j = 0; j < allViews.size(); j++) {
				ParameterValue value = mParameters.get(j).getParameterValue();
				if (null == value) {
					super.notifyDataSetChanged();
					continue;
				}
				switch (stateOfParameters[j]) {
				case KIND_NORMAL:
					notifyNormal(j);
					break;
				case KIND_DISK:// ��ʾ�Ǳ���
					notifyDisk(j);
					break;
				case KIND_ODOMETER:
					notifyOdometer(j);
					break;
				case KIND_PIEZOMETER:
					notifyPiezoMeter(j);
					break;
				case KIND_STOPWATCH:
					notifyStopWatch(j);
					break;
				case KIND_SWITCH_1:
					notifySwitch_1(j);
					break;
				case KIND_SWITCH_2:
					notifySwitch_2(j);
					break;
				case KIND_SWITCH_3:
				case KIND_SWITCH_4:
				case KIND_SWITCH_5:
					notifySwitch(j);
					break;
				case KIND_THERMOGRAPH:
					notifyThermograph(j);
					break;
				default:
					if (null != value) {
						TextView textView = (TextView) allViews.get(j)
								.findViewById(R.id.tv_value);
						textView.setText(value.getValue().toString());
					}
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
		showTag("activity" + "onDestroy");
		unbindService(mServiceConnection);
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		mClock_Status = null;
	}

	@Override
	public void finish() {
		try {
			// mClock_Status = null;
			LightSensor.removeLightListen();
			stopService(new Intent(DataClockActivity.this, FlashService.class));
		} catch (Exception e) {
		}
		super.finish();
	}

	public void onLightStateChange(int state) {
		mDaily_state = state;

		if (state == LightSensor.STATE_DAY) {
			Constants.COLOR = Color.WHITE;
		} else {
			Constants.COLOR = Color.RED;
		}
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.parameter_memu_next:
			// DataHistogramActivity.mHandler.sendMessage(msg);
			startActivity(new Intent(DataClockActivity.this,
					DataHistogramActivity.class));
			DataClockActivity.this.finish();
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

	public void onShakeListener() {
		// DataHistogramActivity.mHandler.sendMessage(msg);
		startActivity(new Intent(DataClockActivity.this,
				DataHistogramActivity.class));
		DataClockActivity.this.finish();
		APPViewPageActivity.form_status = 2;
		showTag("onShake");
	}

}
