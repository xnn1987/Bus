package cn.com.actia.smartdiag.tools;

/*
 * Author: poper Email:xut@bluemobi.cn
 * Created Date:2013-3-4
 * Copyright @ 2013 BU
 * Description: ¿‡√Ë ˆ
 *
 * History:
 */
public class CommonUtils {
	private static long lastClickTime;

	public static boolean isFastDoubleClick() {
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if (0 < timeD && timeD < 800) {
			return true;
		}
		lastClickTime = time;
		return false;
	}
}
