package com.example.haritacalismalari;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    static ArrayList<String> names  = new ArrayList<String>(  );
    static ArrayList<LatLng> locations = new ArrayList<LatLng>();
    static ArrayAdapter arrayAdapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate( R.menu.yer_ekle,menu );
        return super.onCreateOptionsMenu( menu );
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.yer_ekle){
            Intent intent = new Intent( MainActivity.this,MapsActivity.class );
            intent.putExtra( "info" ,"new");
            startActivity( intent );
            finish();

        }
        return super.onOptionsItemSelected( item );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        listView = findViewById( R.id.listview );

        try {

            MapsActivity.database = this.openOrCreateDatabase( "Places",MODE_PRIVATE,null );
            Cursor cursor = MapsActivity.database.rawQuery( "SELECT * FROM places ",null );

            int nameIx = cursor.getColumnIndex( "name" );
            int latitudeIx = cursor.getColumnIndex( "latitude" );
            int longitudeIx = cursor.getColumnIndex( "longitude" );

           // cursor.moveToFirst();

            while (cursor.moveToNext()){

                String nameFromDatabase = cursor.getString( nameIx );
                String latitudeFromDatabase = cursor.getString( latitudeIx );
                String longitudeFromDatabase= cursor.getString( longitudeIx );

                names.add( nameFromDatabase );

                Double l1 = Double.parseDouble( latitudeFromDatabase );
                Double l2 = Double.parseDouble( longitudeFromDatabase );

                LatLng locationFromDatabase  = new LatLng( l1,l2 );

                locations.add( locationFromDatabase );
            }
            cursor.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        arrayAdapter = new ArrayAdapter( this,android.R.layout.simple_list_item_1,names );
        listView.setAdapter( arrayAdapter );

        

        listView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent( MainActivity.this,MapsActivity.class );
                intent.putExtra( "info","old" );
                intent.putExtra( "position",position );
                startActivity( intent );
                finish();
            }
        } );
    }
}
