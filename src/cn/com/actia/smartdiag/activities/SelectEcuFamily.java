package cn.com.actia.smartdiag.activities;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import cn.com.actia.smartdiag.R;
import cn.com.actia.smartdiag.constants.Constants;
import cn.com.actia.smartdiag.model.selection.SystemInfoData;
import cn.com.actia.smartdiag.model.wapper.entity.vehicle.GetSystemInfoDataResponse;
import cn.com.actia.smartdiag.wrapper.VehicleManager;

/*
 * Author: poper Email:xut@bluemobi.cn
 * Created Date:2013-3-27
 * Copyright @ 2013 BU
 * Description: ������
 *
 * History:
 */
public class SelectEcuFamily extends APPBaseActivity {
	ListView listView;
	ArrayAdapter<String> adapter;
	List<String> tempecufamNames;
	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case Constants.NEED_TO_ACTIVATE:
				registDevices();
				break;
			case 0:
				listView.setAdapter(adapter);

				listView.setOnItemClickListener(new OnItemClickListener() {// ���ü����¼�
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						startActivity(new Intent(SelectEcuFamily.this,
								SelectEcu.class).putExtra("ecuFam",
								tempecufamNames.get(position)));
					}
				});
				break;
			case -1:
				// ��

				break;
			case -2:

			default:

				break;
			}
			cancelProgressDialog();
		};
	};

	Runnable runnable = new Runnable() {
		public void run() {
			if (null != app.getmShadowModelYeal().mModel
					&& null != app.getmShadowModelYeal().mModelYear) {// ���model�Ѿ�ѡ�񣬲���modelYear�Ѿ�ѡ��

				GetSystemInfoDataResponse lSystemResp = VehicleManager
						.getSystemInfoDataForModel(
								app.getmShadowModelYeal().mModel,
								app.getmShadowModelYeal().mModelYear);
				// TODO �����Ҫ���������
				// if (!(lSystemResp.getCode() == VehicleManager.NO_ERROR)) {//
				// ����д�����
				// mHandler.sendEmptyMessage(-2);
				// return;
				// }

				final List<SystemInfoData> alldatas = lSystemResp
						.getSystemInfoDataList();
				final List<String> tempecufamNamesDisplay = new ArrayList<String>();
				tempecufamNames = new ArrayList<String>();
				for (SystemInfoData d : alldatas) {
					if (!tempecufamNames.contains(d.getECUFamInfoData()
							.getName())) {// ȥ���ظ�ֵ
						tempecufamNames.add(d.getECUFamInfoData().getName());
						tempecufamNamesDisplay.add(d.getECUFamInfoData()
								.getDisplayName());
					}
				}
				if (tempecufamNamesDisplay.size() == 0) {
					mHandler.sendEmptyMessage(-1);// ��ecufamily
				} else {
					adapter = new ArrayAdapter<String>(SelectEcuFamily.this,
							android.R.layout.simple_list_item_1,
							tempecufamNamesDisplay);
					mHandler.sendEmptyMessage(0);
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_ecu_and_ecufamily);
		listView = (ListView) findViewById(R.id.listview);
		showPrograssDialog(this, getResources().getString(R.string.app_prompt),
				getResources().getString(R.string.fast_scan_is_scan_3));
		new Thread(runnable).start();
	}

}
