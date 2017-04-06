package cn.com.actia.smartdiag.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import cn.com.actia.smartdiag.R;
import cn.com.actia.smartdiag.constants.Constants;
import cn.com.actia.smartdiag.tools.Utils;

public class LoadingActivity extends Activity {
	public int nextActiviyFlag = 0;
	public static String bluetoothAdress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loading_layout);
		bluetoothAdress = getSharedPreferences(
				Constants.SHAREPREFERECE_TAG_NAME, Context.MODE_PRIVATE)
				.getString(Constants.BLUETOOTH_ADRESS, "");
		if (bluetoothAdress.equals("")) {
			nextActiviyFlag = 1;
		} else {
			nextActiviyFlag = 0;
			// Utils.checksuitable(bluetoothAdress);
		}
		// 获取语言信息
		Utils.setLocaleLanguage();
		// 打开蓝牙
		Utils.openBluetooth();
		Utils.initAllTools(this);
		Utils.getXMLData(this);
		new Handler().postDelayed(new Runnable() {
			public void run() {
				// TODO 移动这句话可以选择是否显示主页
				LoadingActivity.this.startActivity(new Intent(
						LoadingActivity.this, MainActivity.class));
				// if (nextActiviyFlag == 0) {
				//
				// } else {
				// LoadingActivity.this.startActivity(new Intent(
				// LoadingActivity.this, VCISettingActivity.class));
				// }
				LoadingActivity.this.finish();
			}
		}, 3000);
	}

	// 屏蔽返回按鈕
	@Override
	public void onBackPressed() {
	}
}
