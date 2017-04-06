package cn.com.actia.smartdiag.activities;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import cn.com.actia.businesslogic.Parameter;
import cn.com.actia.businesslogic.data.ParameterCategory;
import cn.com.actia.smartdiag.R;
import cn.com.actia.smartdiag.constants.Constants;
import cn.com.actia.smartdiag.model.selection.SystemInfoData;
import cn.com.actia.smartdiag.model.vin.data.ModelYear;
import cn.com.actia.smartdiag.model.vin.data.ModelYearRange;
import cn.com.actia.smartdiag.model.vin.data.ModelYearSingle;
import cn.com.actia.smartdiag.model.wapper.entity.ConfigureSystemInfoResponse;
import cn.com.actia.smartdiag.model.wapper.entity.GetAllParameterResponse;
import cn.com.actia.smartdiag.model.wapper.entity.vehicle.GetSystemInfoDataResponse;
import cn.com.actia.smartdiag.tools.Utils;
import cn.com.actia.smartdiag.wrapper.DiagBusinessLogicWrapper;
import cn.com.actia.smartdiag.wrapper.VehicleManager;

/*
 * Author: poper Email:xut@bluemobi.cn
 * Created Date:2013-3-27
 * Copyright @ 2013 BU
 * Description: 类描述
 *
 * History:
 */
