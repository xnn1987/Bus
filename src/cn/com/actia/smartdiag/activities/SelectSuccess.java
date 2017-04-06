package cn.com.actia.smartdiag.activities;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cn.com.actia.businesslogic.Parameter;
import cn.com.actia.businesslogic.data.ParameterCategory;
import cn.com.actia.smartdiag.R;
import cn.com.actia.smartdiag.beans.NeedBean;
import cn.com.actia.smartdiag.beans.ShadowModelYeal;
import cn.com.actia.smartdiag.constants.Constants;
import cn.com.actia.smartdiag.model.selection.SystemInfoData;
import cn.com.actia.smartdiag.model.vin.VINInfo;
import cn.com.actia.smartdiag.model.vin.VINInfoType;
import cn.com.actia.smartdiag.model.vin.data.Make;
import cn.com.actia.smartdiag.model.vin.data.Model;
import cn.com.actia.smartdiag.model.vin.data.ModelYear;
import cn.com.actia.smartdiag.model.vin.data.ModelYearRange;
import cn.com.actia.smartdiag.model.vin.data.ModelYearSingle;
import cn.com.actia.smartdiag.model.vin.data.VehicleSelection;
import cn.com.actia.smartdiag.model.wapper.entity.ConfigureSystemInfoResponse;
import cn.com.actia.smartdiag.model.wapper.entity.GetAllParameterResponse;
import cn.com.actia.smartdiag.model.wapper.entity.vehicle.GetAllMakesResponse;
import cn.com.actia.smartdiag.model.wapper.entity.vehicle.GetModelsForMakeResponse;
import cn.com.actia.smartdiag.model.wapper.entity.vehicle.GetSystemInfoDataResponse;
import cn.com.actia.smartdiag.model.wapper.entity.vehicle.GetVINInfoResponse;
import cn.com.actia.smartdiag.tools.CommonUtils;
import cn.com.actia.smartdiag.tools.Utils;
import cn.com.actia.smartdiag.wrapper.DiagBusinessLogicWrapper;
import cn.com.actia.smartdiag.wrapper.VehicleManager;

@SuppressLint("HandlerLeak")
public class SelectSuccess extends APPBaseActivity implements OnClickListener {
	public static final int ACTIVITY_STATE_MANUAL = 1;
	public static final int ACTIVITY_STATE_AUTOMATIC = 2;
	public static final int ACTIVITY_STATE_SEMI_AUTOMATIC = 3;

	public static final int MODEL_RESULT_CODE = 11;
	public static final int MAKE_RESULT_CODE = 13;

	public static final String CONTENT_MAKE = "content_make";
	public static final String CONTENT_MODEL = "content_model";
	public static final String CONTENT_MODELYRER = "content_modelyear";

	public static final String PREFERENCE_NAME = "select_sharepreference";
	public static final String PREFERENCE_ECUName = "ECUName";
	public static final String PREFERENCE_VehicleName = "VehicleName";
	public static final String PREFERENCE_ECUFamily = "ECUFamily";

	public static final String ACTIVITY_STATE = "selet_state";

	private SharedPreferences mPreferences;

	private int mActivity_state;

	private Button changeBtn;
	private TextView headInformationTV;
	private TextView tvMake;
	private TextView tvModel;

	private ImageView ivMake;
	private ImageView ivModel;

	private static boolean isGetAVIN;

	private NeedBean needBean;
	private SharedPreferences bluetoothPreference;

