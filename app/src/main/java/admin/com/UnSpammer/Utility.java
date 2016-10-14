package admin.com.UnSpammer;

import android.app.ActivityManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Admin on 11/10/2016.
 */
public class Utility {
    public static boolean isServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
//    private boolean isNetworkAvailable() {
//        ConnectivityManager connectivityManager
//                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
//        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
//    }

    public static String setCallNumber(String str) {
        String number = str
                .replaceAll("\\s+","")
                .replaceAll("-","")
                .replaceAll("\\(","")
                .replaceAll("\\)","");
        if (number.startsWith("+")) {
            number = number.substring(3);
        } else if (number.startsWith("0")) {
            number = number.substring(1);
        }
        return number;
    }

}
