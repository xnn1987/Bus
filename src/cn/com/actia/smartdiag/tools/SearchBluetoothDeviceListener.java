package cn.com.actia.smartdiag.tools;

import java.util.List;

import android.bluetooth.BluetoothDevice;

public interface SearchBluetoothDeviceListener {
	/**
	 * ����ʼɨ���ʱ����ô˷���
	 * 
	 * @param whetherStarted
	 */
	public void onStartSearch(boolean whetherStarted);

	/**
	 * @param devices
	 *            ��ɨ�����ʱ����ô˷���
	 */
	public void onCancelSearch(List<BluetoothDevice> devices);

	/**
	 * �������ʱ�����
	 * 
	 * @param msg
	 *            �������Ϣ
	 */
	public void onError(String msg);
}
