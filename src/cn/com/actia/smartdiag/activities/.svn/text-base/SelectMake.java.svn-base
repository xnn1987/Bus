package cn.com.actia.smartdiag.activities;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import cn.com.actia.smartdiag.R;
import cn.com.actia.smartdiag.beans.ShadowModelYeal;
import cn.com.actia.smartdiag.constants.Constants;
import cn.com.actia.smartdiag.model.vin.data.Make;
import cn.com.actia.smartdiag.model.vin.data.Model;
import cn.com.actia.smartdiag.model.wapper.entity.vehicle.GetAllMakesResponse;
import cn.com.actia.smartdiag.tools.CommonUtils;
import cn.com.actia.smartdiag.tools.Utils;
import cn.com.actia.smartdiag.wrapper.VehicleManager;

public class SelectMake extends APPBaseActivity {
	public static final String ASIA = "ASIA";
	public static final String CHINA = "CHINA";
	public static final String US = "US";
	public static final String EUROPE = "EUROPE";

	String mLabel = "CHINA";

	private ViewPager mViewPager;
	private ArrayList<RadioButton> mRadioButtons;
	private RadioGroup mRadioGroup;

	// 这个是选择大洲或国家的group
	private RadioGroup mMenuGroup;

	Intent mIntent;

	private int mActivityState;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(app.Vehcle_Select_Activity_Name);
		setContentView(R.layout.selection_vehicle_make_layout);

		mIntent = getIntent();
		// 默认状态是不返回状态
		mActivityState = mIntent.getIntExtra(SelectSuccess.ACTIVITY_STATE,
				SelectSuccess.ACTIVITY_STATE_MANUAL);

		mViewPager = (ViewPager) findViewById(R.id.flipper_item_viewpage);
		mViewPager
				.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
					public void onPageSelected(int index) {
						if (mRadioButtons.size() > index)
							mRadioButtons.get(index).setChecked(true);
					}

					public void onPageScrolled(int arg0, float arg1, int arg2) {

					}

