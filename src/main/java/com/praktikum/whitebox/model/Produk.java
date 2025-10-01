package com.praktikum.whitebox.model;
import java.util.Objects;

public class Produk {
    private String kode;
    private String nama;
    private String kategori;
    private double harga;
    private int stok;
    private int stokMinimum;
    private boolean aktif;
    public Produk() {}
    public Produk(String kode, String nama, String kategori, double
            harga, int stok, int stokMinimum) {
        this.kode = kode;
        this.nama = nama;
        this.kategori = kategori;
        this.harga = harga;
        this.stok = stok;
        this.stokMinimum = stokMinimum;
        this.aktif = true;
    }
    // Getters and Setters
    public String getKode() { return kode; }
    public void setKode(String kode) { this.kode = kode; }
    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }
    public String getKategori() { return kategori; }
    public void setKategori(String kategori) { this.kategori = kategori;
    }
    public double getHarga() { return harga; }
    public void setHarga(double harga) { this.harga = harga; }
    public int getStok() { return stok; }
    public void setStok(int stok) { this.stok = stok; }
    public int getStokMinimum() { return stokMinimum; }
    public void setStokMinimum(int stokMinimum) { this.stokMinimum =
            stokMinimum; }
    public boolean isAktif() { return aktif; }
    public void setAktif(boolean aktif) { this.aktif = aktif; }
    // Business Logic Methods
    public boolean isStokHabis() {
        return stok == 0;
    }
    public boolean isStokMenipis() {
        return stok > 0 && stok <= stokMinimum;
    }
    public boolean isStokAman() {
        return stok > stokMinimum;
    }
    public void kurangiStok(int jumlah) {
        if (jumlah <= 0) {
            throw new IllegalArgumentException("Jumlah harus positif");
        }
        if (jumlah > stok) {
            throw new IllegalArgumentException("Stok tidak mencukupi");
        }
        this.stok -= jumlah;
    }
    public void tambahStok(int jumlah) {
        if (jumlah <= 0) {
            throw new IllegalArgumentException("Jumlah harus positif");
        }
        this.stok += jumlah;
    }
    public double hitungTotalHarga(int jumlah) {
        if (jumlah <= 0) {
            throw new IllegalArgumentException("Jumlah harus positif");
        }
        return harga * jumlah;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Produk produk = (Produk) o;
        return Objects.equals(kode, produk.kode);
    }
    @Override
    public int hashCode() {
        return Objects.hash(kode);
    }
    @Override
    public String toString() {
        return "Produk{" +
                "kode='" + kode + '\'' +
                ", nama='" + nama + '\'' +
                ", kategori='" + kategori + '\'' +
                ", harga=" + harga +
                ", stok=" + stok +
                ", stokMinimum=" + stokMinimum +
                ", aktif=" + aktif +
                '}';
    }
}