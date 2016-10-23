package com.shuai.test.location;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.CellLocation;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.shuai.test.R;
import com.shuai.test.util.ResourcesUtil;

import java.util.List;

public class TestLocationActivity extends Activity {
    private static final String TAG = TestLocationService.class.getSimpleName();

    Button btnGPSShowLocation;
    Button btnNWShowLocation;
    Button mBtnGetCellInfo;

    TestLocationService appLocationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_location);
        appLocationService = new TestLocationService(
                TestLocationActivity.this);

        btnGPSShowLocation = (Button) findViewById(R.id.btnGPSShowLocation);
        btnGPSShowLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                Location gpsLocation = appLocationService
                        .getLocation(LocationManager.GPS_PROVIDER);

                if (gpsLocation != null) {
                    double latitude = gpsLocation.getLatitude();
                    double longitude = gpsLocation.getLongitude();
                    Toast.makeText(
                            getApplicationContext(),
                            "Mobile Location (GPS): \nLatitude: " + latitude
                                    + "\nLongitude: " + longitude,
                            Toast.LENGTH_LONG).show();
                } else {
                    showSettingsAlert("GPS");
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

                Location nwLocation = appLocationService
                        .getLocation(LocationManager.NETWORK_PROVIDER);

                if (nwLocation != null) {
                    double latitude = nwLocation.getLatitude();
                    double longitude = nwLocation.getLongitude();
                    Toast.makeText(
                            getApplicationContext(),
                            "Mobile Location (NW): \nLatitude: " + latitude
                                    + "\nLongitude: " + longitude,
                            Toast.LENGTH_LONG).show();
                } else {
                    showSettingsAlert("NETWORK");
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

    }

    public void showSettingsAlert(String provider) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                TestLocationActivity.this);

        alertDialog.setTitle(provider + " SETTINGS");

        alertDialog
                .setMessage(provider + " is not enabled! Want to go to settings menu?");

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

    /**
     * 获取手机基站信息
     */
    private void getCellLocationInfo() {

        TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        String operator = manager.getNetworkOperator();
        if(!TextUtils.isEmpty(operator)) {
            /**通过operator获取 MCC 和MNC */
            int mcc = Integer.parseInt(operator.substring(0, 3));
            int mnc = Integer.parseInt(operator.substring(3));
        }else{
            Log.e(TAG,"can not getNetworkOperator");
        }

        CellLocation location = manager.getCellLocation();
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
        List<NeighboringCellInfo> infoLists = manager.getNeighboringCellInfo();
        System.out.println("infoLists:" + infoLists + "     size:" + infoLists.size());
        for (NeighboringCellInfo info : infoLists) {
            strength += (-133 + 2 * info.getRssi());// 获取邻区基站信号强度
            //info.getLac();// 取出当前邻区的LAC
            //info.getCid();// 取出当前邻区的CID
            System.out.println("rssi:" + info.getRssi() + "   strength:" + strength);
        }

    }
}
