package cn.com.actia.smartdiag.beans;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.RectF;
import cn.com.actia.smartdiag.R;

public class DeepDiagModule {
	public static enum ModuleStatus {
		Status_White, Status_Red, Status_Green;
	}

	private Context mContext;
	private Paint mTextPaint;
	private Paint mNumPaint;

	private ModuleStatus mModuleStatus = ModuleStatus.Status_White;
	private RectF mLocRectF;

	private RectF mNumRectF;
	private String mName;
	private Bitmap mBgBitmap;

	private Bitmap errorBgBitmap;

	private int mErrorNum = 0;

	public void setmLocRectF(RectF mLocRectF) {

		this.mLocRectF = mLocRectF;
		this.mNumRectF = new RectF();

		mNumRectF.top = mLocRectF.top - errorBgBitmap.getHeight() / 2;
		mNumRectF.left = mLocRectF.right - errorBgBitmap.getWidth() / 2;
		mNumRectF.right = mNumRectF.left + errorBgBitmap.getWidth();
		mNumRectF.bottom = mNumRectF.top + errorBgBitmap.getHeight();
	}

	public ModuleStatus getmModuleStatus() {
		return mModuleStatus;
	}

	public void setmModuleStatus(ModuleStatus mModuleStatus) {
		this.mModuleStatus = mModuleStatus;
	}

	public RectF getmLocRectF() {
		return mLocRectF;
	}

	public int getmErrorNum() {
		return mErrorNum;
	}

	public void setmErrorNum(int mErrorNum) {
		this.mErrorNum = mErrorNum;
		if (mErrorNum == 0) {
			setmModuleStatus(ModuleStatus.Status_Green);
		} else {
			setmModuleStatus(ModuleStatus.Status_Red);
		}
	}

	public DeepDiagModule(Context context, String name) {
		this(context, name, new RectF());
	}

	public DeepDiagModule(Context context, String name, RectF rectF) {
		this.mLocRectF = rectF;

		mContext = context;

		errorBgBitmap = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.deep_diag_num_bg);

		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setColor(Color.BLACK);
		mTextPaint.setTextSize(20);
		mTextPaint.setTextAlign(Align.CENTER);

		mNumPaint = new Paint();
		mNumPaint.setAntiAlias(true);
		mNumPaint.setColor(Color.WHITE);
		mNumPaint.setTextSize(errorBgBitmap.getHeight() * 6 / 10);
		mNumPaint.setTextAlign(Align.CENTER);

		this.mName = name;

	}

	public void drawSelf(Canvas canvas) {
		if (mErrorNum > 0) {
			mModuleStatus = ModuleStatus.Status_Red;
		}
		if (mModuleStatus == ModuleStatus.Status_White) {
			mBgBitmap = BitmapFactory.decodeResource(mContext.getResources(),
					R.drawable.deep_diag_state_white);
		} else if (mModuleStatus == ModuleStatus.Status_Green) {
			mBgBitmap = BitmapFactory.decodeResource(mContext.getResources(),
					R.drawable.deep_diag_state_green);
		} else if (mModuleStatus == ModuleStatus.Status_Red) {
			mBgBitmap = BitmapFactory.decodeResource(mContext.getResources(),
					R.drawable.deep_diag_state_red);
		}

		canvas.drawBitmap(mBgBitmap, mLocRectF.left, mLocRectF.top, null);
		if (mErrorNum > 0) {
			canvas.drawBitmap(errorBgBitmap, mNumRectF.left, mNumRectF.top,
					null);
			canvas.drawText(mErrorNum + "", mNumRectF.centerX(),
					mNumRectF.centerY() + mNumRectF.height() / 4, mNumPaint);

		}
		canvas.drawText(mName, mLocRectF.centerX(), mLocRectF.centerY() + 10,
				mTextPaint);
	}
}
