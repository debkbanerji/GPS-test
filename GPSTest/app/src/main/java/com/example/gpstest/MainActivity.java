package com.example.gpstest;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LocationListener {

    LocationManager locationManager;
    List<PointOfInterest> pointList;
    PointOfInterest closest;

    TextView latitudeTextView;
    TextView longitudeTextView;
    TextView closestPointView;
    ListView pointsListView;

    List<String> summaryList;
    ArrayAdapter<String> summaryListAdapter;

    private static final String[] INITIAL_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
    };
    private static final int INITIAL_REQUEST = 1337;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Requesting permissions
        if (!hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            requestPermissions(INITIAL_PERMS, INITIAL_REQUEST);
        }

        //Getting views
        latitudeTextView = (TextView) findViewById(R.id.latitudeText);
        longitudeTextView = (TextView) findViewById(R.id.longitudeText);
        closestPointView = (TextView) findViewById(R.id.closestPointView);
        pointsListView = (ListView) findViewById(R.id.pointsListView);

        //Setting up adapter
        summaryList = new ArrayList<>();
        summaryListAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                android.R.id.text1, summaryList);
        pointsListView.setAdapter(summaryListAdapter);
        pointsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PointOfInterest pointOfInterest = pointList.get(position);
                openInMaps(pointOfInterest);
            }
        });


        //Setting listener for closest point click
        closestPointView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (closest != null) {
                    openInMaps(closest);
                }
            }
        });

        //populating pointList
        pointList = new ArrayList<>();
//        pointList.add(new PointOfInterest("Point A", 28.57031893, 77.26256392));
//        pointList.add(new PointOfInterest("Point B", 28.5706638, 77.26319753));
        pointList.add(new PointOfInterest("Introduction at gate", 28.525392, 77.186973));
        pointList.add(new PointOfInterest("Entering the Complex", 28.525392, 77.186696));
        pointList.add(new PointOfInterest("At the Mosque Entrance", 28.524791, 77.185605));
        pointList.add(new PointOfInterest("Inside the Mosque", 28.524785, 77.185329));
        pointList.add(new PointOfInterest("Iron Pillar", 28.524762, 77.184965));
        pointList.add(new PointOfInterest("Screen of Arches", 28.524739, 77.184848));
        pointList.add(new PointOfInterest("Iltutmish's Extension", 28.525053, 77.184867));
        pointList.add(new PointOfInterest("Alai Minar", 28.525817, 77.185269));
        pointList.add(new PointOfInterest("Tomb of Iltutmish", 28.525174, 77.184555));
        pointList.add(new PointOfInterest("Alauddin's Madrasa", 28.524308, 77.184372));
        pointList.add(new PointOfInterest("Qutab Minar", 28.524403, 77.185427));
        pointList.add(new PointOfInterest("Alai Darwaza", 28.524332, 77.185658));
        pointList.add(new PointOfInterest("Imam Zamin's Tomb", 28.524194, 77.185889));
        pointList.add(new PointOfInterest("Major Smith's Folly", 28.523969, 77.186543));
        pointList.add(new PointOfInterest("Sanderson's Sundial", 28.524226, 77.186365));
        pointList.add(new PointOfInterest("Mughal Sarai & Garden", 28.525630, 77.186524));
        pointList.add(new PointOfInterest("Metcalf's Follies", 28.524897, 77.187204));


        for (int i = 0; i < pointList.size(); i++) {
            PointOfInterest p = pointList.get(i);
            summaryList.add((i + 1) + ". " + p);
        }
        summaryListAdapter.notifyDataSetChanged();

        //getting GPS location from LocationManager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    500,   // Interval in milliseconds
                    10, this);
        } catch (SecurityException e) {
            Toast.makeText(getBaseContext(), "Security exception: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    private void openInMaps(PointOfInterest pointOfInterest) {
        double latitude = pointOfInterest.getLatitude();
        double longitude = pointOfInterest.getLongitude();
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude + "&mode=w");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }


    @Override
    public void onLocationChanged(Location location) {
        latitudeTextView.setText("Current Latitude: " + location.getLatitude());
        longitudeTextView.setText("Current Longitude: " + location.getLongitude());

        summaryList.clear();

        closest = pointList.get(0);
        for (int i = 0; i < pointList.size(); i++) {
            PointOfInterest p = pointList.get(i);
            summaryList.add((i + 1) + ". " + p.distanceSummary(location));
            if (closest.getDistance(location) > p.getDistance(location)) {
                closest = p;
            }
        }
//        for (PointOfInterest p : pointList) {
//            summaryList.add(p.distanceSummary(location));
//            if (closest.getDistance(location) > p.getDistance(location)) {
//                closest = p;
//            }
//        }
//        Toast.makeText(getBaseContext(), "Refreshed data", Toast.LENGTH_LONG).show();
        summaryListAdapter.notifyDataSetChanged();
        closestPointView.setText(closest.distanceSummary(location));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(getBaseContext(), "Gps turned on ", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(getBaseContext(), "Gps turned off ", Toast.LENGTH_LONG).show();
    }

    private boolean hasPermission(String perm) {
        return (PackageManager.PERMISSION_GRANTED == checkSelfPermission(perm));
    }


}
