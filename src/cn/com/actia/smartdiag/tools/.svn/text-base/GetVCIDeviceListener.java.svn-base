package cn.com.actia.smartdiag.tools;

import java.util.List;

import android.bluetooth.BluetoothDevice;

public interface GetVCIDeviceListener {
	/** 开始获取 */
	public void onGetStart();
	
	/** 状态改变 */
	public void onStateChange(String msg);

	/** 获取完成 */
	public void onCompleted(List<BluetoothDevice> VCIDevices);

	/** 出错 */
	public void onError(String msg);
}
