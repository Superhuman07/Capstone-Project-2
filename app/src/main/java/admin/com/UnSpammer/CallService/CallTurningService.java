package admin.com.UnSpammer.CallService;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;

import admin.com.UnSpammer.R;
import admin.com.UnSpammer.DataBaseHelpers.ListsContract;
import admin.com.UnSpammer.Utility;
import admin.com.UnSpammer.Widget;
// Thank you http://stackoverflow.com/questions/9904426/how-to-block-a-mobile-number-call-and-message-receiving-in-android-application-d
public class CallTurningService extends Service {
    private String TAG = this.getClass().getSimpleName();
    private Context mContext;
    TelephonyManager telephonymanager;
    StateListener phoneStateListener;
    public CallTurningService() {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        Toast.makeText(CallTurningService.this, getResources().getString(R.string.service_started), Toast.LENGTH_LONG).show();
        Intent intent = new Intent(Widget.TAG_SERVICE_STATE).setPackage(this.getPackageName());
        sendBroadcast(intent);
        phoneStateListener = new StateListener();
        telephonymanager = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
        telephonymanager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    class StateListener extends PhoneStateListener{
        Cursor cursor;
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            Log.d(TAG, incomingNumber);
            switch(state){
                case TelephonyManager.CALL_STATE_RINGING:
                    Log.d(TAG, "RINGING");

                    try{
                        TelephonyManager manager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
                        Class c = Class.forName(manager.getClass().getName());
                        Method m = c.getDeclaredMethod("getITelephony");
                        m.setAccessible(true);
                        ITelephony telephony = (ITelephony)m.invoke(manager);
                        String test[] = new String[1];
                        test[0] = Utility.setCallNumber(incomingNumber);
                        cursor = mContext.getContentResolver().query(ListsContract.BlackListEntry.CONTENT_URI,
                                null,
                                ListsContract.BlackListEntry.COLUMN_NUMBER + "= ?",
                                test, null);
                        if (cursor !=null && cursor.getCount() == 0) {
                            Log.d(TAG, "Call list is not empty no call to block");
              }
                        else{
                        telephony.endCall();
                        Toast.makeText(getApplicationContext(),
                                    getResources().getString(R.string.rejected)+incomingNumber,
                                    Toast.LENGTH_SHORT).show();}
                    } catch(Exception e){
                        Log.d(TAG,e.getMessage());
                    }
                    finally {
                        if (cursor != null)
                            cursor.close();
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Log.d(TAG, "OFFHOOK");
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    Log.d(TAG, "IDLE");
                    break;
            }
        }
    }
    // Disabling the service when it is destroyed
    @Override
    public void onDestroy() {
        telephonymanager.listen(phoneStateListener,  PhoneStateListener.LISTEN_NONE);
        Intent intent = new Intent(Widget.TAG_SERVICE_STATE).setPackage(this.getPackageName());
        sendBroadcast(intent);
        Toast.makeText(CallTurningService.this, getResources().getString(R.string.service_stopped), Toast.LENGTH_SHORT).show();
    }
}
