package admin.com.UnSpammer.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.onegravity.contactpicker.contact.Contact;
import com.onegravity.contactpicker.contact.ContactDescription;
import com.onegravity.contactpicker.contact.ContactSortOrder;
import com.onegravity.contactpicker.core.ContactPickerActivity;
import com.onegravity.contactpicker.picture.ContactPictureType;

import java.util.ArrayList;
import java.util.List;

import admin.com.UnSpammer.Adapters.ListItemAdapter;
import admin.com.UnSpammer.AnalyticsApplication;
import admin.com.UnSpammer.DataBaseHelpers.ListsContract;
import admin.com.UnSpammer.Helpers.DividerItemDecoration;
import admin.com.UnSpammer.Helpers.SwipeHelper;
import admin.com.UnSpammer.CallService.CallTurningService;
import admin.com.UnSpammer.R;
import admin.com.UnSpammer.Utility;

public class BlockerActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private Switch onOffSwitch;
    private AdView mAdView;
    Tracker mTracker;
    private final String TAG = this.getClass().getSimpleName();
    public static final int EDIT_LIST_LOADER = 0;
    private ListItemAdapter adapter;
    private RecyclerView recyclerView;
    private TextView emptyListNotice;

    private FloatingActionMenu menuLabelsRight;
    private FloatingActionButton deleteAllButton;
    private FloatingActionButton addContactsButton;
    private final int REQUEST_CONTACT = 0;
    public static ArrayList<String> blocked;

    private boolean loggingOn = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block);

        if (!checkPhonePermission()) {
            Toast.makeText(BlockerActivity.this, getResources().getString(R.string.telephone_permission_not_granted), Toast.LENGTH_LONG).show();
            finish();
        }

        if (!checkContactsReadPermission()) {
            Toast.makeText(BlockerActivity.this, getResources().getString(R.string.contacts_permission_not_granted), Toast.LENGTH_LONG).show();
            finish();
        }

        mTracker = ((AnalyticsApplication) getApplication()).getmTracker();
        mTracker.setScreenName(TAG);
        mTracker.send(new HitBuilders.ScreenViewBuilder()
                .build());

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        onOffSwitch = (Switch) findViewById(R.id.switch_on_off);
        assert (onOffSwitch != null);
        onOffSwitch.setChecked(Utility.isServiceRunning(CallTurningService.class, BlockerActivity.this));
        setOnOffSwitch();

        recyclerView = (RecyclerView) findViewById(R.id.list_recycler_view);
        getSupportLoaderManager().initLoader(EDIT_LIST_LOADER,null, this);

        emptyListNotice = (TextView) findViewById(R.id.empty_list_notice);
        deleteAllButton = (FloatingActionButton) findViewById(R.id.delete_all_button);
        addContactsButton = (FloatingActionButton) findViewById(R.id.add_contact_button);

        adapter = new ListItemAdapter(BlockerActivity.this, null);

        recyclerView.setLayoutManager(new LinearLayoutManager(BlockerActivity.this));
        recyclerView.addItemDecoration(new DividerItemDecoration(ContextCompat.getDrawable(getApplicationContext(), R.drawable.divider_gradient)));
        recyclerView.setAdapter(adapter);


        blocked = new ArrayList<>();
        menuLabelsRight = (FloatingActionMenu) findViewById(R.id.menu_labels_right);
        menuLabelsRight.setClosedOnTouchOutside(true);

        deleteAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
        // Adding a dialog to the deleting confirmation
                new AlertDialog.Builder(BlockerActivity.this)
                        .setTitle(getResources().getString(R.string.dialog_title))
                        .setMessage(getResources().getString(R.string.dialog_message))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Deleting the list contacts
                                getContentResolver().delete(ListsContract.BlackListEntry.CONTENT_URI, null, null);
                                Toast.makeText(BlockerActivity.this, "Deleted !", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();

            }
        });

        setAddContactsButtonListener();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        onOffSwitch.setChecked(Utility.isServiceRunning(CallTurningService.class, BlockerActivity.this));
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(BlockerActivity.this,
                ListsContract.BlackListEntry.CONTENT_URI,
                null,
                null,
                null,
                ListsContract.BlackListEntry.COLUMN_NAME + " ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
        ItemTouchHelper.Callback callback = new SwipeHelper(adapter);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(recyclerView);
        if (data.getCount()>0) {
            emptyListNotice.setVisibility(View.GONE);
        } else emptyListNotice.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CONTACT && resultCode == Activity.RESULT_OK &&
                data != null && data.hasExtra(ContactPickerActivity.RESULT_CONTACT_DATA)) {

            List<Contact> contacts = (List<Contact>) data.getSerializableExtra(ContactPickerActivity.RESULT_CONTACT_DATA);
            blocked.clear();
            ArrayList<ContentValues> cv = new ArrayList<>();
            ContentValues cvArray[] = new ContentValues[contacts.size()];
            for (Contact contact : contacts) {
                try {

                    String s = Utility.setCallNumber(contact.getPhone(0));

                    ContentValues value = new ContentValues();

                    if (!contact.getLastName().equals("---")) {
                        value.put(ListsContract.BlackListEntry.COLUMN_NAME, contact.getFirstName() + " " + contact.getLastName());
                    } else
                        value.put(ListsContract.BlackListEntry.COLUMN_NAME, contact.getFirstName());

                    value.put(ListsContract.BlackListEntry.COLUMN_NUMBER, s);
                    cv.add(value);

                    blocked.add(s);
                    if (loggingOn)
                        Log.d(TAG + "Saved Number", s);
                }
                catch (NullPointerException e){
                    Log.e(TAG, e.toString());
                }
            }
            getContentResolver().bulkInsert(ListsContract.BlackListEntry.CONTENT_URI, cv.toArray(cvArray));

        }
    }

    private void setAddContactsButtonListener() {
        addContactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuLabelsRight.close(true);
                if (checkContactsReadPermission()) {
                    startContactsPickerActivity();
                    Log.d(TAG,"Permitted");
                }
                else Toast.makeText(BlockerActivity.this, getResources().getString(R.string.contacts_permission_not_granted), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setOnOffSwitch() {
        onOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                // Checking the services for the call turning down
                if (b && Utility.isServiceRunning(CallTurningService.class, BlockerActivity.this)) {
                    if (loggingOn)
                        Log.d(TAG +" Switching", b+"");
                }
                else if (b && !Utility.isServiceRunning(CallTurningService.class, BlockerActivity.this)) {
                    // Considering the worst case when it doesn't start for some reason.
                    boolean stopped = true;
                    while (stopped) {
                        stopped = stopService(new Intent(BlockerActivity.this, CallTurningService.class));
                        Log.d("Blocking",  stopped+"");
                        if (!stopped) {
                            if (loggingOn)
                            Log.d(TAG, "Started Successfully.");
                        }
                    }
                    startService(new Intent(BlockerActivity.this, CallTurningService.class));
                    if (loggingOn)
                        Log.d(TAG + " Switching", b+"");
                }
                else if (!b && Utility.isServiceRunning(CallTurningService.class, BlockerActivity.this)) {
                    //Switch Off but Service running
                    //Stop Service
                    boolean running = true;
                    while (running) {
                        running = stopService(new Intent(BlockerActivity.this, CallTurningService.class));
                        if (loggingOn)
                            Log.d(TAG, running+"");

                        if (!running) {
                            if (loggingOn)
                                Log.d(TAG, "Stopped Successfully.");
                        }
                    }
                    if (loggingOn)
                        Log.d(TAG + " Switching", b+"");
                }
                else if (!b && !Utility.isServiceRunning(CallTurningService.class, BlockerActivity.this)) {
                    if (loggingOn)
                        Log.d(TAG + " Switching", b+"");
                }
            }
        });
    }
    // Checking and sending for the permissons
    private boolean checkContactsReadPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            try {
                int pid = android.os.Process.myPid();
                PackageManager pckMgr = getPackageManager();
                int uid = pckMgr.getApplicationInfo(getComponentName().getPackageName(), PackageManager.GET_META_DATA).uid;
                enforcePermission(android.Manifest.permission.READ_CONTACTS, pid, uid, getResources().getString(R.string.contacts_permission_not_granted));
                return true;
            }
            catch (PackageManager.NameNotFoundException | SecurityException e) {
                Log.e(TAG, e.getMessage());
                return false;
            }
        } else return true;
    }

    // Checking and sending for the permissons
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
                Log.e(TAG, e.getMessage());
                return false;
            }
        } else return true;
    }
    // Library contacts picker method to pickup contacts
    private void startContactsPickerActivity() {
        Intent intent = new Intent(BlockerActivity.this, ContactPickerActivity.class)
                .putExtra(ContactPickerActivity.EXTRA_CONTACT_BADGE_TYPE, ContactPictureType.ROUND.name())
                .putExtra(ContactPickerActivity.EXTRA_CONTACT_DESCRIPTION, ContactDescription.ADDRESS.name())
                .putExtra(ContactPickerActivity.EXTRA_CONTACT_DESCRIPTION_TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                .putExtra(ContactPickerActivity.EXTRA_CONTACT_SORT_ORDER, ContactSortOrder.AUTOMATIC.name());
        startActivityForResult(intent, REQUEST_CONTACT);
    }

}


