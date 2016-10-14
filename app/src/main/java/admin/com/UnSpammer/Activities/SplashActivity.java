package admin.com.UnSpammer.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import admin.com.UnSpammer.R;

public class SplashActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Thread welcomeThread = new Thread() {
            @Override
            public void run() {
                try {
                    super.run();
                    sleep(5000) ; //Delay of 5 seconds
                } catch (Exception e) {
                    Log.d(TAG, "Something wrong with clock");
                } finally {

                    Intent i = new Intent(SplashActivity.this,
                            DialerActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        };
        welcomeThread.start();
    }
}