public class SelectEcu extends APPBaseActivity {
	ListView listView;
	SharedPreferences mPreferences;
	ArrayAdapter<String> adapter;
	List<SystemInfoData> needSystemInfoDatas;
	String ecuFam;
	int position;

	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case Constants.NEED_TO_ACTIVATE:
				registDevices();
				break;
			case 0:
				listView.setAdapter(adapter);
				listView.setOnItemClickListener(new OnItemClickListener() {// 设置监听事件
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						SelectEcu.this.position = position;
						new Handler(getMainLooper()).post(new Runnable() {

							public void run() {
								SelectEcu.this.showPrograssDialog(
										SelectEcu.this,
										getResources().getString(
												R.string.app_prompt),
										getResources().getString(
												R.string.fast_scan_is_scan_3));
							}
						});
						new Thread() {
							public void run() {
								ArrayList<Parameter> ps = (ArrayList<Parameter>) getNeededParameter(
										needSystemInfoDatas
												.get(SelectEcu.this.position),
										app.mNeedBean.Categories);
								if (null == ps) {
									// 匹配此汽车信息失败
									mHandler.sendEmptyMessage(-2);
								} else {
									app.mDatas = ps;
									mPreferences
											.edit()
											.putString(
													SelectSuccess.CONTENT_MAKE,
													app.getmMake().getName())
											.commit();
									mPreferences
											.edit()
											.putString(
													SelectSuccess.CONTENT_MODEL,
													app.getmShadowModelYeal().mModel
															.getName())
											.commit();
									ModelYear year = app.getmShadowModelYeal().mModelYear;
									String yearStr = "";
									if (year instanceof ModelYearSingle) {
										yearStr += ((ModelYearSingle) year)
												.getYear();
									} else {
										yearStr += ((ModelYearRange) year)
												.getBeginYear();
										yearStr += "-";
										yearStr += ((ModelYearRange) year)
												.getEndYear();
									}
									mPreferences
											.edit()
											.putString(
													SelectSuccess.CONTENT_MODELYRER,
													yearStr).commit();
									mHandler.sendEmptyMessage(1);
								}
							};
						}.start();

					}
				});
				break;
			case -1:
				// 找不到所需要的ECU类型
				showMessageDialog(SelectEcu.this, R.string.app_prompt,
						R.string.select_no_message_for_this_ecu,
						Constants.STYLE_NORMAL);
				break;
			case -2:
				// 匹配失败
				showMessageDialog(SelectEcu.this, R.string.app_prompt,
						R.string.select_set_error, Constants.STYLE_NORMAL);
				break;
			case 1:// 成功，可以打开activit了
				showToast(R.string.select_set_vechile_success);
				startActivity(new Intent(SelectEcu.this, DataTextActivity.class));
				break;

			default:
				break;
			}
			cancelProgressDialog();
		};
	};

	Runnable runable = new Runnable() {
		public void run() {
			if (null != app.getmShadowModelYeal().mModel
					&& null != app.getmShadowModelYeal().mModelYear) {

				GetSystemInfoDataResponse lSystemResp = VehicleManager
						.getSystemInfoDataForModel(
								app.getmShadowModelYeal().mModel,
								app.getmShadowModelYeal().mModelYear);

				final List<SystemInfoData> alldatas = lSystemResp
						.getSystemInfoDataList();

				final List<SystemInfoData> ecuFamdatas = new ArrayList<SystemInfoData>();

				for (SystemInfoData d : alldatas) {
					if (d.getECUFamInfoData().getName().equals(ecuFam)) {// 如果是我需要的famillyname
						ecuFamdatas.add(d);
					}
				}
				if (ecuFamdatas.size() == 0) {
					mHandler.sendEmptyMessage(-1);
				} else {
					ArrayList<String> itemsarr = new ArrayList<String>();
					needSystemInfoDatas = new ArrayList<SystemInfoData>();
					for (int i = 0; i < ecuFamdatas.size(); i++) {
						if (ecuFamdatas.get(i).getECUFamInfoData().getName()
								.equals(ecuFam)) {
							itemsarr.add(ecuFamdatas.get(i)
									.getECUNameInfoData().getDisplayName());
							needSystemInfoDatas.add(ecuFamdatas.get(i));
						}
					}
					adapter = new ArrayAdapter<String>(SelectEcu.this,
							android.R.layout.simple_list_item_1, itemsarr);
					mHandler.sendEmptyMessage(0);
				}
			} else {
				mHandler.sendEmptyMessage(-1);
			}
		}
	};
	private SharedPreferences bluetoothPreference;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		bluetoothPreference = getSharedPreferences(
				Constants.SHAREPREFERECE_TAG_NAME, Context.MODE_PRIVATE);

		mPreferences = getSharedPreferences(SelectSuccess.PREFERENCE_NAME,
				Context.MODE_PRIVATE);
		setContentView(R.layout.select_ecu_and_ecufamily);
		listView = (ListView) findViewById(R.id.listview);
		ecuFam = getIntent().getStringExtra("ecuFam");
		showPrograssDialog(this, getResources().getString(R.string.app_prompt),
				getResources().getString(R.string.fast_scan_is_scan_3));
		new Thread(runable).start();

	}

	/** 从一个特定的{@code SystemInfoData}里面去的{@code List<Parameter>} */
	public List<Parameter> getNeededParameter(SystemInfoData data,
			ArrayList<String> neededCategorys) {
		ConfigureSystemInfoResponse configureSystemInfoResponse = null;
		app.setpECUFamily(data.getECUFamInfoData().getName());
		app.setpECUName(data.getECUNameInfoData().getName());
		app.setpVehicleName(data.getVehInfoData().getName());
		if (Utils.isCompatible(bluetoothPreference).what == DiagBusinessLogicWrapper.NO_ERROR) {

			if (!app.isSelectVehicleConfigSystemInfo) {// 若从前没有配置过，则需要重新配置
				configureSystemInfoResponse = DiagBusinessLogicWrapper
						.configureSystemInfo(app.getpVehicleName(),
								app.getpECUFamily(), app.getpECUName());
			}

			if (app.isSelectVehicleConfigSystemInfo
					|| configureSystemInfoResponse.getCode() == DiagBusinessLogicWrapper.NO_ERROR) {
				app.isSelectVehicleConfigSystemInfo = true;
				Constants.isSelectVehicleConfigSystemInfo = true;

				GetAllParameterResponse getAllParameterResponse = DiagBusinessLogicWrapper
						.getAllParameter();
				if (getAllParameterResponse.getCode() == DiagBusinessLogicWrapper.NO_ERROR) {
					List<ParameterCategory> parameterCategories = getAllParameterResponse
							.getParamCategoryList();
					boolean result = true;
					if (neededCategorys.size() == 0) {
						result = false;
					}
					for (String needCategory : neededCategorys) {
						boolean hasThisneed = false;
						for (ParameterCategory ca : parameterCategories) {
							if (ca.getName().equals(needCategory)) {
								hasThisneed = true;
								break;
							}
						}
						if (hasThisneed == false) {//
							// 这里表示所有的ParameterCategory都不包含有needCategory
							result = false;
							break;
						}
					}

					if (result) {// 包含所有必须的ECU
						List<Parameter> resultParameters = new ArrayList<Parameter>();
						for (String need : neededCategorys) {
							for (Parameter parameter : getAllParameterResponse
									.getParameters()) {
								for (ParameterCategory parameterCategory : parameter
										.getParameterCategorySet()) {
									if (need.equals(parameterCategory.getName())) {// 如果包含有这个ECU
										if (!resultParameters
												.contains(parameter)) {
											resultParameters.add(parameter);
										}
										break;
									}
								}
							}
						}
						return resultParameters;
					} else {
						return null;
					}
				}
			}
		}
		return null;
	}

	@Override
	protected void onDestroy() {

		cancelProgressDialog();
		super.onDestroy();
	}
}
