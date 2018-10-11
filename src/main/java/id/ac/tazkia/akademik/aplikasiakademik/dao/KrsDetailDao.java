package id.ac.tazkia.akademik.aplikasiakademik.dao;

import id.ac.tazkia.akademik.aplikasiakademik.entity.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface KrsDetailDao extends PagingAndSortingRepository<KrsDetail,String> {
    List<KrsDetail> findByKrsAndAndMahasiswaAndJadwalIdHariId(Krs krs, Mahasiswa mahasiswa,String hari);
   List<KrsDetail> findByKrsAndMahasiswa(Krs krs, Mahasiswa mahasiswa);

    @Query("SELECT u FROM KrsDetail u WHERE u.mahasiswa = ?1 and u.krs = ?2 and u.status= ?3 and u.jadwal.idHari in(DAYOFWEEK(NOW())-1,DAYOFWEEK(NOW())) order by u.jadwal.idHari,u.jadwal.jamMulai")
    List<KrsDetail> findByMahasiswaAndKrsAndStatus(Mahasiswa mahasiswa, Krs krs, StatusRecord statusRecord);

   // List<KrsDetail> findByStatusNotInAndMahasiswa(StatusRecord statusRecord, Mahasiswa mahasiswa);
}
