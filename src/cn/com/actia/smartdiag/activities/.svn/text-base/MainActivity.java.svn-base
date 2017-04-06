package cn.com.actia.smartdiag.activities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import cn.com.actia.smartdiag.R;
import cn.com.actia.smartdiag.constants.Constants;
import cn.com.actia.smartdiag.tools.Utils;
import cn.com.actia.smartdiag.wrapper.DiagBusinessLogicWrapper;

@SuppressLint("HandlerLeak")
public class MainActivity extends APPBaseActivity implements OnClickListener {
	/**
	 * 蓝牙适配器
	 */
	private SharedPreferences mSharedPreferences;
	private int mVersionCode;

	Handler loginHandler = new Handler() {
		public void handleMessage(Message msg) {
			cancelProgressDialog();
			switch (msg.what) {
			case 0:
				showToast(R.string.app_login_success);
				break;

			default:
				if (msg.obj != null) {
					showMessageDialog(MainActivity.this, R.string.app_prompt,
							msg.obj.toString(), Constants.STYLE_NORMAL);
				} else {
					showMessageDialog(MainActivity.this, R.string.app_prompt,
							R.string.app_login_fail, Constants.STYLE_NORMAL);
				}
				break;
			}
		};
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mSharedPreferences = getSharedPreferences(
				Constants.SHAREPREFERECE_TAG_NAME, Context.MODE_PRIVATE);
		showUserAgreementDialog();
		initListeners();
		/*
		 * showPrograssDialog(this,
		 * getResources().getString(R.string.app_prompt),
		 * Utils.getStringResourse(this, R.string.app_waiting));
		 */

		/*
		 * new Thread() { public void run() { Message msg =
		 * Utils.checksuitable("AA:BB:CC:DD:EE:FF"); mhHandler.sendMessage(msg);
		 * } }.start();
		 */
	}

	public void initListeners() {
		findViewById(R.id.main_top_guide_smartdiag).setOnClickListener(this);
		findViewById(R.id.main_iv1).setOnClickListener(this);
		findViewById(R.id.main_iv2).setOnClickListener(this);
		findViewById(R.id.main_iv3).setOnClickListener(this);
		findViewById(R.id.main_iv4).setOnClickListener(this);
		findViewById(R.id.main_iv5).setOnClickListener(this);
		findViewById(R.id.main_iv6).setOnClickListener(this);
		findViewById(R.id.main_iv7).setOnClickListener(this);
		findViewById(R.id.main_top_guide_right1).setOnClickListener(this);
		findViewById(R.id.main_top_guide_right2).setOnClickListener(this);
		findViewById(R.id.main_top_guide_right3).setOnClickListener(this);
	}

