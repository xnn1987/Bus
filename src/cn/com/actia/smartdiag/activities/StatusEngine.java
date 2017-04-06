package cn.com.actia.smartdiag.activities;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.os.Bundle;
import cn.com.actia.businesslogic.Parameter;
import cn.com.actia.smartdiag.R;
import cn.com.actia.smartdiag.tools.Utils;

@SuppressLint("HandlerLeak")
public class StatusEngine extends APPViewPageActivity {

	public ArrayList<Parameter> getParameters() {
		if (Parameters == null) {
			Parameters = Utils.getUsableParamListForValue(Utils
					.getEngineBundle());
		}
		return Parameters;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		showPrograssDialog(StatusEngine.this,
				getResources().getString(R.string.app_prompt), getResources()
						.getString(R.string.app_scaning));
		new Thread(checkSuitable).start();
	}

}