					public void onPageScrollStateChanged(int arg0) {

					}
				});
		mRadioGroup = (RadioGroup) findViewById(R.id.flipper_item_radiogroup);
		mMenuGroup = (RadioGroup) findViewById(R.id.select_menu_group);
		mMenuGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.select_menu_1:// 表示的是中国
					init(CHINA);
					break;
				case R.id.select_menu_2:// 亚洲
					init(ASIA);
					break;
				case R.id.select_menu_3:// 美国
					init(US);
					break;
				case R.id.select_menu_4:// 欧洲
					init(EUROPE);
					break;

				default:
					init(ASIA);
					break;
				}
			}
		});
		init(mLabel);
	}

	List<Make> allmakes;
	EditText editText;

	public void initSearch() {

		if (findViewById(R.id.select_menu_group).getVisibility() == View.VISIBLE) {
			findViewById(R.id.select_menu_group).setVisibility(View.GONE);
			findViewById(R.id.select_make_search).setVisibility(View.VISIBLE);
			editText = (EditText) findViewById(R.id.select_make_edit);
			editText.addTextChangedListener(new TextWatcher() {

				public void onTextChanged(CharSequence s, int start,
						int before, int count) {

				}

				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {

				}

				public void afterTextChanged(Editable s) {
					System.out.println("onEditorAction");
					filter(s.toString());
				}
			});
		} else {
			findViewById(R.id.select_menu_group).setVisibility(View.VISIBLE);
			findViewById(R.id.select_make_search).setVisibility(View.GONE);
		}
	}

	public void filter(String ed_str) {
		if (null == allmakes) {
			GetAllMakesResponse allMakesResponse = VehicleManager.getAllMakes();
			if (allMakesResponse.getCode() == VehicleManager.NO_ERROR) {
				allmakes = allMakesResponse.getMakeList();
			} else {
				showMessageDialog(SelectMake.this, R.string.app_prompt, "",
						Constants.STYLE_NORMAL);
				return;
			}
			filter(ed_str);
		} else {
			List<Make> showMakes = new ArrayList<Make>();
			for (Make make : allmakes) {
				if (make.getDisplayName().indexOf(ed_str) != -1) {
					showMakes.add(make);
				}
			}
			mViewPager.setAdapter(new SeleteMakeAdapter(showMakes));
		}
	}

	public void init(String label) {

		GetAllMakesResponse allMakesResponse = VehicleManager.getAllMakes();
		if (allMakesResponse.getCode() == VehicleManager.NO_ERROR) {
			List<Make> tempMakes = allMakesResponse.getMakeList();
			List<Make> makes = new ArrayList<Make>();
			for (Make make : tempMakes) {
				if (label == ASIA || label == EUROPE) {
					if (label.equals(make.getManufacturer()
							.getRelatedCountryCodeInfo().getContinent()
							.getName())) {
						makes.add(make);
					}
				} else {
					if (label
							.equals(make.getManufacturer()
									.getRelatedCountryCodeInfo().getCountry()
									.getName())) {
						makes.add(make);
					}
				}
			}
			showTag("size" + makes.size());
			this.allmakes = makes;
			mViewPager.setAdapter(new SeleteMakeAdapter(makes));
		} else {
			showMessageDialog(SelectMake.this, R.string.app_prompt, "",
					Constants.STYLE_NORMAL);
		}

	}

	public void reset() {

	}

	int isback = 0;

	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.search:
			onSearchRequested();
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.search_menu, menu);
		return true;
	}

	@Override
	public boolean onSearchRequested() {
		initSearch();
		return false;
	}

	@Override
	protected void onResume() {
		if (toFinishActivities.contains(this)) {
			toFinishActivities.remove(this);
		}
		super.onResume();
	}

	class SeleteMakeAdapter extends PagerAdapter {
		public static final int NUM_IN_ONE_PAGE = 12;

		/** 这个是所有的小的item对应的Arraylist */
		ArrayList<View> allViews;
		/** 这个是item对应的Arraylist */
		ArrayList<View> itemViews;

		/** 每一个view对应的bean */
		List<Make> makes;

		public SeleteMakeAdapter(List<Make> objs) {
			init(objs);
		}

		String names[] = new String[] { "东风雪铁龙", "东风标致", "别克", "帝豪", "比亚迪",
				"全球鹰" };

		public void init(List<Make> objs) {
			for (int i = names.length - 1; i >= 0; i--) {
				for (int j = 0; j < objs.size(); j++) {
					System.out.println(names[i] + "~~");
					System.out.println(objs.get(j).getDisplayName());
					if (names[i].equals(objs.get(j).getDisplayName())) {
						Make m = objs.remove(j);
						objs.add(0, m);
						break;
					}
				}
			}
			this.makes = objs;

			allViews = new ArrayList<View>();
			itemViews = new ArrayList<View>();
			mRadioGroup.removeAllViews();
			mRadioButtons = new ArrayList<RadioButton>();
			// 此处计算，所有的数据足够分成几个页面/如果能够整除，表示正好分为n页，否则，分n页，还有多余的一部分
			int size = objs.size() % NUM_IN_ONE_PAGE == 0 ? (objs.size() / NUM_IN_ONE_PAGE)
					: (objs.size() / NUM_IN_ONE_PAGE + 1);
			for (int i = 0; i < size; i++) {
				View group = getLayoutInflater().inflate(
						R.layout.select_small_item_group, null);
				for (int j = i * NUM_IN_ONE_PAGE; j < objs.size()
						&& j < (i + 1) * NUM_IN_ONE_PAGE; j++) {
					int containID = getResources().getIdentifier(
							"select_contain" + (j - i * NUM_IN_ONE_PAGE), "id",
							getPackageName());
					LinearLayout containView = (LinearLayout) group
							.findViewById(containID);
					View view = getLayoutInflater().inflate(
							R.layout.select_small_item, null);
					// TODO
					ImageView img = (ImageView) view
							.findViewById(R.id.select_item_img);
					if (!isMakeUsable(makes.get(j), app.mNeedBean)) {
						img.setAlpha(0x33);
						view.setTag(false);
					} else {
						img.setAlpha(0xff);
						view.setTag(true);
					}

					TextView tv = (TextView) view
							.findViewById(R.id.select_item_tv);
					img.setImageDrawable(Utils.getDrawable(makes.get(j)
							.getPicturePath()));
					tv.setText(makes.get(j).getDisplayName());
					containView.addView(view);
					final Integer index = j;
					view.setOnClickListener(new OnClickListener() {
						// TODO
						public void onClick(View v) {
							if ((Boolean) v.getTag()) {
								if (CommonUtils.isFastDoubleClick()) {
									return;
								}
								List<Model> models = VehicleManager
										.getModelsForMake(makes.get(index))
										.getModels();
								if (null != models && models.size() > 0) {
									app.setmMake(makes.get(index));

									if (mActivityState == SelectSuccess.ACTIVITY_STATE_AUTOMATIC) {// 如果是自动模式则需要直接返回数据
										/* 只有自动模式能够直接获取得到默认数据 */
										/*
										 * -----------×model数据默认使用第一个------------
										 * -
										 */
										ShadowModelYeal shadowModelYeal = new ShadowModelYeal();
										shadowModelYeal.mModel = models.get(0);
										shadowModelYeal.mModelYear = models
												.get(0).getModelYearList()
												.get(0);
										app.setmShadowModelYeal(shadowModelYeal);
										/* -----------end------------- */
										setResult(RESULT_OK);
										finish();
									} else {// 否则跳转到下一个界面

										Intent intent = new Intent(
												SelectMake.this,
												SelectModel.class);
										intent.putExtra(
												SelectSuccess.ACTIVITY_STATE,
												mActivityState);
										startActivity(intent);
										toFinishActivities.add(SelectMake.this);
									}

								} else {
									showToast(R.string.tips_no_message);
								}

							} else {
								showToast(R.string.selectmake_no);
							}
						}
					});
				}
				RadioButton rb = (RadioButton) getLayoutInflater().inflate(
						R.layout.radio_btn, null);
				int size1 = getResources().getDimensionPixelOffset(
						R.dimen.radio_size);
				RadioGroup.LayoutParams rbL = new RadioGroup.LayoutParams(
						size1, size1);

				final Integer index = i;
				rb.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						mViewPager.setCurrentItem(index);
					}
				});
				mRadioButtons.add(rb);
				mRadioGroup.addView(rb, rbL);
				itemViews.add(group);
				if (mRadioButtons.size() > 0) {
					mRadioButtons.get(0).setChecked(true);
				}

			}

		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			((ViewPager) container).addView(itemViews.get(position));
			return itemViews.get(position);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView(itemViews.get(position));
		}

		@Override
		public int getCount() {
			return itemViews.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object obj) {
			return view == obj;
		}

	}

}
