package cn.com.actia.smartdiag.activities;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cn.com.actia.smartdiag.R;

/**
 * @author Shaodw
 * @date:2012-11-27
 * @FileName:FastScanListActivity.java
 */
public class FastScanListActivity extends Activity {
	ListView mListView;
	FastScanAdapter mAdapter;
	ArrayList<Bundle> dtcs;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fast_scan_list_layout);
		dtcs = (ArrayList<Bundle>) getIntent().getSerializableExtra("data");
		mListView = (ListView) findViewById(R.id.fast_scan_list);
		mAdapter = new FastScanAdapter();
		mListView.setAdapter(mAdapter);
	}

	private class FastScanAdapter extends BaseAdapter {

		public int getCount() {
			return dtcs.size();
		}

		public Object getItem(int position) {
			return dtcs.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = FastScanListActivity.this
					.getLayoutInflater();
			View v = inflater
					.inflate(R.layout.fast_scan_list_item_layout, null);
			TextView code = (TextView) v.findViewById(R.id.fast_scan_code);
			TextView descrip = (TextView) v
					.findViewById(R.id.fast_scan_descrip_label);
			TextView state = (TextView) v
					.findViewById(R.id.fast_scan_dtc_state_label);
			ImageView stateImage = (ImageView) v
					.findViewById(R.id.fast_scan_dtc_state_label_img);

			code.setText(dtcs.get(position).getString("code"));
			descrip.setText(dtcs.get(position).getString("descrip"));
			state.setText(dtcs.get(position).getString("state"));
			stateImage.setImageResource(dtcs.get(position).getInt("imgID"));
			convertView = v;
			return convertView;
		}
	}
}
