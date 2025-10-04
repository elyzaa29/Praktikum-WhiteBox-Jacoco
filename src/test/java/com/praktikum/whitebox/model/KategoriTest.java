package com.praktikum.whitebox.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class KategoriTest {

    @Test
    @DisplayName("Constructor & Getter: Membuat Kategori dan mengambil nilai atribut")
    void testConstructorAndGetters() {
        Kategori kategori = new Kategori("K001", "Elektronik", "Deskripsi kategori");
        assertEquals("K001", kategori.getIdKategori());
        assertEquals("Elektronik", kategori.getNamaKategori());
        assertEquals("Deskripsi kategori", kategori.getDeskripsi());
    }

    @Test
    @DisplayName("Setter: Mengubah nilai atribut Kategori")
    void testSetters() {
        Kategori kategori = new Kategori("K001", "Elektronik", "Deskripsi kategori");

        kategori.setIdKategori("K002");
        assertEquals("K002", kategori.getIdKategori());

        kategori.setNamaKategori("Peralatan");
        assertEquals("Peralatan", kategori.getNamaKategori());

        kategori.setDeskripsi("Deskripsi baru");
        assertEquals("Deskripsi baru", kategori.getDeskripsi());
    }

    @Test
    @DisplayName("Equals: Sama objek harus true")
    void testEqualsSameObject() {
        Kategori kategori = new Kategori("K001", "Elektronik", "Deskripsi");
        assertTrue(kategori.equals(kategori));
    }

    @Test
    @DisplayName("Equals: Objek berbeda tapi ID sama harus true")
    void testEqualsDifferentObjectSameId() {
        Kategori k1 = new Kategori("K001", "Elektronik", "Deskripsi");
        Kategori k2 = new Kategori("K001", "Peralatan", "Deskripsi lain");
        assertTrue(k1.equals(k2));
    }

    @Test
    @DisplayName("Equals: Objek berbeda ID harus false")
    void testEqualsDifferentObjectDifferentId() {
        Kategori k1 = new Kategori("K001", "Elektronik", "Deskripsi");
        Kategori k2 = new Kategori("K002", "Elektronik", "Deskripsi");
        assertFalse(k1.equals(k2));
    }

    @Test
    @DisplayName("Equals: Dibandingkan dengan null harus false")
    void testEqualsNull() {
        Kategori kategori = new Kategori("K001", "Elektronik", "Deskripsi");
        assertFalse(kategori.equals(null));
    }

    @Test
    @DisplayName("Equals: Dibandingkan dengan kelas berbeda harus false")
    void testEqualsDifferentClass() {
        Kategori kategori = new Kategori("K001", "Elektronik", "Deskripsi");
        String notKategori = "Not a Kategori";
        assertFalse(kategori.equals(notKategori));
    }

    @Test
    @DisplayName("HashCode: Objek dengan ID sama harus sama")
    void testHashCode() {
        Kategori kategori1 = new Kategori("K001", "Elektronik", "Deskripsi");
        Kategori kategori2 = new Kategori("K001", "Peralatan", "Deskripsi lain");
        assertEquals(kategori1.hashCode(), kategori2.hashCode());
    }

    @Test
    @DisplayName("ToString: Format string sesuai atribut")
    void testToString() {
        Kategori kategori = new Kategori("K001", "Elektronik", "Deskripsi");
        String expected = "Kategori{idKategori='K001', namaKategori='Elektronik', deskripsi='Deskripsi'}";
        assertEquals(expected, kategori.toString());
    }
}
