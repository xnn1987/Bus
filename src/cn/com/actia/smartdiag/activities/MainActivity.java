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
	 * ����������
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

	/** �û��հ�װ��������ʱ��ʾ�û���֪���� */
	private void showUserAgreementDialog() {
		try {
			/* ��ȡ��ǰ�汾�İ汾�� */
			mVersionCode = getPackageManager().getPackageInfo(getPackageName(),
					0).versionCode;
			/* ����VersionCode�������ʾ������װ����ֻ��Ҫ�ж������汾�Ƿ���ͬ */
			if (mSharedPreferences.contains("VersionCode")) {
				// ���汾û�б仯����Ҫ���κδ���
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
			// �汾�仯�˻�û����ʾ���û���֪������û����ʾ���û���֪
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
			// �����ȷ����ť
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
			// �����ȡ����ť
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
		// ����״̬
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
		// ������
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
		// ά������
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
		// �г�����
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
		// ȼ�͹���
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
		// ����ɨ��
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
		// ������ʿ
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
