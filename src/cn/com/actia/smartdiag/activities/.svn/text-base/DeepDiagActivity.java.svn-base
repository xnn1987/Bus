package cn.com.actia.smartdiag.activities;

import android.os.Bundle;
import cn.com.actia.smartdiag.widget.DeepDiagView;

public class DeepDiagActivity extends APPBaseActivity {
	DeepDiagView deepDiagView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		deepDiagView = new DeepDiagView(this);
		deepDiagView.isSurvial = true;
		setContentView(deepDiagView);
	}

	@Override
	protected void onDestroy() {
		deepDiagView.isSurvial = false;
		super.onDestroy();
	}
}
