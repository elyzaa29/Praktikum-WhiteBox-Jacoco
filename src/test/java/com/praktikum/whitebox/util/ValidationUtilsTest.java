package com.praktikum.whitebox.util;

import com.praktikum.whitebox.model.Kategori;
import com.praktikum.whitebox.model.Produk;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidationUtilsTest {

    // ======= isValidKodeProduk =======
    @Test
    @DisplayName("Valid kode produk")
    void testIsValidKodeProduk_Valid() {
        assertTrue(ValidationUtils.isValidKodeProduk("P001"));
        assertTrue(ValidationUtils.isValidKodeProduk("ABC123"));
    }

    @Test
    @DisplayName("Invalid kode produk")
    void testIsValidKodeProduk_Invalid() {
        assertFalse(ValidationUtils.isValidKodeProduk(null));
        assertFalse(ValidationUtils.isValidKodeProduk(""));
        assertFalse(ValidationUtils.isValidKodeProduk("A"));        // terlalu pendek
        assertFalse(ValidationUtils.isValidKodeProduk("TOOLONGCODE123")); // terlalu panjang
        assertFalse(ValidationUtils.isValidKodeProduk("@@@"));      // karakter tidak valid
    }

    // ======= isValidNama =======
    @Test
    @DisplayName("Valid nama")
    void testIsValidNama_Valid() {
        assertTrue(ValidationUtils.isValidNama("Produk A"));
        assertTrue(ValidationUtils.isValidNama("Nama 123"));
    }

    @Test
    @DisplayName("Invalid nama")
    void testIsValidNama_Invalid() {
        assertFalse(ValidationUtils.isValidNama(null));
        assertFalse(ValidationUtils.isValidNama(""));
        assertFalse(ValidationUtils.isValidNama("AB")); // terlalu pendek
        String longName = "A".repeat(101);
        assertFalse(ValidationUtils.isValidNama(longName)); // terlalu panjang
    }

    // ======= isValidHarga =======
    @Test
    @DisplayName("Valid harga")
    void testIsValidHarga() {
        assertTrue(ValidationUtils.isValidHarga(100.0));
        assertFalse(ValidationUtils.isValidHarga(0));
        assertFalse(ValidationUtils.isValidHarga(-50));
    }

    // ======= isValidStok =======
    @Test
    @DisplayName("Valid stok")
    void testIsValidStok() {
        assertTrue(ValidationUtils.isValidStok(0));
        assertTrue(ValidationUtils.isValidStok(10));
        assertFalse(ValidationUtils.isValidStok(-1));
    }

    // ======= isValidStokMinimum =======
    @Test
    @DisplayName("Valid stok minimum")
    void testIsValidStokMinimum() {
        assertTrue(ValidationUtils.isValidStokMinimum(0));
        assertTrue(ValidationUtils.isValidStokMinimum(5));
        assertFalse(ValidationUtils.isValidStokMinimum(-2));
    }

    // ======= isValidKuantitas =======
    @Test
    @DisplayName("Valid kuantitas")
    void testIsValidKuantitas() {
        assertTrue(ValidationUtils.isValidKuantitas(1));
        assertFalse(ValidationUtils.isValidKuantitas(0));
        assertFalse(ValidationUtils.isValidKuantitas(-5));
    }

    // ======= isValidPersentase =======
    @Test
    @DisplayName("Valid persentase")
    void testIsValidPersentase() {
        assertTrue(ValidationUtils.isValidPersentase(0));
        assertTrue(ValidationUtils.isValidPersentase(50));
        assertTrue(ValidationUtils.isValidPersentase(100));
        assertFalse(ValidationUtils.isValidPersentase(-1));
        assertFalse(ValidationUtils.isValidPersentase(101));
    }

    // ======= isValidProduk =======
    @Test
    @DisplayName("Valid produk")
    void testIsValidProduk() {
        Produk p = new Produk("P001", "Produk A", "Kategori A", 100.0, 10, 5);
        assertTrue(ValidationUtils.isValidProduk(p));
    }

    @Test
    @DisplayName("Invalid produk")
    void testIsValidProduk_Invalid() {
        Produk p1 = new Produk(null, "Produk A", "Kategori A", 100.0, 10, 5);
        Produk p2 = new Produk("P001", "", "Kategori A", 100.0, 10, 5);
        Produk p3 = new Produk("P001", "Produk A", "", -100.0, -1, -2);

        assertFalse(ValidationUtils.isValidProduk(null));
        assertFalse(ValidationUtils.isValidProduk(p1));
        assertFalse(ValidationUtils.isValidProduk(p2));
        assertFalse(ValidationUtils.isValidProduk(p3));
    }

    // ======= isValidKategori =======
    @Test
    @DisplayName("Valid kategori")
    void testIsValidKategori_Valid() {
        // Nama dan deskripsi valid
        Kategori k = new Kategori("K001", "Kategori A", "Deskripsi kategori");
        assertTrue(ValidationUtils.isValidKategori(k));

        // Nama valid, deskripsi null
        Kategori k2 = new Kategori("K002", "Kat", null);
        assertTrue(ValidationUtils.isValidKategori(k2));
    }

    @Test
    @DisplayName("Invalid kategori")
    void testIsValidKategori_Invalid() {
        // Kategori null
        assertFalse(ValidationUtils.isValidKategori(null));

        // Nama null
        Kategori k1 = new Kategori("K001", null, "Deskripsi");
        assertFalse(ValidationUtils.isValidKategori(k1));

        // Nama kosong
        Kategori k2 = new Kategori("K002", "", "Deskripsi");
        assertFalse(ValidationUtils.isValidKategori(k2));

        // Nama terlalu pendek (< 3)
        Kategori k3 = new Kategori("K003", "AB", "Deskripsi");
        assertFalse(ValidationUtils.isValidKategori(k3));

        // Nama terlalu panjang (> 100)
        String namaPanjang = "A".repeat(101);
        Kategori k5 = new Kategori("K005", namaPanjang, "Deskripsi");
        assertFalse(ValidationUtils.isValidKategori(k5));

        // Deskripsi terlalu panjang (> 500)
        Kategori k4 = new Kategori("K004", "Kategori Valid", "D".repeat(501));
        assertFalse(ValidationUtils.isValidKategori(k4));
    }

}
