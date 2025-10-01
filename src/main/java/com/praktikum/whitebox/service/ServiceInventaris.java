package com.praktikum.whitebox.service;

import com.praktikum.whitebox.model.Produk;
import com.praktikum.whitebox.repository.RepositoryProduk;
import com.praktikum.whitebox.util.ValidationUtils;
import java.util.List;
import java.util.Optional;

public class ServiceInventaris {
    private final RepositoryProduk repositoryProduk;
    public ServiceInventaris(RepositoryProduk repositoryProduk) {
        this.repositoryProduk = repositoryProduk;
    }
    public boolean tambahProduk(Produk produk) {
        if (!ValidationUtils.isValidProduk(produk)) {
            return false;
        }
// Cek apakah produk dengan kode yang sama sudah ada
        Optional<Produk> produkExist =
                repositoryProduk.cariByKode(produk.getKode());
        if (produkExist.isPresent()) {
            return false;
        }
        return repositoryProduk.simpan(produk);
    }
    public boolean hapusProduk(String kode) {
        if (!ValidationUtils.isValidKodeProduk(kode)) {
            return false;
        }
        Optional<Produk> produk = repositoryProduk.cariByKode(kode);
        if (!produk.isPresent()) {
            return false;
        }
// Tidak bisa hapus produk yang masih ada stoknya
        if (produk.get().getStok() > 0) {
            return false;
        }
        return repositoryProduk.hapus(kode);
    }
    public Optional<Produk> cariProdukByKode(String kode) {
        if (!ValidationUtils.isValidKodeProduk(kode)) {
            return Optional.empty();
        }
        return repositoryProduk.cariByKode(kode);
    }
    public List<Produk> cariProdukByNama(String nama) {
        return repositoryProduk.cariByNama(nama);
    }
    public List<Produk> cariProdukByKategori(String kategori) {
        return repositoryProduk.cariByKategori(kategori);
    }
    public boolean updateStok(String kode, int stokBaru) {
        if (!ValidationUtils.isValidKodeProduk(kode) || stokBaru < 0) {
            return false;
        }
        Optional<Produk> produk = repositoryProduk.cariByKode(kode);
        if (!produk.isPresent()) {
            return false;
        }
        return repositoryProduk.updateStok(kode, stokBaru);
    }
    public boolean keluarStok(String kode, int jumlah) {
        if (!ValidationUtils.isValidKodeProduk(kode) || jumlah <= 0) {
            return false;
        }
        Optional<Produk> produkOpt = repositoryProduk.cariByKode(kode);
        if (!produkOpt.isPresent() || !produkOpt.get().isAktif()) {
            return false;
        }
        Produk produk = produkOpt.get();
        if (produk.getStok() < jumlah) {
            return false;
        }
        int stokBaru = produk.getStok() - jumlah;
        return repositoryProduk.updateStok(kode, stokBaru);
    }
    public boolean masukStok(String kode, int jumlah) {
        if (!ValidationUtils.isValidKodeProduk(kode) || jumlah <= 0) {
            return false;
        }
        Optional<Produk> produk = repositoryProduk.cariByKode(kode);
        if (!produk.isPresent() || !produk.get().isAktif()) {
            return false;
        }
        int stokBaru = produk.get().getStok() + jumlah;
        return repositoryProduk.updateStok(kode, stokBaru);
    }
    public List<Produk> getProdukStokMenipis() {
        return repositoryProduk.cariProdukStokMenipis();
    }
    public List<Produk> getProdukStokHabis() {
        return repositoryProduk.cariProdukStokHabis();
    }
    public double hitungTotalNilaiInventaris() {
        List<Produk> semuaProduk = repositoryProduk.cariSemua();
        return semuaProduk.stream()
                .filter(Produk::isAktif)
                .mapToDouble(p -> p.getHarga() * p.getStok())
                .sum();
    }
    public int hitungTotalStok() {
        List<Produk> semuaProduk = repositoryProduk.cariSemua();
        return semuaProduk.stream()
                .filter(Produk::isAktif)
                .mapToInt(Produk::getStok)
                .sum();
    }
}