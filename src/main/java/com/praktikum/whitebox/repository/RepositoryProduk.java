package com.praktikum.whitebox.repository;

import com.praktikum.whitebox.model.Produk;

import java.util.List;
import java.util.Optional;
public interface RepositoryProduk {
    boolean simpan(Produk produk);
    Optional<Produk> cariByKode(String kode);
    List<Produk> cariByNama(String nama);
    List<Produk> cariByKategori(String kategori);
    List<Produk> cariProdukStokMenipis();
    List<Produk> cariProdukStokHabis();
    boolean hapus(String kode);
    boolean updateStok(String kode, int stokBaru);
    List<Produk> cariSemua();
}
