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
// Keluar stokc
@Test
@DisplayName("keluarStok return false kalau kode tidak valid")
void testKeluarStok_KodeTidakValid() {
    try (MockedStatic<ValidationUtils> mocked = mockStatic(ValidationUtils.class)) {
        mocked.when(() -> ValidationUtils.isValidKodeProduk("XXX")).thenReturn(false);

        boolean result = serviceInventaris.keluarStok("XXX", 5);

        assertFalse(result);
        verify(mockRepositoryProduk, never()).cariByKode(anyString());
    }
}

    @Test
    @DisplayName("keluarStok return false kalau jumlah <= 0")
    void testKeluarStok_JumlahTidakValid() {
        try (MockedStatic<ValidationUtils> mocked = mockStatic(ValidationUtils.class)) {
            mocked.when(() -> ValidationUtils.isValidKodeProduk("P001")).thenReturn(true);

            boolean result = serviceInventaris.keluarStok("P001", 0);

            assertFalse(result);
            verify(mockRepositoryProduk, never()).cariByKode(anyString());
        }
    }

    @Test
    @DisplayName("keluarStok return false kalau produk tidak ditemukan")
    void testKeluarStok_ProdukTidakAda() {
        try (MockedStatic<ValidationUtils> mocked = mockStatic(ValidationUtils.class)) {
            mocked.when(() -> ValidationUtils.isValidKodeProduk("P001")).thenReturn(true);

            when(mockRepositoryProduk.cariByKode("P001")).thenReturn(Optional.empty());

            boolean result = serviceInventaris.keluarStok("P001", 3);

            assertFalse(result);
            verify(mockRepositoryProduk, times(1)).cariByKode("P001");
        }
    }

    @Test
    @DisplayName("keluarStok return false kalau produk tidak aktif")
    void testKeluarStok_ProdukTidakAktif() {
        try (MockedStatic<ValidationUtils> mocked = mockStatic(ValidationUtils.class)) {
            mocked.when(() -> ValidationUtils.isValidKodeProduk("P001")).thenReturn(true);

            Produk produk = new Produk("P001", "Laptop", "Elektronik", 5000.0, 10, 1);
            produk.setAktif(false);

            when(mockRepositoryProduk.cariByKode("P001")).thenReturn(Optional.of(produk));

            boolean result = serviceInventaris.keluarStok("P001", 3);

            assertFalse(result);
            verify(mockRepositoryProduk, times(1)).cariByKode("P001");
            verify(mockRepositoryProduk, never()).updateStok(anyString(), anyInt());
        }
    }

    @Test
    @DisplayName("keluarStok return true kalau stok cukup dan update sukses")
    void testKeluarStok_Sukses() {
        try (MockedStatic<ValidationUtils> mocked = mockStatic(ValidationUtils.class)) {
            mocked.when(() -> ValidationUtils.isValidKodeProduk("P001")).thenReturn(true);

            Produk produk = new Produk("P001", "Laptop", "Elektronik", 5000.0, 10, 1);
            produk.setAktif(true);

            when(mockRepositoryProduk.cariByKode("P001")).thenReturn(Optional.of(produk));
            when(mockRepositoryProduk.updateStok("P001", 7)).thenReturn(true); // 10 - 3 = 7

            boolean result = serviceInventaris.keluarStok("P001", 3);

            assertTrue(result);
            verify(mockRepositoryProduk).updateStok("P001", 7);
        }
    }

    @Test
    @DisplayName("keluarStok return false kalau jumlah melebihi stok")
    void testKeluarStok_JumlahLebihDariStok() {
        try (MockedStatic<ValidationUtils> mocked = mockStatic(ValidationUtils.class)) {
            mocked.when(() -> ValidationUtils.isValidKodeProduk("P001")).thenReturn(true);

            Produk produk = new Produk("P001", "Laptop", "Elektronik", 5000.0, 5, 1);
            produk.setAktif(true);

            when(mockRepositoryProduk.cariByKode("P001")).thenReturn(Optional.of(produk));

            boolean result = serviceInventaris.keluarStok("P001", 10);

            assertFalse(result);
            verify(mockRepositoryProduk, never()).updateStok(anyString(), anyInt());
        }
    }

