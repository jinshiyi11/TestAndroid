package com.shuai.test.location;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.shuai.test.util.ResourcesUtil;

import com.shuai.test.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class TestLocationActivity extends Activity implements LocationListener {
    private static final String TAG = TestLocationActivity.class.getSimpleName();

    private Context mContext;
    private Button btnGPSShowLocation;
    private Button btnNWShowLocation;
    private Button mBtnGetCellInfo;
    private Button mBtnGeoCoder;
    private Button mBtnScanWifi;

    private LocationManager mLocationManager;
    private TelephonyManager mTelePhonyManager;
    private WifiManager mWifiManager;
    private MyPhoneStateListener mPhoneStateListener;
    private Geocoder mGeocoder;

    private static final long MIN_DISTANCE_FOR_UPDATE = 10;
    private static final long MIN_TIME_FOR_UPDATE = 1000 * 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_location);
        mContext = this;
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mTelePhonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mGeocoder = new Geocoder(this, Locale.getDefault());
        mPhoneStateListener = new MyPhoneStateListener();

        TestLocationActivityPermissionsDispatcher.initWithPermissionCheck(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        TestLocationActivityPermissionsDispatcher.onRequestPermissionsResult(this,requestCode,grantResults);
    }

    @NeedsPermission({Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
    })
    public void init() {
        mTelePhonyManager.listen(mPhoneStateListener,
                PhoneStateListener.LISTEN_CALL_STATE |
                        PhoneStateListener.LISTEN_DATA_CONNECTION_STATE |
                        PhoneStateListener.LISTEN_CELL_INFO |
                        PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
        );

        String test = null;
        Log.d(TAG, "onCreate" + test);

        btnGPSShowLocation = findViewById(R.id.btnGPSShowLocation);
        btnGPSShowLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                Location gpsLocation = getLocation(LocationManager.GPS_PROVIDER);

                if (gpsLocation != null) {
                    double latitude = gpsLocation.getLatitude();
                    double longitude = gpsLocation.getLongitude();
                    Toast.makeText(
                            getApplicationContext(),
                            "Mobile Location (GPS): \nLatitude: " + latitude
                                    + "\nLongitude: " + longitude,
                            Toast.LENGTH_LONG).show();
                } else {
                    //showSettingsAlert("GPS");
                }

            }
        });

        btnNWShowLocation = (Button) findViewById(R.id.btnNWShowLocation);
        btnNWShowLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                final Resources resources = getResources();
                //com.android.internal.R.bool.config_enableNetworkLocationOverlay
                boolean enableNetworkLocationOverlay = resources.getBoolean(ResourcesUtil.getSystemResourceId("config_enableNetworkLocationOverlay", "bool", "android"));
                //com.android.internal.R.string.config_networkLocationProviderPackageName
                String networkLocationProviderPackageName = getString(ResourcesUtil.getSystemResourceId("config_networkLocationProviderPackageName", "string", "android"));
                //com.android.internal.R.array.config_locationProviderPackageNames
                String[] locationProviderPackageNames = resources.getStringArray(ResourcesUtil.getSystemResourceId("config_locationProviderPackageNames", "array", "android"));

                Log.d(TAG, "enableNetworkLocationOverlay:" + enableNetworkLocationOverlay);
                Log.d(TAG, "networkLocationProviderPackageName:" + networkLocationProviderPackageName);
                Log.d(TAG, "locationProviderPackageNames:" + locationProviderPackageNames);

                Location nwLocation = getLocation(LocationManager.NETWORK_PROVIDER);

                if (nwLocation != null) {
                    double latitude = nwLocation.getLatitude();
                    double longitude = nwLocation.getLongitude();
                    Toast.makeText(
                            getApplicationContext(),
                            "Mobile Location (NW): \nLatitude: " + latitude
                                    + "\nLongitude: " + longitude,
                            Toast.LENGTH_LONG).show();
                } else {
                    //showSettingsAlert("NETWORK");
                }

            }
        });

        mBtnGetCellInfo = (Button) findViewById(R.id.btn_get_cell_info);
        mBtnGetCellInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCellLocationInfo();
            }
        });

        mBtnGeoCoder = (Button) findViewById(R.id.btn_geocoder);
        mBtnGeoCoder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Geocoder.isPresent()) {
                    Log.d(TAG, "Geocoder is present!");
                } else {
                    Log.e(TAG, "Geocoder is not present!");
                    Toast.makeText(mContext, "Geocoder is not present!", Toast.LENGTH_LONG).show();

                }
                onGeoCoder();
            }
        });

        mBtnScanWifi = (Button) findViewById(R.id.btn_scan_wifi);
        mBtnScanWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onScanWifi();
            }
        });

        IntentFilter filter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (!intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
                    throw new IllegalStateException();

                Log.d(TAG, "onReceive wifi scan result");
                List<ScanResult> scanResults = mWifiManager.getScanResults();
                if (scanResults != null) {
                    for (ScanResult item : scanResults) {
                        Log.d(TAG, item.toString());
                    }
                }

            }
        }, filter);


