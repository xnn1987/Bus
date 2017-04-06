package cn.com.actia.smartdiag.activities;

import java.util.ArrayList;

import cn.com.actia.businesslogic.Parameter;
import cn.com.actia.smartdiag.beans.NeedBean;
import cn.com.actia.smartdiag.beans.ShadowModelYeal;
import cn.com.actia.smartdiag.constants.Constants;
import cn.com.actia.smartdiag.model.vin.data.Make;
import cn.com.actia.smartdiag.tools.Utils;
import android.app.Application;

public class MyApplication extends Application {
	public ArrayList<Parameter> mDatas;

	public NeedBean mNeedBean;

	private Make mMake;
	private ShadowModelYeal mShadowModelYeal;

	private String pVehicleName;
	private String pECUFamily;
	private String pECUName;

	public String Vehcle_Select_Activity_Name;

	public String getpVehicleName() {
		return pVehicleName;
	}

	/**
	 * 每次当车辆信息改变的时候将需要重新设置configSystemInfo();
	 * 所以将isSelectVehicleConfigSystemInfo赋值为false
	 */
	public void setpVehicleName(String pVehicleName) {
		this.pVehicleName = pVehicleName;
		isSelectVehicleConfigSystemInfo = false;
		Constants.isSelectVehicleConfigSystemInfo = false;
		Utils.setAllParameters(null);
		Utils.setDtcs(null);
		Utils.setUsableParameters(null);
	}

	public String getpECUFamily() {
		return pECUFamily;
	}

	public void setpECUFamily(String pECUFamily) {
		isSelectVehicleConfigSystemInfo = false;
		Constants.isSelectVehicleConfigSystemInfo = false;
		Utils.setAllParameters(null);
		Utils.setDtcs(null);
		Utils.setUsableParameters(null);
		this.pECUFamily = pECUFamily;
	}

	public String getpECUName() {
		return pECUName;
	}

	public void setpECUName(String pECUName) {
		isSelectVehicleConfigSystemInfo = false;
		Constants.isSelectVehicleConfigSystemInfo = false;
		Utils.setAllParameters(null);
		Utils.setDtcs(null);
		Utils.setUsableParameters(null);
		this.pECUName = pECUName;
	}

	public Make getmMake() {
		return mMake;
	}

	public void setmMake(Make mMake) {
		isSelectVehicleConfigSystemInfo = false;
		Constants.isSelectVehicleConfigSystemInfo = false;
		Utils.setAllParameters(null);
		Utils.setDtcs(null);
		Utils.setUsableParameters(null);
		this.mMake = mMake;
	}

	public ShadowModelYeal getmShadowModelYeal() {
		return mShadowModelYeal;
	}

	public void setmShadowModelYeal(ShadowModelYeal mShadowModelYeal) {
		isSelectVehicleConfigSystemInfo = false;
		Constants.isSelectVehicleConfigSystemInfo = false;
		Utils.setAllParameters(null);
		Utils.setDtcs(null);
		Utils.setUsableParameters(null);
		this.mShadowModelYeal = mShadowModelYeal;
	}

	public boolean isSelectVehicleConfigSystemInfo = false;

	// String pVehicleName = "OBD_VEHICLE";
	// String pECUFamily = "OBD_FAM";
	// String pECUName = "OBD_ECU";

}
