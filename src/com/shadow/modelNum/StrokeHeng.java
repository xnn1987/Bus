package com.shadow.modelNum;

import cn.com.actia.smartdiag.constants.Constants;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;

/** »­ºáÏßµÄ */
public class StrokeHeng extends NumStroke {
	Paint paint;

	public StrokeHeng(float size) {
		super(size);
	}
	

	public StrokeHeng(float x, float y, float size) {
		super(x, y, size);
	}

	public StrokeHeng(PointF location, float size) {
		super(location, size);
	}

	public StrokeHeng(float x, float y, float size, Paint paint) {
		this(x, y, size);
		this.paint = paint;
	}

	private final PointF[] style_half = new PointF[] {
			new PointF(2 * getSize(), 0), new PointF(12 * getSize(), 0),
			new PointF(13 * getSize(), getSize()),
			new PointF(12 * getSize(), 2 * getSize()),
			new PointF(2 * getSize(), 2 * getSize()),
			new PointF(getSize(), getSize()) };;

	@Override
	public void drawSelf(Canvas canvas) {
		Path path = new Path();
		if (null == paint) {
			paint = new Paint();
			paint.setAntiAlias(true);
			paint.setColor(Constants.COLOR);
		}

		for (int i = 0; i < style_half.length; i++) {
			if (i == 0) {
				path.moveTo(getLocation().x + style_half[i].x + 1,
						getLocation().y + style_half[i].y);
			} else {
				path.lineTo(getLocation().x + style_half[i].x + 1,
						getLocation().y + style_half[i].y);
			}
		}
		canvas.drawPath(path, paint);
	}

}
