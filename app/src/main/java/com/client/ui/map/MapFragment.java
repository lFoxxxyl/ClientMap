package com.client.ui.map;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.client.MainActivity;
import com.client.MyAdapter;
import com.client.NetworkService;
import com.client.R;
import com.client.models.Tokens;
import com.client.models.User;
import com.client.models.UserLocation;
import com.client.ui.home.HomeViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.client.models.Tokens.updateTokens;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    SharedPreferences sharedPreferences;
    final String LOGIN = "login";
    final String ACCESS_TOKEN = "accessToken";
    final String REFRESH_TOKEN = "refreshToken";

    final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    GoogleMap mGoogleMap;
    MapView mapView;
    LocationRequest mLocationRequest;
    FusedLocationProviderClient mFusedLocationClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_map, container, false);

        sharedPreferences = this.getActivity().getSharedPreferences("preferences", MODE_PRIVATE);

        mapView = root.findViewById(R.id.mapView);

        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(this);

        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted

        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());


        return root;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(0, 0))
                .title("Marker"));
        mGoogleMap.setMyLocationEnabled(true);

        mFusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
                }
            }
        });


        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1200); // two minute interval 120000
        mLocationRequest.setFastestInterval(1200);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                //The last location in the list is the newest
                Location location = locationList.get(locationList.size() - 1);
                Log.i("MapsActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());
                mLastLocation = location;
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker.remove();
                }

                //Place current location marker
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Current Position");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                //mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);

                //move map camera
                //mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));



                DBHelper dbHelper = new DBHelper(getActivity());
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                Cursor c = db.query("mytable", null, "checked=?", new String[] {"1"}, null, null, null);

                int loginColIndex = c.getColumnIndex("login");
                String[] logins = new String[c.getCount()+2];
                logins[0]=String.valueOf(location.getLatitude());
                logins[1]=String.valueOf(location.getLongitude());
                Log.d("DB", "row insert54ted, ID = " + c.getCount());
                c.moveToFirst();
                for (int i = 0; i<c.getCount();i++) {
                    logins[i + 2] = c.getString(loginColIndex);
                    Log.d("DB", "row inserted, ID = " + c.getString(loginColIndex));
                    c.moveToNext();
                }
                c.close();
                dbHelper.close();

                MyTask myTask = new MyTask();
                myTask.execute(logins);

            }
        }
    };


    private class MyTask extends AsyncTask<String,String,List<UserLocation>> {

        @Override
        protected List<UserLocation> doInBackground(String... values) {

            if(Tokens.checkToken(sharedPreferences.getString(ACCESS_TOKEN, ""))<30)
                updateTokens(getActivity());
            List<UserLocation> userLocations = new ArrayList<>();
            try {
                Response<ResponseBody> response =   NetworkService.getInstance().getJSONApi().setLocation(
                        sharedPreferences.getString(ACCESS_TOKEN, ""),Double.valueOf(values[0]) ,Double.valueOf(values[1])).execute();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Response<List<UserLocation>> response =   NetworkService.getInstance().getJSONApi().getLocation(
                        sharedPreferences.getString(ACCESS_TOKEN, ""), Arrays.copyOfRange(values, 2, values.length)).execute();
                userLocations = response.body();
                Log.d("DB", "row wdwdwdwdw, ID = " +userLocations);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return userLocations;
        }

        @Override
        protected void onPostExecute(List<UserLocation> userLocations) {
            mGoogleMap.clear();
            if (userLocations!=null)
            for (int i = 0; i < userLocations.size(); i++) {
                if (userLocations.get(i).getLatitude() != null && userLocations.get(i).getLongitude() != null) {
                    LatLng latLng = new LatLng(userLocations.get(i).getLatitude(), userLocations.get(i).getLongitude());
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.title(userLocations.get(i).getLogin());
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                    mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);
                }
            }
        }
    }

    class DBHelper extends SQLiteOpenHelper {
        public DBHelper(Context context) {
            // конструктор суперкласса
            super(context, "myDB", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table mytable ("
                    + "id integer primary key autoincrement,"
                    + "login text unique,"
                    + "checked boolean" + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }


    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}