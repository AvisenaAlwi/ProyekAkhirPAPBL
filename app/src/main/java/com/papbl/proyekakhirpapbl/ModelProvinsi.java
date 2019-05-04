package com.papbl.proyekakhirpapbl;

import com.google.android.gms.maps.model.LatLng;

public class ModelProvinsi {
    private String nama;
    private String deskripsi;
    private double berasPerKg,jagungPerKg,gandumPerKg,dagingPerkg;
    private LatLng lokasi;

    public ModelProvinsi (){

    }

    public ModelProvinsi(String nama, String deskripsi, double berasPerKg, double jagungPerKg, double gandumPerKg, double dagingPerkg, LatLng lokasi) {
        this.nama = nama;
        this.deskripsi = deskripsi;
        this.berasPerKg = berasPerKg;
        this.jagungPerKg = jagungPerKg;
        this.gandumPerKg = gandumPerKg;
        this.dagingPerkg = dagingPerkg;
        this.lokasi = lokasi;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public double getBerasPerKg() {
        return berasPerKg;
    }

    public void setBerasPerKg(double berasPerKg) {
        this.berasPerKg = berasPerKg;
    }

    public double getJagungPerKg() {
        return jagungPerKg;
    }

    public void setJagungPerKg(double jagungPerKg) {
        this.jagungPerKg = jagungPerKg;
    }

    public double getGandumPerKg() {
        return gandumPerKg;
    }

    public void setGandumPerKg(double gandumPerKg) {
        this.gandumPerKg = gandumPerKg;
    }

    public double getDagingPerkg() {
        return dagingPerkg;
    }

    public void setDagingPerkg(double dagingPerkg) {
        this.dagingPerkg = dagingPerkg;
    }

    public LatLng getLokasi() {
        return lokasi;
    }

    public void setLokasi(LatLng lokasi) {
        this.lokasi = lokasi;
    }
}
