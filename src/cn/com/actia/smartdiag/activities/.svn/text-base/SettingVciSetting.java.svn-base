package cn.com.actia.smartdiag.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.widget.Toast;
import cn.com.actia.smartdiag.R;
import cn.com.actia.smartdiag.constants.Constants;
import cn.com.actia.smartdiag.model.wapper.entity.GetVCIInfoResponse;
import cn.com.actia.smartdiag.model.wapper.entity.SetVCIAddressResponse;
import cn.com.actia.smartdiag.model.wapper.entity.web.CheckVCIUpdateResponse;
import cn.com.actia.smartdiag.model.wapper.entity.web.DownloadVCIUpdateResponse;
import cn.com.actia.smartdiag.model.wapper.entity.web.FlashVCIResponse;
import cn.com.actia.smartdiag.tools.Utils;
import cn.com.actia.smartdiag.wrapper.CloudWebWrapper;
import cn.com.actia.smartdiag.wrapper.DiagBusinessLogicWrapper;

/*
 * Author: poper Email:xut@bluemobi.cn
 * Created Date:2013-3-27
 * Copyright @ 2013 BU
 * Description: 类描述
 *
 * History:
 */
public class SettingVciSetting extends PreferenceActivity implements
		OnPreferenceClickListener {
	SharedPreferences preference = null;

	Preference preference1 = null;
	Preference preference2 = null;
	Preference preference3 = null;
	Preference preference4 = null;
	Preference preference5 = null;

	boolean isfirmwareExist = true;

	Handler loginHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			cancelProgressDialog();
			switch (msg.what) {
			case Constants.NEED_TO_ACTIVATE:
				registDevices();
				break;
			case 0:// 表示成功
				new Handler().post(new Runnable() {

					public void run() {
						new AlertDialog.Builder(SettingVciSetting.this)
								.setTitle(R.string.app_prompt)
								.setMessage(R.string.vci_setting_success)
								.setNegativeButton(R.string.app_ok, null)
								.create().show();
					}
				});
				break;

			default:
				Builder builder = new Builder(SettingVciSetting.this);
				builder.setTitle(SettingVciSetting.this.getResources()
						.getString(R.string.app_prompt));
				if (null != msg.obj)
					builder.setMessage(msg.obj.toString());
				else {

				}
				builder.setNeutralButton(SettingVciSetting.this.getResources()
						.getString(R.string.app_ok),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
							}
						});
				final AlertDialog dialog = builder.create();
				dialog.show();
				break;
			}
		};
	};

	private SharedPreferences bluetoothPreference;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.vcisetting);

		bluetoothPreference = getSharedPreferences(
				Constants.SHAREPREFERECE_TAG_NAME, Context.MODE_PRIVATE);
		preference = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());

		preference1 = findPreference(getResources().getString(
				R.string.vci_key_1));
		preference2 = findPreference(getResources().getString(
				R.string.vci_key_2));
		preference3 = findPreference(getResources().getString(
				R.string.vci_key_3));
		preference4 = findPreference(getResources().getString(
				R.string.vci_key_4));
		preference5 = findPreference(getResources().getString(
				R.string.vci_key_5));

		preference1.setOnPreferenceClickListener(this);
		preference4.setOnPreferenceClickListener(this);
		preference5.setOnPreferenceClickListener(this);
		initPreference(false);
	}

	Handler mhHandler = new Handler() {
		public void handleMessage(Message msg) {
			System.out.println("~~~" + msg);
			cancelProgressDialog();
			if (msg.what == Constants.NO_ERROR) {
				preference2.setSummary(getBootloaderVersion);
				preference3.setSummary(getgetVCIVersion);
			} else if (msg.what == Constants.NEED_UPDATA_NO_FIRWARE) {
				preference2.setSummary(getBootloaderVersion);
				isfirmwareExist = false;
				String summary = getString(R.string.end_nofirware);
				System.out.println("!!!" + summary);
				preference3.setSummary(summary);// 没有版本号，显示为无版本号
			} else if (msg.what == -200) {
			} else {
				showMessageDialog(msg.obj.toString());
			}
		};
	};
	String getBootloaderVersion;
	String getgetVCIVersion;

	boolean isRestart = false;

	private void initPreference(final boolean isAdresschanged) {
		if (null != Constants.BLUETOOTH_ADRESS
				&& !"".equals(Constants.BLUETOOTH_ADRESS)) {
			preference1.setSummary(Constants.BLUETOOTH_NAME);// 如果已经又蓝牙设备连接了
			new Thread(new Runnable() {

				public void run() {
					new Handler(getMainLooper()).post(new Runnable() {
						public void run() {
							showPrograssDialog(SettingVciSetting.this,
									getString(R.string.app_prompt),
									getString(R.string.app_waiting));
						}
					});
					if (isAdresschanged) {
						// DiagBusinessLogicWrapper.resetWrapper();
						try {
							Thread.sleep(5000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}

					SetVCIAddressResponse res = DiagBusinessLogicWrapper
							.setVCIAddress(Constants.BLUETOOTH_ADRESS);
					System.out.println(res.getCode() + ""
							+ Utils.diagECodeToString(res.getCode()));

					if (res.getCode() == DiagBusinessLogicWrapper.NO_ERROR) {
						Message msg = Utils.isCompatible(PreferenceManager
								.getDefaultSharedPreferences(SettingVciSetting.this));
						System.out.println("~~~" + msg);
						if (msg.what == Constants.NEED_UPDATA_NO_FIRWARE
								|| msg.what == Constants.NO_ERROR
								|| msg.what == Constants.NEED_TO_ACTIVATE) {
							GetVCIInfoResponse getVciInfoResponse = DiagBusinessLogicWrapper
									.getVCIInfo();
							if (getVciInfoResponse.getCode() == DiagBusinessLogicWrapper.NO_ERROR) {
								getBootloaderVersion = getVciInfoResponse
										.getBootloaderVersion();
								getgetVCIVersion = getVciInfoResponse
										.getVCIVersion();
								mhHandler.sendEmptyMessage(Constants.NO_ERROR);
							} else if (getVciInfoResponse.getCode() == DiagBusinessLogicWrapper.NO_FIRMWARE_ERROR) {// VCI没有FIRMWARE
								// TODO
								getBootloaderVersion = getVciInfoResponse
										.getBootloaderVersion();
								mhHandler
										.sendEmptyMessage(Constants.NEED_UPDATA_NO_FIRWARE);
							} else if (getVciInfoResponse.getCode() == DiagBusinessLogicWrapper.NEED_TO_ACTIVATE) {
								loginHandler
										.sendEmptyMessage(Constants.NEED_TO_ACTIVATE);
							} else if (msg.what == Constants.NEED_UPDATA_NO_FIRWARE) {
								mhHandler
										.sendEmptyMessage(Constants.NEED_UPDATA_NO_FIRWARE);
							} else {
								mhHandler.sendEmptyMessage(-200);
							}
						} else {
							mhHandler.sendMessage(msg);
						}
					} else if (res.getCode() == DiagBusinessLogicWrapper.NEED_TO_ACTIVATE) {
						loginHandler
								.sendEmptyMessage(Constants.NEED_TO_ACTIVATE);
					} else if (DiagBusinessLogicWrapper.isVCIConnected()
							.getCode() == DiagBusinessLogicWrapper.NO_ERROR) {
						GetVCIInfoResponse getVciInfoResponse = DiagBusinessLogicWrapper
								.getVCIInfo();
						if (getVciInfoResponse.getCode() == DiagBusinessLogicWrapper.NO_ERROR) {
							getBootloaderVersion = getVciInfoResponse
									.getBootloaderVersion();
							getgetVCIVersion = getVciInfoResponse
									.getVCIVersion();
							mhHandler.sendEmptyMessage(Constants.NO_ERROR);
						} else if (getVciInfoResponse.getCode() == DiagBusinessLogicWrapper.NO_FIRMWARE_ERROR) {// VCI没有FIRMWARE
							// TODO
							getBootloaderVersion = getVciInfoResponse
									.getBootloaderVersion();
							mhHandler
									.sendEmptyMessage(Constants.NEED_UPDATA_NO_FIRWARE);
						} else if (getVciInfoResponse.getCode() == DiagBusinessLogicWrapper.NEED_TO_ACTIVATE) {
							loginHandler
									.sendEmptyMessage(Constants.NEED_TO_ACTIVATE);
						} else {
							mhHandler.sendEmptyMessage(-200);
							return;
						}
					} else {
						mhHandler.sendEmptyMessage(-200);
						return;
					}
				}
			}).start();
		} else {
			if (isRestart) {
				this.finish();
			} else {
				startActivityForResult(new Intent(SettingVciSetting.this,
						VCISettingActivity.class), vcisetting_code);
			}
		}
	}

	private Handler dialoghandler = new Handler() {// 用来更改dialog的状态
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constants.DOWNLOAD_VCI_START:
				System.out.println("DOWNLOAD_VCI_START");
				break;
			case Constants.DOWNLOAD_VCI_SUCCESS:
				System.out.println("DOWNLOAD_VCI_SUCCESS");
				break;
			case Constants.DOWNLOAD_VCI_FAILD:
				System.out.println("DOWNLOAD_VCI_FAILD");
				break;
			case Constants.FLASH_VCI_START:
				System.out.println("FLASH_VCI_START");
				break;
			case Constants.FLASH_VCI_SUCCESS:
				System.out.println("FLASH_VCI_SUCCESS");
				break;
			case Constants.FLASH_VCI_FAILD:
				System.out.println("FLASH_VCI_FAILD");
				break;

			default:
				break;
			}
		};
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// if (resultCode != RESULT_CANCELED) {

		// initPreference(false);
		// }

		switch (requestCode) {
		case vcisetting_code:
			if (resultCode == RESULT_OK) {
				isRestart = true;
				initPreference(true);
			}
			break;

		default:
			if (resultCode != RESULT_CANCELED) {
				isRestart = true;
				initPreference(false);
			}
			break;
		}
	}

	final int vcisetting_code = 101;
	final int regist_devices_code = 102;

	public boolean onPreferenceClick(Preference preference) {
		if (preference.getKey().equals(
				getResources().getString(R.string.vci_key_1))) {// 点击跳转到vci的匹配界面
			startActivityForResult(new Intent(SettingVciSetting.this,
					VCISettingActivity.class), vcisetting_code);
		} else if (preference.getKey().equals(
				getResources().getString(R.string.vci_key_4))) {// 点击检测VCI的更新
			System.out.println(this.preference);
			checkUpdate();
		} else {// 点击暂时没什么特别的作用

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
		startActivityForResult(new Intent(this, RegistDevicesActivity.class),
				regist_devices_code);
		this.finish();
	}

	public void showMessageDialog(String msg) {
		Builder builder = new Builder(SettingVciSetting.this);
		builder.setTitle(getString(R.string.app_prompt));
		builder.setMessage(msg);
		System.out.println(getResources().getString(R.string.app_ok));
		builder.setNeutralButton(R.string.app_ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						((Dialog) dialog)
								.setOnCancelListener(new OnCancelListener() {
									public void onCancel(DialogInterface dialog) {
										SettingVciSetting.this
												.startActivityForResult(
														new Intent(
																SettingVciSetting.this,
																VCISettingActivity.class),
														vcisetting_code);
									}
								});
						dialog.cancel();
					}
				});
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	Handler uphandler = new Handler() {
		public void handleMessage(final Message msg) {
			switch (msg.what) {
			case 200:
				final Message resultMessage = new Message();
				final String content3 = getString(R.string.end_get_load_new_version);// "正下载最新的版本";
				cancelProgressDialog();
				showPrograssDialog(SettingVciSetting.this,
						getString(R.string.app_prompt), content3);
				new Thread(new Runnable() {
					public void run() {
						dialoghandler
								.sendEmptyMessage(Constants.DOWNLOAD_VCI_START);// 开始刷新
						final DownloadVCIUpdateResponse downloadVCIUpdateResponse = CloudWebWrapper
								.downloadVCIUpdate((CheckVCIUpdateResponse) msg.obj);
						// 如果下载成功
						if (downloadVCIUpdateResponse.getCode() == CloudWebWrapper.NO_ERROR) {
							resultMessage.what = Constants.NO_ERROR;
							dialoghandler
									.sendEmptyMessage(Constants.FLASH_VCI_START);// 开始刷新
						} else {// 如果下载没成功
							dialoghandler
									.sendEmptyMessage(Constants.DOWNLOAD_VCI_FAILD);
							resultMessage.what = Constants.ERROR_OTHER_ERRORS;
							resultMessage.obj = Utils
									.cloudECodeToString(downloadVCIUpdateResponse
											.getCode());
						}
						DiagBusinessLogicWrapper.resetWrapper();
						try {
							Thread.sleep(6000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						FlashVCIResponse flashVCIResponse = DiagBusinessLogicWrapper// 刷新VCI设备
								.flashVCI(downloadVCIUpdateResponse);
						System.out.println("华丽的CODE："
								+ flashVCIResponse.getCode());
						if (flashVCIResponse.getCode() == DiagBusinessLogicWrapper.NO_ERROR) {// 刷新设备成功
							dialoghandler
									.sendEmptyMessage(Constants.FLASH_VCI_SUCCESS);
							resultMessage.what = Constants.NO_ERROR;
						} else {// 刷新设备不成功
							dialoghandler
									.sendEmptyMessage(Constants.FLASH_VCI_FAILD);
							resultMessage.what = Constants.FLASH_VCI_FAILD;
							resultMessage.obj = Utils
									.diagECodeToString(flashVCIResponse
											.getCode());
						}
					}
				}).start();

				break;
			default:
				break;
			}
		};
	};

	// 检查更新
	public void checkUpdate() {
		Utils.initUtils(this);
		final String title1 = getString(R.string.app_prompt);// 提示
		final String content1 = getString(R.string.end_get_conn_sdcloud);// 正在链接服务器
		new Handler(getMainLooper()).post(new Runnable() {
			public void run() {
				showPrograssDialog(SettingVciSetting.this, title1, content1);
			}
		});

		new Thread() {
			public void run() {
				Message loginMessage = Utils.login();// 登录

				switch (loginMessage.what) {
				case Constants.NO_ERROR:// 登陆成功或者是错误信息为Constants.UPDATE_SD_NEEDCHECK;都需要检查更新
					final String content2 = getString(R.string.end_get_check_vci_info);// "正在检测VCI信息";
					new Handler(getMainLooper()).post(new Runnable() {
						public void run() {
							cancelProgressDialog();
							showPrograssDialog(SettingVciSetting.this, title1,
									content2);
						}
					});
					final Message isRun = new Message();
					isRun.what = 0;

					if (isRun.what == 0) {
						Message msg = Utils.isCompatible(bluetoothPreference);
						if (msg.what == 0 || !isfirmwareExist) {
							final Message resultMessage = new Message();
							final CheckVCIUpdateResponse checkVCIUpdateResponse = CloudWebWrapper
									.checkVCIUpdate(true);
							if (checkVCIUpdateResponse.getCode() == 0
									|| checkVCIUpdateResponse.getCode() == DiagBusinessLogicWrapper.NO_FIRMWARE_ERROR
									|| checkVCIUpdateResponse.getCode() == DiagBusinessLogicWrapper.NEEDUPDATEVCI_ERROR) {// 如果检测成功
								new Handler(getMainLooper())
										.post(new Runnable() {

											public void run() {
												cancelProgressDialog();
												new AlertDialog.Builder(
														SettingVciSetting.this)
														.setTitle(
																R.string.app_prompt)
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
																		uphandler
																				.sendMessage(uphandler
																						.obtainMessage(
																								200,
																								checkVCIUpdateResponse));
																	}
																}).create()
														.show();
											}
										});
							} else {
								resultMessage.what = Constants.ERROR_OTHER_ERRORS;
								if (null != checkVCIUpdateResponse.getDto()) {
									String msg1 = checkVCIUpdateResponse
											.getDto().getMessage();
									if (null == msg1 || "".equals(msg1)) {
										msg1 = Utils
												.cloudECodeToString(checkVCIUpdateResponse
														.getCode());
									}
									resultMessage.obj = msg1;
								} else {
									// TODO
									resultMessage.obj = Utils
											.cloudECodeToString(checkVCIUpdateResponse
													.getCode());
								}
							}

						} else {
							loginHandler.sendMessage(msg);
						}
					}

					break;
				case Constants.UPDATE_SD_NEEDCHECK:// 登陆成功或者是错误信息为Constants.UPDATE_SD_NEEDCHECK;都需要检查更新

					break;

				default:
					loginHandler.sendMessage(loginMessage);
					break;
				}
				// loginHandler.sendMessage(loginMessage);
			};
		}.start();
	}

	private ProgressDialog mProgressDialog;
}
