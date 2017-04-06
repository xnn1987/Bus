package cn.com.actia.smartdiag.tools;

/**
 * @author Shadow
 * @category 用来处理
 */
public class DataFormatUtils {
	public static void formatMinMaxDataBigNum_2(int[] values, float min,
			float max) {
		int digNum = getIntDigit((int) max);

		int bei = 1;

		if (digNum > 3) {// 超过了999
			bei = (int) Math.pow(10, digNum - 3);
			min = min / (float) bei;
			max = max / (float) bei;
		}
		int need_min = (int) Math.floor(min);
		int need_max = (int) Math.floor(max);

		System.out.println(need_max);
		System.out.println(need_min);
		if (digNum >= 3) {
			while (need_min % 5 != 0) {// 将最小值转化为以5结尾的数字
				need_min -= 1;
			}
			while (need_max % 5 != 0) {// 将最大值转化为以5结尾的数字
				need_max += 1;
			}
			while (((need_max - need_min) / 8f) % 5 != 0) {
				need_max += 5;
			}
		} else {
			if (need_max > 40) {
				while (need_min % 5 != 0) {// 将最小值转化为以5结尾的数字
					need_min -= 1;
				}
				while (need_max % 5 != 0) {// 将最大值转化为以5结尾的数字
					need_max += 1;
				}
				while ((need_max - need_min) % 8 != 0) {
					need_max += 5;
				}
			} else {
				while ((need_max - need_min) % 8 != 0) {
					need_max += 1;
				}
			}
		}
		need_max *= bei;
		need_min *= bei;

		values[0] = need_max;
		values[1] = need_min;
		values[2] = (need_max - need_min) / 8;
		values[3] = bei;

	}

	public static void formatMinMaxDataBigNum(int[] values, float min, float max) {
		int needMin = (int) Math.floor(min);
		int needMax = (int) Math.ceil(max);
		// 获取needMax的数字位数
		int digit = getIntDigit(needMax);

		/* 每次取值需要增长的值 */
		int increment = (int) Math.pow(10, digit - 3);
		increment = increment <= 0 ? 1 : increment;
		// System.out.println(increment);

		/* 保留两位有效数字后得到的值 */
		float tempIncrement = increment;
		needMax = (int) (Math.ceil(needMax / tempIncrement));
		// System.out.println(needMax);
		if (needMin < 0) {// 如果最小值小于零就需要考虑0 的位置了
			boolean isDigitNeeded = false;
			while (!(needMax % 7 == 0 && isDigitNeeded)) {

				// 得到间隔的数，和间隔的数的位数
				float temp = needMax / 7f;
				int num = getIntDigit((int) Math.ceil(temp));
				// System.out.println("temp" + temp);
				// System.out.println("num" + num);
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
			// System.out.println("最终的：" + needMax + "~" + needMax * increment);
			// System.out.println(needMax / 7);

		} else {// 最小值设定为0，不需要考虑0 的位置
			boolean isDigitNeeded = false;
			needMin = 0;
			while (needMax % 8 != 0 || !isDigitNeeded) {

				// 得到间隔的数，和间隔的数的位数
				float temp = needMax / 8f;
				int num = getIntDigit((int) Math.ceil(temp));
				// System.out.println("temp" + temp);
				// System.out.println("num" + num);
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
				if (!(needMax % 8 == 0 && isDigitNeeded))
					needMax += 1;
			}

		}
		values[0] = needMin;
		values[1] = needMax * increment;
	}

	/** 0 是min 1，是max */
	public static void formatMinMaxData(int[] values, float min, float max) {
		int needMin = (int) Math.floor(min);
		int needMax = (int) Math.ceil(max);
		// 获取needMax的数字位数
		int digit = getIntDigit(needMax);

		/* 每次取值需要增长的值 */
		int increment = (int) Math.pow(10, digit - 3);
		increment = increment <= 0 ? 1 : increment;
		// System.out.println(increment);

		/* 保留两位有效数字后得到的值 */
		float tempIncrement = increment;
		needMax = (int) (Math.ceil(needMax / tempIncrement));
		// System.out.println(needMax);

		if (needMin < 0) {
			/* 如果负值与正值大概相等 */
			if (needMin + needMax <= 100) {

			} else {// 如果负值与正直相差较大
				if (needMax >= 1000) {
					needMin = -100;
					needMax = 7100;
				} else {
				}
			}
		} else {// 最小值设定为0，不需要考虑0 的位置
			boolean isDigitNeeded = false;
			needMin = 0;
			while (needMax % 8 != 0 || !isDigitNeeded) {

				// 得到间隔的数，和间隔的数的位数
				float temp = needMax / 8f;
				int num = getIntDigit((int) Math.ceil(temp));
				// System.out.println("temp" + temp);
				// System.out.println("num" + num);
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
				if (!(needMax % 8 == 0 && isDigitNeeded))
					needMax += 1;
			}

		}
		values[0] = needMin;
		values[1] = needMax * increment;
	}

	// 获取一个整数的数位
	public static int getIntDigit(int num) {
		int count = 0;
		int tempNum = num;
		do {
			tempNum = tempNum / 10;
			count++;
		} while (tempNum >= 1);
		return count;
	}

	public static int getZeroNum(int num) {
		int zeroNum = 0;
		while (num % 10 == 0) {
			zeroNum++;
			num /= 10;
		}
		return zeroNum;
	}
}
