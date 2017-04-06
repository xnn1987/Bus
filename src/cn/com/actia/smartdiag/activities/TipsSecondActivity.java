package cn.com.actia.smartdiag.activities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import cn.com.actia.smartdiag.R;
import cn.com.actia.smartdiag.beans.TipsSecondBean;
import cn.com.actia.smartdiag.constants.Constants;
import cn.com.actia.smartdiag.tools.TipsUtils;
import cn.com.actia.smartdiag.tools.Utils;

public class TipsSecondActivity extends APPBaseActivity implements
		OnItemClickListener {
	ListView mListView;
	public static final String TITLE = "title";
	public static final String DATE = "date";
	public static final String PREVIEW = "preview";
	public static final String INTENT_PATH = "intent_path";
	public static final String INTENT_TITLE = "intent_title";
	ArrayList<TipsSecondBean> tipsSecondBeans;

	String mPath;
	
	String title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tips_second_layout);
		if (getIntent().hasExtra(INTENT_PATH)) {
			mPath = getIntent().getStringExtra(INTENT_PATH);
		} else {
			mPath = null;
		}
		  title = Utils
				.getStringResourse(this, R.string.tips_title_second);
		if (getIntent().hasExtra(INTENT_TITLE)) {
			String name = getIntent().getStringExtra(INTENT_TITLE);
			title = title + ">" + name;
		}
		setTitle(title);
		String[] sonStrs = null;
		try {
			sonStrs = getResources().getAssets().list(mPath);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		if (sonStrs.length == 0) {
			showMessageDialog(TipsSecondActivity.this, R.string.app_prompt,
					R.string.tips_no_message, Constants.STYLE_EXIT_ACTIVITY);
		} else {
			tipsSecondBeans = new ArrayList<TipsSecondBean>();
			for (String str : sonStrs) {
				TipsSecondBean bean = null;
				String sonPath = mPath + "/" + str;
				System.out.println(sonPath);
				try {
					bean = TipsUtils.initCategoriesSecond(getResources()
							.getAssets().open(sonPath));
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
				tipsSecondBeans.add(bean);
			}

			mListView = (ListView) findViewById(R.id.tips_second_listview);

			ArrayList<Map<String, String>> mDatas;
			mDatas = new ArrayList<Map<String, String>>();
			for (TipsSecondBean b : tipsSecondBeans) {
				Map<String, String> maps = new HashMap<String, String>();
				maps.put(TITLE, b.title);
				maps.put(DATE, b.date);
				maps.put(PREVIEW, b.simpleBodycontent);
				mDatas.add(maps);
			}

			/*
			 * ArrayList<Ma> Ratchet or open end wrench - - Oil catch/recycle
			 * container - Funnel - New oil filter - New Oil - Clean rag
			 */

			SimpleAdapter adapter = new SimpleAdapter(this, mDatas,
					R.layout.tips_second_item_layout, new String[] { TITLE,
							DATE, PREVIEW }, new int[] {
							R.id.tips_second_item_tv_title,
							R.id.tips_second_item_tv_data,
							R.id.tips_second_item_tv_preview });
			mListView.setAdapter(adapter);
			mListView.setOnItemClickListener(this);
		}
	}

	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent(TipsSecondActivity.this,
				TipsThirdActivity.class);
		intent.putExtra(INTENT_PATH, tipsSecondBeans.get(position));
		intent.putExtra(TITLE, title);
		startActivity(intent);
	}
}
