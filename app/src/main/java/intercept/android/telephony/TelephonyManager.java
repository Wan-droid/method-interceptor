package intercept.android.telephony;

public class TelephonyManager {

    public static String getDeviceId(android.telephony.TelephonyManager manager) {
        return "Intercept-getDeviceId";
    }

    public static String getDeviceId(android.telephony.TelephonyManager manager, int id) {
        return "Intercept-getDeviceId-" + id;
    }

    public static String getImei(android.telephony.TelephonyManager manager) {
        return "Intercept-getImei";
    }
}
