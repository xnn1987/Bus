package cn.com.actia.smartdiag.activities;

import java.util.ArrayList;

import android.os.Bundle;
import cn.com.actia.businesslogic.Parameter;
import cn.com.actia.smartdiag.R;
import cn.com.actia.smartdiag.tools.Utils;

public class StatusWater extends APPViewPageActivity {
	@Override
	public ArrayList<Parameter> getParameters() {
		if (null == Parameters) {
			Parameters = Utils.getUsableParamListForValue(Utils
					.getWaterTemperatureBundle());
		}
		return Parameters;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		showPrograssDialog(StatusWater.this,
				getResources().getString(R.string.app_prompt), getResources()
						.getString(R.string.app_scaning));
		new Thread(checkSuitable).start();
	}
}
