package cn.com.actia.smartdiag.activities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
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
import android.widget.TextView;
import cn.com.actia.smartdiag.R;
import cn.com.actia.smartdiag.beans.ShadowModelYeal;
import cn.com.actia.smartdiag.constants.Constants;
import cn.com.actia.smartdiag.model.vin.data.Model;
import cn.com.actia.smartdiag.model.vin.data.ModelYear;
import cn.com.actia.smartdiag.model.vin.data.ModelYearRange;
import cn.com.actia.smartdiag.model.vin.data.ModelYearSingle;
import cn.com.actia.smartdiag.model.wapper.entity.vehicle.GetModelsForMakeResponse;
import cn.com.actia.smartdiag.tools.CommonUtils;
import cn.com.actia.smartdiag.tools.Utils;
import cn.com.actia.smartdiag.wrapper.VehicleManager;

public class SelectModel extends APPBaseActivity {
	private ViewPager mViewPager;
	private ArrayList<RadioButton> mRadioButtons;
	private RadioGroup mRadioGroup;

	Intent mIntent;
	private int mActivityState;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.selection_vehicle_model_layout);
		setTitle(app.Vehcle_Select_Activity_Name);
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
		init();
	}

	int isback = 0;

	EditText et_Str;
	EditText et_year;
	View searchGroup;

	public void initSearch() {
		if (findViewById(R.id.select_model_search_viewgroup).getVisibility() == View.GONE) {
			findViewById(R.id.select_model_search_viewgroup).setVisibility(
					View.VISIBLE);
			init();
		} else {
			findViewById(R.id.select_model_search_viewgroup).setVisibility(
					View.GONE);
			init();
			return;
		}

		searchGroup = findViewById(R.id.select_model_search_viewgroup);
		et_Str = (EditText) findViewById(R.id.select_model_search_et_str);
		et_year = (EditText) findViewById(R.id.select_model_search_et_year);

		TextWatcher watcher1 = new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			public void afterTextChanged(Editable s) {
				String strYear = et_year.getText().toString().trim();
				String str_str = et_Str.getText().toString().trim();
				int year = 0;
				if (null != strYear && strYear.length() != 0) {
					year = Integer.parseInt(strYear);
				} else {
				}
				mViewPager.setAdapter(new SeleteModelAdapter(filter(str_str,
						year)));
			}
		};

		TextWatcher watcher2 = new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			public void afterTextChanged(Editable s) {
				String strYear = et_year.getText().toString().trim();
				String str_str = et_Str.getText().toString().trim();
				int year = 0;
				if (null != strYear && strYear.length() != 0) {
					year = Integer.parseInt(strYear);
				} else {
				}
				mViewPager.setAdapter(new SeleteModelAdapter(filter(str_str,
						year)));
			}
		};
		et_Str.addTextChangedListener(watcher1);
		et_year.addTextChangedListener(watcher2);
		OnClickListener ll = new OnClickListener() {
			public void onClick(View v) {
				String strYear = et_year.getText().toString().trim();
				String str_str = et_Str.getText().toString().trim();
				int year = 0;
				if (null != strYear && strYear.length() != 0) {
					year = Integer.parseInt(strYear);
				} else {
					year = Calendar.getInstance(Locale.getDefault()).get(
							Calendar.YEAR);
				}

				switch (v.getId()) {
				case R.id.select_model_add:// 加号
					year += 1;
					et_year.setText(year + "");
					break;
				case R.id.select_model_min:// 减号
					year -= 1;
					et_year.setText(year + "");
					break;

				default:
					// 开始按钮
					mViewPager.setAdapter(new SeleteModelAdapter(filter(
							str_str, year)));
					break;
				}
			}
		};
		findViewById(R.id.select_model_add).setOnClickListener(ll);
		findViewById(R.id.select_model_min).setOnClickListener(ll);
		// findViewById(R.id.select_model_search_start).setOnClickListener(ll);
	}

	@Override
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

	private List<ShadowModelYeal> filterModels;

	public List<ShadowModelYeal> filter(String edText, int year) {
		boolean isStringNeed = false;
		boolean isYearNeed = false;
		if (null != edText && edText.length() > 0) {
			isStringNeed = true;
		}
		if (0 != year) {
			isYearNeed = true;
		}
		List<Model> nowModels = new ArrayList<Model>();
		if (null == filterModels) {// 获取所有的Model
			GetModelsForMakeResponse getModelsForMakeResponse = VehicleManager
					.getModelsForMake(app.getmMake());
			if (getModelsForMakeResponse.getCode() == VehicleManager.NO_ERROR) {
				nowModels = getModelsForMakeResponse.getModels();
			}
			filterModels = new ArrayList<ShadowModelYeal>();
			for (Model d : nowModels) {
				for (ModelYear mY : d.getModelYearList()) {
					ShadowModelYeal smy = new ShadowModelYeal();
					smy.mModel = d;
					smy.mModelYear = mY;
					filterModels.add(smy);
				}
			}
		}

		if (!(isStringNeed || isYearNeed)) {// 如果都没有，则什么都不用处理
			return filterModels;
		}

		List<ShadowModelYeal> templist = new ArrayList<ShadowModelYeal>(
				filterModels);

		List<ShadowModelYeal> needlist = new ArrayList<ShadowModelYeal>();
		if (isStringNeed) {
			for (ShadowModelYeal shadowMY : templist) {
				if (-1 != shadowMY.mModel.getDisplayName().indexOf(edText)) {
					needlist.add(shadowMY);
				}
			}
		} else {
			needlist = new ArrayList<ShadowModelYeal>(templist);
		}

		if (isYearNeed) {
			templist = new ArrayList<ShadowModelYeal>(needlist);
			needlist = new ArrayList<ShadowModelYeal>();
			for (ShadowModelYeal shadowMY : templist) {
				ModelYear m = shadowMY.mModelYear;
				if (m instanceof ModelYearSingle) {
					ModelYearSingle s = (ModelYearSingle) m;
					if (s.getYear() == year) {
						needlist.add(shadowMY);
					}
				} else {
					ModelYearRange r = (ModelYearRange) m;
					if (r.getBeginYear() <= year && r.getEndYear() >= year) {
						needlist.add(shadowMY);
					}
				}
			}
		}
		return needlist;
	}

	public void init() {
		GetModelsForMakeResponse getModelsForMakeResponse = VehicleManager
				.getModelsForMake(app.getmMake());
		if (getModelsForMakeResponse.getCode() == VehicleManager.NO_ERROR) {
			List<ShadowModelYeal> shadowModelYeals = new ArrayList<ShadowModelYeal>();
			List<Model> models = null;
			if (mActivityState == SelectSuccess.ACTIVITY_STATE_SEMI_AUTOMATIC) {
				models = new ArrayList<Model>();
				if (null != app.getmShadowModelYeal().mModel) {// 如果已经有了Model的数据则直接使用，否则需要使用所有的数据
					models.add(app.getmShadowModelYeal().mModel);
				} else {// 如果没有则使用所有的数据
					models = getModelsForMakeResponse.getModels();
				}
			} else {//
				models = getModelsForMakeResponse.getModels();
			}
			for (Model m : models) {
				for (ModelYear y : m.getModelYearList()) {
					if (mActivityState == SelectSuccess.ACTIVITY_STATE_SEMI_AUTOMATIC
							&& app.getmShadowModelYeal().mYear != 0) {// 如果是半自动并且此处的mYear存在
						if (y instanceof ModelYearSingle) {
							if (app.getmShadowModelYeal().mYear == ((ModelYearSingle) y)
									.getYear()) {
								ShadowModelYeal smy = new ShadowModelYeal();
								smy.mModel = m;
								smy.mModelYear = y;
								shadowModelYeals.add(smy);
							}
						} else {
							int begin = ((ModelYearRange) y).getBeginYear();
							int end = ((ModelYearRange) y).getEndYear();
							if (app.getmShadowModelYeal().mYear <= end
									&& app.getmShadowModelYeal().mYear >= begin) {
								ShadowModelYeal smy = new ShadowModelYeal();
								smy.mModel = m;
								smy.mModelYear = y;
								shadowModelYeals.add(smy);
							}
						}
					} else {
						ShadowModelYeal smy = new ShadowModelYeal();
						smy.mModel = m;
						smy.mModelYear = y;
						shadowModelYeals.add(smy);
					}
				}
			}
			if (shadowModelYeals.size() != 0) {
				mViewPager.setAdapter(new SeleteModelAdapter(shadowModelYeals));
			} else {// 如果大小为零,則表表示沒有所判斷出來的年份的MODEL信息，需要重新计算
				shadowModelYeals = new ArrayList<ShadowModelYeal>();
				for (Model m : models) {
					for (ModelYear y : m.getModelYearList()) {

						ShadowModelYeal smy = new ShadowModelYeal();
						smy.mModel = m;
						smy.mModelYear = y;
						shadowModelYeals.add(smy);
					}
				}
				mViewPager.setAdapter(new SeleteModelAdapter(shadowModelYeals));
			}

		} else {
			showMessageDialog(SelectModel.this, R.string.app_prompt,
					R.string.tips_no_message, Constants.STYLE_EXIT_ACTIVITY);
		}

	}

	public void reset() {

	}

	class SeleteModelAdapter extends PagerAdapter {
		public static final int NUM_IN_ONE_PAGE = 9;

		/** 这个是所有的小的item对应的Arraylist */
		ArrayList<View> allViews;
		/** 这个是item对应的Arraylist */
		ArrayList<View> itemViews;

		/** 每一个view对应的bean */
		List<ShadowModelYeal> models;

		public SeleteModelAdapter(List<ShadowModelYeal> shadowModelYeals) {
			init(shadowModelYeals);
		}

		public void init(List<ShadowModelYeal> objs) {
			this.models = objs;

			allViews = new ArrayList<View>();
			itemViews = new ArrayList<View>();
			mRadioButtons = new ArrayList<RadioButton>();
			mRadioGroup.removeAllViews();

			// 此处计算，所有的数据足够分成几个页面/如果能够整除，表示正好分为n页，否则，分n页，还有多余的一部分
			int size = objs.size() % NUM_IN_ONE_PAGE == 0 ? (objs.size() / NUM_IN_ONE_PAGE)
					: (objs.size() / NUM_IN_ONE_PAGE + 1);
			for (int i = 0; i < size; i++) {
				View group = getLayoutInflater().inflate(
						R.layout.select_small_item_group_model, null);
				for (int j = i * NUM_IN_ONE_PAGE; j < objs.size()
						&& j < (i + 1) * NUM_IN_ONE_PAGE; j++) {
					int containID = getResources().getIdentifier(
							"select_contain" + (j - i * NUM_IN_ONE_PAGE), "id",
							getPackageName());
					LinearLayout containView = (LinearLayout) group
							.findViewById(containID);

					View view = getLayoutInflater().inflate(
							R.layout.select_small_item, null);
					ImageView img = (ImageView) view
							.findViewById(R.id.select_item_img);
					img.setImageDrawable(Utils.getDrawable(models.get(j).mModelYear
							.getRelativePathPicture()));

					if (!isShadowModelYearUsabel(models.get(j), app.mNeedBean)) {
						img.setAlpha(0x33);
						view.setTag(false);
					} else {
						img.setAlpha(0xff);
						view.setTag(true);
					}
					TextView tv = (TextView) view
							.findViewById(R.id.select_item_tv);
					TextView tv1 = (TextView) view
							.findViewById(R.id.select_item_tv1);
					tv1.setVisibility(View.VISIBLE);
					tv.setText(models.get(j).mModel.getDisplayName());
					StringBuffer showSb = new StringBuffer();
					ModelYear y = models.get(j).mModelYear;
					if (y instanceof ModelYearSingle) {
						showSb.append(((ModelYearSingle) y).getYear());
					} else {
						showSb.append(((ModelYearRange) y).getBeginYear())
								.append("~")
								.append(((ModelYearRange) y).getEndYear());
					}
					tv1.setText(showSb.toString());
					containView.addView(view);
					final Integer index = j;
					view.setOnClickListener(new OnClickListener() {

						public void onClick(View v) {
							if ((Boolean) v.getTag()) {
								if (CommonUtils.isFastDoubleClick()) {
									return;
								}
								app.setmShadowModelYeal(models.get(index));
								if (mActivityState == SelectSuccess.ACTIVITY_STATE_AUTOMATIC) {
									setResult(RESULT_OK);
									finish();
								} else {
									for (Activity activity : toFinishActivities) {
										activity.finish();
									}
									Intent intent = new Intent(
											SelectModel.this,
											SelectSuccess.class);
									intent.putExtra(
											SelectSuccess.ACTIVITY_STATE,
											SelectSuccess.ACTIVITY_STATE_AUTOMATIC);
									startActivity(intent);
									finish();
								}
							} else {
								showToast(R.string.selectmodel_no);
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
