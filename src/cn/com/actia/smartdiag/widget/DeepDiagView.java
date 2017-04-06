package cn.com.actia.smartdiag.widget;

import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import cn.com.actia.smartdiag.R;
import cn.com.actia.smartdiag.beans.DeepDiagModule;
import cn.com.actia.smartdiag.beans.DeepDiagModule.ModuleStatus;

public class DeepDiagView extends SurfaceView implements Callback {
	DeepDiagModule[] deepDiagModules;
	String[] names;

	Bitmap homeBitmap;

	Paint redLinePaint;
	Paint blueLinePaint;

	public boolean isSurvial = true;

	SurfaceHolder mHolder;

	float mLeft;
	float mRight;
	float mTop;
	float mBottom;

	float mModuleWidth;
	float mModuleHeight;

	PointF p1 = new PointF();
	PointF p2 = new PointF();
	PointF p3 = new PointF();
	PointF p4 = new PointF();
	PointF p5 = new PointF();
	PointF p6 = new PointF();
	PointF p7 = new PointF();
	PointF p8 = new PointF();

	public DeepDiagView(Context context) {
		super(context);
		mHolder = getHolder();
		mHolder.addCallback(this);
		names = new String[] { "EMS", "ABS", "ACU", "BMS", "IMMC", "TPMS",
				"BMBS" };
		deepDiagModules = new DeepDiagModule[] {
				new DeepDiagModule(context, names[0]),
				new DeepDiagModule(context, names[1]),
				new DeepDiagModule(context, names[2]),
				new DeepDiagModule(context, names[3]),
				new DeepDiagModule(context, names[4]),
				new DeepDiagModule(context, names[5]),
				new DeepDiagModule(context, names[6]) };
		init();
		canvasThread.start();

	}

	private void init() {
		Bitmap b = BitmapFactory.decodeResource(getResources(),
				R.drawable.deep_diag_state_white);
		homeBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.deep_diag_home);
		mModuleWidth = b.getWidth();
		mModuleHeight = b.getHeight();

		redLinePaint = new Paint();
		redLinePaint.setColor(Color.RED);
		redLinePaint.setStrokeWidth(3);

		blueLinePaint = new Paint();
		blueLinePaint.setColor(getResources().getColor(R.color.lightblue));
		blueLinePaint.setStrokeWidth(3);
	}

	public void surfaceCreated(SurfaceHolder holder) {

		isRun = true;
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

		mLeft = width / 12;
		mRight = width * 11 / 12;
		mTop = height / 12;
		mBottom = height * 11 / 12;

		float yDis = (mBottom - mTop) / 16;
		float xDis = (mRight - mLeft) / 9;

		float left1 = mLeft;
		float left2 = mLeft + xDis * 4;
		float left3 = mLeft + xDis * 7;

		float top1 = mTop + yDis * 3;
		float top2 = mTop + yDis * 6;
		float top3 = mTop + yDis * 9;
		float top4 = mTop + yDis * 12;

		p1.y = mTop + yDis;
		p2.y = mTop + yDis * 12;
		p3.y = mTop + yDis;
		p4.y = mTop + yDis;
		p5.y = mTop + yDis * 6.5f;
		p6.y = mTop + yDis * 6.5f;
		p7.y = mTop + yDis * 3.5f;
		p8.y = mTop + yDis * 3.5f;

		p1.x = mLeft + xDis;
		p2.x = mLeft + xDis;
		p3.x = mLeft + xDis * 4;
		p4.x = mLeft + xDis * 6.5f;
		p5.x = mLeft + xDis * 6.5f;
		p6.x = mLeft + xDis * 7f;
		p7.x = mLeft + xDis * 6;
		p8.x = mLeft + xDis * 7f;

		deepDiagModules[0].setmLocRectF(new RectF(left1, top1, left1
				+ mModuleWidth, top1 + mModuleHeight));
		deepDiagModules[1].setmLocRectF(new RectF(left1, top2, left1
				+ mModuleWidth, top2 + mModuleHeight));
		deepDiagModules[2].setmLocRectF(new RectF(left1, top3, left1
				+ mModuleWidth, top3 + mModuleHeight));
		deepDiagModules[3].setmLocRectF(new RectF(left1, top4, left1
				+ mModuleWidth, top4 + mModuleHeight));
		deepDiagModules[4].setmLocRectF(new RectF(left2, top1, left2
				+ mModuleWidth, top1 + mModuleHeight));
		deepDiagModules[5].setmLocRectF(new RectF(left3, top1, left3
				+ mModuleWidth, top1 + mModuleHeight));
		deepDiagModules[6].setmLocRectF(new RectF(left3, top2, left3
				+ mModuleWidth, top2 + mModuleHeight));

		deepDiagModules[2].setmModuleStatus(ModuleStatus.Status_White);

	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		isRun = false;
	}

	boolean canTakeAChange = true;
	boolean isRun = true;
	int k = 0;

	private Thread canvasThread = new Thread() {
		public void run() {
			while (isSurvial) {
				while (isRun) {

					try {
						sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					k++;
					int n = k % 2;
					if (n == 0) {
						canTakeAChange = true;
						if (k / 7 < 7) {
							deepDiagModules[k / 7].setmErrorNum(new Random(3)
									.nextInt() % 3);
						}
					}

					if (canTakeAChange) {
						Canvas canvas = mHolder.lockCanvas();
						if(canvas==null){
							return;
						}
						canvas.drawLine(p1.x, p1.y, p2.x, p2.y, redLinePaint);
						canvas.drawLine(p1.x, p1.y, p3.x, p3.y, redLinePaint);
						canvas.drawLine(p3.x, p3.y, p4.x, p4.y, blueLinePaint);
						canvas.drawLine(p4.x, p4.y, p5.x, p5.y, blueLinePaint);
						canvas.drawLine(p5.x, p5.y, p6.x, p6.y, blueLinePaint);
						canvas.drawLine(p7.x, p7.y, p8.x, p8.y, blueLinePaint);
//						System.out.println(canvas);
						for (int i = 0; i < deepDiagModules.length; i++) {
							deepDiagModules[i].drawSelf(canvas);
						}
						canvas.drawBitmap(homeBitmap,
								p3.x - homeBitmap.getWidth() / 2, p3.y
										- homeBitmap.getHeight() / 2, null);
						mHolder.unlockCanvasAndPost(canvas);
						canTakeAChange = false;
					}

				}
			}
		};
	};
}