	private void getMakeModelFromShare() {
		app.setmMake(null);
		app.setmShadowModelYeal(null);

		if (mPreferences.getString(CONTENT_MAKE, "").length() != 0) {// 读取sharepreferce里面的数据
			String makeName = mPreferences.getString(CONTENT_MAKE, "");
			String modelName = mPreferences.getString(CONTENT_MODEL, "");
			String modelyearNum = mPreferences.getString(CONTENT_MODELYRER, "");

			GetAllMakesResponse getAllMakesResponse = VehicleManager
					.getAllMakes();
			if (getAllMakesResponse.getCode() == VehicleManager.NO_ERROR) {
				for (Make make : getAllMakesResponse.getMakeList()) {
					if (make.getName().equals(makeName)) {
						app.setmMake(make);
						app.setmShadowModelYeal(new ShadowModelYeal());
						GetModelsForMakeResponse getModelsForMakeResponse = VehicleManager
								.getModelsForMake(make);
						if (getModelsForMakeResponse.getCode() == VehicleManager.NO_ERROR) {
							for (Model model : getModelsForMakeResponse
									.getModels()) {
								if (model.getName().equals(modelName)) {
									app.getmShadowModelYeal().mModel = model;
									List<ModelYear> modelYear = model
											.getModelYearList();
									String[] yearNums = modelyearNum.split("-");
									if (yearNums.length == 1) {
										for (ModelYear y : modelYear) {
											if (y instanceof ModelYearSingle) {
												if (((ModelYearSingle) y)
														.getYear() == Integer
														.parseInt(yearNums[1])) {
													app.getmShadowModelYeal().mModelYear = y;
													break;
												}
											}
										}
									} else if (yearNums.length == 2) {
										int start = Integer
												.parseInt(yearNums[0]);
										int end = Integer.parseInt(yearNums[1]);
										for (ModelYear y : modelYear) {
											if (y instanceof ModelYearRange) {
												if (((ModelYearRange) y)
														.getBeginYear() == start
														&& ((ModelYearRange) y)
																.getEndYear() == end) {
													app.getmShadowModelYeal().mModelYear = y;
												}
											}
										}
									}
								}
							}
						}
						break;
					}
				}
			}
		}
		if (null == app.getmMake() || null == app.getmShadowModelYeal()
				|| null == app.getmShadowModelYeal().mModel
				|| null == app.getmShadowModelYeal().mModelYear) {
			app.setmMake(null);
			app.setmShadowModelYeal(null);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		bluetoothPreference = getSharedPreferences(
				Constants.SHAREPREFERECE_TAG_NAME, Context.MODE_PRIVATE);
		setTitle(app.Vehcle_Select_Activity_Name);
		mPreferences = getSharedPreferences(PREFERENCE_NAME,
				Context.MODE_PRIVATE);
		setContentView(R.layout.selection_vehicle_successful_layout);
		// 可以判断从上文传回来的参数，如果需要改变状态，则改变
		mActivity_state = getIntent().getIntExtra(ACTIVITY_STATE,
				ACTIVITY_STATE_MANUAL);
		if (getIntent().hasExtra(ACTIVITY_STATE)) {

		} else {
			getMakeModelFromShare();
		}

		headInformationTV = (TextView) findViewById(R.id.select_tv_information);
		findViewById(R.id.select_btn_confirm).setOnClickListener(this);
		(changeBtn = (Button) findViewById(R.id.select_btn_manual_selection))
				.setOnClickListener(this);
		(tvMake = (TextView) findViewById(R.id.select_tvmake))
				.setOnClickListener(this);
		(tvModel = (TextView) findViewById(R.id.select_tvmodel))
				.setOnClickListener(this);
		(ivMake = (ImageView) findViewById(R.id.select_tvmake_iv))
				.setOnClickListener(this);
		(ivModel = (ImageView) findViewById(R.id.select_tvmodel_iv))
				.setOnClickListener(this);

		if (null == app.getmMake() && !isGetAVIN) {
			showPrograssDialog(SelectSuccess.this,
					Utils.getStringResourse(this, R.string.app_prompt),
					Utils.getStringResourse(this, R.string.select_is_guessing));
			new Thread(new Runnable() {
				public void run() {
					getVIN(handlerVin);
				}
			}).start();
		} else {
			setMakeView(app.getmMake());
			setModelView(app.getmShadowModelYeal());

			if (mActivity_state == ACTIVITY_STATE_AUTOMATIC) {
				switchToSelectMissing();
			} else {
				switchToFirstLoading();
			}
		}
	}

	/** 转换为第一次启动的模式 */
	public void switchToFirstLoading() {
		// 您选择的汽车
		changeBtn.setText(R.string.select_get_other_car);
		// 使用其他汽车
		headInformationTV.setText(R.string.select_used_car);
	}

	/** 转变为可以改变内容的模式 */
	public void switchToSelectMissing() {
		// 使用其他手动模式获取车辆信息
		changeBtn.setText(R.string.select_get_other_manual);
		// 手动模式
		headInformationTV.setText(R.string.select_used_car);
	}

	/** 启动自动的Activity实则是改变此Activity的状态 */
	/** 如果是自动的，但是参数不全，我将抛出错位 */
	/**
	 * 自动的方式 1.将此Acitivity的状态转变为 {@code SelectSuccess#ACTIVITY_STATE_AUTOMATIC}
	 * 状态； 2.将{@code app.mMake,app.mShadowModel赋值} 3.将视图转变状态
	 * 如果是自动状态，却缺少需要的参数，则会抛出 错误
	 * 
	 */
	public void startActivityAutomatic(GetVINInfoResponse getVinInfoResponse) {
		mActivity_state = ACTIVITY_STATE_AUTOMATIC;
		app.setmShadowModelYeal(new ShadowModelYeal());
		if (null != getVinInfoResponse.getGuessedMake()) {
			app.setmMake(getVinInfoResponse.getGuessedMake());
		} else {
			try {
				throw new Exception();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (null != getVinInfoResponse.getGuessedModel()) {
			app.getmShadowModelYeal().mModel = getVinInfoResponse
					.getGuessedModel();
		} else {
			try {
				throw new Exception();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		VINInfo vinInfo = null;
		for (VINInfo i : getVinInfoResponse.getAvailableVINInfo()) {
			if (i.getType() == VINInfoType.MODEL_YEAR) {
				vinInfo = i;
				break;
			}
		}
		if (null != vinInfo) {
			int year = Integer.parseInt(vinInfo.getData());
			for (ModelYear my : app.getmShadowModelYeal().mModel
					.getModelYearList()) {
				if (my instanceof ModelYearSingle) {
					if (year == ((ModelYearSingle) my).getYear()) {
						app.getmShadowModelYeal().mModelYear = my;
						break;
					}
				} else {
					int start = ((ModelYearRange) my).getBeginYear();
					int end = ((ModelYearRange) my).getEndYear();
					if (year >= start && year <= end) {
						app.getmShadowModelYeal().mModelYear = my;
						break;
					}
				}
			}
			if (app.getmShadowModelYeal() == null) {
				try {
					throw new Exception();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return;
			} else if (null == app.getmShadowModelYeal().mModelYear) {// 自动选择，如果没有匹配的年份则提示用户，并跳转到对应页面进行选择
				new Handler(getMainLooper()).post(new Runnable() {
					public void run() {
						showToast(R.string.select_model_year_no_find);
					}
				});
				try {
					throw new Exception();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					new Handler(Looper.getMainLooper()).post(new Runnable() {
						public void run() {
							switchToSelectMissing();
							setMakeView(app.getmMake());
						}
					});
					Intent intent = new Intent(SelectSuccess.this,
							SelectModel.class);
					intent.putExtra(ACTIVITY_STATE,
							ACTIVITY_STATE_SEMI_AUTOMATIC);
					startActivity(intent);
					toFinishActivities.add(this);
				}

			}
		} else {
			try {
				throw new Exception();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return;
		}
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			public void run() {
				switchToSelectMissing();
				setMakeView(app.getmMake());
			}
		});

	}

	/**
	 * 启动半自动的Activity <a>先将app.mMake,app,mShadowModel赋值，然后根据mShadowModel等参数是否存在，
	 * 考虑启动哪个Activity，去选择mMake,或者mShadowModel</a>
	 */

	public void startActivtiyPartial(GetVINInfoResponse getVinInfoResponse) {
		app.setmShadowModelYeal(new ShadowModelYeal());
		if (null != getVinInfoResponse.getGuessedMake()) {
			app.setmMake(getVinInfoResponse.getGuessedMake());
		}
		if (null != getVinInfoResponse.getGuessedModel()) {
			app.getmShadowModelYeal().mModel = getVinInfoResponse
					.getGuessedModel();
		}
		VINInfo vinInfo = null;
		for (VINInfo i : getVinInfoResponse.getAvailableVINInfo()) {
			if (i.getType() == VINInfoType.MODEL_YEAR) {
				vinInfo = i;
				break;
			}
		}
		if (null != vinInfo) {
			int year = Integer.parseInt(vinInfo.getData());
			app.getmShadowModelYeal().mYear = year;
			if (null != app.getmShadowModelYeal().mModel
					&& null != app.getmShadowModelYeal().mModel
							.getModelYearList()) {
				for (ModelYear my : app.getmShadowModelYeal().mModel
						.getModelYearList()) {
					if (my instanceof ModelYearSingle) {
						if (year == ((ModelYearSingle) my).getYear()) {
							app.getmShadowModelYeal().mModelYear = my;
							break;
						} else {
							int start = ((ModelYearRange) my).getBeginYear();
							int end = ((ModelYearRange) my).getEndYear();
							if (year >= start && year <= end) {
								app.getmShadowModelYeal().mModelYear = my;
								break;
							}
						}
					}
				}
			}
		}

		if (null == app.getmMake()) {
			Intent intent = new Intent(SelectSuccess.this, SelectMake.class);
			intent.putExtra(ACTIVITY_STATE, ACTIVITY_STATE_SEMI_AUTOMATIC);
			startActivity(intent);
			toFinishActivities.add(this);
		} else {
			Intent intent = new Intent(SelectSuccess.this, SelectModel.class);
			intent.putExtra(ACTIVITY_STATE, ACTIVITY_STATE_SEMI_AUTOMATIC);
			startActivity(intent);
			toFinishActivities.add(this);
		}
	}

	/** 启动手动的Activity */
	public void startActivityManual() {
		Intent intent = new Intent(SelectSuccess.this, SelectMake.class);
		intent.putExtra(ACTIVITY_STATE, ACTIVITY_STATE_MANUAL);
		startActivity(intent);
		toFinishActivities.add(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		System.out.println(requestCode);
		System.out.println(resultCode);
		System.out.println(data);

		switch (resultCode) {
		case RESULT_OK:// 获取make的数据
			if (requestCode == MAKE_RESULT_CODE) {
				setMakeView(app.getmMake());
			} else {
				setModelView(app.getmShadowModelYeal());
			}
			break;
		default:// 获取model的数据

			break;
		}
	}

	/** 设置make界面显示的内容 */
	public void setMakeView(Make make) {
		if (make == null) {
			tvMake.setText("点击设置内容");
			ivMake.setImageResource(R.drawable.select_vehicle_default);
			return;
		} else {
			tvMake.setText(make.getDisplayName());
			ivMake.setImageDrawable(Utils.getDrawable(make.getPicturePath()));
		}
		setModelView(app.getmShadowModelYeal());
		// TODO
	}

	/** 设置Model界面显示的内容 */
	public void setModelView(ShadowModelYeal model) {
		if (model == null) {
			tvModel.setText("点击设置内容");
			return;
		} else {
			if (null != app.getmShadowModelYeal().mModel) {
				StringBuffer showSb = new StringBuffer(
						app.getmShadowModelYeal().mModel.getDisplayName());
				ModelYear y = app.getmShadowModelYeal().mModelYear;
				if (null == y) {
					tvModel.setText("点击设置内容");
				} else {
					if (y instanceof ModelYearSingle) {
						showSb.append("\r\n").append(
								((ModelYearSingle) y).getYear());
					} else {
						showSb.append("\r\n")
								.append(((ModelYearRange) y).getBeginYear())
								.append("~")
								.append(((ModelYearRange) y).getEndYear());
					}
					tvModel.setText(showSb.toString());
					ivModel.setImageDrawable(Utils.getDrawable(model.mModelYear
							.getRelativePathPicture()));
				}

			}
		}
	}

	public void clickMake() {
		switch (mActivity_state) {
		case ACTIVITY_STATE_AUTOMATIC:
			Intent intent = new Intent(SelectSuccess.this, SelectMake.class);
			intent.putExtra(ACTIVITY_STATE, mActivity_state);
			startActivityForResult(intent, MAKE_RESULT_CODE);
			break;

		default:
			// TODO
			showToast("Can't change in this state!");
			break;
		}
	}

	public void clickModel() {
		switch (mActivity_state) {
		case ACTIVITY_STATE_AUTOMATIC:
			Intent intent = new Intent(SelectSuccess.this, SelectModel.class);
			intent.putExtra(ACTIVITY_STATE, mActivity_state);
			startActivityForResult(intent, MODEL_RESULT_CODE);
			break;

		default:
			// TODO
			showToast("Can't change in this state!");
			break;
		}
	}

	public void showVinDialog(String title, String content,
			OnCancelListener cancelLS) {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				SelectSuccess.this);
		AlertDialog dialog = builder.create();
		dialog.setTitle(title);
		dialog.setMessage(content);
		showMessageDialog(this, 1, 1, 1);
		dialog.setOnCancelListener(cancelLS);
		dialog.setButton(Utils.getStringResourse(SelectSuccess.this,
				R.string.app_prompt), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		dialog.show();
	}

	/** 根据vin获取信息，并选择跳转的界面 */
	public void getVIN(Handler handler) {
		Message msg = Utils.getVIN(bluetoothPreference);
		cancelProgressDialog();
		if (DiagBusinessLogicWrapper.NO_ERROR == msg.what) {
			String vin = null;
			if (null != msg.obj)
				vin = msg.obj.toString();
			if (null == vin || "".equals(vin)) {// 如果vin为空，则有为空的处理
				startActivityManual();
				showTag("没有vin手动");
			} else {// 如果vin不为空，则可以获取vin的信息
				final GetVINInfoResponse getVinInfoResponse = VehicleManager
						.getVINInfo(vin);
				if (VehicleManager.NO_ERROR == getVinInfoResponse.getCode()) {
					VehicleSelection vehicleSelection = getVinInfoResponse
							.getVehicleSelectionType();
					if (vehicleSelection == VehicleSelection.AUTOMATIC) {// 自动
						handler.handleMessage(handler.obtainMessage(0, 1, 1,
								getVinInfoResponse));
						// TODO
						startActivityAutomatic(getVinInfoResponse);
						showTag("全自动");
					} else if (vehicleSelection == VehicleSelection.SEMI_AUTOMATIC) {// 半自动
						handler.handleMessage(handler.obtainMessage(0, 2, 2,
								getVinInfoResponse));
						startActivtiyPartial(getVinInfoResponse);
						showTag("半自动");
					} else {// 手动
						handler.handleMessage(handler.obtainMessage(0, 3, 3,
								getVinInfoResponse));
						startActivityManual();
						showTag("手动");
					}
				} else {
					handler.handleMessage(msg);
					// Looper.prepare();
					// showMessageDialog(SelectSuccess.this,
					// R.string.app_prompt,
					// msg.obj.toString(), Constants.STYLE_NORMAL);
				}
			}
		} else {
			handler.handleMessage(msg);
			// Looper.prepare();
			// showMessageDialog(SelectSuccess.this, R.string.app_prompt,
			// msg.obj.toString(),
			// Constants.STYLE_EXIT_ACTIVITY_STARTSETTING);
		}
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
				String error = Utils
						.diagECodeToString(configureSystemInfoResponse
								.getCode());
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
					// if (neededCategorys.size() == 0) {
					// result = false;
					// }
					// for (String needCategory : neededCategorys) {
					// boolean hasThisneed = false;
					// for (ParameterCategory ca : parameterCategories) {
					// if (ca.getName().equals(needCategory)) {
					// hasThisneed = true;
					// break;
					// }
					// }
					// if (hasThisneed == false) {//
					// 这里表示所有的ParameterCategory都不包含有needCategory
					// result = false;
					// break;
					// }
					// }

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

	public void getNeededParameter(final ArrayList<String> categroys,
			final Handler handler) {
		final Handler tempHandler = new Handler(Looper.getMainLooper()) {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0:
					showPrograssDialog(SelectSuccess.this,
							Utils.getStringResourse(SelectSuccess.this,
									R.string.app_prompt),
							Utils.getStringResourse(SelectSuccess.this,
									R.string.app_waiting));
					getNeededParameter(categroys, msg.obj.toString(), handler);
					break;

				default:
					handler.sendMessage(msg);
					break;
				}
			};
		};

		if (null != app.getmShadowModelYeal().mModel
				&& null != app.getmShadowModelYeal().mModelYear) {

			GetSystemInfoDataResponse lSystemResp = VehicleManager
					.getSystemInfoDataForModel(
							app.getmShadowModelYeal().mModel,
							app.getmShadowModelYeal().mModelYear);

			final List<SystemInfoData> alldatas = lSystemResp
					.getSystemInfoDataList();
			final List<String> tempecufamNamesDisplay = new ArrayList<String>();
			final List<String> tempecufamNames = new ArrayList<String>();
			for (SystemInfoData d : alldatas) {
				if (!tempecufamNames.contains(d.getECUFamInfoData().getName())) {
					tempecufamNames.add(d.getECUFamInfoData().getName());
					tempecufamNamesDisplay.add(d.getECUFamInfoData()
							.getDisplayName());
				}
			}

			final String[] ECUFamNames = new String[tempecufamNamesDisplay
					.size()];
			tempecufamNamesDisplay.toArray(ECUFamNames);

			cancelProgressDialog();
			new Handler(getMainLooper()).post(new Runnable() {

				public void run() {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							SelectSuccess.this);
					builder.setItems(ECUFamNames,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									tempHandler.sendMessage(handler
											.obtainMessage(0,
													tempecufamNames.get(which)));
								}
							});
					final AlertDialog alertDialog = builder.create();
					alertDialog.setButton(Utils.getStringResourse(
							SelectSuccess.this, R.string.app_cancel),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									tempHandler.handleMessage(handler.obtainMessage(
											-3, Utils.getStringResourse(
													SelectSuccess.this,
													R.string.select_exit)));
									alertDialog.dismiss();
								}
							});
					alertDialog
							.setTitle(R.string.drivermanager_current_get_ecufamily);
					alertDialog.setCanceledOnTouchOutside(false);
					alertDialog.show();
				}
			});
		}
	}

