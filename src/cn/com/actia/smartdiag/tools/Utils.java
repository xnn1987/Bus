package cn.com.actia.smartdiag.tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.com.actia.businesslogic.DTC;
import cn.com.actia.businesslogic.Parameter;
import cn.com.actia.businesslogic.data.ParameterCategory;
import cn.com.actia.businesslogic.util.SDDictWrapper;
import cn.com.actia.communication.bluetooth.BluetoothInterface;
import cn.com.actia.dto.VersionDTO;
import cn.com.actia.environment.EnvironmentUtil;
import cn.com.actia.smartdiag.R;
import cn.com.actia.smartdiag.activities.APPBaseActivity;
import cn.com.actia.smartdiag.activities.SelectSuccess;
import cn.com.actia.smartdiag.beans.DrivingManagerBean;
import cn.com.actia.smartdiag.beans.NeedBean;
import cn.com.actia.smartdiag.constants.Constants;
import cn.com.actia.smartdiag.constants.FlashConstants;
import cn.com.actia.smartdiag.model.driving.DrivingAnalysisAdviceInfo;
import cn.com.actia.smartdiag.model.driving.DrivingEnvironment;
import cn.com.actia.smartdiag.model.wapper.entity.ConfigureSystemInfoResponse;
import cn.com.actia.smartdiag.model.wapper.entity.GetAllParameterResponse;
import cn.com.actia.smartdiag.model.wapper.entity.GetDTCResponse;
import cn.com.actia.smartdiag.model.wapper.entity.GetDrivingInfoResponse;
import cn.com.actia.smartdiag.model.wapper.entity.GetUsableParameterResponse;
import cn.com.actia.smartdiag.model.wapper.entity.GetVehicleInfoResponse;
import cn.com.actia.smartdiag.model.wapper.entity.IsSDCloudCompatibleResponse;
import cn.com.actia.smartdiag.model.wapper.entity.IsSDVCICompatibleResponse;
import cn.com.actia.smartdiag.model.wapper.entity.IsVCIConnectedResponse;
import cn.com.actia.smartdiag.model.wapper.entity.SetVCIAddressResponse;
import cn.com.actia.smartdiag.model.wapper.entity.lic.ActivateDeviceAndVCIResponse;
import cn.com.actia.smartdiag.model.wapper.entity.web.CheckSDUpdateResponse;
import cn.com.actia.smartdiag.model.wapper.entity.web.CheckVCIUpdateResponse;
import cn.com.actia.smartdiag.model.wapper.entity.web.DownloadSDUpdateResponse;
import cn.com.actia.smartdiag.model.wapper.entity.web.DownloadVCIUpdateResponse;
import cn.com.actia.smartdiag.model.wapper.entity.web.FlashVCIResponse;
import cn.com.actia.smartdiag.model.wapper.entity.web.InitResponse;
import cn.com.actia.smartdiag.model.wapper.entity.web.LogonResponse;
import cn.com.actia.smartdiag.range.ODBRangeBean;
import cn.com.actia.smartdiag.range.OtherRangeBean;
import cn.com.actia.smartdiag.range.RangeBean;
import cn.com.actia.smartdiag.wrapper.CloudWebWrapper;
import cn.com.actia.smartdiag.wrapper.DiagBusinessLogicWrapper;
import cn.com.actia.smartdiag.wrapper.ILicensingManager;
import cn.com.actia.smartdiag.wrapper.LicensingManager;

public class Utils {
	private static ArrayList<ODBRangeBean> mOBDRanges;
	private static ArrayList<OtherRangeBean> mOtherRanges;

	private static List<BluetoothDevice> mSearchDevices;
	private static List<BluetoothDevice> mVCIDevices;
	private static BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
			.getDefaultAdapter();
	private static Toast mToast;
	private static List<Parameter> mAllParameters;
	private static List<Parameter> mUsableParameters;

	private static Bundle engineBundle;
	private static Bundle waterTemperatureBundle;
	private static Bundle oilBundle;
	private static Bundle lightsBundle;
	private static Bundle securityBundle;

	private static NeedBean fuelManagementNeed;
	private static NeedBean maintenanceNeed;
	private static NeedBean depthDiagnosisNeed;

	public static NeedBean getdepthDiagnosisNeed() {
		return depthDiagnosisNeed;
	}

	public static NeedBean getFuelManagementNeed() {
		return fuelManagementNeed;
	}

	public static NeedBean getMaintenanceNeed() {
		return maintenanceNeed;
	}

	private static Context mContext;

	private static List<DTC> DTCList;

	public static void initUtils(Context context) {
		mContext = context;
	}

	public static void setDtcs(List<DTC> dtcs) {
		DTCList = dtcs;
	}

	public static List<DTC> getDtcs(Activity nowActivity, Handler mHandler,
			SharedPreferences preferences) {
		// if (DTCList != null) {
		// return DTCList;
		// }
		Message msg = checkSuitable(nowActivity, preferences);
		if (msg.what == 0) {
			if (Constants.isSelectVehicleConfigSystemInfo) {
				GetDTCResponse DTCs = DiagBusinessLogicWrapper.getDTC();
				if (DTCs.getCode() == 0) {
					DTCList = DTCs.getDtcs();
					if (null != DTCs.getDtcs()) {
						// List<DTC> list = DTCs.getDtcs();
						/*
						 * for (DTC d context.unregisterReceiver(mReceiver);:
						 * list) { System.out.println("getCharacterization" +
						 * d.getCharacterization());
						 * System.out.println("getCode" + d.getCode());
						 * System.out.println("getDescriptionLabel" +
						 * d.getDescriptionLabel());
						 * System.out.println("getDtcStatus" +
						 * d.getDtcStatus()); System.out.println("getEcuName" +
						 * d.getEcuName());
						 * 
						 * System.out.println("getDtcStatus" +
						 * d.getDtcType().name());
						 * System.out.println("getDtcStatus" +
						 * d.getDtcType().ordinal()); }
						 */
					} else {
						msg.what = DTCs.getCode();
						msg.obj = diagECodeToString(msg.what);
						showTag("无故障！");
					}
				} else {
					showTag(DTCs.getCode() + "	:" + DTCs.getMessage());
				}
			}
		} else {
			mHandler.handleMessage(msg);
		}
		mHandler.handleMessage(msg);
		return DTCList;
	}

	public static void showToast(Context context, String msg) {
		if (null == mToast) {
			mToast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
			mToast.show();
		} else {
			mToast.setText(msg);
			mToast.show();
		}
	}

	public static String getStringResourse(Context context, int id) {
		return context.getResources().getString(id);
	}

	public static void showTag(String msg) {
		if (Constants.WITHOUT_SHOW_TAGS)
			Log.e(Constants.TAG_SMART_DIAG, msg);
	}

	/** 获取语言及国家信息 */
	public static void setLocaleLanguage() {
		showTag(String.format("%s-%s", Constants.Language, Constants.Country));
	}

	public static BluetoothAdapter openBluetooth() {
		if (mBluetoothAdapter.isEnabled()) {

		} else {
			mBluetoothAdapter.enable();
		}
		return mBluetoothAdapter;
	}

	public static void initAllTools(Context context) {
		BluetoothInterface.initCon(context);
		EnvironmentUtil.initContext(context);
		initUtils(context);
		SDDictWrapper.setCurrentLanguage(EnvironmentUtil.getCurrentLanguage());
	}

	/**
	 * <p>
	 * 开始扫描发现蓝牙设备，在{@linkplain Constants.searchTime} 的时间后退出扫描
	 * </p>
	 * 
	 * @param context
	 *            <a>传进来的上下文，用来注册广播事件</a>
	 */
	public static void startSearchBluetoothDevice(Context context) {
		startSearchBluetoothDevice(context, null);
	}

