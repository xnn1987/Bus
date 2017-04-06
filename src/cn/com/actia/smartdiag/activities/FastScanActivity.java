package cn.com.actia.smartdiag.activities;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.util.FloatMath;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.com.actia.businesslogic.DTC;
import cn.com.actia.businesslogic.DTCStatus;
import cn.com.actia.businesslogic.DTCType;
import cn.com.actia.smartdiag.R;
import cn.com.actia.smartdiag.constants.Constants;
import cn.com.actia.smartdiag.model.wapper.entity.ClearDtcResponse;
import cn.com.actia.smartdiag.tools.Utils;
import cn.com.actia.smartdiag.wrapper.DiagBusinessLogicWrapper;

/**
 * 快速扫描
 * 
 * @author XtShadow
 * 
 */
@SuppressLint({ "FloatMath", "HandlerLeak" })
public class FastScanActivity extends APPBaseActivity implements
		OnClickListener {
	private Button btn_0;
	/** 地盘 */
	private Button btn_1;
	/** 车身 */
	private Button btn_2;
	/** 发动机 */
	private Button btn_3;
	/** 网络 */
	private Button btn_4;

	private TextView tv_0;
	/** 地盘数字 */
	private TextView tv_1;
	/** 车身 数字 */
	private TextView tv_2;
	/** 发动机数字 */
	private TextView tv_3;
	/** 网络数字 */
	private TextView tv_4;

	private int num_PENDING_wrong = 0;
	private int num_CONFIRMED_wrong = 0;
	private int num_UNKNOWN_wrong = 0;
	private int num_PERMANENT_wrong = 0;

	// private Drawable hasErrorDrawable;
	// private Drawable notErrorDrawable;

	/** 得分 */
	private TextView tv_score;
	/** 扫描时间 */
	private TextView tv_time;
	/** 错误个数 */
	private TextView tv_num;
	/** 放大镜按钮 */
	private ImageView iv_search;

	/** 故障总数 */
	private int num_all_wrong;
	/** 底盘故障数 */
	private int num_chassis_wrong = 0;
	/** 车身故障数 */
	private int num_carrosserie_wrong = 0;
	/** 发动机故障数 */
	private int num_engine_wrong = 0;
	/** 网络故障数 */
	private int num_network_wrong = 0;

	private int score = 100;

	private TextView tv_change;

	private Thread threadBtnFlash;
	private Thread threadScanSearch;
	private boolean isScaning = true;
	private boolean isActivitySurvival;

	/** 这个是一个计时器数值与flash有关 */
	private int timer;
	/** 搜索按钮移动的半径 */
	private float flashDistance = 10;
	/** 搜索按钮刷新的间隔时间 */
	private long flashV = 20;
	/** 搜索按钮每次旋转的角度 */
	private float flashDegree = (float) (Math.PI / 80f);
	private long flashTime;

	private List<DTC> DTCList;

	private boolean isInit = true;

	boolean isSearchCouldBePress;

	/** 刷新时的放大镜的动画 */
	private Runnable flashRunnable = new Runnable() {
		public void run() {
			Matrix moveMatrix = new Matrix();
			moveMatrix.setTranslate(FloatMath.cos(timer * flashDegree)
					* flashDistance, FloatMath.sin(timer * flashDegree)
					* flashDistance + 10f);
			iv_search.setImageMatrix(moveMatrix);

			int t = timer;

			if (t % 20 == 0) {
				switch ((t / 50) % 3) {
				case 0:
					if (isScaning)
						tv_change.setText(R.string.fast_scan_is_scan_1);
					break;
				case 1:
					if (isScaning)
						tv_change.setText(R.string.fast_scan_is_scan_2);
					break;
				case 2:
					if (isScaning)
						tv_change.setText(R.string.fast_scan_is_scan_3);
					break;

				default:
					break;
				}
			}
		}
	};

	private Runnable tvTimeRunnable = new Runnable() {
		public void run() {
			tv_time.setText(flashTime + "");
		}
	};
	private SharedPreferences bluetoothPreference;

	public void setDTCList(List<DTC> dTCList) {
		DTCList = dTCList;
	}

	// public List<DTC> getDTCList() {
	// // if (null == DTCList)
	// // DTCList = Utils.getDtcs();
	// // return DTCList;
	// }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		bluetoothPreference = getSharedPreferences(
				Constants.SHAREPREFERECE_TAG_NAME, Context.MODE_PRIVATE);
		setContentView(R.layout.fast_scan_layout);
		initWidget();
		initListener();
		initWrongNumAndImage();
		initOthers();

	}

	public void initWrongNumAndImage() {
		num_carrosserie_wrong = 0;
		num_chassis_wrong = 0;
		num_engine_wrong = 0;
		num_network_wrong = 0;

		num_PENDING_wrong = 0;
		num_CONFIRMED_wrong = 0;
		num_UNKNOWN_wrong = 0;
		num_PERMANENT_wrong = 0;
		isInit = true;
		findViewById(R.id.fast_scan_clear_dtc).setVisibility(View.GONE);
		setWrongNumAndImage();
		// findViewById(R.id.fast_scan_table).setVisibility(View.INVISIBLE);
	}

	public void setWrongNumAndImage() {
		/* 总错误是由地盘，车身，发动机，网络的所有错误的和 */
		num_all_wrong = num_carrosserie_wrong + num_chassis_wrong
				+ num_engine_wrong + num_network_wrong;
		// TODO 这里计算得分的方式需要完善
		score = 100 - num_CONFIRMED_wrong * 5 - num_PENDING_wrong * 3
				- num_PERMANENT_wrong * 10 - num_UNKNOWN_wrong * 10;
		if (score < 0) {
			score = 0;
		}
		tv_num.setText(num_all_wrong + "");
		if (num_chassis_wrong == 0) {
			tv_1.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
		}
		if (num_all_wrong != 0) {
			btn_0.setEnabled(true);
			tv_0.setText(num_all_wrong + "");
			btn_0.setCompoundDrawablesWithIntrinsicBounds(null, getResources()
					.getDrawable(R.drawable.fast_scan_more_iv0), null, null);
			tv_0.setVisibility(View.VISIBLE);
		} else {
			btn_0.setCompoundDrawablesWithIntrinsicBounds(null, getResources()
					.getDrawable(R.drawable.fast_scan_iv0), null, null);
			btn_0.setEnabled(false);
			tv_0.setVisibility(View.INVISIBLE);
		}
		if (num_chassis_wrong != 0) {
			btn_1.setEnabled(true);
			tv_1.setVisibility(View.VISIBLE);
			tv_1.setText(num_chassis_wrong + "");
			btn_1.setCompoundDrawablesWithIntrinsicBounds(null, getResources()
					.getDrawable(R.drawable.fast_scan_more_iv1), null, null);
		} else {
			btn_1.setEnabled(false);
			btn_1.setCompoundDrawablesWithIntrinsicBounds(null, getResources()
					.getDrawable(R.drawable.fast_scan_iv1), null, null);
			tv_1.setVisibility(View.INVISIBLE);
		}
		if (num_carrosserie_wrong != 0) {
			btn_2.setEnabled(true);
			tv_2.setVisibility(View.VISIBLE);
			tv_2.setText(num_carrosserie_wrong + "");
			btn_2.setCompoundDrawablesWithIntrinsicBounds(null, getResources()
					.getDrawable(R.drawable.fast_scan_more_iv2), null, null);
		} else {
			btn_2.setEnabled(false);
			btn_2.setCompoundDrawablesWithIntrinsicBounds(null, getResources()
					.getDrawable(R.drawable.fast_scan_iv2), null, null);
			tv_2.setVisibility(View.INVISIBLE);
		}

		if (num_engine_wrong != 0) {
			btn_3.setEnabled(true);
			tv_3.setVisibility(View.VISIBLE);
			tv_3.setText(num_engine_wrong + "");
			btn_3.setCompoundDrawablesWithIntrinsicBounds(null, getResources()
					.getDrawable(R.drawable.fast_scan_more_iv3), null, null);
		} else {
			btn_3.setEnabled(false);
			btn_3.setCompoundDrawablesWithIntrinsicBounds(null, getResources()
					.getDrawable(R.drawable.fast_scan_iv3), null, null);
			tv_3.setVisibility(View.INVISIBLE);
		}
		if (num_network_wrong != 0) {
			btn_4.setEnabled(true);
			tv_4.setVisibility(View.VISIBLE);
			tv_4.setText(num_network_wrong + "");
			btn_4.setCompoundDrawablesWithIntrinsicBounds(null, getResources()
					.getDrawable(R.drawable.fast_scan_more_iv4), null, null);
		} else {
			btn_4.setEnabled(false);
			btn_4.setCompoundDrawablesWithIntrinsicBounds(null, getResources()
					.getDrawable(R.drawable.fast_scan_iv4), null, null);
			tv_4.setVisibility(View.INVISIBLE);
		}

		if (score == 100) {
			// findViewById(R.id.fast_scan_table).setVisibility(View.GONE);
			if (isInit) {

			} else {
				tv_change.setText(R.string.fast_scan_is_scan_3);
				isInit = false;
			}
			findViewById(R.id.fast_scan_clear_dtc).setVisibility(View.GONE);
		} else {
			findViewById(R.id.fast_scan_table).setVisibility(View.VISIBLE);
			findViewById(R.id.fast_scan_clear_dtc).setVisibility(View.VISIBLE);
			findViewById(R.id.fast_scan_clear_dtc).setOnClickListener(
					new OnClickListener() {
						public void onClick(View v) {
							ClearDtcResponse result = DiagBusinessLogicWrapper
									.clearDtc();
							if (result.getCode() == 0) {
								showToast(R.string.fast_scan_clear_dtc_success);
								isScaning = true;
								timer = 0;
								isBtnPressed = true;
								initWrongNumAndImage();
								initOthers();
							} else {
								showMessageDialog(
										FastScanActivity.this,
										R.string.app_prompt,
										R.string.fast_scan_clear_dtc_failed,
										cn.com.actia.smartdiag.constants.Constants.STYLE_NORMAL);
							}

						}
					});
		}
		if (num_all_wrong <= 1) {
			((TextView) findViewById(R.id.fast_scan_faults))
					.setText(R.string.fast_scan_scan_time_3_pl);
			((Button) findViewById(R.id.fast_scan_clear_dtc))
					.setText(R.string.fast_scan_clear_dtc_pl);
			if (num_all_wrong <= 0) {
				if (!isBtnPressed) {
					tv_change.setText(R.string.fast_scan_congratuation);
				}
			} else if (num_all_wrong == 1) {
				tv_change.setText(R.string.fast_scan_fault_code_pl);
			}
		} else {
			((TextView) findViewById(R.id.fast_scan_faults))
					.setText(R.string.fast_scan_scan_time_3);
			((Button) findViewById(R.id.fast_scan_clear_dtc))
					.setText(R.string.fast_scan_clear_dtc);
			if (num_all_wrong <= 0) {

			} else if (num_all_wrong == 1) {
				tv_change.setText(R.string.fast_scan_fault_code_pl);
			} else {
				tv_change.setText(R.string.fast_scan_fault_code);
			}
		}
		isBtnPressed = false;
		tv_score.setText("" + score);
		((ProgressBar) findViewById(R.id.fast_scan_progress_bar))
				.setProgress(score);
		((ProgressBar) findViewById(R.id.fast_scan_progress_bar_shadow))
				.setProgress(score);
	}

	/** 是否按下了button的标志 */
	boolean isBtnPressed = false;

	public void initWidget() {
		btn_0 = (Button) findViewById(R.id.fast_scan_iv0);
		btn_1 = (Button) findViewById(R.id.fast_scan_iv1);
		btn_2 = (Button) findViewById(R.id.fast_scan_iv2);
		btn_3 = (Button) findViewById(R.id.fast_scan_iv3);
		btn_4 = (Button) findViewById(R.id.fast_scan_iv4);

		tv_0 = (TextView) findViewById(R.id.fast_scan_tv0);
		tv_1 = (TextView) findViewById(R.id.fast_scan_tv1);
		tv_2 = (TextView) findViewById(R.id.fast_scan_tv2);
		tv_3 = (TextView) findViewById(R.id.fast_scan_tv3);
		tv_4 = (TextView) findViewById(R.id.fast_scan_tv4);

		iv_search = (ImageView) findViewById(R.id.fast_scan_search);
		tv_score = (TextView) findViewById(R.id.fast_scan_score);
		tv_time = (TextView) findViewById(R.id.fast_scan_time);
		tv_num = (TextView) findViewById(R.id.fast_scan_num);

		tv_change = (TextView) findViewById(R.id.fast_scan_tv_with_change);
	}

	public void initListener() {
		btn_0.setOnClickListener(this);
		btn_1.setOnClickListener(this);
		btn_2.setOnClickListener(this);
		btn_3.setOnClickListener(this);
		btn_4.setOnClickListener(this);

		iv_search.setOnClickListener(this);
	}

	public void initOthers() {
		// hasErrorDrawable =
		// getResources().getDrawable(R.drawable.fast_scan_iv1);
		// notErrorDrawable =
		// getResources().getDrawable(R.drawable.fast_scan_iv1);
		isActivitySurvival = true;
		// tv_change.setText(R.string.fast_scan_is_scan);
		isSearchCouldBePress = false;
		threadBtnFlash = new Thread() {
			public void run() {
				Handler handler = new Handler(getMainLooper());
				while (isActivitySurvival) {
					try {
						Thread.sleep(flashV);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (isScaning) {
						timer += 1;
						handler.post(flashRunnable);
						if (timer % (1000 / flashV) == 0) {
							flashTime = timer / (1000 / flashV);
							handler.post(tvTimeRunnable);
						}
					} else {

					}
				}
			}
		};
		threadBtnFlash.start();
		threadScanSearch = new Thread(new Runnable() {
			public void run() {
				DTCList = Utils.getDtcs(FastScanActivity.this, mHandler,
						bluetoothPreference);
				if (null != DTCList) {
					for (DTC dtc : DTCList) {
						DTCStatus state = dtc.getDtcStatus();
						if (state == DTCStatus.CONFIRMED) {
							num_CONFIRMED_wrong++;
						}
						if (state == DTCStatus.PENDING) {
							num_PENDING_wrong++;
						}
						if (state == DTCStatus.PERMANENT) {
							num_PERMANENT_wrong++;
						}
						if (state == DTCStatus.UNKNOWN) {
							num_UNKNOWN_wrong++;
						}

						DTCType type = dtc.getDtcType();
						if (type == DTCType.BODY) {
							num_carrosserie_wrong++;
						}
						if (type == DTCType.CHASSIS) {
							num_chassis_wrong++;
						}
						if (type == DTCType.NETWORK) {
							num_network_wrong++;
						}
						if (type == DTCType.POWERTRAIN) {
							num_engine_wrong++;
						}

					}
				} else {

				}
				new Handler(getMainLooper()).postDelayed(new Runnable() {
					public void run() {
						isActivitySurvival = false;
						isScaning = false;
						isSearchCouldBePress = true;
						showToast(R.string.fast_scan_end);
						setWrongNumAndImage();
					}
				}, 2000);

			}
		});
		threadScanSearch.start();
	}

	Handler mHandler = new Handler() {
		public void handleMessage(final android.os.Message msg) {
			switch (msg.what) {
			case Constants.NEED_TO_ACTIVATE:
				registDevices();
				break;
			case Constants.NO_ERROR:
				break;
			default:
				new Handler(getMainLooper()).post(new Runnable() {

					public void run() {
						try {
							showMessageDialog(FastScanActivity.this,
									getString(R.string.app_prompt),
									msg.obj.toString(),
									Constants.STYLE_EXIT_ACTIVITY);
						} catch (Exception e) {
							// 可能会又空指针异常
						}
					}
				});
				break;
			}
			cancelProgressDialog();
		};
	};

	public void onClick(View v) {
		if (!isSearchCouldBePress) {
			showToast(R.string.fast_scan_wait);
			return;
		}
		Intent intent = new Intent(FastScanActivity.this,
				FastScanListActivity.class);
		ArrayList<Bundle> bundles = new ArrayList<Bundle>();
		switch (v.getId()) {
		case R.id.fast_scan_iv0:/* 全车 */
			for (DTC dtc : DTCList) {
				Bundle b = new Bundle();
				b.putString("code", dtc.getCode());
				b.putString("descrip", dtc.getDescriptionLabel());
				b.putString("state", dtc.getDTCStatusLabel());
				if (dtc.getDtcType().equals(DTCType.CHASSIS)) {
					b.putInt("imgID", R.drawable.fast_scan_iv1_normal);
				} else if (dtc.getDtcType().equals(DTCType.BODY)) {
					b.putInt("imgID", R.drawable.fast_scan_iv2_normal);
				} else if (dtc.getDtcType().equals(DTCType.POWERTRAIN)) {
					b.putInt("imgID", R.drawable.fast_scan_iv3_normal);
				} else if (dtc.getDtcType().equals(DTCType.NETWORK)) {
					b.putInt("imgID", R.drawable.fast_scan_iv4_normal);
				} else {
					b.putInt("imgID", R.drawable.fast_scan_iv0_normal);
				}
				bundles.add(b);

			}
			break;
		case R.id.fast_scan_iv1:/* 底盘 */
			for (DTC dtc : DTCList) {
				if (dtc.getDtcType() == DTCType.CHASSIS) {
					Bundle b = new Bundle();
					b.putString("code", dtc.getCode());
					b.putString("descrip", dtc.getDescriptionLabel());
					b.putString("state", dtc.getDTCStatusLabel());
					b.putInt("imgID", R.drawable.fast_scan_iv1_normal);
					bundles.add(b);

				}
			}
			break;
		case R.id.fast_scan_iv2:/* 车身 */
			for (DTC dtc : DTCList) {
				if (dtc.getDtcType() == DTCType.BODY) {
					Bundle b = new Bundle();
					b.putString("code", dtc.getCode());
					b.putString("descrip", dtc.getDescriptionLabel());
					b.putString("state", dtc.getDTCStatusLabel());
					b.putInt("imgID", R.drawable.fast_scan_iv2_normal);
					bundles.add(b);
				}
			}
			break;
		case R.id.fast_scan_iv3:/* 发动机 */
			for (DTC dtc : DTCList) {
				if (dtc.getDtcType() == DTCType.POWERTRAIN) {
					Bundle b = new Bundle();
					b.putString("code", dtc.getCode());
					b.putString("descrip", dtc.getDescriptionLabel());
					b.putString("state", dtc.getDTCStatusLabel());
					b.putInt("imgID", R.drawable.fast_scan_iv3_normal);
					bundles.add(b);
				}
			}
			break;
		case R.id.fast_scan_iv4:/* 网络 */
			for (DTC dtc : DTCList) {
				if (dtc.getDtcType() == DTCType.NETWORK) {
					Bundle b = new Bundle();
					b.putString("code", dtc.getCode());
					b.putString("descrip", dtc.getDescriptionLabel());
					b.putString("state", dtc.getDTCStatusLabel());
					b.putInt("imgID", R.drawable.fast_scan_iv4_normal);
					bundles.add(b);
				}
			}
			break;
		case R.id.fast_scan_search:
			isScaning = true;
			timer = 0;
			isBtnPressed = true;
			initWrongNumAndImage();
			initOthers();
			return;
		}

		if (bundles.size() > 0) {
			intent.putExtra("data", bundles);
			startActivity(intent);
		} else {
			showToast(R.string.fast_scan_noerrorcode);
		}

	}

}
