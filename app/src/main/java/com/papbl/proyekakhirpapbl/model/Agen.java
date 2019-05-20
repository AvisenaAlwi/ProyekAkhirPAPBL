package com.papbl.proyekakhirpapbl.model;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class Agen {
    private int id, id_city;
    private String nama;
    private String alamat;
    private List<Sembako> sembako;
    private LatLng lokasi;

    public Agen() {

    }

    public Agen(int id_city, String nama, String alamat, List<Sembako> sembako, LatLng lokasi) {
        this.id_city = id_city;
        this.nama = nama;
        this.alamat = alamat;
        this.sembako = sembako;
        this.lokasi = lokasi;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_city() {
        return id_city;
    }

    public void setId_city(int id_city) {
        this.id_city = id_city;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public List<Sembako> getSembako() {
        return sembako;
    }

    public void setSembako(List<Sembako> sembako) {
        this.sembako = sembako;
    }

    public LatLng getLokasi() {
        return lokasi;
    }

    public void setLokasi(LatLng lokasi) {
        this.lokasi = lokasi;
    }
}
