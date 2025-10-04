package com.praktikum.whitebox.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit Test Kategori")
public class KategoriTest {

    @Test
    @DisplayName("Constructor dan Getter bekerja")
    void testConstructorAndGetter() {
        Kategori kategori = new Kategori("K001", "Elektronik", "Produk elektronik");

        assertEquals("K001", kategori.getIdKategori());
        assertEquals("Elektronik", kategori.getNamaKategori());
        assertEquals("Produk elektronik", kategori.getDeskripsi());
    }

    @Test
    @DisplayName("Setter bekerja")
    void testSetter() {
        Kategori kategori = new Kategori("K001", "Elektronik", "Produk elektronik");

        kategori.setIdKategori("K002");
        kategori.setNamaKategori("Fashion");
        kategori.setDeskripsi("Produk fashion");

        assertEquals("K002", kategori.getIdKategori());
        assertEquals("Fashion", kategori.getNamaKategori());
        assertEquals("Produk fashion", kategori.getDeskripsi());
    }

    @Test
    @DisplayName("Equals dan HashCode berdasarkan idKategori")
    void testEqualsAndHashCode() {
        Kategori k1 = new Kategori("K001", "Elektronik", "Produk elektronik");
        Kategori k2 = new Kategori("K001", "Elektronik Lain", "Deskripsi lain");
        Kategori k3 = new Kategori("K002", "Fashion", "Produk fashion");

        // k1 dan k2 sama karena idKategori sama
        assertEquals(k1, k2);
        assertEquals(k1.hashCode(), k2.hashCode());

        // k1 dan k3 berbeda
        assertNotEquals(k1, k3);
        assertNotEquals(k1.hashCode(), k3.hashCode());
    }

    @Test
    @DisplayName("toString mengembalikan string yang sesuai")
    void testToString() {
        Kategori kategori = new Kategori("K001", "Elektronik", "Produk elektronik");

        String expected = "Kategori{idKategori='K001', namaKategori='Elektronik', deskripsi='Produk elektronik'}";
        assertEquals(expected, kategori.toString());
    }

    @Test
    @DisplayName("Equals mengembalikan false jika dibandingkan dengan null atau object lain")
    void testEqualsWithNullAndOtherObject() {
        Kategori kategori = new Kategori("K001", "Elektronik", "Produk elektronik");

        assertNotEquals(kategori, null);
        assertNotEquals(kategori, "StringBukanKategori");
    }
}
