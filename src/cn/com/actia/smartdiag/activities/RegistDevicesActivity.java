package cn.com.actia.smartdiag.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import cn.com.actia.smartdiag.R;
import cn.com.actia.smartdiag.constants.Constants;
import cn.com.actia.smartdiag.tools.Utils;

@SuppressLint("HandlerLeak")
public class RegistDevicesActivity extends APPBaseActivity implements
		OnClickListener {
	private EditText et1;
	private EditText et2;

	Handler mHandler = new Handler() {
		public void handleMessage(final android.os.Message msg) {
			cancelProgressDialog();
			switch (msg.what) {
			case Constants.ERROR_OTHER_ERRORS:
				new Handler(getMainLooper()).post(new Runnable() {
					public void run() {
						showMessageDialog(RegistDevicesActivity.this,
								R.string.app_prompt, msg.obj.toString(),
								Constants.STYLE_NORMAL);
						cancelProgressDialog();
					}
				});
				break;
			case 0x1111:
				showPrograssDialog(RegistDevicesActivity.this,
						getString(R.string.app_prompt),
						getString(R.string.regist_devices_is_registing));
				break;

			default:
				new Handler(getMainLooper()).post(new Runnable() {
					public void run() {
						showToast(R.string.regist_devices_success);
					}
				});
				setResult(RESULT_OK);
				RegistDevicesActivity.this.finish();
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.regist_devices_layout);
		init();
	}

	private void init() {
		et1 = (EditText) findViewById(R.id.regist_devices_et1);
		et2 = (EditText) findViewById(R.id.regist_devices_et2);
		findViewById(R.id.regist_devices_ok).setOnClickListener(this);
		findViewById(R.id.regist_devices_cancel).setOnClickListener(this);
	}

	protected boolean CheckNetwork() {
		boolean flag = false;
		ConnectivityManager cwjManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cwjManager.getActiveNetworkInfo() != null)
			flag = cwjManager.getActiveNetworkInfo().isAvailable();
		if (!flag) {
			Builder b = new AlertDialog.Builder(this).setTitle(
					R.string.regist_devices_not_network).setMessage(
					R.string.regist_devices_open_network);
			b.setPositiveButton(R.string.app_ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							Intent mIntent = new Intent("/");
							if (android.os.Build.VERSION.SDK_INT > 10) {
								mIntent = new Intent(
										android.provider.Settings.ACTION_WIRELESS_SETTINGS);
							} else {
								mIntent = new Intent();
								ComponentName component = new ComponentName(
										"com.android.settings",
										"com.android.settings.WirelessSettings");
								mIntent.setComponent(component);
								mIntent.setAction("android.intent.action.VIEW");
							}
							// ComponentName comp = new ComponentName(
							// "com.android.settings",
							// "com.android.settings.WirelessSettings");
							// mIntent.setComponent(comp);
							// mIntent.setAction("android.intent.action.VIEW");
							startActivity(mIntent);
						}
					})
					.setNegativeButton(R.string.app_cancel,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									RegistDevicesActivity.this.finish();
								}
							}).create();
			b.show();
		}
		return flag;
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!CheckNetwork()) {
			return;
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.regist_devices_ok:// 如果是確定按鈕
			if (!CheckNetwork()) {
				return;
			}
			final String pVCISerialNumber = et1.getText().toString().trim();
			final String pVCIActivationCode = et2.getText().toString().trim();

			new Thread() {
				public void run() {
					mHandler.sendEmptyMessage(0x1111);
					Message msg = Utils.RegistDevice(pVCISerialNumber,
							pVCIActivationCode);
					try {
						sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					mHandler.handleMessage(msg);
				};
			}.start();
			break;

		default:
			// 如果点击的是注册页面，的退出按钮，直接退出此界面，不退出程序
			// Constants.BLUETOOTH_ADRESS = "";
			// Constants.BLUETOOTH_NAME = "";
			startActivity(new Intent(RegistDevicesActivity.this,
					VCISettingActivity.class));
			RegistDevicesActivity.this.finish();
			break;
		}
	}
}
