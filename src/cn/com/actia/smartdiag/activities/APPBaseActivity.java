package cn.com.actia.smartdiag.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import cn.com.actia.smartdiag.R;
import cn.com.actia.smartdiag.beans.NeedBean;
import cn.com.actia.smartdiag.beans.ShadowModelYeal;
import cn.com.actia.smartdiag.constants.Constants;
import cn.com.actia.smartdiag.model.selection.ECUFamilyInfoData;
import cn.com.actia.smartdiag.model.selection.SystemInfoData;
import cn.com.actia.smartdiag.model.vin.data.Make;
import cn.com.actia.smartdiag.model.vin.data.Model;
import cn.com.actia.smartdiag.model.vin.data.ModelYear;
import cn.com.actia.smartdiag.model.wapper.entity.vehicle.GetModelsForMakeResponse;
import cn.com.actia.smartdiag.model.wapper.entity.vehicle.GetSystemInfoDataResponse;
import cn.com.actia.smartdiag.tools.Utils;
import cn.com.actia.smartdiag.wrapper.VehicleManager;

public class APPBaseActivity extends Activity {
	public static ArrayList<Activity> toFinishActivities = new ArrayList<Activity>();
	protected MyApplication app;
	private ProgressDialog mProgressDialog;

	public void registDevices() {
		startActivity(new Intent(this, RegistDevicesActivity.class));
		this.finish();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		app = (MyApplication) getApplication();
	}

	public void showToast(String msg) {
		Utils.showToast(this, msg);
	}

	public void showToast(int stringid) {
		String msg = Utils.getStringResourse(this, stringid);
		showToast(msg);
	}

	public void showTag(String msg) {
		Utils.showTag(msg);
	}

	public String getStringResourse(int id) {
		return Utils.getStringResourse(this, id);
	}

	public boolean isAdressAvillible() {
		if (null == Constants.BLUETOOTH_ADRESS
				|| "".equals(Constants.BLUETOOTH_ADRESS)) {
			showToast(R.string.get_a_vic_first);
			startActivity(new Intent(APPBaseActivity.this,
					VCISettingActivity.class));
			return false;
		}
		return true;
	}

	boolean isMakeUsable(Make make, NeedBean need) {
		GetModelsForMakeResponse getModelsForMakeResponse = VehicleManager
				.getModelsForMake(make);
		if (getModelsForMakeResponse.getCode() == VehicleManager.NO_ERROR) {
			List<Model> models = getModelsForMakeResponse.getModels();
			for (Model model : models) {
				if (isModelUsable(model, need)) {
					return true;
				}
			}
		}
		return false;
	}

	boolean isShadowModelYearUsabel(ShadowModelYeal modelyear, NeedBean need) {
		if (null == need)
			return true;
		if (null == need.EcuFamily || "".equals(need.EcuFamily))
			return true;
		GetSystemInfoDataResponse getSystemInfoDataResponse = VehicleManager
				.getSystemInfoDataForModel(modelyear.mModel,
						modelyear.mModelYear);
		List<SystemInfoData> systemInfoDatas = getSystemInfoDataResponse
				.getSystemInfoDataList();
		for (SystemInfoData data : systemInfoDatas) {
			ECUFamilyInfoData ecuFamilyInfoData = data.getECUFamInfoData();
			String ecufamilyName = ecuFamilyInfoData.getName();

			if (need.EcuFamily.equals(ecufamilyName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @author poper 2013-4-23 下午4:42:16
	 * @param model
	 * @param need
	 *            包含sdCenterDataSource配置的对应功能的ECUFamily的名字
	 * @return
	 * @return boolean
	 */
	boolean isModelUsable(Model model, NeedBean need) {
		List<ModelYear> modelYears = model.getModelYearList();
		for (ModelYear year : modelYears) {
			ShadowModelYeal shadowModelYeal = new ShadowModelYeal();
			shadowModelYeal.mModel = model;
			shadowModelYeal.mModelYear = year;
			if (isShadowModelYearUsabel(shadowModelYeal, need)) {
				return true;
			}
		}
		return false;
	}

	public synchronized void showPrograssDialog(Context context, String title,
			String msg) {
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

	public synchronized void cancelProgressDialog() {
		if (null != mProgressDialog && mProgressDialog.isShowing()) {
			mProgressDialog.cancel();
		}
		mProgressDialog = null;
	}

	public void showMessageDialog(Context context, int titleId, String msg,
			int styleId) {
		String title = context.getResources().getString(titleId);
		showMessageDialog(context, title, msg, styleId);
	}

	public void showMessageDialog(Context context, int titleId, int msgId,
			int styleId) {
		String title = context.getResources().getString(titleId);
		String msg = context.getResources().getString(msgId);
		showMessageDialog(context, title, msg, styleId);
	}

	public int isDialogShowing = -1;

	public void showMessageDialog(final Context context, String title,
			String msg, int styleId) {
		Builder builder = new Builder(context);
		builder.setTitle(title);
		builder.setMessage(msg);
		int k;
		switch (styleId) {
		case Constants.STYLE_TWO_BUTTON:
			k = Constants.STYLE_NORMAL;
			showTag("Constants.STYLE_TWO_BUTTON");
			builder.setNeutralButton(
					context.getResources().getString(R.string.app_ok),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					});
			builder.setNegativeButton(getStringResourse(R.string.app_cancel),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					});
			break;
		case Constants.STYLE_EXIT_ACTIVITY_STARTSETTING:
			k = Constants.STYLE_EXIT_ACTIVITY_STARTSETTING;
			System.out.println(getResources().getString(R.string.app_ok));
			showTag("Constants.STYLE_EXIT_ACTIVITY_STARTSETTING");
			builder.setNeutralButton(
					context.getResources().getString(R.string.app_ok),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							((Dialog) dialog)
									.setOnCancelListener(new OnCancelListener() {
										public void onCancel(
												DialogInterface dialog) {
											startActivity(new Intent(context,
													VCISettingActivity.class));
											((Activity) context).finish();
										}
									});
							dialog.cancel();
						}
					});
			break;
		case Constants.STYLE_EXIT_ACTIVITY:
			k = Constants.STYLE_EXIT_ACTIVITY;
			System.out.println(getResources().getString(R.string.app_ok));
			showTag("Constants.STYLE_EXIT_ACTIVITY");
			builder.setNeutralButton(
					context.getResources().getString(R.string.app_ok),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							((Dialog) dialog)
									.setOnCancelListener(new OnCancelListener() {
										public void onCancel(
												DialogInterface dialog) {
											((Activity) context).finish();
										}
									});
							dialog.cancel();
						}
					});
			break;
		default:
			k = Constants.STYLE_NORMAL;
			showTag("default");
			builder.setNeutralButton(getResources().getString(R.string.app_ok),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					});
			break;
		}
		final AlertDialog dialog = builder.create();
		if (k == isDialogShowing) {

		} else {
			dialog.show();
			isDialogShowing = k;
		}

		dialog.setOnCancelListener(new OnCancelListener() {

			public void onCancel(DialogInterface dialog) {
				isDialogShowing = -1;
			}
		});

	}
}
