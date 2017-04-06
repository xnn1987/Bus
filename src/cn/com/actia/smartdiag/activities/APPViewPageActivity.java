package cn.com.actia.smartdiag.activities;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import cn.com.actia.businesslogic.NumericParameter;
import cn.com.actia.businesslogic.Parameter;
import cn.com.actia.smartdiag.R;
import cn.com.actia.smartdiag.constants.Constants;
import cn.com.actia.smartdiag.model.wapper.entity.GetParameterResponse;
import cn.com.actia.smartdiag.tools.GetVecicleStatuesParamsTools;
import cn.com.actia.smartdiag.tools.Utils;
import cn.com.actia.smartdiag.wrapper.DiagBusinessLogicWrapper;

@SuppressLint("HandlerLeak")
public abstract class APPViewPageActivity extends APPBaseActivity {
	public static final int MSG_CHECKSUITABLE = 0;
	public static final int MSG_STATECHANGE = 1;

	public static int form_status = 0;

	ViewPager mViewPager;
	RadioGroup mRadioGroup;
	ArrayList<RadioButton> mRadioButtons;
	ViewPagerAdapter mViewPagerAdapter;
	ArrayList<Parameter> Parameters;

	boolean isSurival = true;
	long flashTime = 1000;

	private ArrayList<Parameter> mItemBeans;

