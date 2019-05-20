package com.papbl.proyekakhirpapbl.model;

public class Sembako {
    private String nama, unit;
    private int harga;

    public Sembako() {

    }

    public Sembako(String nama, int harga, String unit) {
        this.nama = nama;
        this.harga = harga;
        this.unit = unit;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getHarga() {
        return harga;
    }

    public void setHarga(int harga) {
        this.harga = harga;
    }
}
