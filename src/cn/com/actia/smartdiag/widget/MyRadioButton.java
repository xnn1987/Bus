package cn.com.actia.smartdiag.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.widget.ImageView;
import cn.com.actia.smartdiag.R;

/*
 * Author: poper Email:xut@bluemobi.cn
 * Created Date:2013-3-4
 * Copyright @ 2013 BU
 * Description: ¿‡√Ë ˆ
 *
 * History:
 */
public class MyRadioButton extends ImageView {
	Bitmap mCheckedImg;
	Bitmap mDisCheckedImg;

	public MyRadioButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		mDisCheckedImg = BitmapFactory.decodeResource(getResources(),
				R.drawable.widget_view_switch_off);
		mCheckedImg = BitmapFactory.decodeResource(getResources(),
				R.drawable.widget_view_switch_on);
		setChecked(false);
	}

	public void setChecked(boolean checked) {
		if (checked) {
			setImageBitmap(mCheckedImg);
		} else {
			setImageBitmap(mDisCheckedImg);
		}
	}

}
