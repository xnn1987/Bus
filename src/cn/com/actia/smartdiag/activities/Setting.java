package cn.com.actia.smartdiag.activities;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;
import cn.com.actia.dto.VersionDTO;
import cn.com.actia.smartdiag.R;
import cn.com.actia.smartdiag.constants.Constants;
import cn.com.actia.smartdiag.model.wapper.entity.web.CheckSDUpdateResponse;
import cn.com.actia.smartdiag.model.wapper.entity.web.DownloadSDUpdateResponse;
import cn.com.actia.smartdiag.tools.Utils;
import cn.com.actia.smartdiag.wrapper.CloudWebWrapper;

@SuppressLint("HandlerLeak")
public class Setting extends PreferenceActivity implements
		OnPreferenceChangeListener, OnPreferenceClickListener {
	static final String TAG = "PreferenceActivityDemoActivity";
	SharedPreferences preference = null;

	Preference preferenceVersion = null;
	Preference preferenceCheckUpdate = null;
	Preference preferenceSetting = null;
	Preference preferenceAllowUpdate = null;
	PackageInfo packInfo;

	CheckBoxPreference timePreference = null;
	CheckBoxPreference lightPreference = null;
	EditTextPreference day_valuePreference = null;
	EditTextPreference night_valuePreference = null;
	EditTextPreference light_valuePreference = null;

	EditTextPreference text_showPreference = null;

	String timeUnit_h;
	String timeUnit_s;

	String lightUnit = "  lux";

	private ProgressDialog mProgressDialog;

	Toast mToast;

	public void showToast(String msg) {
		if (null == mToast) {
			mToast = Toast.makeText(this, msg, Toast.LENGTH_LONG);
			mToast.show();
		} else {
			mToast.setText(msg);
			mToast.show();
		}
	}

	public void showToast(int strID) {
		String msg = getString(strID);
		if (null == mToast) {
			mToast = Toast.makeText(this, msg, Toast.LENGTH_LONG);
			mToast.show();
		} else {
			mToast.setText(msg);
			mToast.show();
		}
	}

	Handler loginHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			cancelProgressDialog();
			switch (msg.what) {
			case Constants.NEED_TO_ACTIVATE:
				registDevices();
				break;
			case 0:// 表示成功

				File file = new File(msg.obj.toString());
				if (file.isFile()) {
					Log.e("OpenFile", msg.obj.toString());
					Intent intent = new Intent();
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.setAction(android.content.Intent.ACTION_VIEW);
					intent.setDataAndType(Uri.fromFile(file),
							"application/vnd.android.package-archive");
					startActivity(intent);
				}
				break;

			default:
				Builder builder = new Builder(Setting.this);
				builder.setTitle(Setting.this.getResources().getString(
						R.string.app_prompt));
				if (null != msg.obj)
					builder.setMessage(msg.obj.toString());
				else {

				}
				builder.setNeutralButton(
						Setting.this.getResources().getString(R.string.app_ok),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
							}
						});
				// builder.setNegativeButton(Setting.this.getResources()
				// .getString(R.string.app_cancel),
				// new DialogInterface.OnClickListener() {
				// public void onClick(DialogInterface dialog,
				// int which) {
				// dialog.cancel();
				// }
				// });
				final AlertDialog dialog = builder.create();
				dialog.show();
				break;
			}
		};
	};

	Handler checkHandler = new Handler() {

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 设置显示Preferences
		addPreferencesFromResource(R.xml.setting);
		timeUnit_h = getResources().getString(R.string.setting_time_unit_h);
		timeUnit_s = getResources().getString(R.string.setting_time_unit_s);
		initPreference();
		initsummary();
		initListener();
	}

	private void initPreference() {

		preference = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());

		preferenceVersion = findPreference(getString(R.string.key_SMART_CENTER_VERSION));

		timePreference = (CheckBoxPreference) findPreference(getResources()
				.getString(R.string.key_IS_TIME_ENABLE));
		lightPreference = (CheckBoxPreference) findPreference(getResources()
				.getString(R.string.key_IS_LIGHT_ENABLE));
		day_valuePreference = (EditTextPreference) findPreference(getResources()
				.getString(R.string.key_SWITCH_TO_DAY));
		night_valuePreference = (EditTextPreference) findPreference(getResources()
				.getString(R.string.key_SWITCH_TO_NIGHT));
		light_valuePreference = (EditTextPreference) findPreference(getResources()
				.getString(R.string.key_SWITCH_LIGHT));

		text_showPreference = (EditTextPreference) findPreference(getResources()
				.getString(R.string.key_TIME_TEXT_SHOW));

		preferenceCheckUpdate = findPreference(getString(R.string.key_CHECK_FOR_UPDATE));
		preferenceSetting = findPreference(getString(R.string.key_SETTING));
		preferenceAllowUpdate = findPreference(getString(R.string.key_ALLOW_UPDATA));

		// light_valuePreference.setDialogLayoutResource(R.layout.loading_layout);

	}

	private void initsummary() {

		String day = preference.getString(
				getString(R.string.key_SWITCH_TO_DAY), "5");
		// if (null == day || "".equals(day) || "null".equals(day)) {
		// day_valuePreference.getEditor()
		// .putString(day_valuePreference.getKey(), "2").commit();
		// day = 2 + "";
		// }
		day_valuePreference.setSummary(day + "" + timeUnit_h);
		day_valuePreference.getEditText().setInputType(// 转变为白天的时间
				EditorInfo.TYPE_CLASS_NUMBER);

		String night = preference.getString(
				getString(R.string.key_SWITCH_TO_NIGHT), "17");
		// if (null == night || "".equals(night) || "null".equals(night)) {
		// night_valuePreference.getEditor()
		// .putString(night_valuePreference.getKey(), "17").commit();
		// night = 17 + "";
		// }

		night_valuePreference.setSummary(night + timeUnit_h);
		night_valuePreference.getEditText().setInputType(// 转变为晚上的时间
				EditorInfo.TYPE_CLASS_NUMBER);

		String light = preference.getString(
				getString(R.string.key_SWITCH_LIGHT), "20");
		// if (null == light || "".equals(light) || "null".equals(light)) {
		// light_valuePreference.getEditor()
		// .putString(light_valuePreference.getKey(), "20").commit();
		// light = 20 + "";
		// }

		light_valuePreference.setSummary(light + lightUnit);
		light_valuePreference.getEditText().setInputType(// 转变为隧道模式的光感值
				EditorInfo.TYPE_CLASS_NUMBER);

		text_showPreference.setSummary(text_showPreference.getText()
				+ timeUnit_s);

		text_showPreference.getEditText().setInputType(
				EditorInfo.TYPE_CLASS_NUMBER);
		try {
			packInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		preferenceVersion.setSummary(packInfo.versionName + "");
	}

	private void initListener() {
		day_valuePreference.setOnPreferenceChangeListener(this);
		night_valuePreference.setOnPreferenceChangeListener(this);
		light_valuePreference.setOnPreferenceChangeListener(this);

		text_showPreference.setOnPreferenceChangeListener(this);
		preferenceCheckUpdate.setOnPreferenceClickListener(this);
		preferenceAllowUpdate.setOnPreferenceClickListener(this);
	}

	public boolean onPreferenceClick(Preference preference) {
		System.out.println(preference.getKey());
		/* 如果是版本信息 */
		if (preference.getKey().equals(
				getString(R.string.key_SMART_CENTER_VERSION))) {

		} else if (preference.getKey().equals(
				getString(R.string.key_CHECK_FOR_UPDATE))) {/* 如果是检查更新 */
			checkUpdate(this.preference.getBoolean(
					getString(R.string.key_ALLOW_UPDATA), true));
		} else if (preference.getKey().equals(
				getString(R.string.key_ALLOW_UPDATA))) {/* 如果是允许检查更新 */

		}
		return false;
	}

	Handler upHandler = new Handler() {
		public void handleMessage(final Message msg) {
			switch (msg.what) {
			case 200:
				final String content3 = getString(R.string.end_get_load_new_version);
				final String versioncode_final = versionCode;
				showPrograssDialog(Setting.this,
						getString(R.string.app_prompt), content3 /*
																 * + "\n" +
																 * versioncode_final
																 */);
				new Thread(new Runnable() {

					public void run() {
						Message resultMessage = new Message();
						// 如果检测成功
						DownloadSDUpdateResponse downloadSDUpdateResponse = CloudWebWrapper
								.downloadSDUpdate((CheckSDUpdateResponse) msg.obj);
						// 如果下载成功
						if (downloadSDUpdateResponse.getCode() == CloudWebWrapper.NO_ERROR) {
							resultMessage.what = Constants.NO_ERROR;
							VersionDTO dto = downloadSDUpdateResponse.getDto()
									.getDto();
							/* 将下载好的文件存放地址传回去 */
							String fileName = dto.getDownUrl().replace(
									dto.getPublishDate() + "", "");
							resultMessage.obj = downloadSDUpdateResponse
									.getPath() + fileName;
						} else {// 如果下载没成功
							resultMessage.what = Constants.ERROR_OTHER_ERRORS;
							resultMessage.obj = Utils
									.cloudECodeToString(downloadSDUpdateResponse
											.getCode());
						}
						loginHandler.handleMessage(resultMessage);
					}
				}).start();

				break;

			default:
				break;
			}
		};
	};

	String versionCode;

	// 检查更新
	public void checkUpdate(final boolean allow) {
		final String title1 = getString(R.string.app_prompt);
		String content1 = getString(R.string.end_get_conn_sdcloud);
		showPrograssDialog(this, title1, content1);
		new Thread() {
			public void run() {
				Message loginMessage = Utils.login();
				switch (loginMessage.what) {
				case 0:// 登陆成功或者是错误信息为Constants.UPDATE_SD_NEEDCHECK;都需要检查更新
				case -1:// 登陆成功或者是错误信息为Constants.UPDATE_SD_NEEDCHECK;都需要检查更新
					final String content2 = getString(R.string.end_get_check_version_info);
					new Handler(getMainLooper()).post(new Runnable() {
						public void run() {
							showPrograssDialog(Setting.this, title1, content2);
						}
					});
					String versionCode = "";
					try {
						versionCode = getPackageManager().getPackageInfo(
								getPackageName(), 0).versionCode
								+ "";
					} catch (NameNotFoundException e) {
						e.printStackTrace();
					}

					Message resultMessage = new Message();
					final CheckSDUpdateResponse checkSDUpdateResponse = CloudWebWrapper
							.checkSDUpdate(versionCode, allow);
					System.out.println("b:" + allow);
					if (checkSDUpdateResponse.getCode() == 0) {
						// TODO
						final Message isRun = new Message();
						isRun.what = 0;
						Setting.this.versionCode = versionCode;

						new Handler(getMainLooper()).post(new Runnable() {
							public void run() {
								new AlertDialog.Builder(Setting.this)
										.setTitle(R.string.app_prompt)
										// TODO改为string.xml里面的内容
										.setMessage(
												getString(R.string.end_makesure_download))
										.setNegativeButton(
												R.string.app_cancel,
												new DialogInterface.OnClickListener() {
													public void onClick(
															DialogInterface dialog,
															int which) {
														cancelProgressDialog();
													}
												})
										.setCancelable(false)
										.setNeutralButton(
												R.string.app_ok,
												new DialogInterface.OnClickListener() {
													public void onClick(
															DialogInterface dialog,
															int which) {
														upHandler
																.handleMessage(upHandler
																		.obtainMessage(
																				200,
																				checkSDUpdateResponse));
													}
												}).create().show();
							}
						});// 需要休息超过4000ms，供vci刷新设备

						// upHandler.handleMessage(upHandler.obtainMessage(200,
						// checkSDUpdateResponse));
					} else {
						resultMessage.what = Constants.ERROR_OTHER_ERRORS;
						String msg = checkSDUpdateResponse.getDto()
								.getMessage();
						msg = Utils.cloudECodeToString(checkSDUpdateResponse
								.getCode());
						resultMessage.obj = msg;
					}

					break;

				default:
					loginHandler.sendMessage(loginMessage);
					break;
				}
				// loginHandler.sendMessage(loginMessage);
			};
		}.start();
	}

	public boolean onPreferenceChange(Preference preference, Object newValue) {
		System.out.println(preference);
		// 判断是哪个Preference改变了
		if (preference.getKey().equals(light_valuePreference.getKey())) {
			int k;
			try {
				k = Integer.parseInt(newValue.toString());
			} catch (Exception e) {
				e.printStackTrace();
				k = 20;
			}
			if (k < 0) {
				String msg = getString(R.string.setting_over_lay) + "(>0)";
				showToast(msg);
			}
			this.preference.edit()
					.putString(light_valuePreference.getKey(), k + "").commit();
			light_valuePreference.setSummary(k + lightUnit);

		}
		if (preference.getKey().equals(night_valuePreference.getKey())) {
			int k;
			try {
				k = Integer.parseInt(newValue.toString());
			} catch (Exception e) {
				e.printStackTrace();
				k = 17;
			}
			if (k > 24 || k < 0) {
				String msg = getString(R.string.setting_over_lay) + "(0-24)";
				showToast(msg);
				k = 17;
			}
			this.preference.edit()
					.putString(night_valuePreference.getKey(), k + "").commit();
			night_valuePreference.setSummary(k + timeUnit_h);
		}
		if (preference.getKey().equals(day_valuePreference.getKey())) {
			int k;
			try {
				k = Integer.parseInt(newValue.toString());
			} catch (Exception e) {
				e.printStackTrace();
				k = 5;
			}
			if (k > 24 || k < 0) {
				String msg = getString(R.string.setting_over_lay) + "(0-24)";
				showToast(msg);
				k = 5;
			}
			this.preference.edit()
					.putString(day_valuePreference.getKey(), k + "").commit();
			day_valuePreference.setSummary(k + timeUnit_h);
		}
		if (preference.getKey().equals(text_showPreference.getKey())) {
			int k;
			try {
				k = Integer.parseInt(newValue.toString());
			} catch (Exception e) {
				e.printStackTrace();
				k = 2;
			}
			this.preference.edit()
					.putString(text_showPreference.getKey(), k + "").commit();
			text_showPreference.setSummary(k + timeUnit_s);
		}
		return false;
	}

	public void showPrograssDialog(Context context, String title, String msg) {
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.setMessage(msg);
			mProgressDialog.show();
			return;
		} else {

		}
		mProgressDialog = ProgressDialog.show(context, title, msg);
		/* 进度框不能被终止 */
		mProgressDialog.setCancelable(false);
	}

	public void cancelProgressDialog() {
		if (null != mProgressDialog && mProgressDialog.isShowing()) {
			mProgressDialog.cancel();
		}
		mProgressDialog = null;
	}

	public void registDevices() {
		startActivity(new Intent(this, RegistDevicesActivity.class));
		this.finish();
	}

}