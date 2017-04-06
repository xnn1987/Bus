package cn.com.actia.smartdiag.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import cn.com.actia.smartdiag.R;
import cn.com.actia.smartdiag.beans.Categorie;
import cn.com.actia.smartdiag.constants.Constants;
import cn.com.actia.smartdiag.tools.TipsUtils;

@SuppressLint("DefaultLocale")
public class TipsFirstActivity extends APPBaseActivity implements
		OnItemClickListener {
	ListView listView;
	ArrayList<Categorie> categories;
	ArrayList<Map<String, String>> datas;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tips_first_layout);
		listView = (ListView) findViewById(R.id.tips_first_listview);

		try {
			categories = TipsUtils.initCategoriesMain(getResources()
					.getAssets().open("Tips/Categories.xml",
							AssetManager.ACCESS_STREAMING));
		} catch (Exception e) {
			e.printStackTrace();
			showDialog(Constants.STYLE_EXIT_ACTIVITY_STARTSETTING);
		}

		 datas = new ArrayList<Map<String, String>>();
		for (Categorie c : categories) {
			Map<String, String> maps = new HashMap<String, String>();
			String key = String.format("%s-%s", Constants.Language,
					Constants.Country);
			if (!c.displayNames.displayName.containsKey(key)) {
				key = c.displayNames.defaultLocale;
			}
			maps.put("value", c.displayNames.displayName.get(key));
			datas.add(maps);
		}

		/*
		 * ArrayList<Ma> Ratchet or open end wrench - - Oil catch/recycle
		 * container - Funnel - New oil filter - New Oil - Clean rag
		 */

		SimpleAdapter adapter = new SimpleAdapter(this, datas,
				R.layout.tips_first_item_layout, new String[] { "value" },
				new int[] { R.id.clock_first_item_tv });

		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
	}

	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		String language;
		Categorie categorie = categories.get(position);
		// 如果包含手机语言对应的情况,则直接取对应的语言
		if (categorie.displayNames.displayName.containsKey(String.format(
				"%s-%s", Constants.Language, Constants.Country))) {
			language = Constants.Language;
		} else {
			// 如果不是手机语言信息对应的信息，则取默认的String 的后半部分
			language = categorie.displayNames.defaultLocale.split("-")[0];
		}
		String path = null;
		if (categorie.relative) {
			path = "Tips" + categorie.folderPath;
		} else {
			path = categorie.folderPath;
		}
		path += "/" + language.toUpperCase(new Locale("en"));

		Intent intent = new Intent(TipsFirstActivity.this,
				TipsSecondActivity.class);
		intent.putExtra(TipsSecondActivity.INTENT_PATH, path);
		intent.putExtra(TipsSecondActivity.INTENT_TITLE, datas.get(position).get("value"));
		startActivity(intent);
	}
}
