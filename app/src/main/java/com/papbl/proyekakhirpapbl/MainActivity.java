package com.papbl.proyekakhirpapbl;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Toolbar toolbar;
    private GoogleMap gMap;
    private RecyclerView rvProvinsi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        rvProvinsi = findViewById(R.id.rv);

        setSupportActionBar(toolbar);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ArrayList<ModelProvinsi> data = new ArrayList<>();
        data.add(new ModelProvinsi("Provinsi A", "", 0, 0, 0, 0,null));
        data.add(new ModelProvinsi("Provinsi B", "", 0, 0, 0, 0,null));
        data.add(new ModelProvinsi("Provinsi C", "", 0, 0, 0, 0,null));
        data.add(new ModelProvinsi("Provinsi D", "", 0, 0, 0, 0,null));

        RecyclerViewAdapter adapter = new RecyclerViewAdapter(data);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        rvProvinsi.setLayoutManager(lm);
        rvProvinsi.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rvProvinsi.setAdapter(adapter);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap =  googleMap;

    }


}