// hitung total nilai inventaris
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

    // hapus produk
    @Test
    @DisplayName("hapusProduk return true kalau kode valid, produk ada, stok 0, dan berhasil dihapus")
    void testHapusProduk_Sukses() {
        try (MockedStatic<ValidationUtils> mocked = mockStatic(ValidationUtils.class)) {
            String kode = "PRD001";
            Produk produk = new Produk("PRD001", "Produk A", "Elektronik", 10000.0, 0, 1); // stok 0

            mocked.when(() -> ValidationUtils.isValidKodeProduk(kode)).thenReturn(true);
            when(mockRepositoryProduk.cariByKode(kode)).thenReturn(Optional.of(produk));
            when(mockRepositoryProduk.hapus(kode)).thenReturn(true);

            boolean result = serviceInventaris.hapusProduk(kode);

            assertTrue(result);
            verify(mockRepositoryProduk, times(1)).cariByKode(kode);
            verify(mockRepositoryProduk, times(1)).hapus(kode);
        }
    }

    @Test
    @DisplayName("hapusProduk return false kalau stok produk masih > 0")
    void testHapusProduk_GagalKarenaStokMasihAda() {
        try (MockedStatic<ValidationUtils> mocked = mockStatic(ValidationUtils.class)) {
            String kode = "PRD002";
            Produk produk = new Produk("PRD002", "Produk B", "Elektronik", 15000.0, 10, 1); // stok 10

            mocked.when(() -> ValidationUtils.isValidKodeProduk(kode)).thenReturn(true);
            when(mockRepositoryProduk.cariByKode(kode)).thenReturn(Optional.of(produk));

            boolean result = serviceInventaris.hapusProduk(kode);

            assertFalse(result);
            verify(mockRepositoryProduk, times(1)).cariByKode(kode);
            verify(mockRepositoryProduk, never()).hapus(anyString());
        }
    }

    @Test
    @DisplayName("hapusProduk return false kalau produk tidak ditemukan")
    void testHapusProduk_GagalKarenaProdukTidakAda() {
        try (MockedStatic<ValidationUtils> mocked = mockStatic(ValidationUtils.class)) {
            String kode = "PRD003";

            mocked.when(() -> ValidationUtils.isValidKodeProduk(kode)).thenReturn(true);
            when(mockRepositoryProduk.cariByKode(kode)).thenReturn(Optional.empty());

            boolean result = serviceInventaris.hapusProduk(kode);

            assertFalse(result);
            verify(mockRepositoryProduk, times(1)).cariByKode(kode);
            verify(mockRepositoryProduk, never()).hapus(anyString());
        }
    }

    @Test
    @DisplayName("hapusProduk return false kalau kode tidak valid")
    void testHapusProduk_GagalKarenaKodeTidakValid() {
        try (MockedStatic<ValidationUtils> mocked = mockStatic(ValidationUtils.class)) {
            String kode = "";

            mocked.when(() -> ValidationUtils.isValidKodeProduk(kode)).thenReturn(false);

            boolean result = serviceInventaris.hapusProduk(kode);

            assertFalse(result);
            verifyNoInteractions(mockRepositoryProduk);
        }
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

    // Masuk stok return false sana berhasil..
    @Test
    @DisplayName("masukStok return true kalau valid, produk aktif, dan update sukses")
    void testMasukStok_Sukses() {
        try (MockedStatic<ValidationUtils> mocked = mockStatic(ValidationUtils.class)) {
            // Arrange
            String kode = "PRD001";
            int jumlah = 5;

            // Mock validasi kode produk
            mocked.when(() -> ValidationUtils.isValidKodeProduk(kode)).thenReturn(true);

            // Buat produk aktif dengan stok awal 10
            Produk produk = new Produk("PRD001", "Produk A", "Elektronik", 1000.0, 10, 1);
            produk.setAktif(true);

            when(mockRepositoryProduk.cariByKode(kode)).thenReturn(Optional.of(produk));
            when(mockRepositoryProduk.updateStok(eq(kode), eq(15))).thenReturn(true);

            // Act
            boolean result = serviceInventaris.masukStok(kode, jumlah);

            // Assert
            assertTrue(result);
            verify(mockRepositoryProduk, times(1)).updateStok(kode, 15);
        }
    }

    @Test
    @DisplayName("masukStok return false kalau kode produk tidak valid")
    void testMasukStok_KodeTidakValid() {
        try (MockedStatic<ValidationUtils> mocked = mockStatic(ValidationUtils.class)) {
            String kode = "INVALID";
            int jumlah = 5;

            mocked.when(() -> ValidationUtils.isValidKodeProduk(kode)).thenReturn(false);

            boolean result = serviceInventaris.masukStok(kode, jumlah);

            assertFalse(result);
            verifyNoInteractions(mockRepositoryProduk);
        }
    }

    @Test
    @DisplayName("masukStok return false kalau jumlah <= 0")
    void testMasukStok_JumlahTidakValid() {
        try (MockedStatic<ValidationUtils> mocked = mockStatic(ValidationUtils.class)) {
            String kode = "PRD001";
            int jumlah = 0;

            mocked.when(() -> ValidationUtils.isValidKodeProduk(kode)).thenReturn(true);

            boolean result = serviceInventaris.masukStok(kode, jumlah);

            assertFalse(result);
            verifyNoInteractions(mockRepositoryProduk);
        }
    }

    @Test
    @DisplayName("masukStok return false kalau produk tidak ditemukan")
    void testMasukStok_ProdukTidakAda() {
        try (MockedStatic<ValidationUtils> mocked = mockStatic(ValidationUtils.class)) {
            String kode = "PRD001";
            int jumlah = 5;

            mocked.when(() -> ValidationUtils.isValidKodeProduk(kode)).thenReturn(true);
            when(mockRepositoryProduk.cariByKode(kode)).thenReturn(Optional.empty());

            boolean result = serviceInventaris.masukStok(kode, jumlah);

            assertFalse(result);
            verify(mockRepositoryProduk, times(1)).cariByKode(kode);
        }
    }

    @Test
    @DisplayName("masukStok return false kalau produk tidak aktif")
    void testMasukStok_ProdukTidakAktif() {
        try (MockedStatic<ValidationUtils> mocked = mockStatic(ValidationUtils.class)) {
            String kode = "PRD001";
            int jumlah = 5;

            mocked.when(() -> ValidationUtils.isValidKodeProduk(kode)).thenReturn(true);

            // Buat produk tapi set aktif = false
            Produk produk = new Produk("PRD001", "Produk A", "Elektronik", 1000.0, 10, 1);
            produk.setAktif(false);

            when(mockRepositoryProduk.cariByKode(kode)).thenReturn(Optional.of(produk));

            boolean result = serviceInventaris.masukStok(kode, jumlah);

            assertFalse(result);
            verify(mockRepositoryProduk, times(1)).cariByKode(kode);
            verify(mockRepositoryProduk, never()).updateStok(anyString(), anyInt());
        }
    }
    // Ngitung Total Stock
    @Test
    @DisplayName("hitungTotalStok return jumlah stok semua produk aktif")
    void testHitungTotalStok_AdaProdukAktif() {
        // Arrange
        Produk p1 = new Produk("P001", "Laptop", "Elektronik", 5000.0, 10, 1);
        p1.setAktif(true);

        Produk p2 = new Produk("P002", "Mouse", "Elektronik", 200.0, 5, 1);
        p2.setAktif(true);

        Produk p3 = new Produk("P003", "Keyboard", "Elektronik", 300.0, 7, 1);
        p3.setAktif(false); // tidak dihitung

        when(mockRepositoryProduk.cariSemua()).thenReturn(Arrays.asList(p1, p2, p3));

        // Act
        int totalStok = serviceInventaris.hitungTotalStok();

        // Assert
        assertEquals(15, totalStok); // 10 + 5
        verify(mockRepositoryProduk, times(1)).cariSemua();
    }

    @Test
    @DisplayName("hitungTotalStok return 0 kalau semua produk tidak aktif")
    void testHitungTotalStok_SemuaTidakAktif() {
        Produk p1 = new Produk("P001", "Laptop", "Elektronik", 5000.0, 10, 1);
        p1.setAktif(false);

        Produk p2 = new Produk("P002", "Mouse", "Elektronik", 200.0, 5, 1);
        p2.setAktif(false);

        when(mockRepositoryProduk.cariSemua()).thenReturn(Arrays.asList(p1, p2));

        int totalStok = serviceInventaris.hitungTotalStok();

        assertEquals(0, totalStok);
        verify(mockRepositoryProduk, times(1)).cariSemua();
    }

    @Test
    @DisplayName("hitungTotalStok return 0 kalau tidak ada produk")
    void testHitungTotalStok_Kosong() {
        when(mockRepositoryProduk.cariSemua()).thenReturn(Collections.emptyList());

        int totalStok = serviceInventaris.hitungTotalStok();

        assertEquals(0, totalStok);
        verify(mockRepositoryProduk, times(1)).cariSemua();
    }
    // getProdukStockHabis
    @Test
    @DisplayName("getProdukStokHabis return daftar produk dengan stok habis")
    void testGetProdukStokHabis_AdaData() {
        Produk p1 = new Produk("P001", "Laptop", "Elektronik", 5000.0, 0, 1);
        p1.setAktif(true);

        Produk p2 = new Produk("P002", "Mouse", "Elektronik", 200.0, 0, 1);
        p2.setAktif(true);

        List<Produk> habisList = Arrays.asList(p1, p2);

        // Mock repository
        when(mockRepositoryProduk.cariProdukStokHabis()).thenReturn(habisList);

        // Act
        List<Produk> result = serviceInventaris.getProdukStokHabis();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Laptop", result.get(0).getNama());
        assertEquals(0, result.get(0).getStok());
        verify(mockRepositoryProduk, times(1)).cariProdukStokHabis();
    }

    @Test
    @DisplayName("getProdukStokHabis return list kosong kalau tidak ada produk habis")
    void testGetProdukStokHabis_TidakAdaData() {
        // Mock repository return empty
        when(mockRepositoryProduk.cariProdukStokHabis()).thenReturn(Collections.emptyList());

        // Act
        List<Produk> result = serviceInventaris.getProdukStokHabis();

        // Assert
        assertTrue(result.isEmpty());
        verify(mockRepositoryProduk, times(1)).cariProdukStokHabis();
    }

}