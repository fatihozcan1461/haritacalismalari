package com.example.haritacalismalari;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    static SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_maps );
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById( R.id.map );
        mapFragment.getMapAsync( this );
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

         Intent intent = getIntent();
         String info = intent.getStringExtra( "info" );
         if (info.matches( "new" )) {

             locationManager = (LocationManager) this.getSystemService( Context.LOCATION_SERVICE );
             locationListener = new LocationListener() {
                 @Override
                 public void onLocationChanged(Location location) {

                     SharedPreferences sharedPreferences = MapsActivity.this.getSharedPreferences( "com.example.haritacalismalari", MODE_PRIVATE );
                     boolean firstTimeCheck = sharedPreferences.getBoolean( "ilk kullanışı deği ", false );

                     if (!firstTimeCheck) {
                         LatLng veri_kullanici_yeri = new LatLng( location.getLatitude(), location.getLongitude() );
                         mMap.moveCamera( CameraUpdateFactory.newLatLngZoom( veri_kullanici_yeri, 15 ) );
                         sharedPreferences.edit().putBoolean( "ilk kullanışı değil", false ).apply();
                     }
                /*mMap.clear();

                LatLng kullaniciyeri = new LatLng(location.getLatitude(),location.getLongitude());
                mMap.addMarker(new MarkerOptions().position(kullaniciyeri).title("Kullanıcı Yeri"));
                mMap.moveCamera( CameraUpdateFactory.newLatLngZoom(kullaniciyeri,12));*/

                     Geocoder geocoder = new Geocoder( getApplicationContext(), Locale.getDefault() );
                     try {
                         List<Address> adreslist = geocoder.getFromLocation( location.getLatitude(), location.getLongitude(), 2 );
                         if (adreslist != null && adreslist.size() > 0) {
                             System.out.println( "address: " + adreslist.get( 0 ).toString() + adreslist.get( 1 ).toString() );
                         }

                     } catch (IOException e) {
                         e.printStackTrace();
                     }

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
             };

         /*

        if(ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions( this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1 );
        }else{
            locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER,5000,50,locationListener );
        }
         */

             if (Build.VERSION.SDK_INT >= 23) {

                 if (checkSelfPermission( Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
                     requestPermissions( new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1 );
                 } else {
                     locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 5000, 50, locationListener );

                     mMap.clear();

                     Location lastLocation = locationManager.getLastKnownLocation( LocationManager.GPS_PROVIDER );
                     LatLng userLastLocation = new LatLng( lastLocation.getLatitude(), lastLocation.getLongitude() );
                     mMap.addMarker( new MarkerOptions().title( "Olduğun Yer " ).position( userLastLocation ) );
                     mMap.moveCamera( CameraUpdateFactory.newLatLngZoom( userLastLocation, 15 ) );
                 }

             } else {
                 locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 5000, 50, locationListener );

                 Location lastLocation = locationManager.getLastKnownLocation( LocationManager.GPS_PROVIDER );
                 LatLng userLastLocation = new LatLng( lastLocation.getLatitude(), lastLocation.getLongitude() );
                 mMap.addMarker( new MarkerOptions().title( "Olduğun Yer " ).position( userLastLocation ) );
                 mMap.moveCamera( CameraUpdateFactory.newLatLngZoom( userLastLocation, 15 ) );
             }

             mMap.setOnMapLongClickListener( this );
         }else{
             mMap.clear();
             int position = intent.getIntExtra( "position",0 );
             LatLng location = new LatLng( MainActivity.locations.get( position ).latitude,MainActivity.locations.get( position ).longitude );
             String placeName = MainActivity.names.get( position );

             mMap.addMarker( new MarkerOptions().title( placeName ).position( location ) );
             mMap.moveCamera( CameraUpdateFactory.newLatLngZoom( location,15 ) );

         }





    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0){
            if (requestCode == 1){
                if (ContextCompat.checkSelfPermission( this,Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED){
                    locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER,1100,110,locationListener );

                    Intent intent = getIntent();
                    String info = intent.getStringExtra( "info" );

                    if (info.matches( "new" )){
                        Location lastLocation = locationManager.getLastKnownLocation( LocationManager.GPS_PROVIDER );
                        LatLng userLastLocation = new LatLng( lastLocation.getLatitude(),lastLocation.getLongitude() );
                        if (userLastLocation != null ){

                            mMap.addMarker( new MarkerOptions().title( "Olduğun Yer " ).position( userLastLocation ) );
                            mMap.moveCamera( CameraUpdateFactory.newLatLngZoom(userLastLocation,15  ) );
                        }
                    }else{
                        mMap.clear();
                        int position = intent.getIntExtra( "position",0 );
                        LatLng location = new LatLng( MainActivity.locations.get( position ).latitude,MainActivity.locations.get( position ).longitude );
                        String placeName = MainActivity.names.get( position );

                        mMap.addMarker( new MarkerOptions().title( placeName ).position( location ) );
                        mMap.moveCamera( CameraUpdateFactory.newLatLngZoom( location,15 ) );
                    }

                }
            }
        }
        super.onRequestPermissionsResult( requestCode, permissions, grantResults );
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        Geocoder geocoder = new Geocoder( getApplicationContext(), Locale.getDefault() );

        String address = "";

        try {
            List<Address> adresList = geocoder.getFromLocation( latLng.latitude,latLng.longitude,1 );

            if (adresList != null && adresList.size() > 0){
                //tıklanan yerin boş olup olmadığın bakıyor.deniz çöl gibi alanlarda boş çıkacağı için bu kontrolden geçirmek zorundayız.
                if (adresList.get( 0 ).getThoroughfare() != null){
                    address += adresList.get(0).getThoroughfare();

                    if(adresList.get(0).getThoroughfare() != null  ){
                        address += adresList.get( 0 ).getSubThoroughfare();
                    }
                }
            }else{
                address = "New Places";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (address.matches( "" )){
            address = "No Address";
        }
        mMap.addMarker( new MarkerOptions().title( address ).position( latLng ) );
        Toast.makeText( getApplicationContext(),"Yeni Yer Oluşturuldu",Toast.LENGTH_SHORT ).show();

        MainActivity.names.add( address );
        MainActivity.locations.add( latLng );
        MainActivity.arrayAdapter.notifyDataSetChanged();

        try{

            Double l1 = latLng.latitude;
            Double l2 = latLng.longitude;

            String coord1 = l1.toString();
            String coord2 = l2.toString();

            database = this.openOrCreateDatabase( "Places",MODE_PRIVATE,null );

            database.execSQL( "CREATE TABLE IF NOT EXISTS places(name VARCHAR,latitude VARCHAR,longitude VARCHAR)" );

            String toCompile = "INSERT INTO places (name,latitude,longitude) VALUES (?,?,?)";

            SQLiteStatement sqLiteStatement = database.compileStatement( toCompile );

            sqLiteStatement.bindString( 1,address);
            sqLiteStatement.bindString( 2,coord1);
            sqLiteStatement.bindString( 3,coord2);

            sqLiteStatement.execute();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
