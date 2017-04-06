package cn.com.actia.smartdiag.constants;

import java.util.Locale;

import android.graphics.Color;

public class Constants {
	public static final int NEED_TO_ACTIVATE = -100;
	public static final int NEED_UPDATA_NO_FIRWARE = -233;
	public static final int UPDATE_SD_NEEDCHECK = -1;
	public static final int UPDATE_VCI_NEEDCHECK = -2;
	public static final int ERROR_OTHER_ERRORS = 101;
	public static final int ERROR_GET_A_VCI_FIRST =102;
	public static final int NO_ERROR = 0;

	public static final int DOWNLOAD_VCI_START = 9;//��ʼ����
	public static final int DOWNLOAD_VCI_SUCCESS = 10;//����ʧ��
	public static final int DOWNLOAD_VCI_FAILD = 11;//���سɹ�
	public static final int FLASH_VCI_START = 19;//��ʼˢ��
	public static final int FLASH_VCI_FAILD = 20;//ˢ��ʧ��
	public static final int FLASH_VCI_SUCCESS = 21;//ˢ�³ɹ�

	public static final String COMPLETED_ADRESS = "COMPLETED_ADRESS";
	public static final String COMPLETED_NAME = "COMPLETED_NAME";

	public static final String SHAREPREFERECE_TAG_NAME = "cn.com.actia.smartdiag";
	public static final String SHAREPREFERECE_SETTING = "smartDiag-setting";
	/** ��־�Ƿ���ʾTAGS */
	public static final boolean WITHOUT_SHOW_TAGS = true;
	public static final String TAG_SMART_DIAG = "Shadow_Smart_Diag_TAG";

	/** ����ʽ�� */
	public static final int STYLE_NORMAL = 0;
	/** ������ť��ʽ�� */
	public static final int STYLE_TWO_BUTTON = 1;

	public static final int STYLE_EXIT_ACTIVITY_STARTSETTING = 2;
	public static final int STYLE_EXIT_ACTIVITY = 3;

	public static final String DEVICES_NAME_FRONT = "ACTIA-VCI-";
	/** �豸ɨ���ʱ�� */
	public static final long searchTime = 15 * 1000;
	/** ����������Ϣ */
	public static final String Language = Locale.getDefault().getLanguage();
	public static final String Country = Locale.getDefault().getCountry();

	public static String Connected_devices_adress;

	public static boolean isConnected;

	/** �������Ƿ�������ϵͳ�ı�־ */
	public static boolean isSelectVehicleConfigSystemInfo = false;
	public static boolean isGetBundleSuitable = false;
	public static boolean isLastOBDVEHICLE = false;

	public static final int PAGE_OF_ITEMS = 6;

	/** �Ƿ���Ի�ȡAllparameter����usableParameter���Ӽ� */

	// public static String BLUETOOTH_ADRESS = "AA:BB:CC:DD:EE:FF";
	// public static String BLUETOOTH_NAME = "TEST+EXAMPLE";
	public static String BLUETOOTH_ADRESS = "";
	public static String BLUETOOTH_NAME = "";
	public static int COLOR = Color.WHITE;
	// public static int COLOR2 = Color.GRAY;
	public static boolean SystemINFO_CONFIGED = false;

	public static boolean isInitCloud = false;

}