	public static boolean isDiscoverying = false;

	static Runnable runable = new Runnable() {
		public void run() {
			runable_Num--;
			if (runable_Num == 0) {
				if (mBluetoothAdapter.isDiscovering())
					mBluetoothAdapter.cancelDiscovery();
				if (null != mListener && isDiscoverying) {
					mListener.onCancelSearch(mSearchDevices);
					showTag("Stop search...");
				}
				try {
					mContext.unregisterReceiver(mReceiver);
				} catch (Exception e) {
					showTag("未添加的Receiver");
				}
			}
		}
	};
	private static SearchBluetoothDeviceListener mListener = null;

	public static void startSearchBluetoothDevice(final Context context,
			final SearchBluetoothDeviceListener listener) {
		mListener = listener;
		if (!mBluetoothAdapter.isEnabled()) {
			if (null != mListener) {
				mListener.onError(getStringResourse(context,
						R.string.util_bluetooth_opend_or_no));
			}
			try {
				throw new Exception(getStringResourse(context,
						R.string.util_bluetooth_opend_or_no));
			} catch (Exception e) {
				e.printStackTrace();
				context.unregisterReceiver(mReceiver);
				return;
			}
		}
		context.registerReceiver(mReceiver, new IntentFilter(
				BluetoothDevice.ACTION_FOUND));
		mSearchDevices = new ArrayList<BluetoothDevice>();
		isDiscoverying = true;
		if (null != mListener)
			mListener.onStartSearch(mBluetoothAdapter.startDiscovery());
		else {
			mBluetoothAdapter.startDiscovery();
		}
		showTag("Start search...");
		runable_Num++;
		new Handler(Looper.getMainLooper()).postDelayed(runable,
				Constants.searchTime);
	}

	public static int runable_Num = 0;

	public static void cancelDisCovery(Context context) {
		// if (mBluetoothAdapter.isDiscovering())
		// mBluetoothAdapter.cancelDiscovery();
		// if (null != mListener && isDiscoverying) {
		// mListener.onCancelSearch(mSearchDevices);
		// showTag("Stop search...");
		// }
		// try {
		// mContext.unregisterReceiver(mReceiver);
		// } catch (Exception e) {
		// showTag("未添加的Receiver");
		// }
		try {
			showTag("Stop search...");
			isDiscoverying = false;
			context.unregisterReceiver(mReceiver);
			if (mBluetoothAdapter.isDiscovering())
				mBluetoothAdapter.cancelDiscovery();
		} catch (Exception e) {
			showTag("未添加的Receiver");
		}
	}

	Set<BluetoothDevice> devices;

