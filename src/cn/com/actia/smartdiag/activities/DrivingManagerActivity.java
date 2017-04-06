package cn.com.actia.smartdiag.activities;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;
import cn.com.actia.smartdiag.R;
import cn.com.actia.smartdiag.beans.DrivingManagerBean;
import cn.com.actia.smartdiag.constants.Constants;
import cn.com.actia.smartdiag.constants.FlashConstants;
import cn.com.actia.smartdiag.services.FlashDrivingManagerService;
import cn.com.actia.smartdiag.services.FlashDrivingManagerService.FlashDrivingManagerBinder;
import cn.com.actia.smartdiag.tools.Utils;

import com.shoushuo.android.tts.ITts;
import com.shoushuo.android.tts.ITtsCallback;

@SuppressLint("HandlerLeak")
public class DrivingManagerActivity extends APPBaseActivity {
	/** 未准备状态 */
	public static final int SPEECH_STATE_NO = 0;
	/** 准备状态 */
	public static final int SPEECH_STATE_REALLY = 1;
	/** 正在进行状态 */
	public static final int SPEECH_STATE_ISSPEECHING = 2;

	private FlashDrivingManagerService mService;
	private Intent mIntent;

	TextToSpeech mSpeech;
	private ITts ttsService;

	private boolean ttsBound;
	private ServiceConnection connection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder iservice) {
			ttsService = ITts.Stub.asInterface(iservice);
			ttsBound = true;
			// 在应用第一个使用 TTS 的地方，调用下面的 initialize 方法，比如如果有
			// 两个 Activity都使用手说 TTS，则第二个 Activity在此不需要再调用。
			try {
				ttsService.initialize();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			try {
				ttsService.registerCallback(new ITtsCallback.Stub() {
					public void speakCompleted() throws RemoteException {
						speechState = SPEECH_STATE_REALLY;
					}
				});
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

		public void onServiceDisconnected(ComponentName arg0) {
			ttsService = null;
			ttsBound = false;
		}
	};

	public int speechState = 0;

	ImageView iv;
	TextView tv1;
	TextView tv2;

	// 是否能够实现文字转语言
	private boolean isSpeechDoes = false;

	/** 是否支持当前语言 */
	private boolean isLocalDoes = false;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case Constants.NEED_TO_ACTIVATE:
				registDevices();
				break;
			case FlashDrivingManagerService.ERROR:
				cancelProgressDialog();
				unbindService(mServiceConnection);
				showTag("ERROR");
				break;
			case FlashDrivingManagerService.DATA:
				if (isSpeechDoes) {// 如果支持语言，则只有在准备状态才可以改变
					if (speechState == SPEECH_STATE_REALLY) {
						DrivingManagerBean tempBean = (DrivingManagerBean) msg.obj;
						StringBuffer sb = new StringBuffer();
						// sb.append(getResources().getString(
						// R.string.drivermanager_current_analysis));
						sb.append(tempBean.mDrivAnaysisInfo).append(",");
						// sb.append(getResources().getString(
						// R.string.drivermanager_current_advices));
						sb.append(tempBean.mDrivAdviceInfo);
						speech(sb.toString());

						if (null != tempBean.mDrivAnaysisInfo
								&& tempBean.mDrivAnaysisInfo.length() > 0) {
							tv1.setText(tempBean.mDrivAnaysisInfo);
						}
						if (null != tempBean.mDrivAdviceInfo
								&& tempBean.mDrivAdviceInfo.length() > 0) {
							tv2.setText(tempBean.mDrivAdviceInfo);
						}
						switch (tempBean.mDrivingEnvironment) {
						case FlashConstants.DRIVING_HIGHWAY:
							iv.setImageResource(R.drawable.driving_manager_highway);
							break;
						case FlashConstants.DRIVING_SUBURBAN:
							iv.setImageResource(R.drawable.driving_manager_suburban);
							break;
						case FlashConstants.DRIVING_URBAN:
							iv.setImageResource(R.drawable.driving_manager_urban);
							break;
						default:
							iv.setImageResource(R.drawable.driving_manager_unknow);
							break;
						}
						cancelProgressDialog();

						Utils.showTag("mDrivingEnvironment:"
								+ tempBean.mDrivingEnvironment);
						Utils.showTag("mDrivAnaysisInfo:"
								+ tempBean.mDrivAnaysisInfo);
						Utils.showTag("mDrivAdviceInfo:"
								+ tempBean.mDrivAdviceInfo);
					}
				} else {// 如果不支持语言，状态可以随时改变
					DrivingManagerBean tempBean = (DrivingManagerBean) msg.obj;
					if (null != tempBean.mDrivAnaysisInfo
							&& tempBean.mDrivAnaysisInfo.length() > 0) {
						tv1.setText(tempBean.mDrivAnaysisInfo);
					}
					if (null != tempBean.mDrivAdviceInfo
							&& tempBean.mDrivAdviceInfo.length() > 0) {
						tv2.setText(tempBean.mDrivAdviceInfo);
					}
					switch (tempBean.mDrivingEnvironment) {
					case FlashConstants.DRIVING_HIGHWAY:
						iv.setImageResource(R.drawable.driving_manager_highway);
						break;
					case FlashConstants.DRIVING_SUBURBAN:
						iv.setImageResource(R.drawable.driving_manager_suburban);
						break;
					case FlashConstants.DRIVING_URBAN:
						iv.setImageResource(R.drawable.driving_manager_urban);
						break;
					default:
						iv.setImageResource(R.drawable.driving_manager_unknow);
						break;
					}
					cancelProgressDialog();
					Utils.showTag("mDrivingEnvironment:"
							+ tempBean.mDrivingEnvironment);
					Utils.showTag("mDrivAnaysisInfo:"
							+ tempBean.mDrivAnaysisInfo);
					Utils.showTag("mDrivAdviceInfo:" + tempBean.mDrivAdviceInfo);
				}

				break;

			default:
				break;
			}
		};
	};
	private ServiceConnection mServiceConnection = new ServiceConnection() {

		public void onServiceDisconnected(ComponentName name) {
			mService = null;
		}

		public void onServiceConnected(ComponentName name, IBinder service) {
			mService = ((FlashDrivingManagerBinder) service)
					.getFlashDrivingManagerService();
			mService.setHandler(mHandler);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.driving_manager_layout);
		iv = (ImageView) findViewById(R.id.driving_iv1);
		tv1 = (TextView) findViewById(R.id.driving_tv1);
		tv2 = (TextView) findViewById(R.id.driving_tv2);
		((ToggleButton) findViewById(R.id.driving_manager_speack))
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						System.out.println("~~" + isChecked);
						if (isChecked) {
							if (isLocalDoes) {// 如果支持当前语言
								isSpeechDoes = true;
							} else {// 如果不支持当前语言
								if (Constants.Language.equals("zh")) {
									// TODO 如果是中文，提示要安装插件
									showToast("需要下载中文插件");
									final AlertDialog dialog = new AlertDialog.Builder(
											DrivingManagerActivity.this)
											.create();
									dialog.setTitle(R.string.app_prompt);
									dialog.setMessage(Utils
											.getStringResourse(
													DrivingManagerActivity.this,
													R.string.drivermanager_need_chinese));
									dialog.setButton(
											Utils.getStringResourse(
													DrivingManagerActivity.this,
													R.string.app_prompt),
											new DialogInterface.OnClickListener() {
												public void onClick(
														DialogInterface arg0,
														int arg1) {
													Intent it = new Intent(
															Intent.ACTION_VIEW,
															Uri.parse("https://play.google.com/store/apps/details?id=com.shoushuo.android.tts&feature=search_result#?t=W251bGwsMSwxLDEsImNvbS5zaG91c2h1by5hbmRyb2lkLnR0cyJd"));
													DrivingManagerActivity.this
															.startActivity(it);
												}
											});
									dialog.setButton2(
											Utils.getStringResourse(
													DrivingManagerActivity.this,
													R.string.app_cancel),
											new DialogInterface.OnClickListener() {

												public void onClick(
														DialogInterface arg0,
														int arg1) {
													dialog.dismiss();
												}
											});
									dialog.show();
								} else {// 如果是其他语言
									showMessageDialog(
											DrivingManagerActivity.this,
											R.string.app_prompt,
											R.string.drivermanager_no_support_local,
											Constants.STYLE_NORMAL);
									buttonView.setChecked(false);
								}
							}
						} else {// 如果正在说话，则停止
							if (null != ttsService) {
								try {
									ttsService.stop();
								} catch (RemoteException e) {
									e.printStackTrace();
								}
								isSpeechDoes = false;
								speechState = SPEECH_STATE_REALLY;
							}

							try {
								if (null != mSpeech) {
									mSpeech.stop();
									isSpeechDoes = false;
									speechState = SPEECH_STATE_REALLY;
								}
							} catch (Exception e) {
								showTag("一个我知道的错误");
							}
						}
					}
				});
		mIntent = new Intent(DrivingManagerActivity.this,
				FlashDrivingManagerService.class);
		showPrograssDialog(this,
				Utils.getStringResourse(this, R.string.app_prompt),
				Utils.getStringResourse(this, R.string.fast_scan_is_scan_1));
		bindService(mIntent, mServiceConnection, BIND_AUTO_CREATE);
	}

	HashMap<String, String> myHashAlarm = new HashMap<String, String>();

	// 初始化文字转语音
	public void initTextToSpeach() {
		if (Constants.Language.equals("zh")) {// 如果当前是中文
			if (!ttsBound) {
				String actionName = "com.shoushuo.android.tts.intent.action.InvokeTts";
				Intent intent = new Intent(actionName);
				isLocalDoes = this.bindService(intent, connection,
						Context.BIND_AUTO_CREATE);// 如果绑定成功，则当前语言可用，否则不可用
				speechState = SPEECH_STATE_REALLY;
			}
			return;
		}

		mSpeech = new TextToSpeech(DrivingManagerActivity.this,
				new OnInitListener() {
					public void onInit(int status) {
						if (status == TextToSpeech.SUCCESS) {
							isLocalDoes = true;
						} else {
							// isLocalDoes = false;
						}
						if (isLocalDoes) {// 如果支持当前语言
							speechState = SPEECH_STATE_REALLY;
							mSpeech.setOnUtteranceCompletedListener(new OnUtteranceCompletedListener() {
								public void onUtteranceCompleted(
										String utteranceId) {
									speechState = SPEECH_STATE_REALLY;
									showTag("onUtteranceCompleted");
								}
							});
							myHashAlarm.put(
									TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,
									"end");
						}
					}
				});
	}

	public void speech(String text) {
		switch (speechState) {
		case SPEECH_STATE_ISSPEECHING:

			break;
		case SPEECH_STATE_REALLY:// 如果是准备状态，则可以说话
			if (Constants.Language.equals("zh")) {
				try {
					ttsService.speak(text, TextToSpeech.QUEUE_FLUSH);
					speechState = SPEECH_STATE_ISSPEECHING;
					return;
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
			speechState = SPEECH_STATE_ISSPEECHING;
			mSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, myHashAlarm);
			showTag("SPEAK");

			break;
		default:

			break;
		}
	}

	@Override
	protected void onStart() {
		initTextToSpeach();
		super.onStart();
	}

	@Override
	protected void onDestroy() {
		if (ttsBound) {
			ttsBound = false;
			this.unbindService(connection);
		}

		try {
			if (null != mSpeech) {
				mSpeech.stop();
				mSpeech.shutdown();
			}

		} catch (Exception e) {
			showTag("我知道的一个错误，在onDestroy里面");
		}
		unbindService(mServiceConnection);
		super.onDestroy();
	}
}
