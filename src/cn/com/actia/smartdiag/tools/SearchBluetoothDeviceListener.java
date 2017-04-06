package cn.com.actia.smartdiag.tools;

import java.util.List;

import android.bluetooth.BluetoothDevice;

public interface SearchBluetoothDeviceListener {
	/**
	 * 当开始扫描的时候调用此方法
	 * 
	 * @param whetherStarted
	 */
	public void onStartSearch(boolean whetherStarted);

	/**
	 * @param devices
	 *            当扫描结束时候调用此方法
	 */
	public void onCancelSearch(List<BluetoothDevice> devices);

	/**
	 * 当出错的时候调用
	 * 
	 * @param msg
	 *            错误的信息
	 */
	public void onError(String msg);
}
