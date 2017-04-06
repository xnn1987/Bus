package cn.com.actia.smartdiag.beans;

import android.util.FloatMath;
import cn.com.actia.smartdiag.tools.DataFormatUtils;
import cn.com.actia.smartdiag.tools.Utils;

@SuppressWarnings("FloatMath")
public class TempretureBean {
	// 分割块
	public static final int cutedParts = 8;

	// 实际的最大值最小值
	private float actualMax;
	private float actualMin;
	// 显示的最大值最小值
	public int disPlayMax;
	public int disPlayMin;

	// 显示的值
	public float disPlayValue;
	public float actualValue;

	public int[] datas = new int[cutedParts + 1];

	/** 系数 */
	public int ratio;

	/** 单位 */
	public String unit;

	// 传进来的值
	public TempretureBean(float min, float max) {
		this.actualMin = min;
		float tempMax = max + (max - min) / 7;
		this.actualMax = max;

		initRange(min, tempMax);
		initDatas(disPlayMin, disPlayMax);
	}

	/**
	 * 根据实际的最大值和最小值，获取将要显示的最大值和最小值的范围并对 {@link #disPlayMax},{@link #disPlayMin}
	 * 进行赋值
	 */
	public void initRange(float min, float max) {
		int needMin = (int) FloatMath.floor(min);
		int needMax = (int) FloatMath.ceil(max);

		int tempMax = FormatData(needMax, 4);
		int tempMin = tempMax * -1;

		boolean isDealtable = true;
		if (needMin < 0) {
			int gap = needMax + needMin;
			if (gap <= 100) {
				if (gap < -100) {
					// TODO 这里需要显示不能够处理的范围
					Utils.showTag("不能处理的范围");
					isDealtable = false;
				} else {
					tempMax = FormatData(needMax, 4);
					tempMin = tempMax * -1;
				}
			} else if (needMax >= 1000) {
				if (needMin < -50) {
					// TODO 这里需要显示不能够处理的范围
					isDealtable = false;
					Utils.showTag("不能处理的范围");
				}
				if (needMax > 6750) {
					// TODO 这里需要显示不能够处理的范围
					isDealtable = false;
					Utils.showTag("不能处理的范围");
				} else {
					// 如果最大值超过了1000
					tempMin = -50;
					tempMax = 6750;
				}
			} else {// 其他之外
				int result[] = FormatDataWithMin(needMin, needMax);
				tempMax = result[1];
				tempMin = result[0];

			}
		} else {
			tempMax = FormatData(needMax, 8);
			tempMin = 0;
		}
		if (isDealtable) {
			this.disPlayMax = tempMax;
			this.disPlayMin = tempMin;
		} else {
			this.disPlayMax = needMax;
			this.disPlayMin = needMin;
		}
	}

	public void initDatas(int min, int max) {
		ratio = 1;
		int tempAdd = (max - min) / cutedParts;
		int zeroNum = DataFormatUtils.getZeroNum(tempAdd);
		if (max >= 1000) {
			ratio = (int) Math.pow(10, zeroNum);
		}
		for (int i = 0; i <= cutedParts; i++) {
			datas[i] = (min + tempAdd * i) / ratio;
		}
		disPlayMax /= ratio;
		disPlayMin /= ratio;
	}

	/** 如果最小值不为正的时候使用这个方法处理 */
	public int[] FormatDataWithMin(float min, float max) {
		int needMin = (int) FloatMath.floor(min);
		int needMax = (int) FloatMath.ceil(max);
		// 获取needMax的数字位数
		int digit = DataFormatUtils.getIntDigit(needMax);

		/* 每次取值需要增长的值 */
		int increment = (int) Math.pow(10, digit - 3);
		increment = increment <= 0 ? 1 : increment;

		/* 保留两位有效数字后得到的值 */
		float tempIncrement = increment;
		needMax = (int) (FloatMath.ceil(needMax / tempIncrement));

		boolean isDigitNeeded = false;
		while (!(needMax % 7 == 0 && isDigitNeeded)) {
			// 得到间隔的数，和间隔的数的位数
			float temp = needMax / 7f;
			int num = DataFormatUtils.getIntDigit((int) FloatMath.ceil(temp));
			switch (num) {
			case 1:
				if (Math.abs(needMin) < temp)
					isDigitNeeded = true;
				break;
			case 2:
				if (Math.abs(needMin) < temp
						&& (temp % 25 == 0 || temp % 10 == 0 || temp % 15 == 0))
					isDigitNeeded = true;
				break;
			case 3:
				if (Math.abs(needMin) < temp
						&& (temp % 25 == 0 || temp % 100 == 0 || temp % 150 == 0))
					isDigitNeeded = true;
				break;

			default:

				break;
			}
			if (!(needMax % 7 == 0 && isDigitNeeded))
				needMax += 1;
			else {
				needMin = -(int) needMax / 7 * increment;
			}
		}
		return new int[] { needMin, needMax * increment };
	}

	public int FormatData(float max, int cutedParts) {

		int needMax = (int) FloatMath.ceil(max);
		// 获取needMax的数字位数
		int digit = DataFormatUtils.getIntDigit(needMax);

		/* 每次取值需要增长的值 */
		int increment = (int) Math.pow(10, digit - 3);
		increment = increment <= 0 ? 1 : increment;

		/* 保留两位有效数字后得到的值 */
		float tempIncrement = increment;
		needMax = (int) (FloatMath.ceil(needMax / tempIncrement));

		boolean isDigitNeeded = false;
		while (needMax % cutedParts != 0 || !isDigitNeeded) {
			// 得到间隔的数，和间隔的数的位数
			float temp = needMax / (cutedParts * 1.0f);
			int num = DataFormatUtils.getIntDigit((int) FloatMath.ceil(temp));
			switch (num) {
			case 1:
				isDigitNeeded = true;
				break;
			case 2:
				if (temp % 25 == 0 || temp % 10 == 0 || temp % 15 == 0)
					isDigitNeeded = true;
				break;
			case 3:
				if (temp % 25 == 0 || temp % 100 == 0 || temp % 150 == 0)
					isDigitNeeded = true;
				break;

			default:

				break;
			}
			if (!(needMax % cutedParts == 0 && isDigitNeeded))
				needMax += 1;
		}
		return needMax * increment;
	}

	public float getActualMax() {
		return actualMax;
	}

	public float getActualMin() {
		return actualMin;
	}
}
