package com.praktikum.whitebox.service;
import com.praktikum.whitebox.model.Produk;
import com.praktikum.whitebox.repository.RepositoryProduk;
import com.praktikum.whitebox.util.ValidationUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

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

    // tambah produk yang invalid dan harus false
    @Test
    @DisplayName("Tambah produk invalid harus return false")
    void testTambahProdukInvalid() {
        Produk produkInvalid = new Produk("", "Elektronik", -1000);
        // Nama kosong, harga minus = dianggap invalid

        boolean hasil = serviceInventaris.tambahProduk(produkInvalid);

        assertFalse(hasil, "Produk tidak valid harus return false");
    }

    // hapus produk dengan kode yang ngga valid
    @Test
    @DisplayName("Hapus produk gagal - kode produk tidak valid")
    void testHapusProdukKodeTidakValid() {
        // Arrange
        String kodeTidakValid = ""; // misalnya kode kosong dianggap tidak valid

        // Act
        boolean hasil = serviceInventaris.hapusProduk(kodeTidakValid);

        // Assert
        assertFalse(hasil); // seharusnya return false
        verify(mockRepositoryProduk, never()).hapusProduk(anyString());
    }

    @Test
    @DisplayName("Hapus produk gagal jika produk tidak ditemukan")
    void testHapusProdukProdukTidakDitemukan() {
        // Arrange
        String kodeProduk = "PROD999"; // kode produk yang tidak ada
        when(mockRepositoryProduk.cariByKode(kodeProduk)).thenReturn(Optional.empty());

        // Act
        boolean hasil = serviceInventaris.hapusProduk(kodeProduk);

        // Assert
        assertFalse(hasil); // seharusnya false
        verify(mockRepositoryProduk).cariByKode(kodeProduk); // dipanggil sekali
        verify(mockRepositoryProduk, never()).hapusProduk(anyString()); // tidak lanjut hapus
    }

    // hapus produk stok berhasil
    @Test
    @DisplayName("Hapus produk berhasil jika stok habis")
    void testHapusProdukBerhasilStokHabis() {
        // Arrange
        String kodeProduk = "PROD004";
        Produk produkStokHabis = new Produk(
                "PROD004",
                "Flashdisk",
                "Elektronik",
                100000,
                0,   // stok 0 → boleh dihapus
                1
        );

        // repository menemukan produk dengan stok 0
        when(mockRepositoryProduk.cariByKode(kodeProduk))
                .thenReturn(Optional.of(produkStokHabis));
        // repository hapus berhasil
        when(mockRepositoryProduk.hapus(kodeProduk)).thenReturn(true);

        // Act
        boolean hasil = serviceInventaris.hapusProduk(kodeProduk);

        // Assert
        assertTrue(hasil); // seharusnya true
        verify(mockRepositoryProduk).cariByKode(kodeProduk);
        verify(mockRepositoryProduk).hapus(kodeProduk); // pastikan hapus dipanggil
    }

    // cari produk dengan kode
    @Test
    @DisplayName("cariProdukByKode return Optional.empty kalau kode tidak valid")
    void testCariProdukByKode_KodeTidakValid() {
        Optional<Produk> result = serviceInventaris.cariProdukByKode("");
        assertTrue(result.isEmpty());
        verifyNoInteractions(mockRepositoryProduk);
    }

    @Test
    @DisplayName("cariProdukByKode berhasil kalau kode valid")
    void testCariProdukByKode_KodeValid() {
        String kodeValid = "PRD001";
        Produk produk = new Produk("PRD001", "Laptop", "Elektronik", 5000000, 5, 2);

        when(mockRepositoryProduk.cariByKode(kodeValid))
                .thenReturn(Optional.of(produk));

        Optional<Produk> result = serviceInventaris.cariProdukByKode(kodeValid);

        assertTrue(result.isPresent());
        assertEquals("Laptop", result.get().getNama());
        verify(mockRepositoryProduk, times(1)).cariByKode(kodeValid);
    }

    // Cari Produk By Nama
    @Test
    @DisplayName("cariProdukByNama return empty list kalau nama kosong atau null")
    void testCariProdukByNama_NamaKosong() {
        // Kasus nama kosong
        String nama = "";
        List<Produk> result = serviceInventaris.cariProdukByNama(nama);
        assertTrue(result.isEmpty());

        // Kasus nama null
        nama = null;
        result = serviceInventaris.cariProdukByNama(nama);
        assertTrue(result.isEmpty());

        // Pastikan repository tidak dipanggil sama sekali
        verifyNoInteractions(mockRepositoryProduk);
    }

    @Test
    @DisplayName("cariProdukByNama berhasil kalau nama valid")
    void testCariProdukByNama_NamaValid() {
        String nama = "Laptop";

        // Buat list mock dengan objek Produk yang benar-benar memiliki nama
        List<Produk> mockList = new ArrayList<>();
        Produk p = new Produk("P001", "Laptop", 5000);
        p.setKode("P001");
        p.setNama("Laptop"); // wajib set nama supaya getNama() tidak null
        p.setHarga(5000);
        mockList.add(p);

        // Mock behavior repository
        when(mockRepositoryProduk.cariByNama(nama)).thenReturn(mockList);

        // Panggil method service
        List<Produk> result = serviceInventaris.cariProdukByNama(nama);

        // Assertions
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Laptop", result.get(0).getNama());

        // Pastikan repository dipanggil sekali
        verify(mockRepositoryProduk, times(1)).cariByNama(nama);
    }

    @Test
    @DisplayName("cariProdukByNama return empty list kalau nama valid tapi tidak ditemukan")
    void testCariProdukByNama_TidakDitemukan() {
        String nama = "TidakAda";

        // Mock behavior repository mengembalikan list kosong
        when(mockRepositoryProduk.cariByNama(nama)).thenReturn(new ArrayList<>());

        List<Produk> result = serviceInventaris.cariProdukByNama(nama);

        assertTrue(result.isEmpty());
        verify(mockRepositoryProduk, times(1)).cariByNama(nama);
    }

    // cari produk by kategori mengambilkan kategori sesuai produk
    @Test
    @DisplayName("cariProdukByKategori mengembalikan daftar produk sesuai kategori")
    void testCariProdukByKategori_AdaData() {
        // Arrange
        Produk p1 = new Produk("P001", "Laptop", "Elektronik", 5000000, 10, 5);
        Produk p2 = new Produk("P002", "Mouse", "Elektronik", 50000, 20, 5);
        List<Produk> produkList = Arrays.asList(p1, p2);

        when(mockRepositoryProduk.cariByKategori("Elektronik"))
                .thenReturn(produkList);

        // Act
        List<Produk> result = serviceInventaris.cariProdukByKategori("Elektronik");

        // Assert
        assertEquals(2, result.size());
        assertEquals("Laptop", result.get(0).getNama());  // ✅ sekarang bukan null lagi
        verify(mockRepositoryProduk).cariByKategori("Elektronik");
    }

    @Test
    @DisplayName("cariProdukByKategori mengembalikan list kosong jika tidak ada data")
    void testCariProdukByKategori_TidakAdaData() {
        // Arrange
        when(mockRepositoryProduk.cariByKategori("Fashion"))
                .thenReturn(Collections.emptyList());

        // Act
        List<Produk> result = serviceInventaris.cariProdukByKategori("Fashion");

        // Assert
        assertTrue(result.isEmpty());
        verify(mockRepositoryProduk).cariByKategori("Fashion");
    }

    // Update Stock
    @Test
    @DisplayName("updateStok return false kalau kode tidak valid")
    void testUpdateStok_KodeTidakValid() {
        try (MockedStatic<ValidationUtils> mocked = mockStatic(ValidationUtils.class)) {
            String kode = "INVALID";
            int stokBaru = 10;

            // Mock validasi gagal
            mocked.when(() -> ValidationUtils.isValidKodeProduk(kode)).thenReturn(false);

            boolean result = serviceInventaris.updateStok(kode, stokBaru);

            assertFalse(result);
            verifyNoInteractions(mockRepositoryProduk);
        }
    }

    @Test
    @DisplayName("updateStok return false kalau stok negatif")
    void testUpdateStok_StokNegatif() {
        try (MockedStatic<ValidationUtils> mocked = mockStatic(ValidationUtils.class)) {
            String kode = "P001";
            int stokBaru = -5;

            mocked.when(() -> ValidationUtils.isValidKodeProduk(kode)).thenReturn(true);

            boolean result = serviceInventaris.updateStok(kode, stokBaru);

            assertFalse(result);
            verifyNoInteractions(mockRepositoryProduk);
        }
    }

    @Test
    @DisplayName("updateStok return false kalau produk tidak ditemukan")
    void testUpdateStok_ProdukTidakDitemukan() {
        try (MockedStatic<ValidationUtils> mocked = mockStatic(ValidationUtils.class)) {
            String kode = "P001";
            int stokBaru = 10;

            mocked.when(() -> ValidationUtils.isValidKodeProduk(kode)).thenReturn(true);
            when(mockRepositoryProduk.cariByKode(kode)).thenReturn(Optional.empty());

            boolean result = serviceInventaris.updateStok(kode, stokBaru);

            assertFalse(result);
            verify(mockRepositoryProduk, times(1)).cariByKode(kode);
            verify(mockRepositoryProduk, never()).updateStok(anyString(), anyInt());
        }
    }

    @Test
    @DisplayName("updateStok return true kalau produk ditemukan dan update sukses")
    void testUpdateStok_Sukses() {
        try (MockedStatic<ValidationUtils> mocked = mockStatic(ValidationUtils.class)) {
            String kode = "P001";
            int stokBaru = 20;
            Produk produk = new Produk(kode, "Laptop", "Elektronik", 5000.0, 10, 2);

            mocked.when(() -> ValidationUtils.isValidKodeProduk(kode)).thenReturn(true);
            when(mockRepositoryProduk.cariByKode(kode)).thenReturn(Optional.of(produk));
            when(mockRepositoryProduk.updateStok(eq(kode), eq(stokBaru))).thenReturn(true);

            boolean result = serviceInventaris.updateStok(kode, stokBaru);

            assertTrue(result);
            verify(mockRepositoryProduk, times(1)).cariByKode(kode);
            verify(mockRepositoryProduk, times(1)).updateStok(kode, stokBaru);
        }
    }

    @Test
    @DisplayName("updateStok return false kalau produk ditemukan tapi update gagal")
    void testUpdateStok_UpdateGagal() {
        try (MockedStatic<ValidationUtils> mocked = mockStatic(ValidationUtils.class)) {
            String kode = "P001";
            int stokBaru = 20;
            Produk produk = new Produk(kode, "Laptop", "Elektronik", 5000.0, 10, 2);

            mocked.when(() -> ValidationUtils.isValidKodeProduk(kode)).thenReturn(true);
            when(mockRepositoryProduk.cariByKode(kode)).thenReturn(Optional.of(produk));
            when(mockRepositoryProduk.updateStok(eq(kode), eq(stokBaru))).thenReturn(false);

            boolean result = serviceInventaris.updateStok(kode, stokBaru);

            assertFalse(result);
            verify(mockRepositoryProduk, times(1)).cariByKode(kode);
            verify(mockRepositoryProduk, times(1)).updateStok(kode, stokBaru);
        }
    }
}