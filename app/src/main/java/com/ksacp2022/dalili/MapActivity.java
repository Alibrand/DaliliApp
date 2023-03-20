package com.ksacp2022.dalili;

import static android.widget.Toast.*;
import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import com.google.maps.android.data.Geometry;
import com.google.maps.android.data.geojson.GeoJsonFeature;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.google.maps.android.data.geojson.GeoJsonLineStringStyle;
import com.ksacp2022.dalili.databinding.ActivityMapBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapBinding binding;
    FirebaseFirestore firestore;
    FirebaseStorage storage;
    Location user_location;
    AppCompatButton directions, clear, walking, driving,show_steps;
    ImageButton next,previous;
    TextView instruction_text;
    FusedLocationProviderClient fusedLocationClient;
    LatLng selected_marker;
    GeoJsonLayer currentJsonLayer;
    ProgressDialog progressDialog;
    String profile = "driving-car";
    LinearLayoutCompat profiles;
    CardView summary_panel,instruction_window;
    TextView route_profile, distance, duration;
    int step_index=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);


        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        directions = findViewById(R.id.directions);
        clear = findViewById(R.id.clear);
        profiles = findViewById(R.id.profiles);
        walking = findViewById(R.id.walking);
        driving = findViewById(R.id.driving);
        route_profile = findViewById(R.id.route_profile);
        distance = findViewById(R.id.distance);
        duration = findViewById(R.id.duration);
        summary_panel = findViewById(R.id.summary);
        show_steps = findViewById(R.id.show_steps);
        next = findViewById(R.id.next);
        previous = findViewById(R.id.previous);
        instruction_text = findViewById(R.id.instruction);
        instruction_window = findViewById(R.id.instruction_window);
        





        directions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawShortRoute(new LatLng(user_location.getLatitude(), user_location.getLongitude()), selected_marker);
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.clear();
                load_places();
                clear.setVisibility(View.GONE);
                directions.setVisibility(View.GONE);
                profiles.setVisibility(View.GONE);
                summary_panel.setVisibility(View.GONE);
                directions.setEnabled(true);
                instruction_window.setVisibility(View.GONE);
            }
        });

        walking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profile = "foot-hiking";
                drawShortRoute(new LatLng(user_location.getLatitude(), user_location.getLongitude()), selected_marker);
            }
        });
        driving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profile = "driving-car";
                drawShortRoute(new LatLng(user_location.getLatitude(), user_location.getLongitude()), selected_marker);
            }
        });

        show_steps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                instruction_window.setVisibility(View.VISIBLE);
                show_current_step();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                step_index++;
                show_current_step();
            }
        });
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                step_index--;
                show_current_step();
            }
        });


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

        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.setTrafficEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        JSONObject object = new JSONObject();
        currentJsonLayer = new GeoJsonLayer(mMap, object);


        LatLng aseer = new LatLng(19.112718, 42.853666);

        CameraPosition cameraPosition = new CameraPosition(aseer, 8.0f, 0f, 0f);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        mMap.moveCamera(cameraUpdate);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(MapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 110);

            return;
        }

        mMap.setMyLocationEnabled(true);


        fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                user_location = location;
                CameraUpdate update;
                if (getIntent().getDoubleExtra("lat", 0.0) == 0.0)
                    update = CameraUpdateFactory.newLatLngZoom(
                            new LatLng(location.getLatitude(), location.getLongitude()), 15);
                else {
                    double lat = getIntent().getDoubleExtra("lat", location.getLatitude());
                    double lng = getIntent().getDoubleExtra("lng", location.getLongitude());
                    update = CameraUpdateFactory.newLatLngZoom(
                            new LatLng(lat, lng), 15);

                }
                mMap.animateCamera(update);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });


        load_places();


    }

    private void load_places() {
        progressDialog.setTitle("Loading Places");
        progressDialog.setMessage("Please Wait");
        progressDialog.show();

        firestore.collection("places")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        progressDialog.dismiss();
                        for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()
                        ) {
                            Map<String, Object> data = doc.getData();
                            GeoPoint place_location = (GeoPoint) data.get("location");
                            LatLng location = new LatLng(place_location.getLatitude(), place_location.getLongitude());
                            String image_url = data.get("image_url").toString();


                            //create custom marker for ever place
                            StorageReference ref = storage.getReference();
                            StorageReference image = ref.child("places_images/" + image_url);
                            GlideApp.with(MapActivity.this)
                                    .load(image)
                                    .apply(new RequestOptions().override(180, 180))
                                    .circleCrop()
                                    .into(new CustomTarget<Drawable>() {
                                        @Override
                                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                            Marker marker = mMap.addMarker(new MarkerOptions().position(location).title(data.get("name").toString()));
                                            marker.setTag(doc.getId());
                                            Drawable background = ContextCompat.getDrawable(MapActivity.this, R.drawable.ic_outline_circle_24);
                                            background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
                                            Drawable vectorDrawable = resource;

                                            vectorDrawable.setBounds(10, 10, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
                                            Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth() + 15, vectorDrawable.getIntrinsicHeight() + 15, Bitmap.Config.ARGB_8888);
                                            Canvas canvas = new Canvas(bitmap);
                                            Paint paint = new Paint();
                                            paint.setColor(Color.WHITE);
                                            paint.setStrokeWidth(1);
                                            canvas.drawCircle(canvas.getWidth() / 2, canvas.getHeight() / 2, 100, paint);
                                            //background.draw(canvas);
                                            vectorDrawable.draw(canvas);

                                            marker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
                                        }


                                        @Override
                                        public void onLoadCleared(@Nullable Drawable placeholder) {

                                        }
                                    });


                        }


                        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                            @Override
                            public void onMapClick(@NonNull LatLng latLng) {
                                directions.setVisibility(View.INVISIBLE);

                            }
                        });
                        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(@NonNull Marker marker) {
                                directions.setVisibility(View.VISIBLE);
                                selected_marker = marker.getPosition();
                                return false;
                            }
                        });

                        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                            @Override
                            public void onInfoWindowClick(@NonNull Marker marker) {
                                String place_id = marker.getTag().toString();
                                Intent intent = new Intent(MapActivity.this, PlacePageActivity.class);
                                intent.putExtra("place_id", place_id);
                                startActivity(intent);
                            }
                        });


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        makeText(MapActivity.this, "Failed to load places", LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                });
    }


    private void drawShortRoute(LatLng start, LatLng end) {
        progressDialog.setTitle("Finding Best Route");
        progressDialog.setMessage("Please Wait");
        progressDialog.show();
        step_index=0;
        instruction_window.setVisibility(View.GONE);

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://api.openrouteservice.org/v2/directions/" + profile + "/geojson";

        JSONObject postData = new JSONObject();
        JSONArray coordinates = new JSONArray();
        JSONArray start_point = new JSONArray();
        JSONArray end_point = new JSONArray();
        try {
            start_point.put(start.longitude);
            start_point.put(start.latitude);
            coordinates.put(start_point);
            end_point.put(end.longitude);
            end_point.put(end.latitude);
            coordinates.put(end_point);
            postData.put("coordinates", coordinates);
        } catch (JSONException e) {
            e.printStackTrace();
        }
// Request a string response from the provided URL.
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, url, postData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {


                        progressDialog.dismiss();
                        if (currentJsonLayer.isLayerOnMap())
                            currentJsonLayer.removeLayerFromMap();
                        currentJsonLayer = new GeoJsonLayer(mMap, response);
                        currentJsonLayer.addLayerToMap();
                        GeoJsonFeature feature = currentJsonLayer.getFeatures().iterator().next();
                        GeoJsonLineStringStyle lineStringStyle = new GeoJsonLineStringStyle();
                        lineStringStyle.setColor(Color.RED);
                        lineStringStyle.setWidth(35);
                        feature.setLineStringStyle(lineStringStyle);

                        if (feature.hasProperty("summary")) {
                            String summary = feature.getProperty("summary");
                            try {
                                JSONObject route_summary = new JSONObject(summary);
                                double totaldistance = route_summary.getDouble("distance");
                                double totalduration = route_summary.getDouble("duration");
                                summary_panel.setVisibility(View.VISIBLE);
                                duration.setText(duration_label(totalduration));
                                distance.setText(distance_label(totaldistance));
                                route_profile.setText(profile);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }




                        CameraUpdate update;
                        update = CameraUpdateFactory.newLatLngBounds(feature.getBoundingBox(), 220);

                        mMap.animateCamera(update);
                        clear.setVisibility(View.VISIBLE);
                        profiles.setVisibility(View.VISIBLE);
                        directions.setEnabled(false);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();

                // As of f605da3 the following should work
                try {
                    if (error.networkResponse != null) {

                        int statusCode = error.networkResponse.statusCode;
                        if (error.networkResponse.data != null) {

                            String body = new String(error.networkResponse.data, "UTF-8");
                            if (statusCode == 400) {


                                JSONObject obj = new JSONObject(body);
                                JSONObject error_obj = obj.getJSONObject("error");
                                String errorMsg = error_obj.getString("message");

                                // getting error msg message may be different according to your API
                                //Display this error msg to user
                                makeText(getApplicationContext(), errorMsg, LENGTH_SHORT).show();


                            }
                        }
                    }
                } catch (UnsupportedEncodingException | JSONException e) {
                    e.printStackTrace();
                    Log.e("TAG", "UNKNOWN ERROR :" + e.getMessage());
                    makeText(getApplicationContext(), "Something went Wrong!", LENGTH_SHORT).show();
                }

            }

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "5b3ce3597851110001cf62488310917010d3436c8a6e67dc7d6286c6");
                params.put("Content-Type", "application/json");

                return params;
            }
        };

// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private String distance_label(double distance) {
        if (distance < 1000)
            return distance + " m";
        else
            return (num_round(distance / 1000) + " Km");
    }

    private String duration_label(double duration) {
        if (duration < 60)
            return duration + " s";
        else if (duration < 3600)
            return Math.round(duration / 60) + " min";
        else if (duration < 86400)
            return Math.round(duration / 3600) + " h";
        else
            return Math.round(duration / 86400) + " Days";
    }

    private double num_round(double num) {
        return Math.round(num * 100.0) / 100.0;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 110) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                makeText(MapActivity.this, "We could not determine your location", LENGTH_LONG).show();
            }

        }
    }

    private void show_current_step(){
        GeoJsonFeature feature = currentJsonLayer.getFeatures().iterator().next();
        String segments=feature.getProperty("segments");
        Geometry geometry= feature.getGeometry();
        List<LatLng> coordinates= (List<LatLng>) geometry.getGeometryObject();
        try {
            JSONArray segmentsObject=new JSONArray(segments);
            JSONObject fsegment=segmentsObject.getJSONObject(0);
            JSONArray steps=fsegment.getJSONArray("steps");
            //JSONObject geometryObject=fsegment.getJSONObject("geometry");
           // JSONArray coordinates=geometryObject.getJSONArray("coordinates");
            if(step_index==steps.length())
                step_index=0;
            else  if (step_index<0)
                step_index=steps.length()-1;
            JSONObject cur_step=steps.getJSONObject(step_index);

            String instruct=cur_step.getString("instruction");
            JSONArray way_points=cur_step.getJSONArray("way_points");
            int index=way_points.getInt(0);
            LatLng point=coordinates.get(index);

            CameraUpdate update=CameraUpdateFactory.newLatLngZoom(point,18.0f);
            mMap.animateCamera(update);

            instruction_text.setText(instruct);



        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


}