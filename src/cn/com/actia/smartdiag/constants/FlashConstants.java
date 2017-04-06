package cn.com.actia.smartdiag.constants;


public class FlashConstants {
	public static final int FLASH_TIME = 1000;

	public static final int MSG_NODATA = -1;
	public static final int MSG_ERROR = 0;
	public static final int MSG_CHECKSUITABLE = 1;
	public static final int MSG_CHECKED_COMPLETED = 11;

	/** 更新状态 */
	public static final int MSG_VALUE_CHANGE = 2;

	/** 部分更新状态 */
	public static final int MSG_VALUE_PART_CHANGE = 3;
	public static final int STATUE_ENGINE = 101;
	public static final int STATUE_LIGHT = 102;
	public static final int STATUE_OIL = 103;
	public static final int STATUE_STOP = 104;
	public static final int STATUE_WATER = 105;

	public static final int DRIVING_HIGHWAY = 201;
	public static final int DRIVING_SUBURBAN = 202;
	public static final int DRIVING_UNKNOWN = 203;
	public static final int DRIVING_URBAN = 204;

	public static final String VEHICLESTATE = "VehicleState";
}