	Handler mHandler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			cancelProgressDialog();
			switch (msg.what) {
			case MSG_CHECKSUITABLE:
				Message result = (Message) msg.obj;

				if (result.what == 0) {
					if (null == result.obj) {
						showToast(R.string.app_state_success);
					} else {
						showToast(result.obj.toString());
					}
					new Thread() {
						public void run() {
							while (isSurival) {
								try {
									sleep(flashTime);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}

								if (getParameters().size() != 0) {
									GetParameterResponse getParamsResp = DiagBusinessLogicWrapper
											.getParameter(getParameters());
									showTag("getParameters().size():"
											+ getParameters().size());
									if (getParamsResp.getCode() == 0) {
										ArrayList<Parameter> parameters = getParamsResp
												.getParameters();
										showTag("parameters.size():"
												+ parameters.size());
										Message msg = new Message();
										msg.what = MSG_STATECHANGE;
										msg.obj = GetVecicleStatuesParamsTools
												.getComparedParameter(
														getParameters().size(),
														parameters.size(),
														parameters);
										mHandler.sendMessage(msg);
									}
								} else {
									new Handler(Looper.getMainLooper())
											.post(new Runnable() {
												public void run() {
													showToast(R.string.tips_no_message);
												}
											});
									APPViewPageActivity.this.finish();
								}
							}
						};
					}.start();
				} else {
					showTag("error code:" + result.what);
					showMessageDialog(APPViewPageActivity.this,
							R.string.app_prompt, result.obj.toString(),
							Constants.STYLE_EXIT_ACTIVITY_STARTSETTING);
				}

				break;
			case MSG_STATECHANGE:
				ArrayList<Parameter> parameters = (ArrayList<Parameter>) msg.obj;
				if (form_status == 0) {
					setmItemBeansValues(parameters);
					mViewPagerAdapter.notifyDataSetChanged();
				}
				if (form_status == 1) {
					Message paramsessage = new Message();
					paramsessage.obj = parameters;
					paramsessage.arg1 = 0;
					paramsessage.arg2 = parameters.size() - 1;
					paramsessage.what = 0;
					// DataClockActivity.mHandler.sendMessage(paramsessage);
					break;
				}
				if (form_status == 2) {
					Message paramsessage = new Message();
					paramsessage.obj = parameters;
					/* arg1开始位置 */
					paramsessage.arg1 = 0;
					/* arg2结束位置 */
					paramsessage.arg2 = parameters.size() - 1;
					paramsessage.what = 0;
					// DataHistogramActivity.mHandler.sendMessage(paramsessage);
					break;
				}
				break;
			default:
				break;
			}
		};
	};

	public Runnable checkSuitable = new Runnable() {
		public void run() {
			Message result = Utils.checkSuitable(APPViewPageActivity.this,
					bluetoothPreference);
			Message msg = new Message();
			msg.what = 0;
			msg.obj = result;
			mHandler.sendMessage(msg);
		}
	};
	private SharedPreferences bluetoothPreference;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		bluetoothPreference = getSharedPreferences(
				Constants.SHAREPREFERECE_TAG_NAME, Context.MODE_PRIVATE);
		setContentView(R.layout.flipper_item_layout);
		initWidget();

	}

	public abstract ArrayList<Parameter> getParameters();

	public void initWidget() {
		mViewPager = (ViewPager) findViewById(R.id.flipper_item_viewpage);
		mRadioGroup = (RadioGroup) findViewById(R.id.flipper_item_radiogroup);
		mViewPager
				.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
					public void onPageSelected(int index) {
						mRadioButtons.get(index).setChecked(true);
					}

					public void onPageScrolled(int arg0, float arg1, int arg2) {

					}

					public void onPageScrollStateChanged(int arg0) {

					}
				});

	}

	public void setmItemBeansValues(ArrayList<Parameter> items) {
		if (null == mItemBeans) {
			setmItemBeans(items);
			return;
		}
		for (int i = 0; i < items.size(); i++) {
			mItemBeans.get(i).setParameterValue(
					items.get(i).getParameterValue());
		}
	}

	public void setmItemBeans(ArrayList<Parameter> items) {
		if (null == items)
			return;
		this.mItemBeans = items;
		mViewPagerAdapter = new ViewPagerAdapter(this);
		mViewPager.setAdapter(mViewPagerAdapter);
	}

	OnClickListener itemClick = new OnClickListener() {

		public void onClick(View v) {
			Message msg = new Message();
			msg.what = -1;
			msg.obj = mItemBeans;
			// DataClockActivity.mHandler.sendMessage(msg);
			// DataClockActivity.mHandler.sendMessage(msg);
			startActivity(new Intent(APPViewPageActivity.this,
					DataClockActivity.class));
		}
	};

	public class ViewPagerAdapter extends PagerAdapter {
		ArrayList<View> mViews;
		LayoutInflater mInflater;
		Resources mResource;
		int size;

		public ViewPagerAdapter(Context context) {
			mViews = new ArrayList<View>();
			mInflater = LayoutInflater.from(context);
			mRadioButtons = new ArrayList<RadioButton>();
			mResource = context.getResources();
			// 计数器
			if (mItemBeans.size() % Constants.PAGE_OF_ITEMS == 0)
				size = mItemBeans.size() / Constants.PAGE_OF_ITEMS;
			else {
				size = mItemBeans.size() / Constants.PAGE_OF_ITEMS + 1;
			}

			for (int i = 0; i < size; i++) {
				LinearLayout group = (LinearLayout) mInflater.inflate(
						R.layout.contain_item_layout, null);
				int start = i * Constants.PAGE_OF_ITEMS;
				int end = start + Constants.PAGE_OF_ITEMS > mItemBeans.size() ? mItemBeans
						.size() : start + Constants.PAGE_OF_ITEMS;
				for (int j = start; j < end; j++) {
					Parameter b = mItemBeans.get(j);
					View itemView = mInflater.inflate(R.layout.item_layout,
							null);
					StringBuffer sb = new StringBuffer(b.getDisplayName());
					if (b instanceof NumericParameter) {
						sb.append("(")
								.append(((NumericParameter) b)
										.getParameterUnit().getLabel())
								.append(")");
					}
					((TextView) itemView.findViewById(R.id.item_name_tv))
							.setText(sb.toString());

					((TextView) itemView.findViewById(R.id.item_value_tv))
							.setText(b.getParameterValue().getValue() + "");
					itemView.setTag(j + "");
					itemView.setOnClickListener(itemClick);
					group.addView(itemView);
				}
				RadioButton rb = new RadioButton(context);
				final Integer index = i;
				rb.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						mViewPager.setCurrentItem(index);
					}
				});
				mRadioButtons.add(rb);
				mRadioGroup.addView(rb);
				mViews.add(group);
				if (mRadioButtons.size() > 0) {
					mRadioButtons.get(0).setChecked(true);
				}
			}
		}

		@Override
		public void notifyDataSetChanged() {
			for (int j = 0; j < mViews.size(); j++) {
				LinearLayout ll = ((LinearLayout) mViews.get(j));
				for (int i = 0; i < ll.getChildCount(); i++) {
					int m = Integer.parseInt(ll.getChildAt(i).getTag()
							.toString());
					((TextView) ll.getChildAt(i).findViewById(
							R.id.item_value_tv)).setText(mItemBeans.get(m)
							.getParameterValue().getValue().toString());
				}
			}

			super.notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return mViews.size();
		}

		@Override
		public boolean isViewFromObject(View v, Object obj) {
			return v == obj;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			((ViewPager) container).addView(mViews.get(position));
			return mViews.get(position);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView(mViews.get(position));
		}

	}

	@Override
	protected void onDestroy() {
		isSurival = false;
		super.onDestroy();
	}
}
