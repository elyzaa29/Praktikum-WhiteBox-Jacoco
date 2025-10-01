package com.praktikum.whitebox.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.*;
@DisplayName("Test Kalkulator Diskon - Path Coverage")
public class KalkulatorDiskonTest {
    private KalkulatorDiskon kalkulatorDiskon;
    @BeforeEach
    void setUp() {
        kalkulatorDiskon = new KalkulatorDiskon();
    }
//Anotasi untuk menjalankan tes dengan berbagai parameter
    @ParameterizedTest
    @DisplayName("Test hitung diskon - berbagai kombinasi kuantitas dan tipe pelanggan")
    //Anotasi untuk menyediakan data parameterized test dari sumber CSV
    @CsvSource({
// kuantitas, tipePelanggan, expectedDiskon
            "1, 'BARU', 20", // 2% dari 1000
            "5, 'BARU', 350", // 5% + 2% = 7% dari 1000*5
            "10, 'REGULER', 1500", // 10% + 5% = 15% dari 1000*10
            "50, 'PREMIUM', 12500", // 15% + 10% = 25% dari 1000*50
            "100, 'PREMIUM', 30000",// 20% + 10% = 30% (maksimal) dari 1000*100
            "200, 'PREMIUM', 60000" // 20% + 10% = 30% (maksimal) dari 1000*200
    })
    void testHitungDiskonVariousCases(int kuantitas, String
            tipePelanggan, double expectedDiskon) {
        double harga = 1000;
        double diskon = kalkulatorDiskon.hitungDiskon(harga, kuantitas,
                tipePelanggan);
        assertEquals(expectedDiskon, diskon, 0.001);
    }
    @Test
    @DisplayName("Test hitung diskon - parameter invalid")
    void testHitungDiskonInvalidParameters() {
// Harga negatif
        Exception exception =
                assertThrows(IllegalArgumentException.class, () -> {
                    kalkulatorDiskon.hitungDiskon(-1000, 5, "REGULER");
                });
        assertEquals("Harga dan kuantitas harus positif",
                exception.getMessage());
// Kuantitas nol
        exception = assertThrows(IllegalArgumentException.class, () -> {
            kalkulatorDiskon.hitungDiskon(1000, 0, "REGULER");
        });
        assertEquals("Harga dan kuantitas harus positif",
                exception.getMessage());
    }
    @Test
    @DisplayName("Test hitung harga setelah diskon")
    void testHitungHargaSetelahDiskon() {
        double harga = 1000;
        int kuantitas = 10;
        String tipePelanggan = "REGULER";
        double hargaSetelahDiskon =
                kalkulatorDiskon.hitungHargaSetelahDiskon(harga, kuantitas,
                        tipePelanggan);
        double expectedTotal = 1000 * 10; // 10000
        double expectedDiskon = 10000 * 0.15; // 1500 (10% + 5%)
        double expectedHargaAkhir = expectedTotal - expectedDiskon; //8500
        assertEquals(expectedHargaAkhir, hargaSetelahDiskon, 0.001);
    }
    //Anotasi untuk menjalankan tes dengan berbagai parameter
    @ParameterizedTest
    @DisplayName("Test kategori diskon berdasarkan persentase")
//Anotasi untuk menyediakan data parameterized test dari sumber CSV
    @CsvSource({
            "0.0, 'TANPA_DISKON'",
            "0.05, 'DISKON_RINGAN'",
            "0.09, 'DISKON_RINGAN'",
            "0.10, 'DISKON_SEDANG'",
            "0.15, 'DISKON_SEDANG'",
            "0.19, 'DISKON_SEDANG'",
            "0.20, 'DISKON_BESAR'",
            "0.25, 'DISKON_BESAR'",
            "0.30, 'DISKON_BESAR'"
    })
    void testGetKategoriDiskon(double persentaseDiskon, String
            expectedKategori) {
        String kategori =
                kalkulatorDiskon.getKategoriDiskon(persentaseDiskon);
        assertEquals(expectedKategori, kategori);
    }
    @Test
    @DisplayName("Test boundary values untuk kuantitas diskon")
    void testBoundaryValuesKuantitas() {
        double harga = 1000;
// Boundary: 4 -> 5 (mulai dapat diskon 5%)
        double diskon4 = kalkulatorDiskon.hitungDiskon(harga, 4, "BARU");
        double diskon5 = kalkulatorDiskon.hitungDiskon(harga, 5, "BARU");
        assertTrue(diskon5 > diskon4);
// Boundary: 9 -> 10 (naik ke diskon 10%)
        double diskon9 = kalkulatorDiskon.hitungDiskon(harga, 9, "BARU");
        double diskon10 = kalkulatorDiskon.hitungDiskon(harga, 10,
                "BARU");
        assertTrue(diskon10 > diskon9);
// Boundary: 49 -> 50 (naik ke diskon 15%)
        double diskon49 = kalkulatorDiskon.hitungDiskon(harga, 49,
                "BARU");
        double diskon50 = kalkulatorDiskon.hitungDiskon(harga, 50,
                "BARU");
        assertTrue(diskon50 > diskon49);
// Boundary: 99 -> 100 (naik ke diskon 20%)
        double diskon99 = kalkulatorDiskon.hitungDiskon(harga, 99,
                "BARU");
        double diskon100 = kalkulatorDiskon.hitungDiskon(harga, 100,
                "BARU");
        assertTrue(diskon100 > diskon99);
    }
}