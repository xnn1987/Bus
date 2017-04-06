package cn.com.actia.smartdiag.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.RadioButton;

public class TwoSwitch extends RadioButton {
	String mShowingText_left = "QQQ";
	String mShowingText_right = "QQQ";
	Paint mPaint_left;
	Paint mPaint_right;

	public TwoSwitch(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
		setLeft();
		setRight();
	}

	public TwoSwitch(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public TwoSwitch(Context context) {
		this(context, null);
	}

	public void setLeft() {
		setChecked(false);
		mPaint_left.setColor(Color.WHITE);
		mPaint_right.setColor(Color.BLACK);
	}

	public void setRight() {
		setChecked(true);
		mPaint_left.setColor(Color.BLACK);
		mPaint_right.setColor(Color.WHITE);
	}

	public void setText(String text) {
		if (text.equals(mShowingText_left)) {
			setLeft();
		} else if (text.equals(mShowingText_right)) {
			setRight();
		} else {
		}
	}

	private void init() {
		mPaint_left = new Paint();
		mPaint_left.setTextSize(40);
		mPaint_left.setAntiAlias(true);
		mPaint_left.setColor(Color.BLACK);
		mPaint_left.setTextAlign(Align.CENTER);
		mPaint_right = new Paint(mPaint_left);
		mPaint_right.setColor(Color.WHITE);
	}

	public void setShowingText1(String showingText1) {
		this.mShowingText_left = showingText1;
	}

	public void setShowingText2(String showingText2) {
		this.mShowingText_right = showingText2;
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		Rect rf = new Rect();
		mPaint_left.getTextBounds("Apaa", 0, 2, rf);
		int textHeight = rf.bottom - rf.top;
		canvas.drawText(mShowingText_left, getWidth() / 10 * 3, getHeight() / 2
				+ textHeight / 2, mPaint_left);
		canvas.drawText(mShowingText_right, getWidth() / 10 * 7, getHeight()
				/ 2 + textHeight / 2, mPaint_right);
	}
}
