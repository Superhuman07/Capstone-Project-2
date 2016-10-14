package admin.com.UnSpammer.Activities;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;



import admin.com.UnSpammer.R;

public class DialerActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String SHOWCASE_ID = "1" ;
    int withDelay;
    private EditText screen;
   public FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(DialerActivity.this, BlockerActivity.class);
//                startActivity(intent);
//            }
//        });
        // single example





        ActivityCompat.requestPermissions(DialerActivity.this,
                new String[]{android.Manifest.permission.READ_PHONE_STATE, android.Manifest.permission.READ_CONTACTS},
                1);

        if (!checkPhonePermission()) {
            Toast.makeText(DialerActivity.this, getResources().getString(R.string.telephone_permission_not_granted), Toast.LENGTH_LONG).show();
            finish();
        }
        setContentView(R.layout.activity_dialer);
        initializeView();

    }



    private void initializeView() {
        screen = (EditText)findViewById(R.id.screen);
        int idList[] = {R.id.btn1,R.id.btn2,R.id.btn3,
                R.id.btn4,R.id.btn5,R.id.btn6,
                R.id.btn7,R.id.btn8,R.id.btn9,
                R.id.btnDial,R.id.btnDel,R.id.btnStar,
                R.id.btnZero,R.id.btnHash,R.id.fab};

        for(int d: idList){
            View v = (View)findViewById(d);
            v.setOnClickListener(this);
        }
    }

    public void display(String val){
        screen.append(val);
    }

    private boolean checkCallPermission(){
        String permission = "android.permission.CALL_PHONE";
        int res = getApplicationContext().checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn1:
                display("1");
                break;
            case R.id.btn2:
                display("2");
                break;
            case R.id.btn3:
                display("3");
                break;
            case R.id.btn4:
                display("4");
                break;
            case R.id.btn5:
                display("5");
                break;
            case R.id.btn6:
                display("6");
                break;
            case R.id.btn7:
                display("7");
                break;
            case R.id.btn8:
                display("8");
                break;
            case R.id.btn9:
                display("9");
                break;
            case R.id.btnZero:
                display("0");
                break;
            case R.id.btnStar:
                display("*");
                break;
            case R.id.btnHash:
                display("#");
                break;
            case R.id.btnDial:
                if(screen.getText().toString().isEmpty())
                    Toast.makeText(getApplicationContext(),"Enter some digits",Toast.LENGTH_SHORT).show();
                else if(checkCallPermission())
                    startActivity(new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+screen.getText())));
                break;
            case R.id.btnDel:
                if(screen.getText().toString().length()>=1) {
                    String newScreen = screen.getText().toString().substring(0, screen.getText().toString().length() - 1);
                    screen.setText(newScreen);
                }
                break;
            case R.id.fab:
                Intent intent = new Intent(DialerActivity.this, BlockerActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
    private boolean checkPhonePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            try {
                int pid = android.os.Process.myPid();
                PackageManager pckMgr = getPackageManager();
                int uid = pckMgr.getApplicationInfo(getComponentName().getPackageName(), PackageManager.GET_META_DATA).uid;
                enforcePermission(Manifest.permission.READ_PHONE_STATE, pid, uid, getResources().getString(R.string.telephone_permission_not_granted));
                return true;
            }
            catch (PackageManager.NameNotFoundException | SecurityException e) {
                return false;
            }
        } else return true;
    }
}
