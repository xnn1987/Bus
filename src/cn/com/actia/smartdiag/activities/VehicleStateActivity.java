package cn.com.actia.smartdiag.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import cn.com.actia.smartdiag.R;
import cn.com.actia.smartdiag.constants.FlashConstants;

/**
 * 
 * @author XtShadow ³µÁ¾×´Ì¬
 */
public class VehicleStateActivity extends APPBaseActivity implements
		OnClickListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vehicle_state);
		initListeners();
	}

	private void initListeners() {
		findViewById(R.id.vehicle_status_iv1).setOnClickListener(this);
		findViewById(R.id.vehicle_status_iv2).setOnClickListener(this);
		findViewById(R.id.vehicle_status_iv3).setOnClickListener(this);
		findViewById(R.id.vehicle_status_iv4).setOnClickListener(this);
		findViewById(R.id.vehicle_status_iv5).setOnClickListener(this);
	}

	public void onClick(View v) {
		Intent intent = new Intent(VehicleStateActivity.this,
				DataTextActivity.class);
		switch (v.getId()) {
		case R.id.vehicle_status_iv1:
			intent.putExtra(FlashConstants.VEHICLESTATE,
					FlashConstants.STATUE_ENGINE);
			break;
		case R.id.vehicle_status_iv2:
			intent.putExtra(FlashConstants.VEHICLESTATE,
					FlashConstants.STATUE_OIL);
			break;
		case R.id.vehicle_status_iv3:
			intent.putExtra(FlashConstants.VEHICLESTATE,
					FlashConstants.STATUE_STOP);
			break;
		case R.id.vehicle_status_iv4:
			intent.putExtra(FlashConstants.VEHICLESTATE,
					FlashConstants.STATUE_WATER);
			break;
		case R.id.vehicle_status_iv5:
			intent.putExtra(FlashConstants.VEHICLESTATE,
					FlashConstants.STATUE_LIGHT);
			break;
		}
		showTag(((Button) v).getText().toString());
		startActivity(intent);
	}
}
