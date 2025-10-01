package com.praktikum.whitebox.service;

public class KalkulatorDiskon {
    public double hitungDiskon(double harga, int kuantitas, String tipePelanggan) {
        if (harga <= 0 || kuantitas <= 0) {
            throw new IllegalArgumentException("Harga dan kuantitas harus positif");
        }
        double diskon = 0.0;
// Diskon berdasarkan kuantitas
        if (kuantitas >= 100) {
            diskon += 0.20; // 20%
        } else if (kuantitas >= 50) {
            diskon += 0.15; // 15%
        } else if (kuantitas >= 10) {
            diskon += 0.10; // 10%
        } else if (kuantitas >= 5) {
            diskon += 0.05; // 5%
        }
// Diskon berdasarkan tipe pelanggan
        if ("PREMIUM".equalsIgnoreCase(tipePelanggan)) {
            diskon += 0.10; // 10% tambahan
        } else if ("REGULER".equalsIgnoreCase(tipePelanggan)) {
            diskon += 0.05; // 5% tambahan
        } else if ("BARU".equalsIgnoreCase(tipePelanggan)) {
            diskon += 0.02; // 2% tambahan
        }
// Maksimal diskon 30%
        diskon = Math.min(diskon, 0.30);
        return harga * kuantitas * diskon;
    }
    public double hitungHargaSetelahDiskon(double harga, int kuantitas, String tipePelanggan) {
        double totalSebelumDiskon = harga * kuantitas;
        double diskon = hitungDiskon(harga, kuantitas, tipePelanggan);
        return totalSebelumDiskon - diskon;
    }
    public String getKategoriDiskon(double persentaseDiskon) {
        if (persentaseDiskon <= 0) {
            return "TANPA_DISKON";
        } else if (persentaseDiskon < 0.10) {
            return "DISKON_RINGAN";
        } else if (persentaseDiskon < 0.20) {
            return "DISKON_SEDANG";
        } else {
            return "DISKON_BESAR";
        }
    }
}