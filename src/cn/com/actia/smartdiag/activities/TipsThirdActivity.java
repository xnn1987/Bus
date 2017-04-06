package cn.com.actia.smartdiag.activities;

import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import cn.com.actia.smartdiag.R;
import cn.com.actia.smartdiag.beans.TipsSecondBean;

public class TipsThirdActivity extends Activity {
	Map<String, String> data;
	TipsSecondBean mTipsSecondBean;

	TextView title;
	TextView date;
	TextView allDatas;

	String activityTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getIntent().hasExtra(TipsSecondActivity.INTENT_PATH)) {
			mTipsSecondBean = (TipsSecondBean) getIntent()
					.getSerializableExtra(TipsSecondActivity.INTENT_PATH);
			activityTitle = getIntent()
					.getStringExtra(TipsSecondActivity.TITLE);
		}
		setTitle(activityTitle);
		setContentView(R.layout.tips_third_layout);
		title = (TextView) findViewById(R.id.tips_third_title);
		date = (TextView) findViewById(R.id.tips_third_date);
		allDatas = (TextView) findViewById(R.id.tips_third_value);

		title.setText(mTipsSecondBean.title);
		date.setText(mTipsSecondBean.date);
		allDatas.setText(mTipsSecondBean.bodyContent);

	}
}
