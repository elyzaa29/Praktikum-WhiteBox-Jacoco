package com.praktikum.whitebox.model;

import java.util.Objects;

public class Kategori {
    private String idKategori;
    private String namaKategori;
    private String deskripsi;

    // Constructor
    public Kategori(String idKategori, String namaKategori, String deskripsi) {
        this.idKategori = idKategori;
        this.namaKategori = namaKategori;
        this.deskripsi = deskripsi;
    }

    // Getters & Setters
    public String getIdKategori() {
        return idKategori;
    }

    public void setIdKategori(String idKategori) {
        this.idKategori = idKategori;
    }

    public String getNamaKategori() {
        return namaKategori;
    }

    public void setNamaKategori(String namaKategori) {
        this.namaKategori = namaKategori;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    // Equals & hashCode berdasarkan idKategori
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Kategori)) return false;
        Kategori kategori = (Kategori) o;
        return Objects.equals(idKategori, kategori.idKategori);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idKategori);
    }

    @Override
    public String toString() {
        return "Kategori{" +
                "idKategori='" + idKategori + '\'' +
                ", namaKategori='" + namaKategori + '\'' +
                ", deskripsi='" + deskripsi + '\'' +
                '}';
    }
}