//        mLocationManager.addNmeaListener(new GpsStatus.NmeaListener() {
//            @Override
//            public void onNmeaReceived(long timestamp, String nmea) {
//                Log.d(TAG, "onNmeaReceived,timestamp:" + timestamp + ",nmea:" + nmea);
//
//            }
//        });

    }

    private void onGeoCoder() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<Address> addressList = mGeocoder.getFromLocationName("天安门", 8);
                    Log.d(TAG, "" + addressList);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        t.start();

    }

    public void showSettingsAlert(String provider) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                TestLocationActivity.this);

        alertDialog.setTitle(provider + " SETTINGS");

        alertDialog.setMessage(provider + " is not enabled! Want to go to settings menu?");

        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        TestLocationActivity.this.startActivity(intent);
                    }
                });

        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public Location getLocation(String provider) {
        Location location = null;
        if (mLocationManager.isProviderEnabled(provider)) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                return null;
            }
            mLocationManager.requestLocationUpdates(provider,
                    MIN_TIME_FOR_UPDATE, MIN_DISTANCE_FOR_UPDATE, this);
            if (mLocationManager != null) {
                location = mLocationManager.getLastKnownLocation(provider);

                return location;
            }
        }
        return null;
    }

    /**
     * 获取手机基站信息
     */
    private void getCellLocationInfo() {
        String operator = mTelePhonyManager.getNetworkOperator();
        if (!TextUtils.isEmpty(operator)) {
            /**通过operator获取 MCC 和MNC */
            int mcc = Integer.parseInt(operator.substring(0, 3));
            int mnc = Integer.parseInt(operator.substring(3));
        } else {
            Log.e(TAG, "can not getNetworkOperator");
        }

        CellLocation location = mTelePhonyManager.getCellLocation();
        if (location != null) {
            if (location instanceof GsmCellLocation) {
                GsmCellLocation gsmCellLocation = (GsmCellLocation) location;
                int lac = gsmCellLocation.getLac();
                int cellid = gsmCellLocation.getCid();


            } else if (location instanceof CdmaCellLocation) {
                CdmaCellLocation cdmaCellLocation = (CdmaCellLocation) location;
                int systemId = cdmaCellLocation.getSystemId();
            } else {

            }
        }

        int strength = 0;
        /**通过getNeighboringCellInfo获取BSSS */
//        List<NeighboringCellInfo> infoLists = mTelePhonyManager.getNeighboringCellInfo();
//        System.out.println("infoLists:" + infoLists + "     size:" + infoLists.size());
//        for (NeighboringCellInfo info : infoLists) {
//            strength += (-133 + 2 * info.getRssi());// 获取邻区基站信号强度
//            //info.getLac();// 取出当前邻区的LAC
//            //info.getCid();// 取出当前邻区的CID
//            System.out.println("rssi:" + info.getRssi() + "   strength:" + strength);
//        }

    }

    private void onScanWifi() {
        mWifiManager.startScan();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged:" + location);
        Toast.makeText(this,location.toString(),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
