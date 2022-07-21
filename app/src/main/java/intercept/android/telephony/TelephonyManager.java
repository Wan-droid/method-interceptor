package intercept.android.telephony;

public class TelephonyManager {

    public static String getDeviceId(android.telephony.TelephonyManager manager){
        return "234234234";
    }
    public static String getDeviceId(android.telephony.TelephonyManager manager,int id){
        return "qweqweqweq-"+id;
    }

    public static String getImei(android.telephony.TelephonyManager manager){
        return "getImei-";
    }
}
