package com.praktikum.whitebox.service;
import com.praktikum.whitebox.model.Produk;
import com.praktikum.whitebox.repository.RepositoryProduk;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test Service Inventaris dengan Mocking")
public class ServiceInventarisTest {
    @Mock
    private RepositoryProduk mockRepositoryProduk;
    private ServiceInventaris serviceInventaris;
    private Produk produkTest;
    @BeforeEach
    void setUp() {
        serviceInventaris = new ServiceInventaris(mockRepositoryProduk);
        produkTest = new Produk("PROD001", "Laptop Gaming", "Elektronik",
                15000000, 10, 5);
    }
    @Test
    @DisplayName("Tambah produk berhasil - semua kondisi valid")
    void testTambahProdukBerhasil() {
// Arrange
        when(mockRepositoryProduk.cariByKode("PROD001")).thenReturn(Optional.empty());
        when(mockRepositoryProduk.simpan(produkTest)).thenReturn(true);
// Act
        boolean hasil = serviceInventaris.tambahProduk(produkTest);
// Assert
        assertTrue(hasil);
        verify(mockRepositoryProduk).cariByKode("PROD001");
        verify(mockRepositoryProduk).simpan(produkTest);
    }
    @Test
    @DisplayName("Tambah produk gagal - produk sudah ada")
    void testTambahProdukGagalSudahAda() {
// Arrange
        when(mockRepositoryProduk.cariByKode("PROD001")).thenReturn(Optional.of(produkTest));
// Act
        boolean hasil = serviceInventaris.tambahProduk(produkTest);
// Assert
        assertFalse(hasil);
        verify(mockRepositoryProduk).cariByKode("PROD001");
        verify(mockRepositoryProduk, never()).simpan(any(Produk.class));
    }
    @Test
    @DisplayName("Keluar stok berhasil - stok mencukupi")
    void testKeluarStokBerhasil() {
// Arrange
        when(mockRepositoryProduk.cariByKode("PROD001")).thenReturn(Optional.of(produkTest));
        when(mockRepositoryProduk.updateStok("PROD001",
                5)).thenReturn(true);
// Act
        boolean hasil = serviceInventaris.keluarStok("PROD001", 5);
// Assert
        assertTrue(hasil);
        verify(mockRepositoryProduk).updateStok("PROD001", 5);
    }
    @Test
    @DisplayName("Keluar stok gagal - stok tidak mencukupi")
    void testKeluarStokGagalStokTidakMencukupi() {
// Arrange
        when(mockRepositoryProduk.cariByKode("PROD001")).thenReturn(Optional.of(produkTest));
// Act
        boolean hasil = serviceInventaris.keluarStok("PROD001", 15);
// Assert
        assertFalse(hasil);
        verify(mockRepositoryProduk, never()).updateStok(anyString(),
                anyInt());
    }
    @Test
    @DisplayName("Hitung total nilai inventaris")
    void testHitungTotalNilaiInventaris() {
// Arrange
        Produk produk1 = new Produk("PROD001", "Laptop", "Elektronik",
                10000000, 2, 1);
        Produk produk2 = new Produk("PROD002", "Mouse", "Elektronik",
                500000, 5, 2);
        Produk produkNonAktif = new Produk("PROD003", "Keyboard",
                "Elektronik", 300000, 3, 1);
        produkNonAktif.setAktif(false);
        List<Produk> semuaProduk = Arrays.asList(produk1, produk2,
                produkNonAktif);
        when(mockRepositoryProduk.cariSemua()).thenReturn(semuaProduk);
        // Act
        double totalNilai =
                serviceInventaris.hitungTotalNilaiInventaris();
// Assert
        double expected = (10000000 * 2) + (500000 * 5); // hanya produk aktif
        assertEquals(expected, totalNilai, 0.001);
        verify(mockRepositoryProduk).cariSemua();
    }
    @Test
    @DisplayName("Get produk stok menipis")
    void testGetProdukStokMenipis() {
// Arrange
        Produk produkStokAman = new Produk("PROD001", "Laptop",
                "Elektronik", 10000000, 10, 5);
        Produk produkStokMenipis = new Produk("PROD002", "Mouse",
                "Elektronik", 500000, 3, 5);
        List<Produk> produkMenipis =
                Collections.singletonList(produkStokMenipis);
        when(mockRepositoryProduk.cariProdukStokMenipis()).thenReturn(produkMenipis);
// Act
        List<Produk> hasil = serviceInventaris.getProdukStokMenipis();
// Assert
        assertEquals(1, hasil.size());
        assertEquals("PROD002", hasil.get(0).getKode());
        verify(mockRepositoryProduk).cariProdukStokMenipis();
    }
}