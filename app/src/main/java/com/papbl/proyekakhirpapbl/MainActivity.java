package com.papbl.proyekakhirpapbl;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.papbl.proyekakhirpapbl.model.Agen;
import com.papbl.proyekakhirpapbl.model.Sembako;
import com.papbl.proyekakhirpapbl.model_api.ModelAPIAgens;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

import io.nlopez.smartlocation.SmartLocation;
import io.reactivex.disposables.Disposable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMapClickListener {

    private final String TAG = "Main Activity";
    private final int STATE_LOADING = 1;
    private final int STATE_REQUIRE_LOCATION_PERMISSION = 2;
    private final int STATE_FAILED_FETCH_DATA = 3;
    private final int STATE_READY = 4;

    private GoogleMap gMap;
    private String cityName = "--";
    private Button btnRefresh;
    private LinearLayout containerDetailContent;
    private List<Agen> agens;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_main);

        // Mendifinisikan linear layout yang nantinya dipake buat
        containerDetailContent = findViewById(R.id.container_sembako);
        // Mendefinisikan tombol refresh izin akses lokasi
        btnRefresh = findViewById(R.id.btn_refresh);

        // Mendapatkan fragment Google Maps
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null)
            // Untuk mendapatkan maps secara asynchronus
            mapFragment.getMapAsync(this);

    }

    /**
     * Dipanggil ketika map sudah siap
     *
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        gMap.getUiSettings().setCompassEnabled(true);
        gMap.setOnMarkerClickListener(this);
        gMap.setOnMapClickListener(this);

        // Untuk meminta izin akses lokasi jika belum diizinkan
        checkAndRequestPermissionLocation();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;

        gMap.setMyLocationEnabled(true);
        gMap.getUiSettings().setMyLocationButtonEnabled(true);


        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Jika GPS tidak aktif, maka akan menampilkan dialog untuk mengaktifkannya
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }
    }

    /**
     * Method untuk meminta izin akses lokasi, jika sudah maka akan memanggil detectLocation();
     */
    public void checkAndRequestPermissionLocation() {
        setStateBottomSheet(STATE_LOADING);
        Disposable result =
                new RxPermissions(this)
                        .requestEachCombined(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                        .subscribe(permission -> { // will emit 2 Permission objects
                            if (permission.granted) {
                                // `permission.name` is granted !
                                setStateBottomSheet(STATE_READY);
                                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                                    return;

                                gMap.setMyLocationEnabled(true);
                                gMap.getUiSettings().setMyLocationButtonEnabled(true);
                                detectLocation();
                            } else if (permission.shouldShowRequestPermissionRationale) {
                                // Denied permission without ask never again
                                setStateBottomSheet(STATE_REQUIRE_LOCATION_PERMISSION);
                                showReqeustPermissionMessage();
                            } else {
                                setStateBottomSheet(STATE_REQUIRE_LOCATION_PERMISSION);
                                showReqeustPermissionMessage();
                            }
                        });
    }

    /**
     * Method untuk mendeteksi posisi device saat ini, lalu dari situ mengambil alamatnya dan mengambil
     * SubAdminArea atau disini sama dengan Kota-nya
     */
    private void detectLocation() {
        final SmartLocation smartLocation = SmartLocation.with(this);

        smartLocation.location()
                .oneFix()
                .start(location -> {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 12.4f);
                    gMap.animateCamera(cameraUpdate);

                    smartLocation.geocoding()
                            .reverse(location, (location1, addressList) -> {
                                if (!addressList.isEmpty()) {
                                    Address bestResult = addressList.get(0);
                                    cityName = bestResult.getSubAdminArea();
                                    TextView tvLocation = findViewById(R.id.tvLocation);
                                    tvLocation.setText(cityName);

                                    setStateFetchData(STATE_LOADING, null);
                                    getAndShowAgensFromCityName(cityName);
                                }
                            });
                });
    }


    /**
     * Untuk mengambil data dari Restfull API server menggunakan library retrofit
     *
     * @param cityName
     */
    private void getAndShowAgensFromCityName(String cityName) {
        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("http://10.0.2.2/papbl-api/")
                .baseUrl("http://sendtome.000webhostapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        APIInterface apiInterface = retrofit.create(APIInterface.class);

        Call<ModelAPIAgens> modelAPIAgensCall = apiInterface.getAgensByCityName(cityName);
        modelAPIAgensCall.enqueue(new Callback<ModelAPIAgens>() {
            @Override
            public void onResponse(Call<ModelAPIAgens> call, Response<ModelAPIAgens> response) {
                if (response.body() != null) {
                    ModelAPIAgens modelAPIAgens = response.body();
                    agens = modelAPIAgens.data;
                    btnRefresh.setVisibility(View.GONE);
                    addAgensToMarker(agens);
                    setStateFetchData(STATE_READY, null);
                } else {
                    setStateFetchData(STATE_READY, "Tidak ada data untuk <b>" + cityName + "</b>");
                }
            }

            @Override
            public void onFailure(Call<ModelAPIAgens> call, Throwable t) {
                Log.e(TAG, t.getMessage());
                setStateFetchData(STATE_FAILED_FETCH_DATA, null);
            }
        });
    }

    /**
     * Jadikan marker dari list Agens
     *
     * @param agens
     */
    private void addAgensToMarker(@NonNull List<Agen> agens) {
        if (gMap != null) {
            gMap.clear();
            for (int i = 0; i < agens.size(); i++) {
                Agen agen = agens.get(i);
                MarkerOptions marker = new MarkerOptions()
                        .position(agen.getLokasi())
                        .snippet(agen.getAlamat())
                        .title(agen.getNama());
                gMap.addMarker(marker);
            }
        }
    }

    /**
     * Callback onClick pada view
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_require_permission:
                checkAndRequestPermissionLocation();
                break;
            case R.id.btn_refresh:
                setStateFetchData(STATE_LOADING, null);
                getAndShowAgensFromCityName(cityName);
                break;
        }
    }

    /**
     * Callback ketika marker pada maps diklik
     *
     * @param marker
     * @return
     */
    @Override
    public boolean onMarkerClick(Marker marker) {
        int index = -1;
        for (int i = 0; i < agens.size(); i++)
            if (agens.get(i).getNama().equals(marker.getTitle())) index = i;

        Agen agen = agens.get(index);
        containerDetailContent.removeAllViewsInLayout();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = 30;

        TextView t = new TextView(this);
        t.setText(Html.fromHtml("<b>" + agen.getNama() + "</b>"));
        t.setTextSize(20);
        t.setLayoutParams(params);
        containerDetailContent.addView(t);

        TextView tt = new TextView(this);
        tt.setText(Html.fromHtml("Alamat : <b>" + agen.getAlamat() + "</b>"));
        tt.setTextSize(16);
        tt.setLayoutParams(params);
        containerDetailContent.addView(tt);

        TextView ttt = new TextView(this);
        ttt.setText(Html.fromHtml("Daftar harga sembako di agen <b>" + agen.getNama() + "</b> : "));
        ttt.setTextSize(16);
        ttt.setLayoutParams(params);
        containerDetailContent.addView(ttt);

        for (Sembako sembako : agen.getSembako()) {
            TextView textView = new TextView(this);
            textView.setTextSize(15);
            textView.setInputType(EditorInfo.TYPE_TEXT_FLAG_CAP_WORDS);

            DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
            DecimalFormatSymbols formatRp = new DecimalFormatSymbols();
            formatRp.setCurrencySymbol("Rp. ");
            formatRp.setMonetaryDecimalSeparator(',');
            formatRp.setGroupingSeparator('.');
            kursIndonesia.setDecimalFormatSymbols(formatRp);
            String text = sembako.getNama() + " : <b>" + kursIndonesia.format(sembako.getHarga()) + "/" + sembako.getUnit() + "</b>";
            textView.setText(Html.fromHtml(text));
            textView.setLayoutParams(params);

            containerDetailContent.addView(textView);
        }
        findViewById(R.id.layout_success_fetch_data).setVisibility(View.GONE);
        return false;
    }

    /**
     * Callback ketika maps diklik
     *
     * @param latLng
     */
    @Override
    public void onMapClick(LatLng latLng) {
        containerDetailContent.removeAllViewsInLayout();
        if (!agens.isEmpty())
            findViewById(R.id.layout_success_fetch_data).setVisibility(View.VISIBLE);
    }

    /**
     * Menampilkan pesan jika izin lokasi belum didapatkan
     */
    public void showReqeustPermissionMessage() {
        new AlertDialog.Builder(this)
                .setTitle("Kami membutuhkan izin Anda")
                .setMessage("Kami membutuhkan akses lokasi Anda untuk fitur pada aplikasi ini")
                .setPositiveButton("OK", (dialog, which) -> {
                    checkAndRequestPermissionLocation();
                })
                .setNegativeButton("BATAL", null)
                .show();
    }

    /**
     * Tampilkan pesan ketika gps tidak aktif
     */
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("GPS Anda kelihatannya tidak aktif, mau Anda aktifkan ?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> {
                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                })
                .setNegativeButton("No", (dialog, id) -> dialog.cancel());
        final AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Method ini untuk bergantian state pada bottom sheet
     *
     * @param state
     */
    private void setStateBottomSheet(int state) {
        ConstraintLayout loadingView = findViewById(R.id.loading_view);
        ConstraintLayout requirePermissionView = findViewById(R.id.require_permissionz_view);
        ConstraintLayout readyView = findViewById(R.id.ready_view);
        loadingView.setVisibility(View.GONE);
        requirePermissionView.setVisibility(View.GONE);
        readyView.setVisibility(View.GONE);
        switch (state) {
            case STATE_LOADING:
                loadingView.setVisibility(View.VISIBLE);
                break;
            case STATE_REQUIRE_LOCATION_PERMISSION:
                requirePermissionView.setVisibility(View.VISIBLE);
                break;
            case STATE_READY:
                readyView.setVisibility(View.VISIBLE);
                break;
        }
    }

    /**
     * Method ini untuk bergantian state status akses data dari restfull API server
     *
     * @param state
     * @param message
     */
    private void setStateFetchData(int state, String message) {
        LinearLayout layoutLoading = findViewById(R.id.layout_loading_fetch_data);
        LinearLayout layoutFailed = findViewById(R.id.layout_failed_fetch_data);
        LinearLayout layoutSuccess = findViewById(R.id.layout_success_fetch_data);
        layoutLoading.setVisibility(View.GONE);
        layoutFailed.setVisibility(View.GONE);
        layoutSuccess.setVisibility(View.GONE);
        switch (state) {
            case STATE_LOADING:
                layoutLoading.setVisibility(View.VISIBLE);
                break;
            case STATE_FAILED_FETCH_DATA:
                layoutFailed.setVisibility(View.VISIBLE);
                break;
            case STATE_READY:
                if (message != null)
                    ((TextView) findViewById(R.id.tv_success)).setText(Html.fromHtml(message));
                else
                    ((TextView) findViewById(R.id.tv_success)).setText("Klik marker pada peta untuk melihat detailnya.");
                layoutSuccess.setVisibility(View.VISIBLE);
                break;
        }
    }
}