	/** 用户刚安装或者升级时显示用户需知界面 */
	private void showUserAgreementDialog() {
		try {
			/* 获取当前版本的版本号 */
			mVersionCode = getPackageManager().getPackageInfo(getPackageName(),
					0).versionCode;
			/* 若有VersionCode变量则表示曾经安装过，只需要判断两个版本是否相同 */
			if (mSharedPreferences.contains("VersionCode")) {
				// 若版本没有变化则不需要做任何处理
				if (mSharedPreferences.getInt("VersionCode", 0) == mVersionCode) {
					if (mSharedPreferences.contains(Constants.COMPLETED_ADRESS)) {
						Constants.BLUETOOTH_ADRESS = mSharedPreferences
								.getString(Constants.COMPLETED_ADRESS, "");
						Constants.BLUETOOTH_NAME = mSharedPreferences
								.getString(Constants.COMPLETED_NAME, "");
					} else {
						startActivity(new Intent(MainActivity.this,
								VCISettingActivity.class));
					}
					return;
				}
			}
			// 版本变化了或没有显示过用户须知，但是没有显示过用户须知
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			AlertDialog dialog = builder.create();
			dialog.setCancelable(false);
			dialog.setTitle(R.string.main_user_agreement);
			try {
				BufferedReader bf = null;
				if (Constants.Language.equals("zh"))
					bf = new BufferedReader(new InputStreamReader(
							getResources().getAssets().open(
									"EULA_SmartDiagCenter_ZH.txt")));
				else {
					bf = new BufferedReader(new InputStreamReader(
							getResources().getAssets().open(
									"EULA_SmartDiagCenter_EN.txt")));
				}
				StringBuffer sb = new StringBuffer();
				String str = null;
				while (null != (str = bf.readLine())) {
					sb.append(str).append("\r\n");
				}
				dialog.setMessage(sb.toString());
				bf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// 这个是确定按钮
			dialog.setButton(getResources().getString(R.string.app_agree),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							mSharedPreferences.edit()
									.putInt("VersionCode", mVersionCode)
									.commit();
							if (mSharedPreferences
									.contains(Constants.COMPLETED_ADRESS)) {
								Constants.BLUETOOTH_ADRESS = mSharedPreferences
										.getString(Constants.COMPLETED_ADRESS,
												"");
								Constants.BLUETOOTH_NAME = mSharedPreferences
										.getString(Constants.COMPLETED_NAME, "");
							} else {
								startActivity(new Intent(MainActivity.this,
										VCISettingActivity.class));
							}
						}
					});
			// 这个是取消按钮
			dialog.setButton2(getResources().getString(R.string.app_disagree),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
							am.killBackgroundProcesses("cn.com.actia.smartdiag");
							System.exit(0);
							MainActivity.this.finish();
						}
					});
			dialog.show();
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void startUserConfigure() {
		// Utils.Login(this, null);
		showPrograssDialog(this, getString(R.string.app_prompt),
				getString(R.string.login_islogin));
		new Thread() {
			public void run() {
				final Message msg = Utils.login();
				new Handler(getMainLooper()).post(new Runnable() {

					public void run() {
						loginHandler.handleMessage(msg);
					}
				});
			};
		}.start();
		showTag("startUserConfigure");
	}

	private void startCustomerConfigure() {
		startActivity(new Intent(MainActivity.this, Setting.class));
		showTag("startCustomerConfigure");
	}

	private void startVCIConfigure() {
		startActivity(new Intent(MainActivity.this, SettingVciSetting.class));
		showTag("startVCIConfigure");
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		// case R.id.main_memu_user:
		// startUserConfigure();
		// break;
		case R.id.main_memu_customer:
			startCustomerConfigure();
			break;

		case R.id.main_memu_vci:
			startVCIConfigure();
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		Utils.initUtils(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public void onBackPressed() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final AlertDialog d = builder.create();
		d.setTitle(R.string.app_prompt);
		d.setMessage(getResources().getString(R.string.main_sure_to_exit));
		d.setButton(getResources().getString(R.string.app_ok),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
						am.killBackgroundProcesses("cn.com.actia.smartdiag");
						MainActivity.this.finish();
						System.exit(0);
					}
				});
		d.setButton2(getResources().getString(R.string.app_cancel),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						d.dismiss();
					}
				});
		d.show();
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.main_top_guide_smartdiag:
			break;
		// 车辆状态
		case R.id.main_iv1:
			if (isAdressAvillible()) {
				if (Constants.isLastOBDVEHICLE) {

				} else {
					Constants.isSelectVehicleConfigSystemInfo = false;
					Utils.setAllParameters(null);
					Utils.setUsableParameters(null);
				}
				Constants.isLastOBDVEHICLE = true;
				startActivity(new Intent(this, VehicleStateActivity.class));
			}
			break;
		// 深度诊断
		case R.id.main_iv2:
			if (!isAdressAvillible()) {
				return;
			}
			if (Constants.isLastOBDVEHICLE) {
				Constants.isSelectVehicleConfigSystemInfo = false;
				Utils.setAllParameters(null);
				Utils.setUsableParameters(null);
			} else {

			}
			Constants.isLastOBDVEHICLE = false;
			app.Vehcle_Select_Activity_Name = Utils.getStringResourse(
					MainActivity.this, R.string.select_title_deep);
			app.mNeedBean = Utils.getdepthDiagnosisNeed();
			startActivity(new Intent(MainActivity.this, SelectSuccess.class));
			// startActivity(new Intent(this, DeepDiagActivity.class));
			break;
		// 维护保养
		case R.id.main_iv3:
			if (!isAdressAvillible()) {
				return;
			}
			if (Constants.isLastOBDVEHICLE) {
				Constants.isSelectVehicleConfigSystemInfo = false;
				Utils.setAllParameters(null);
				Utils.setUsableParameters(null);
			} else {

			}
			app.Vehcle_Select_Activity_Name = Utils.getStringResourse(
					MainActivity.this, R.string.select_title_main);
			Constants.isLastOBDVEHICLE = false;
			app.mNeedBean = Utils.getMaintenanceNeed();
			startActivity(new Intent(MainActivity.this, SelectSuccess.class));
			break;
		// 行车管理
		case R.id.main_iv4:
			if (!isAdressAvillible()) {
				return;
			}
			if (Constants.isLastOBDVEHICLE) {

			} else {
				Constants.isSelectVehicleConfigSystemInfo = false;
				Utils.setAllParameters(null);
				Utils.setUsableParameters(null);
			}
			Constants.isLastOBDVEHICLE = true;
			startActivity(new Intent(this, DrivingManagerActivity.class));
			break;
		// 燃油管理
		case R.id.main_iv5:
			if (!isAdressAvillible()) {
				return;
			}
			if (Constants.isLastOBDVEHICLE) {
				Constants.isSelectVehicleConfigSystemInfo = false;
				Utils.setAllParameters(null);
				Utils.setUsableParameters(null);
			} else {

			}
			app.Vehcle_Select_Activity_Name = Utils.getStringResourse(
					MainActivity.this, R.string.select_title_fuel);
			Constants.isLastOBDVEHICLE = false;
			app.mNeedBean = Utils.getFuelManagementNeed();
			startActivity(new Intent(MainActivity.this, SelectSuccess.class));
			// showToast(R.string.tips_no_message);
			break;
		// 快速扫描
		case R.id.main_iv6:
			if (!isAdressAvillible()) {
				return;
			}
			if (Constants.isLastOBDVEHICLE) {

			} else {
				Constants.isSelectVehicleConfigSystemInfo = false;
				Utils.setAllParameters(null);
				Utils.setUsableParameters(null);
			}
			Constants.isLastOBDVEHICLE = true;
			startActivity(new Intent(MainActivity.this, FastScanActivity.class));
			break;
		// 爱车贴士
		case R.id.main_iv7:
			startActivity(new Intent(MainActivity.this, TipsFirstActivity.class));
			break;
		case R.id.main_top_guide_right1:
			startUserConfigure();
			break;
		case R.id.main_top_guide_right2:
			startCustomerConfigure();
			break;
		case R.id.main_top_guide_right3:
			startVCIConfigure();
			break;

		default:
			break;
		}
	}

}