	/** 扫描蓝牙设备的时候所用到的监听器 */
	private static final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			// 当发现一个设备的时候
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				/* 如果是VCI的设备名 */
				if (null != device
						&& device.getName() != null
						&& !device.getName().equals("")
						&& device.getName().startsWith(
								Constants.DEVICES_NAME_FRONT)) {
					mSearchDevices.add(device);
					showTag(device.getAddress() + ":" + device.getName());
				}
			}
		}
	};

	/**
	 * 获取设备 1,看本机是否有配对的VCI设备，若有则直接使用，若没有，则调用
	 * {@link Utils#startSearchBluetoothDevice(Context, SearchBluetoothDeviceListener)}
	 */
	public static void getVCIDevices(final Context context,
			final GetVCIDeviceListener getVCIDeviceListener) {
		// 开始了
		getVCIDeviceListener.onGetStart();
		mVCIDevices = new ArrayList<BluetoothDevice>();
		SearchBluetoothDeviceListener bluetoothDeviceListener = new SearchBluetoothDeviceListener() {

			public void onStartSearch(boolean whetherStarted) {
				getVCIDeviceListener.onStateChange(Utils.getStringResourse(
						context, R.string.bluetooth_start_scan));
			}

			public void onError(String msg) {
				getVCIDeviceListener.onError(msg);
			}

			public void onCancelSearch(List<BluetoothDevice> devices) {

				if (devices.size() == 0) {
					getVCIDeviceListener.onError(Utils.getStringResourse(
							context, R.string.bluetooth_no_vci));
					return;
				}
				// BluetoothDevice btDevice = devices.get(0);
				// try {
				// boolean a = ClsUtils.setPin(btDevice.getClass(),
				// btDevice, "1234"); // 手机和蓝牙采集器配对
				// boolean b = ClsUtils.createBond(btDevice.getClass(),
				// btDevice);
				// boolean c = ClsUtils.cancelPairingUserInput(
				// btDevice.getClass(), btDevice);
				// System.out.println(a + ":" + b + ":" + c);
				// } catch (Exception e) {
				// e.printStackTrace();
				// }

				mVCIDevices = mSearchDevices;
				if (isDiscoverying) {
					getVCIDeviceListener.onCompleted(mVCIDevices);
				}
			}
		};
		if (mBluetoothAdapter.isEnabled()) {
			/* 获取本地已经配对的蓝牙设备 */
			getVCIDeviceListener.onStateChange(Utils.getStringResourse(context,
					R.string.bluetooth_get_location_vci));
			Set<BluetoothDevice> bondedDevices = mBluetoothAdapter
					.getBondedDevices();
			Iterator<BluetoothDevice> it = bondedDevices.iterator();
			while (it.hasNext()) {
				BluetoothDevice d = it.next();
				if (d.getName().startsWith(Constants.DEVICES_NAME_FRONT)) {
					mVCIDevices.add(d);
				}
			}
			if (mVCIDevices.size() == 0) {
				getVCIDeviceListener.onStateChange(Utils.getStringResourse(
						context, R.string.bluetooth_no_location_vci));
				startSearchBluetoothDevice(context, bluetoothDeviceListener);
			} else {
				// 直接使用
				// getVCIDeviceListener.onCompleted(mVCIDevices);
				getVCIDeviceListener.onStateChange(Utils.getStringResourse(
						context, R.string.bluetooth_no_location_vci));
				startSearchBluetoothDevice(context, bluetoothDeviceListener);
			}
		} else {
			getVCIDeviceListener.onError(Utils.getStringResourse(context,
					R.string.bluetooth_no_suitable));
		}
	}

	public static void Login(final Context context, String userName) {
		Login(context, userName,/* null, */null);
	}

	private static void Login(final Context context, String userName,
	/* String title, */String ErrorCode) {
		Builder builder = new Builder(context);
		ExpandableListView listView = new ExpandableListView(context);
		listView.setAdapter(new LoginExpandableListAdapter(context));
		View dialogView = LayoutInflater.from(context).inflate(
				R.layout.login_layout, null);
		builder.setView(dialogView);
		if (null != ErrorCode && !"".equals(ErrorCode)) {
			builder.setMessage(ErrorCode);
		}
		builder.setTitle(R.string.login_login);
		/*
		 * if (null != title && !"".equals(title)) { builder.setTitle(title); }
		 */
		final EditText userNameTV = (EditText) dialogView
				.findViewById(R.id.login_user_name);

		userNameTV.setText(userName);
		userNameTV.setText("bluemobitest");
		final EditText userPwdeTV = (EditText) dialogView
				.findViewById(R.id.login_user_psw);
		userPwdeTV.setText("bluemobitest123");
		builder.setNegativeButton(R.string.app_cancel, null);
		builder.setPositiveButton(R.string.app_ok, new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

				String userName = userNameTV.getText().toString().trim();
				String userPwd = userPwdeTV.getText().toString().trim();
				String locale = String.format("%s-%s", Constants.Language,
						Constants.Country);
				CloudWebWrapper.resetWrapper();
				InitResponse initResp = CloudWebWrapper.init(userName, userPwd,
						locale);
				showTag(initResp.getCode() + "");
				switch (initResp.getCode()) {
				case CloudWebWrapper.NO_ERROR:
					showTag(Utils.getStringResourse(context,
							R.string.app_login_success));
					break;

				default:
					Login(context,
							userName, /* "登录", */
							Utils.getStringResourse(context,
									R.string.app_login_fail)
									+ initResp.getMessage());
				}
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();

	}

	public static Drawable getDrawable(String path) {
		try {
			return Drawable.createFromStream(mContext.getResources()
					.getAssets().open(path), "");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mContext.getResources().getDrawable(
				R.drawable.select_vehicle_default);
	}

	static class LoginExpandableListAdapter extends BaseExpandableListAdapter {
		String[][] names = new String[][] { { "徐达", "徐克强" }, { "李国强", "李世民" },
				{ "胡锦涛" } };
		String[] groups = new String[] { "徐", "李", "胡" };
		Context mContext;

		public LoginExpandableListAdapter(Context context) {
			mContext = context;
		}

		public int getGroupCount() {
			return names.length;
		}

		public int getChildrenCount(int groupPosition) {
			return names[groupPosition].length;
		}

		public Object getGroup(int groupPosition) {
			return names[groupPosition];
		}

		public Object getChild(int groupPosition, int childPosition) {
			return names[groupPosition][childPosition];
		}

		public long getGroupId(int groupPosition) {
			return groupPosition + 1;
		}

		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		public boolean hasStableIds() {
			return false;
		}

		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			TextView textView = new TextView(mContext);
			textView.setText(groups[groupPosition]);
			return textView;
		}

		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			TextView textView = new TextView(mContext);
			textView.setText(names[groupPosition][childPosition]);
			return textView;
		}

		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return false;
		}

	}

	public static Message isCompatible(SharedPreferences preferences) {
		Message msg = new Message();
		IsVCIConnectedResponse isConnRes = DiagBusinessLogicWrapper
				.isVCIConnected();
		// 判断是否已经链接
		if (isConnRes.getCode() == DiagBusinessLogicWrapper.NO_ERROR) {// 已经链接
			IsSDVCICompatibleResponse isCompatibleRes = DiagBusinessLogicWrapper
					.isSDVCICompatible();
			if (isCompatibleRes.getCode() == DiagBusinessLogicWrapper.NO_ERROR) {
				msg.what = DiagBusinessLogicWrapper.NO_ERROR;
			} else if (isCompatibleRes.getCode() == DiagBusinessLogicWrapper.NO_FIRMWARE_ERROR) {
				msg.what = Constants.NEED_UPDATA_NO_FIRWARE;
			} else if (isCompatibleRes.getCode() == DiagBusinessLogicWrapper.NEED_TO_ACTIVATE) {
				msg.what = Constants.NEED_TO_ACTIVATE;
			} else {
				msg.what = isCompatibleRes.getCode();
				msg.obj = diagECodeToString(isCompatibleRes.getCode());
			}
		} else if (isConnRes.getCode() == DiagBusinessLogicWrapper.NEED_TO_ACTIVATE) {
			msg.what = Constants.NEED_TO_ACTIVATE;
		} else if (isConnRes.getCode() == DiagBusinessLogicWrapper.NO_FIRMWARE_ERROR) {
			msg.what = Constants.NEED_UPDATA_NO_FIRWARE;
		} else {// 未链接
			if (null == Constants.BLUETOOTH_ADRESS
					|| "".equals(Constants.BLUETOOTH_ADRESS)) {
				msg.what = Constants.ERROR_OTHER_ERRORS;
				msg.obj = getStringResourse(mContext, R.string.app_getvic_first);
				return msg;
			}
			if (null == Constants.BLUETOOTH_ADRESS
					|| "".equals(Constants.BLUETOOTH_ADRESS)) {
				msg.what = Constants.ERROR_GET_A_VCI_FIRST;
				msg.obj = getStringResourse(mContext, R.string.get_a_vic_first);
			}
			SetVCIAddressResponse isSetVciRes = DiagBusinessLogicWrapper
					.setVCIAddress(Constants.BLUETOOTH_ADRESS);
			if (isSetVciRes.getCode() == DiagBusinessLogicWrapper.NO_ERROR) {// 如果蓝牙设置成功
				// 则可以判断蓝牙是否合适
				IsSDVCICompatibleResponse isCompatibleRes = DiagBusinessLogicWrapper
						.isSDVCICompatible();
				if (isCompatibleRes.getCode() == DiagBusinessLogicWrapper.NO_ERROR) {
					msg.what = DiagBusinessLogicWrapper.NO_ERROR;

				} else if (isCompatibleRes.getCode() == DiagBusinessLogicWrapper.NO_FIRMWARE_ERROR) {
					msg.what = Constants.NEED_UPDATA_NO_FIRWARE;
				} else if (isCompatibleRes.getCode() == DiagBusinessLogicWrapper.NEED_TO_ACTIVATE) {
					msg.what = Constants.NEED_TO_ACTIVATE;
				} else {
					msg.what = isCompatibleRes.getCode();
					msg.obj = diagECodeToString(isCompatibleRes.getCode());
				}
			} else if (isSetVciRes.getCode() == DiagBusinessLogicWrapper.NEED_TO_ACTIVATE) {
				msg.what = Constants.NEED_TO_ACTIVATE;
			} else if (isSetVciRes.getCode() == DiagBusinessLogicWrapper.NO_FIRMWARE_ERROR) {
				msg.what = Constants.NEED_UPDATA_NO_FIRWARE;
			} else {
				msg.what = isSetVciRes.getCode();
				msg.obj = diagECodeToString(isSetVciRes.getCode());
			}
		}
		if (msg.what == Constants.NO_ERROR
				|| msg.what == Constants.NEED_TO_ACTIVATE
				|| msg.what == Constants.NEED_UPDATA_NO_FIRWARE) {
			preferences
					.edit()
					.putString(Constants.COMPLETED_ADRESS,
							Constants.BLUETOOTH_ADRESS).commit();
			preferences
					.edit()
					.putString(Constants.COMPLETED_NAME,
							Constants.BLUETOOTH_NAME).commit();

			mContext.getSharedPreferences(Constants.SHAREPREFERECE_TAG_NAME,
					Context.MODE_PRIVATE)
					.edit()
					.putString(Constants.COMPLETED_ADRESS,
							Constants.BLUETOOTH_ADRESS).commit();

			mContext.getSharedPreferences(Constants.SHAREPREFERECE_TAG_NAME,
					Context.MODE_PRIVATE)
					.edit()
					.putString(Constants.COMPLETED_NAME,
							Constants.BLUETOOTH_NAME).commit();
		}
		return msg;
	}

	int ERROR_CODE = 0;

	/** 获取VIN码 如果Message的what字段为0,则可以从 obj里面获取vin字段 */
	public static Message getVIN(SharedPreferences preferences) {
		Message msg = isCompatible(preferences);
		if (DiagBusinessLogicWrapper.NO_ERROR != msg.what) {// 此处返回错误提示
			return msg;
		}
		String pVehicleName = "OBD_VEHICLE";
		String pECUFamily = "OBD_FAM";
		String pECUName = "OBD_ECU";
		// 汽车系统设置
		ConfigureSystemInfoResponse isconfigResp = DiagBusinessLogicWrapper
				.configureSystemInfo(pVehicleName, pECUFamily, pECUName);
		if (isconfigResp.getCode() == DiagBusinessLogicWrapper.NO_ERROR) {
			mContext.getSharedPreferences(Constants.SHAREPREFERECE_TAG_NAME,
					Context.MODE_PRIVATE)
					.edit()
					.putString(Constants.COMPLETED_ADRESS,
							Constants.BLUETOOTH_ADRESS).commit();

			mContext.getSharedPreferences(Constants.SHAREPREFERECE_TAG_NAME,
					Context.MODE_PRIVATE)
					.edit()
					.putString(Constants.COMPLETED_NAME,
							Constants.BLUETOOTH_NAME).commit();
			GetVehicleInfoResponse isGetInfoRes = DiagBusinessLogicWrapper
					.getVehicleInfo();
			if (isGetInfoRes.getCode() == DiagBusinessLogicWrapper.NO_ERROR) {// 如果能够成功获取信息，则把Vin放进Msg返回
				msg.what = DiagBusinessLogicWrapper.NO_ERROR;
				msg.obj = isGetInfoRes.getVin();
			} else {
				msg.what = isGetInfoRes.getCode();
				msg.obj = diagECodeToString(isGetInfoRes.getCode());
			}
		} else if (isconfigResp.getCode() == DiagBusinessLogicWrapper.NEED_TO_ACTIVATE) {
			msg.what = Constants.NEED_TO_ACTIVATE;
			msg.obj = mContext
					.getString(R.string.regist_devices_need_regist_devices);
		} else {
			msg.what = isconfigResp.getCode();
			msg.obj = diagECodeToString(isconfigResp.getCode());
		}
		return msg;
	}

	public static Message checksuitable(Activity nowActivity,
			String bluetoothAdress, String pVehicleName, String pECUFamily,
			String pECUName, SharedPreferences preferences) {
		Message msg = isCompatible(preferences);
		if (msg.what != DiagBusinessLogicWrapper.NO_ERROR) {
			Constants.isSelectVehicleConfigSystemInfo = false;
			return msg;
		}
		msg = new Message();
		ConfigureSystemInfoResponse coninfoResp = null;

		if (!Constants.isSelectVehicleConfigSystemInfo) {
			coninfoResp = DiagBusinessLogicWrapper.configureSystemInfo(
					pVehicleName, pECUFamily, pECUName);
		}
		if (Constants.isSelectVehicleConfigSystemInfo
				|| coninfoResp.getCode() == DiagBusinessLogicWrapper.NO_ERROR) {
			msg.what = DiagBusinessLogicWrapper.NO_ERROR;
			msg.obj = diagECodeToString(DiagBusinessLogicWrapper.NO_ERROR);
			mContext.getSharedPreferences(Constants.SHAREPREFERECE_TAG_NAME,
					Context.MODE_PRIVATE)
					.edit()
					.putString(Constants.COMPLETED_ADRESS,
							Constants.BLUETOOTH_ADRESS).commit();

			mContext.getSharedPreferences(Constants.SHAREPREFERECE_TAG_NAME,
					Context.MODE_PRIVATE)
					.edit()
					.putString(Constants.COMPLETED_NAME,
							Constants.BLUETOOTH_NAME).commit();
			showTag("Constants.isGetParamSuitable");
			Constants.isSelectVehicleConfigSystemInfo = true;

		} else if (coninfoResp.getCode() == DiagBusinessLogicWrapper.NEED_TO_ACTIVATE) {
			if (null != nowActivity) {
				((APPBaseActivity) nowActivity).registDevices();
			} else {
				msg.what = Constants.NEED_TO_ACTIVATE;
				msg.obj = mContext
						.getString(R.string.regist_devices_need_regist_devices);
			}
		} else {
			msg.what = coninfoResp.getCode();
			msg.obj = diagECodeToString(coninfoResp.getCode());
			Constants.isSelectVehicleConfigSystemInfo = false;
			return msg;
		}
		return msg;
	}

	public static Message checkSuitable(Activity nowActivity,
			SharedPreferences preferences) {
		String pVehicleName = "OBD_VEHICLE";
		String pECUFamily = "OBD_FAM";
		String pECUName = "OBD_ECU";
		return checkSuitable(nowActivity, pVehicleName, pECUFamily, pECUName,
				preferences);
	}

	/** 设置VCIAddress并检测是否可用 */
	public static Message checkSuitable(Activity nowActivity,
			String pVehicleName, String pECUFamily, String pECUName,
			SharedPreferences preferences) {
		return checksuitable(nowActivity, Constants.BLUETOOTH_ADRESS,
				pVehicleName, pECUFamily, pECUName, preferences);
	}

	public static void setAllParameters(List<Parameter> allParameters) {
		Utils.mAllParameters = allParameters;
	}

	/**
	 * 获取所有的 Parameters，若本地有保存，则直接取用本地的
	 * 
	 * @return 返回指为Null表示没有设置VCI的信息
	 */
	public static List<Parameter> getAllParameters() {

		if (null == mAllParameters) {
			if (Constants.isSelectVehicleConfigSystemInfo) {
				GetAllParameterResponse getAllResp = DiagBusinessLogicWrapper
						.getAllParameter();
				if (getAllResp.getCode() == 0) {
					List<Parameter> allParameters = getAllResp.getParameters();
					int k = 0;
					showTag("------allparameters------");
					for (Parameter p : allParameters) {
						showTag(++k + "" + p.getDisplayName());
					}
					showTag("----------------------------");
					setAllParameters(getAllResp.getParameters());
				}
			}
		}
		return mAllParameters;
	}

	public static void setUsableParameters(List<Parameter> usableParameters) {
		Utils.mUsableParameters = usableParameters;
	}

	/**
	 * 获取可用的 Parameters，若本地有保存，则直接取用本地的 若返回指为
	 * 
	 * @return Null表示没有设置VCI的信息
	 */
	public static List<Parameter> getUsableParameters() {
		if (null == mUsableParameters) {
			if (Constants.isSelectVehicleConfigSystemInfo) {
				GetUsableParameterResponse getUsableResp = DiagBusinessLogicWrapper
						.getUsableParameter();
				if (getUsableResp.getCode() == 0) {
					List<Parameter> usableParameters = getUsableResp
							.getParameters();
					int k = 0;
					showTag("------usableparameters------");
					for (Parameter p : usableParameters) {
						showTag(++k + "" + p.getDisplayName());
					}
					showTag("----------------------------");
					setUsableParameters(getUsableResp.getParameters());
				}
			}
		}
		return mUsableParameters;
	}

	public static DrivingManagerBean getDrivingInfo(Handler mhHandler,
			SharedPreferences preferences) {
		DrivingManagerBean bean = new DrivingManagerBean();
		Message msg = isCompatible(preferences);

		if (msg.what == 0) {
			if (Constants.SystemINFO_CONFIGED) {
				GetDrivingInfoResponse drivingInfoResponse = DiagBusinessLogicWrapper
						.getDrivingInfo();
				if (drivingInfoResponse.getCode() == 0) {
					DrivingAnalysisAdviceInfo adviceInfo = drivingInfoResponse
							.getDrivingAnalysisAdviceInfo();
					DrivingEnvironment drivingEnvironment = drivingInfoResponse
							.getGuessedEnvironment();
					if (null != adviceInfo)
						bean.mDrivAdviceInfo = adviceInfo.getDrivAdviceInfo()
								.getAdviceData();
					if (null != adviceInfo)
						bean.mDrivAnaysisInfo = adviceInfo.getDrivAnaysisInfo()
								.getAnalysisData();
					if (drivingEnvironment.equals(DrivingEnvironment.HIGHWAY)) {
						bean.mDrivingEnvironment = FlashConstants.DRIVING_HIGHWAY;
					} else if (drivingEnvironment
							.equals(DrivingEnvironment.SUBURBAN)) {
						bean.mDrivingEnvironment = FlashConstants.DRIVING_SUBURBAN;
					} else if (drivingEnvironment
							.equals(DrivingEnvironment.URBAN)) {
						bean.mDrivingEnvironment = FlashConstants.DRIVING_URBAN;
					} else {
						bean.mDrivingEnvironment = FlashConstants.DRIVING_UNKNOWN;
					}
				} else {
					showTag("获取车辆信息出错");
				}
			} else {
				String pVehicleName = mContext.getSharedPreferences(
						SelectSuccess.PREFERENCE_NAME, Context.MODE_PRIVATE)
						.getString(SelectSuccess.PREFERENCE_VehicleName,
								"OBD_VEHICLE");
				String pECUFamily = mContext.getSharedPreferences(
						SelectSuccess.PREFERENCE_NAME, Context.MODE_PRIVATE)
						.getString(SelectSuccess.PREFERENCE_ECUFamily,
								"OBD_FAM");
				String pECUName = mContext.getSharedPreferences(
						SelectSuccess.PREFERENCE_NAME, Context.MODE_PRIVATE)
						.getString(SelectSuccess.PREFERENCE_ECUName, "OBD_ECU");
				ConfigureSystemInfoResponse configureSystemInfoResponse = DiagBusinessLogicWrapper
						.configureSystemInfo(pVehicleName, pECUFamily, pECUName);
				if (DiagBusinessLogicWrapper.NO_ERROR == configureSystemInfoResponse
						.getCode()) {
					Constants.SystemINFO_CONFIGED = true;

					GetDrivingInfoResponse drivingInfoResponse = DiagBusinessLogicWrapper
							.getDrivingInfo();
					if (drivingInfoResponse.getCode() == 0) {
						DrivingAnalysisAdviceInfo adviceInfo = drivingInfoResponse
								.getDrivingAnalysisAdviceInfo();
						DrivingEnvironment drivingEnvironment = drivingInfoResponse
								.getGuessedEnvironment();
						if (null != adviceInfo)
							bean.mDrivAdviceInfo = adviceInfo
									.getDrivAdviceInfo().getAdviceData();
						if (null != adviceInfo)
							bean.mDrivAnaysisInfo = adviceInfo
									.getDrivAnaysisInfo().getAnalysisData();
						if (drivingEnvironment
								.equals(DrivingEnvironment.HIGHWAY)) {
							bean.mDrivingEnvironment = FlashConstants.DRIVING_HIGHWAY;
						} else if (drivingEnvironment
								.equals(DrivingEnvironment.SUBURBAN)) {
							bean.mDrivingEnvironment = FlashConstants.DRIVING_SUBURBAN;
						} else if (drivingEnvironment
								.equals(DrivingEnvironment.URBAN)) {
							bean.mDrivingEnvironment = FlashConstants.DRIVING_URBAN;
						} else {
							bean.mDrivingEnvironment = FlashConstants.DRIVING_UNKNOWN;
						}
					} else {
						showTag("获取车辆信息出错");
					}
				} else {
					showTag("设置车辆信息出错");
				}
			}
		} else {
			mhHandler.handleMessage(msg);
			showTag("找不到合适的蓝牙设备");
		}
		return bean;
	}

	public static void getXMLData(Context context) {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			SAXParser parser = factory.newSAXParser();
			parser.parse(
					context.getResources().getAssets()
							.open("SDCenterDataSource.xml"), new MyHandler());
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Bundle getErrorCodeBundle() {
		Bundle bundle = new Bundle();
		bundle.putString("numberOfDTCSStoredInThisECU", "OBD2");
		return bundle;
	}

	public static Bundle getEngineBundle() {
		if (Constants.isGetBundleSuitable) {
			return engineBundle;
		}
		return new Bundle();

	}

	public static Bundle getOilBundle() {
		if (Constants.isGetBundleSuitable) {
			return oilBundle;
		}
		return new Bundle();
	}

	public static Bundle getWaterTemperatureBundle() {
		if (Constants.isGetBundleSuitable) {
			return waterTemperatureBundle;
		}
		return new Bundle();
	}

	public static Bundle getSecurityBundle() {
		if (Constants.isGetBundleSuitable)
			return securityBundle;
		return new Bundle();
	}

	public static Bundle getLightsBundle() {
		if (Constants.isGetBundleSuitable) {
			return lightsBundle;
		}
		return new Bundle();
	}

	/**
	 * @param bundle
	 *            用来存放 每个项目对应的parameter的一个bundle，可以用{@link Utils#getOilBundle()}
	 *            等来获取 bundler
	 * @return 包含有这些信息的Parameter,这些Parameter是{@link Utils#mUsableParameters}的子集
	 */
	public static ArrayList<Parameter> getUsableParamListForValue(Bundle bundle) {
		ArrayList<Parameter> params = new ArrayList<Parameter>();
		Set<String> keys = bundle.keySet();
		if (Constants.isSelectVehicleConfigSystemInfo) {
			for (Parameter parameter : getUsableParameters()) {
				if (keys.contains(parameter.getName())) {
					params.add(parameter);
				}
			}
			showTag("getUsableParamListForValue(BundleSize:" + keys.size()
					+ ";UsableSize:" + getUsableParameters().size()
					+ "resultSize:" + params.size() + ")");
		}
		return params;
	}

	/**
	 * @param bundle
	 *            用来存放 每个项目对应的parameter的一个bundle，可以用{@link Utils#getOilBundle()}
	 *            等来获取 bundler
	 * @return 包含有这些信息的Parameter,这些Parameter是{@link Utils#mAllParameters}的子集
	 */
	public static ArrayList<Parameter> getAllParamListForValue(Bundle bundle) {
		ArrayList<Parameter> params = new ArrayList<Parameter>();
		Set<String> keys = bundle.keySet();
		if (Constants.isSelectVehicleConfigSystemInfo)
			for (Parameter parameter : getAllParameters()) {
				if (keys.contains(parameter.getName())) {
					params.add(parameter);
				}
			}
		showTag("getAllParamListForValue(BundleSize:" + keys.size()
				+ ";AllSize:" + getAllParameters().size() + "resultSize:"
				+ params.size() + ")");
		return params;
	}

	public static Message RegistDevice(String pVCISerialNumber,
			String pVCIActivationCode) {
		Message resultMsg = new Message();
		ActivateDeviceAndVCIResponse activateDeviceAndVCIResponse = LicensingManager
				.activateDeviceAndVCI(pVCISerialNumber, pVCIActivationCode);
		if (activateDeviceAndVCIResponse.getCode() == ILicensingManager.NO_ERROR) {
			resultMsg.what = Constants.NO_ERROR;
		} else if (activateDeviceAndVCIResponse.getCode() == ILicensingManager.NO_ERROR_ALREADY_ACTIVATED) {
			// TODO 如果有问题，这里需要被修改
			resultMsg.what = Constants.ERROR_OTHER_ERRORS;
			resultMsg.obj = registECodeToString(activateDeviceAndVCIResponse
					.getCode());
		} else {
			resultMsg.what = Constants.ERROR_OTHER_ERRORS;
			resultMsg.obj = registECodeToString(activateDeviceAndVCIResponse
					.getCode());
		}
		return resultMsg;
	}

	/**
	 * 单独提取出来的，与版本更新相关的方法 如果是普通的错误，则返回值为-1,
	 * 如果是一个比较特殊的错误，<a>则返回值会是一个已经定义的比较特殊的值</a>
	 */
	public static Message login() {
		// String userName = userNameTV.getText().toString().trim();
		// String userPwd = userPwdeTV.getText().toString().trim();

		Message resultMessage = new Message();

		if (Constants.isInitCloud) {
			resultMessage.what = 0;
			return resultMessage;
		}

		String locale = String.format("%s-%s", Constants.Language,
				Constants.Country);
		InitResponse initResponse = CloudWebWrapper.init("bluemobitest",
				"bluemobitest123", locale);
		if (initResponse.getCode() == CloudWebWrapper.NO_ERROR) {
			Constants.isInitCloud = true;
			resultMessage.what = 0;
			IsSDCloudCompatibleResponse isSDCloudCompatibleResponse = CloudWebWrapper
					.isSDCloudCompatible();
			// isSDCloudCompatibleResponse succeed
			if (isSDCloudCompatibleResponse.getCode() == CloudWebWrapper.NO_ERROR) {
				LogonResponse logonResponse = CloudWebWrapper.logon();
				// logonResponse succeed;
				if (logonResponse.getCode() == CloudWebWrapper.NO_ERROR) {
					resultMessage.what = Constants.NO_ERROR;
				} else {// logonResponse failed;
					resultMessage.what = Constants.ERROR_OTHER_ERRORS;
					resultMessage.obj = cloudECodeToString(logonResponse
							.getCode());
				}
			} else {// isSDCloudCompatibleResponse failed
				if (isSDCloudCompatibleResponse.getCode() == CloudWebWrapper.SUITABLEVERSION_ERROR) {// 如果提示信息是版本不合适，则需要有特殊的处理
					resultMessage.what = Constants.UPDATE_SD_NEEDCHECK;// 发送需要更新SDCloud的消息
				} else {// 如果是其他的错误
					resultMessage.what = Constants.ERROR_OTHER_ERRORS;
					resultMessage.obj = cloudECodeToString(isSDCloudCompatibleResponse
							.getCode());
				}
			}
		} else {// initResponse 不成功
			if (initResponse.getCode() == CloudWebWrapper.SUITABLEVERSION_ERROR) {// 如果提示信息是版本不合适，则需要有特殊的处理
				resultMessage.what = Constants.UPDATE_SD_NEEDCHECK;// 发送需要更新SDCloud的消息
			} else {// 如果是其他的错误
				resultMessage.what = Constants.ERROR_OTHER_ERRORS;
				resultMessage.obj = cloudECodeToString(initResponse.getCode());
			}
		}

		return resultMessage;
	}


	public static class MyHandler extends DefaultHandler {
		boolean isGroup = false;
		boolean isParameter = false;

		boolean isEngineWant;
		boolean isOilWant;
		boolean isWaterTemperatureWant;
		boolean isLightsWant;
		boolean isSecurityWant;

		boolean isCustomRanges;

		boolean isOBDRange;
		boolean isNotOBDRange;

		boolean isUIRange;

		boolean isRangeParameter;
		boolean isRangeCategory;

		/** 燃油管理 */
		boolean isFuelManagement;
		/** 维护保养 */
		boolean isMaintenance;

		boolean isDepthDiagnosis;

		String mOilWantName = "Oil";
		String mEngineWantName = "Engine";
		String mWaterTemperatureName = "WaterTemperature";
		String mLightsName = "Lights";
		String mSecurityName = "Security";

		String mMaintenanceName = "Maintenance";
		String mFuelManagementName = "FuelManagement";
		String mDepthDiagnosisName = "DepthDiagnosis";

		public MyHandler() {
		}

		@Override
		public void startDocument() throws SAXException {
			Constants.isGetBundleSuitable = false;
			showTag("isEngineWant:" + isEngineWant + "\nisOilWant:" + isOilWant
					+ "\nisWaterTemperatureWant:" + isWaterTemperatureWant
					+ "\nisLightsWant:" + isLightsWant + "\nisSecurityWant:"
					+ isSecurityWant);
			super.startDocument();
		}

		ODBRangeBean tempOdbRangeBean;
		OtherRangeBean tempOtherRangeBean;

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			if (qName == "CustomRanges") {
				isCustomRanges = true;
			}

			if (qName == "OBDRange") {
				isOBDRange = true;
				mOBDRanges = new ArrayList<ODBRangeBean>();
			}
			if (qName == "NotOBDRange") {
				isNotOBDRange = true;
				mOtherRanges = new ArrayList<OtherRangeBean>();
			}

			if (isCustomRanges) {
				if (isOBDRange) {
					if (qName == "Parameter") {
						isRangeParameter = true;
						tempOdbRangeBean = new ODBRangeBean();
						tempOdbRangeBean.setParameterName(attributes
								.getValue("name"));
						tempOdbRangeBean.setParameterType(attributes
								.getValue("type"));
					}
					if (isRangeParameter) {
						if (qName == "UIRange") {
							tempOdbRangeBean.setMin(Integer.parseInt(attributes
									.getValue("min")));
							tempOdbRangeBean.setMax(Integer.parseInt(attributes
									.getValue("max")));
						}
					}
				}
				if (isNotOBDRange) {
					if (qName == "Category") {
						isRangeCategory = true;
						tempOtherRangeBean = new OtherRangeBean();
						tempOtherRangeBean.setCategoryName(attributes
								.getValue("name"));
					}
					if (isRangeCategory) {
						if (qName == "UIRange") {
							tempOtherRangeBean.setMin(Integer
									.parseInt(attributes.getValue("min")));
							tempOtherRangeBean.setMax(Integer
									.parseInt(attributes.getValue("max")));
						}
					}
				}
			}
			if (qName == "Family") {
				if (isFuelManagement) {
					fuelManagementNeed.EcuFamily = attributes.getValue("name");
					fuelManagementNeed.Categories = new ArrayList<String>();
				}

				if (isMaintenance) {
					maintenanceNeed.EcuFamily = attributes.getValue("name");
					maintenanceNeed.Categories = new ArrayList<String>();
				}

				if (isDepthDiagnosis) {
					depthDiagnosisNeed.EcuFamily = attributes.getValue("name");
					depthDiagnosisNeed.Categories = new ArrayList<String>();
				}
			}

			if (qName == "Category") {
				if (isFuelManagement) {
					fuelManagementNeed.Categories.add(attributes
							.getValue("name"));
				}
				if (isMaintenance) {
					maintenanceNeed.Categories.add(attributes.getValue("name"));
				}

				if (isDepthDiagnosis) {
					depthDiagnosisNeed.Categories.add(attributes
							.getValue("name"));
				}

			}

			if (qName == "Feature" && attributes.getIndex("name") != -1) {
				if (attributes.getValue("name").equals(mFuelManagementName)) {
					isFuelManagement = true;
					fuelManagementNeed = new NeedBean();
				}

				if (attributes.getValue("name").equals(mMaintenanceName)) {
					isMaintenance = true;
					maintenanceNeed = new NeedBean();
				}

				if (attributes.getValue("name").equals(mDepthDiagnosisName)) {
					isDepthDiagnosis = true;
					depthDiagnosisNeed = new NeedBean();
				}
			}

			if (qName == "Group") {
				isGroup = true;
				if (attributes.getIndex("name") != -1) {
					if (null != attributes
							&& attributes.getValue("name").equals(
									mEngineWantName)) {
						isEngineWant = true;
						engineBundle = new Bundle();
					}
					if (null != attributes
							&& attributes.getValue("name").equals(mOilWantName)) {
						isOilWant = true;
						oilBundle = new Bundle();
					}
					if (null != attributes
							&& attributes.getValue("name").equals(
									mWaterTemperatureName)) {
						isWaterTemperatureWant = true;
						waterTemperatureBundle = new Bundle();
					}
					if (null != attributes
							&& attributes.getValue("name").equals(mLightsName)) {
						isLightsWant = true;
						lightsBundle = new Bundle();
					}
					if (null != attributes
							&& attributes.getValue("name")
									.equals(mSecurityName)) {
						isSecurityWant = true;
						securityBundle = new Bundle();
					}

					// TODO
				}
			}
			if (isGroup) {
				if (qName == "Parameter") {
					isParameter = true;
				}
			}
			if (isGroup && isParameter && !isCustomRanges) {
				if (isEngineWant) {
					engineBundle.putString(attributes.getValue("name"),
							attributes.getValue("type"));
				}
				if (isOilWant) {
					oilBundle.putString(attributes.getValue("name"),
							attributes.getValue("type"));
				}
				if (isWaterTemperatureWant) {
					waterTemperatureBundle.putString(
							attributes.getValue("name"),
							attributes.getValue("type"));
				}
				if (isLightsWant) {
					lightsBundle.putString(attributes.getValue("name"),
							attributes.getValue("type"));
				}
				if (isSecurityWant) {
					securityBundle.putString(attributes.getValue("name"),
							attributes.getValue("type"));
				}
				// TODO
			}

			super.startElement(uri, localName, qName, attributes);
		}

		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			if (isRangeParameter) {
				if (qName == "Parameter") {
					isRangeParameter = false;
					mOBDRanges.add(tempOdbRangeBean);
					tempOdbRangeBean = null;
				}
			}
			if (isRangeCategory) {
				if (qName == "Category") {
					isRangeCategory = false;
					mOtherRanges.add(tempOtherRangeBean);
					tempOtherRangeBean = null;
				}
			}

			if (isCustomRanges) {
				if (qName == "CustomRanges") {
					isCustomRanges = false;
					isOBDRange = false;
					isNotOBDRange = false;
				}
			}
			if (qName == "Group") {
				isGroup = false;
				isEngineWant = false;
				isLightsWant = false;
				isOilWant = false;
				isWaterTemperatureWant = false;
				isSecurityWant = false;
				// TODO
			}

			if (qName == "OBDRange") {
				isOBDRange = false;
			}
			if (qName == "NotOBDRange") {
				isNotOBDRange = false;
			}

			if (qName == "Feature") {
				isFuelManagement = false;
				isMaintenance = false;
				isDepthDiagnosis = false;
			}

			if (qName == "Parameter" && !isCustomRanges)
				isParameter = false;
			super.endElement(uri, localName, qName);
		}

		@Override
		public void endDocument() throws SAXException {
			Constants.isGetBundleSuitable = true;

			showTag("isEngineWant:" + isEngineWant + "\nisOilWant:" + isOilWant
					+ "\nisWaterTemperatureWant:" + isWaterTemperatureWant
					+ "\nisLightsWant:" + isLightsWant + "\nisSecurityWant:"
					+ isSecurityWant);

			showTag("oilBundle:" + oilBundle.size() + "\nengineBundle:"
					+ engineBundle.size() + "\nsecurityBundle:"
					+ securityBundle.size() + "\nwaterTemperatureBundle:"
					+ waterTemperatureBundle.size() + "\nlightsBundle:"
					+ lightsBundle.size());

			showTag(fuelManagementNeed.toString());
			showTag(maintenanceNeed.toString());
			showTag(depthDiagnosisNeed.toString());

			for (ODBRangeBean b : mOBDRanges) {
				System.out.println(b.getParameterName());
			}

			for (OtherRangeBean b : mOtherRanges) {
				System.out.println(b.getCategoryName());
			}
			super.endDocument();
		}
	}

	/** 将VCI端的错误码转换成可以识别的文字 */
	public static String diagECodeToString(int code) {
		int stringID;
		switch (code) {
		case DiagBusinessLogicWrapper.NO_ERROR:
			stringID = R.string.NO_ERROR;
			break;
		case DiagBusinessLogicWrapper.NO_FIRMWARE_ERROR:
			stringID = R.string.NO_FIRMWARE_ERROR;
			break;
		case DiagBusinessLogicWrapper.PARAMETER_ERROR:
			stringID = R.string.PARAMETER_ERROR;
			break;
		case DiagBusinessLogicWrapper.NO_VCIADDRESS_ERROR:
			stringID = R.string.NO_VCIADDRESS_ERROR;
			break;
		case DiagBusinessLogicWrapper.SETVCIADDRESS_ERROR:
			stringID = R.string.SETVCIADDRESS_ERROR;
			break;
		case DiagBusinessLogicWrapper.CONNECT_VCI_ERROR:
			stringID = R.string.CONNECT_VCI_ERROR;
			break;
		case DiagBusinessLogicWrapper.CONNECT_VEHICLE_ERROR:
			stringID = R.string.CONNECT_VEHICLE_ERROR;
			break;
		case DiagBusinessLogicWrapper.PARAMETERLIST_ERROR:
			stringID = R.string.PARAMETERLIST_ERROR;
			break;
		case DiagBusinessLogicWrapper.INVALID_ERROR:
			stringID = R.string.INVALID_ERROR;
			break;
		case DiagBusinessLogicWrapper.CRITICAL_ERROR:
			stringID = R.string.CRITICAL_ERROR;
			break;
		case DiagBusinessLogicWrapper.FAIL_ERROR:
			stringID = R.string.FAIL_ERROR;
			break;
		case DiagBusinessLogicWrapper.NEEDUPDATEVCI_ERROR:
			stringID = R.string.NEEDUPDATEVCI_ERROR;
			break;
		case DiagBusinessLogicWrapper.NOUPDATE_ERROR:
			stringID = R.string.NOUPDATE_ERROR;
			break;
		case DiagBusinessLogicWrapper.CLOUD_ERROR:
			stringID = R.string.CLOUD_ERROR;
			break;
		case DiagBusinessLogicWrapper.NEEDUPDATESD_ERROR:
			stringID = R.string.NEEDUPDATESD_ERROR;
			break;
		case DiagBusinessLogicWrapper.BOOTLOAD_ERROR:
			stringID = R.string.BOOTLOAD_ERROR;
			break;
		case DiagBusinessLogicWrapper.PARSEXML_ERROR:
			stringID = R.string.PARSEXML_ERROR;
			break;
		case DiagBusinessLogicWrapper.BLWINTERNAL_ERROR:
			stringID = R.string.BLWINTERNAL_ERROR;
			break;
		case DiagBusinessLogicWrapper.SUITABLEVERSION_ERROR:
			stringID = R.string.SUITABLEVERSION_ERROR;
			break;
		case DiagBusinessLogicWrapper.INPUT_PARAMETER_ERROR:
			stringID = R.string.INPUT_PARAMETER_ERROR;
			break;
		case DiagBusinessLogicWrapper.NO_CONFIG_ERROR:
			stringID = R.string.NO_CONFIG_ERROR;
			break;
		case DiagBusinessLogicWrapper.VEHICLE_NOT_FOUND:
			stringID = R.string.VEHICLE_NOT_FOUND;
			break;
		case DiagBusinessLogicWrapper.ECU_NOT_FOUND:
			stringID = R.string.ECU_NOT_FOUND;
			break;
		case DiagBusinessLogicWrapper.ECU_COMM_ERROR:
			stringID = R.string.ECU_COMM_ERROR;
			break;
		case DiagBusinessLogicWrapper.READ_DTC_ERROR:
			stringID = R.string.READ_DTC_ERROR;
			break;
		case DiagBusinessLogicWrapper.NO_SDCLOUD_INIT_ERROR:
			stringID = R.string.NO_SDCLOUD_INIT_ERROR;
			break;

		default:
			stringID = R.string.NO_ERROR;
			break;
		}
		return getStringResourse(mContext, stringID);
	}

	public static String registECodeToString(int code) {
		int stringID = 0;
		switch (code) {
		case ILicensingManager.NO_ERROR:
			stringID = R.string.NO_ERROR;
			break;
		case ILicensingManager.UNKNOWN_VCI_ERROR:
			stringID = R.string.UNKNOWN_VCI_ERROR;
			break;
		case ILicensingManager.VCI_ACT_CODE_MISMATCH_ERROR:
			stringID = R.string.VCI_ACT_CODE_MISMATCH_ERROR;
			break;
		case ILicensingManager.VCI_HW_MISMATCH_ERROR:
			stringID = R.string.VCI_HW_MISMATCH_ERROR;
			break;
		case ILicensingManager.MAX_NUM_DEVICES_REACH_ERROR:
			stringID = R.string.MAX_NUM_DEVICES_REACH_ERROR;
			break;
		case ILicensingManager.NO_CALL_CONF_SYS_INFO_ERROR:
			stringID = R.string.NO_CALL_CONF_SYS_INFO_ERROR;
			break;
		case ILicensingManager.NO_ERROR_ALREADY_ACTIVATED:
			stringID = R.string.NO_ERROR_ALREADY_ACTIVATED;
			break;
		case ILicensingManager.SERVER_INTERNAL_ERROR:
			stringID = R.string.SERVER_INTERNAL_ERROR;
			break;
		case ILicensingManager.COMM_SERVER_ERROR:
			stringID = R.string.COMM_SERVER_ERROR;
			break;
		case ILicensingManager.UPDATE_CLIENT_ERROR:
			stringID = R.string.UPDATE_CLIENT_ERROR;
			break;
		case ILicensingManager.INPUT_PARAMETER_ERROR:
			stringID = R.string.INPUT_PARAMETER_ERROR;
			break;
		case ILicensingManager.VCI_SN_FORMAT_ERROR:
			stringID = R.string.VCI_SN_FORMAT_ERROR;
			break;
		case ILicensingManager.ACTI_CODE_FORMAT_ERROR:
			stringID = R.string.ACTI_CODE_FORMAT_ERROR;
			break;
		default:
			stringID = R.string.NO_ERROR;
			break;
		}
		return getStringResourse(mContext, stringID);
	}

	/** 将云端的错误码转换成可以识别的文字 */
	public static String cloudECodeToString(int code) {
		int stringID = 0;
		switch (code) {
		case CloudWebWrapper.NO_ERROR:
			stringID = R.string.NO_ERROR;
			break;
		case CloudWebWrapper.USERNAME_NULL:
			stringID = R.string.USERNAME_NULL;
			break;
		case CloudWebWrapper.PASSWORD_NULL:
			stringID = R.string.PASSWORD_NULL;
			break;
		case CloudWebWrapper.EMAIL_NULL:
			stringID = R.string.EMAIL_NULL;
			break;
		case CloudWebWrapper.USERNAME_LENGTH_WRONG:
			stringID = R.string.USERNAME_LENGTH_WRONG;
			break;
		case CloudWebWrapper.PASSWORD_LENGTH_WRONG:
			stringID = R.string.PASSWORD_LENGTH_WRONG;
			break;
		case CloudWebWrapper.EMAIL_FORMAT_WRONG:
			stringID = R.string.EMAIL_FORMAT_WRONG;
			break;
		case CloudWebWrapper.NO_LOGIN:
			stringID = R.string.NO_LOGIN;
			break;
		case CloudWebWrapper.OLD_PASSWORD_NULL:
			stringID = R.string.OLD_PASSWORD_NULL;
			break;
		case CloudWebWrapper.APP_NAME_NULL:
			stringID = R.string.APP_NAME_NULL;
			break;
		case CloudWebWrapper.VERSION_NAME_NULL:
			stringID = R.string.VERSION_NAME_NULL;
			break;
		case CloudWebWrapper.PARAM_NAME_NULL:
			stringID = R.string.PARAM_NAME_NULL;
			break;
		case CloudWebWrapper.DTC_CODE_NULL:
			stringID = R.string.DTC_CODE_NULL;
			break;
		case CloudWebWrapper.REGISTER_FAILURE:
			stringID = R.string.REGISTER_FAILURE;
			break;
		case CloudWebWrapper.USERNAME_EXIST:
			stringID = R.string.USERNAME_EXIST;
			break;
		case CloudWebWrapper.UPDATE_PROFILE_FAILURE:
			stringID = R.string.UPDATE_PROFILE_FAILURE;
			break;
		case CloudWebWrapper.OLD_PASSWORD_NOCORRECT:
			stringID = R.string.OLD_PASSWORD_NOCORRECT;
			break;
		case CloudWebWrapper.UPDATE_PASSWORD_FAILURE:
			stringID = R.string.UPDATE_PASSWORD_FAILURE;
			break;
		case CloudWebWrapper.PASSWORD_FIND_EMAIL_FAILURE:
			stringID = R.string.PASSWORD_FIND_EMAIL_FAILURE;
			break;
		case CloudWebWrapper.DELETE_ACCOUNT_FAILURE:
			stringID = R.string.DELETE_ACCOUNT_FAILURE;
			break;
		case CloudWebWrapper.NO_VERSION:
			stringID = R.string.NO_VERSION;
			break;
		case CloudWebWrapper.NO_NEW_VERSION:
			stringID = R.string.NO_NEW_VERSION;
			break;
		case CloudWebWrapper.DOWNLOAD_FAILURE:
			stringID = R.string.DOWNLOAD_FAILURE;
			break;
		case CloudWebWrapper.PASSWORD_ERROR:
			stringID = R.string.PASSWORD_ERROR;
			break;
		case CloudWebWrapper.USERNAME_NO_EXIST:
			stringID = R.string.USERNAME_NO_EXIST;
			break;
		case CloudWebWrapper.NETWORK_ERROR:
			stringID = R.string.NETWORK_ERROR;
			break;
		case CloudWebWrapper.SERVICE_ERROR:
			stringID = R.string.SERVICE_ERROR;
			break;
		case CloudWebWrapper.UNDEFINE_ERROR:
			stringID = R.string.UNDEFINE_ERROR;
			break;
		case CloudWebWrapper.SUITABLEVERSION_ERROR:
			stringID = R.string.SUITABLEVERSION_ERROR;
			break;
		case CloudWebWrapper.LASTOPERATE_ERROR:
			stringID = R.string.LASTOPERATE_ERROR;
			break;
		case CloudWebWrapper.NOINIT_ERROR:
			stringID = R.string.NOINIT_ERROR;
			break;
		case CloudWebWrapper.XMLNOFOUND_ERROR:
			stringID = R.string.XMLNOFOUND_ERROR;
			break;
		case CloudWebWrapper.CERTIFICATE_ERROR:
			stringID = R.string.CERTIFICATE_ERROR;
			break;
		case CloudWebWrapper.INITPARAMETER_ERROR:
			stringID = R.string.INPUT_PARAMETER_ERROR;
			break;
		case CloudWebWrapper.MEMORYRW_ERROR:
			stringID = R.string.MEMORYRW_ERROR;
			break;

		default:
			stringID = R.string.NO_ERROR;
			break;
		}
		return getStringResourse(mContext, stringID);
	}

	public static ArrayList<ODBRangeBean> getmOBDRanges() {
		return mOBDRanges;
	}

	public static void setmOBDRanges(ArrayList<ODBRangeBean> mOBDRanges) {
		Utils.mOBDRanges = mOBDRanges;
	}

	public static ArrayList<OtherRangeBean> getmOtherRanges() {
		return mOtherRanges;
	}

	public static void setmOtherRanges(ArrayList<OtherRangeBean> mOtherRanges) {
		Utils.mOtherRanges = mOtherRanges;
	}

	public static RangeBean getRangeBean(Set<ParameterCategory> categories) {
		for (ParameterCategory c : categories) {
			for (OtherRangeBean b : mOtherRanges) {
				if (c.getName().equals(b.getCategoryName())) {
					return b;
				}
			}
		}
		return null;
	}

	public static RangeBean getRangeBean(String parameterName) {
		for (ODBRangeBean b : mOBDRanges) {
			if (parameterName.equals(b.getParameterName())) {
				return b;
			}
		}
		return null;
	}

}