	boolean deepDiag;

	public void getNeededParameter(final ArrayList<String> categroys,
			String ecuFam, final Handler handler) {
		List<Parameter> lParamToRead = new ArrayList<Parameter>();
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
				// if (d.getECUFamInfoData().getName().equals(ecuFam)) {//
				// 如果是我需要的famillyname
				ecuFamdatas.add(d);
				// }
			}

			if (ecuFamdatas.size() == 0) {
				// 找不到所需要的ECU类型
				showMessageDialog(SelectSuccess.this, R.string.app_prompt,
						R.string.select_no_message_for_this_ecu,
						Constants.STYLE_NORMAL);
			} else {

				if (ecuFamdatas.size() <= 3 && ecuFamdatas.size() > 0
						&& !deepDiag) {
					for (SystemInfoData systemInfoData : ecuFamdatas) {
						if (systemInfoData.getECUFamInfoData().getName()
								.equals(ecuFam)) {
							lParamToRead = getNeededParameter(systemInfoData,
									categroys);
							if (null != lParamToRead
									&& lParamToRead.size() != 0) {
								break;
							}
						}
					}

					// int i = 0;
					// do {
					// if (ecuFamdatas.get(i).getECUFamInfoData().getName()
					// .equals(ecuFam)) {// 如果是我需要的famillyname
					//
					// }
					// i++;
					// if (i == ecuFamdatas.size() - 1) {
					// break;
					// }
					// } while ((null == lParamToRead || lParamToRead.size() ==
					// 0));

					if (null == lParamToRead) {
						handler.sendMessage(handler.obtainMessage(-1, Utils
								.getStringResourse(this,
										R.string.select_ecu_nomessage_toread)));// 发送错误信息
					} else {
						handler.sendMessage(handler.obtainMessage(0,
								lParamToRead));// 将ArrayList包装到Handler里面传回去
					}

				} else {
					// String[] items = new String[datas.size()];
					ArrayList<String> itemsarr = new ArrayList<String>();
					final List<SystemInfoData> needSystemInfoDatas = new ArrayList<SystemInfoData>();
					for (int i = 0; i < ecuFamdatas.size(); i++) {
						// TODO

						if (ecuFamdatas.get(i).getECUFamInfoData().getName()
								.equals(ecuFam)) {
							itemsarr.add(ecuFamdatas.get(i)
									.getECUNameInfoData().getDisplayName());
							needSystemInfoDatas.add(ecuFamdatas.get(i));
						}
						// items[i] = datas.get(i).getECUNameInfoData()
						// .getDisplayName();
					}
					String[] items = new String[itemsarr.size()];
					items = itemsarr.toArray(items);

					final AlertDialog.Builder builder = new AlertDialog.Builder(
							this);
					builder.setItems(items,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									new Handler(getMainLooper())
											.post(new Runnable() {
												public void run() {
													showPrograssDialog(
															SelectSuccess.this,
															Utils.getStringResourse(
																	SelectSuccess.this,
																	R.string.app_prompt),
															Utils.getStringResourse(
																	SelectSuccess.this,
																	R.string.app_waiting));
												}
											});
									final Integer index = which;
									new Thread(new Runnable() {
										public void run() {
											ArrayList<Parameter> ps = (ArrayList<Parameter>) getNeededParameter(
													needSystemInfoDatas
															.get(index),
													categroys);
											System.out.println(index);
											System.out
													.println(needSystemInfoDatas
															.get(index));
											if (null != ps && ps.size() > 0) {
												handler.sendMessage(handler
														.obtainMessage(0, ps));
											} else {
												handler.sendMessage(handler
														.obtainMessage(
																-1,
																Utils.getStringResourse(
																		SelectSuccess.this,
																		R.string.select_ecu_nomessage)));
											}
										}
									}).start();

								}
							});
					new Handler(getMainLooper()).post(new Runnable() {

						public void run() {
							// TODO Auto-generated method stub
							final AlertDialog alertDialog = builder.create();
							alertDialog.setButton(Utils.getStringResourse(
									SelectSuccess.this, R.string.app_cancel),
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
											handler.handleMessage(handler.obtainMessage(
													-3,
													Utils.getStringResourse(
															SelectSuccess.this,
															R.string.select_exit)));
											alertDialog.dismiss();
										}
									});
							alertDialog
									.setTitle(R.string.drivermanager_current_get_ecu);
							alertDialog.setCanceledOnTouchOutside(false);
							SelectSuccess.this.cancelProgressDialog();
							alertDialog.show();
						}
					});

				}
			}
		} else {
			showTag("出错！");
		}
	}

	Handler handlerList = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			cancelProgressDialog();
			switch (msg.what) {
			case Constants.NEED_TO_ACTIVATE:
				registDevices();
				break;
			case 0:
				if (null != msg.obj) {
					app.mDatas = (ArrayList<Parameter>) msg.obj;
					mPreferences.edit()
							.putString(CONTENT_MAKE, app.getmMake().getName())
							.commit();
					mPreferences
							.edit()
							.putString(CONTENT_MODEL,
									app.getmShadowModelYeal().mModel.getName())
							.commit();
					ModelYear year = app.getmShadowModelYeal().mModelYear;
					String yearStr = "";
					if (year instanceof ModelYearSingle) {
						yearStr += ((ModelYearSingle) year).getYear();
					} else {
						yearStr += ((ModelYearRange) year).getBeginYear();
						yearStr += "-";
						yearStr += ((ModelYearRange) year).getEndYear();
					}
					mPreferences.edit().putString(CONTENT_MODELYRER, yearStr)
							.commit();
					SelectSuccess.this.finish();
					// {// TODO
					// app.isSelectVehicleConfigSystemInfo = false;
					// Constants.isSelectVehicleConfigSystemInfo = false;
					// Utils.setAllParameters(null);
					// Utils.setDtcs(null);
					// Utils.setUsableParameters(null);
					// }
					showToast(R.string.select_set_vechile_success);
					startActivity(new Intent(SelectSuccess.this,
							DataTextActivity.class));
					SelectSuccess.this.finish();
				} else {
					// 匹配此汽车信息失败
					showMessageDialog(SelectSuccess.this, R.string.app_prompt,
							R.string.select_set_error, Constants.STYLE_NORMAL);
				}
				break;
			default:
				app.mDatas = null;
				if (null != msg.obj) {
					showMessageDialog(SelectSuccess.this, R.string.app_prompt,
							msg.obj.toString(), Constants.STYLE_NORMAL);
				}
				break;
			}
		};
	};

	Handler handlerVin = new Handler() {

		public void handleMessage(final Message msg) {
			isGetAVIN = true;
			cancelProgressDialog();
			switch (msg.what) {
			case Constants.NEED_TO_ACTIVATE:
				registDevices();
				break;
			case 0:
				GetVINInfoResponse getVINInfoResponse = (GetVINInfoResponse) msg.obj;
				switch (msg.arg1) {
				case 1:
					startActivityAutomatic(getVINInfoResponse);
					break;
				case 2:
					startActivtiyPartial(getVINInfoResponse);
					break;
				default:
					startActivityManual();
					break;

				}
				break;
			default:
				startActivityManual();
				// new Handler(Looper.getMainLooper()).post(new Runnable() {
				//
				// public void run() {
				// showMessageDialog(SelectSuccess.this, Utils
				// .getStringResourse(SelectSuccess.this,
				// R.string.app_prompt), msg.obj
				// .toString(), Constants.STYLE_NORMAL);
				// }
				// });

				break;
			}
		};
	};

	public void onBackPressed() {
		super.onBackPressed();
		app.mNeedBean = null;
		isGetAVIN = false;
	};

	public void onClick(View v) {
		if (CommonUtils.isFastDoubleClick()) {
			return;
		}
		switch (v.getId()) {
		case R.id.select_btn_confirm:
			if (null == app.getmMake() || null == app.getmShadowModelYeal()
					|| null == app.getmShadowModelYeal().mModel
					|| null == app.getmShadowModelYeal().mModelYear) {
				/** 如果有上述数据则弹出提示 */
				showMessageDialog(SelectSuccess.this, R.string.app_prompt,
						R.string.drivermanager_current_need_data,
						Constants.STYLE_NORMAL);
				break;
			}

			// getNeededParameter(app.mNeedBean.Categories,
			// app.mNeedBean.EcuFamily, handlerList);
			deepDiag = false;

			showPrograssDialog(SelectSuccess.this, Utils.getStringResourse(
					SelectSuccess.this, R.string.app_prompt),
					Utils.getStringResourse(SelectSuccess.this,
							R.string.app_waiting));
			if (null == app.mNeedBean.EcuFamily
					|| "".equals(app.mNeedBean.EcuFamily)) {// 深度诊断
				// new Thread(new Runnable() {
				// public void run() {
				// deepDiag = true;
				// getNeededParameter(app.mNeedBean.Categories,
				// handlerList);
				//
				// }
				// }).start();
				cancelProgressDialog();
				startActivity(new Intent(SelectSuccess.this,
						SelectEcuFamily.class));

			} else {// 燃油管理或者，维护保养
				new Thread(new Runnable() {
					public void run() {
						getNeededParameter(app.mNeedBean.Categories,
								app.mNeedBean.EcuFamily, handlerList);
					}
				}).start();
			}
			isGetAVIN = false;

			break;
		case R.id.select_btn_manual_selection:
			if (mActivity_state == ACTIVITY_STATE_AUTOMATIC) {
				startActivityManual();
			} else {
				showPrograssDialog(SelectSuccess.this, Utils.getStringResourse(
						this, R.string.app_prompt), Utils.getStringResourse(
						this, R.string.select_is_guessing));
				new Thread(new Runnable() {
					public void run() {
						getVIN(handlerVin);
					}
				}).start();
			}
			break;
		case R.id.select_tvmake:
			clickMake();
			break;
		case R.id.select_tvmodel:
			clickModel();
			break;
		default:
			break;
		}
	}
}
