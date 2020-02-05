package id.ac.tazkia.smilemahasiswa.dao;

import id.ac.tazkia.smilemahasiswa.dto.assesment.ScoreDto;
import id.ac.tazkia.smilemahasiswa.dto.schedule.PlotingDto;
import id.ac.tazkia.smilemahasiswa.dto.schedule.ScheduleDto;
import id.ac.tazkia.smilemahasiswa.dto.schedule.SesiDto;
import id.ac.tazkia.smilemahasiswa.entity.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface JadwalDao extends PagingAndSortingRepository<Jadwal, String> {
    @Query("select sum (j.matakuliahKurikulum.jumlahSks)from Jadwal j where j.id in (:id)")
    Long totalSks(@Param("id")String[] id);

    @Query("select new id.ac.tazkia.smilemahasiswa.dto.schedule.ScheduleDto(j.id,j.matakuliahKurikulum.matakuliah.namaMatakuliah,j.kelas.namaKelas,j.dosen.karyawan.namaKaryawan,j.matakuliahKurikulum.jumlahSks,j.jamMulai,j.jamSelesai,j.akses,j.ruangan.namaRuangan, j.hari.namaHari,j.matakuliahKurikulum.matakuliah.namaMatakuliahEnglish)from Jadwal j where j.prodi = :prodi and j.status not in (:id) and j.tahunAkademik= :tahun and j.hari= :hari")
    List<ScheduleDto> schedule(@Param("prodi") Prodi prodi, @Param("id") List<StatusRecord> statusRecord, @Param("tahun") TahunAkademik t, @Param("hari") Hari hari);

    @Query("select new id.ac.tazkia.smilemahasiswa.dto.schedule.PlotingDto(j.id,j.matakuliahKurikulum.matakuliah.namaMatakuliah,j.matakuliahKurikulum.matakuliah.namaMatakuliahEnglish,j.kelas.namaKelas,j.matakuliahKurikulum.jumlahSks,j.dosen.karyawan.namaKaryawan,j.dosen.id,j.kelas.id) from Jadwal j where j.status = 'AKTIF' and j.tahunAkademik= :akademik and j.prodi = :prodi and j.hari is null and j.jamMulai is null and j.jamSelesai is null and j.kelas is not null")
    List<PlotingDto> ploting(@Param("prodi") Prodi prodi,@Param("akademik")TahunAkademik tahunAkademik);

    @Query(value = "SELECT a.sesi as sesi FROM (SELECT * FROM sesi)AS a LEFT JOIN (SELECT id,sesi FROM jadwal WHERE id_hari=?2 AND id_tahun_akademik=?1 AND id_ruangan=?3 AND STATUS='AKTIF')AS b ON a.sesi=b.sesi LEFT JOIN (SELECT id,sesi FROM jadwal WHERE id_kelas=?4 and id_tahun_akademik=?1 AND id_hari=?2 AND STATUS='AKTIF')AS c ON a.sesi=c.sesi LEFT JOIN (SELECT id,sesi FROM jadwal WHERE id_dosen_pengampu=?5 and id_tahun_akademik=?1 AND id_hari=?2 AND STATUS='AKTIF')AS d ON a.sesi=d.sesi WHERE b.id IS NULL AND c.id IS NULL group by a.sesi", nativeQuery = true)
    List<SesiDto> cariSesi(TahunAkademik tahunAkademik, Hari hari, Ruangan ruangan, Kelas kelas, Dosen dosen);

    @Query("select j from Jadwal j where j.id not in (:id) and j.tahunAkademik = :tahun and j.hari = :hari and j.ruangan = :ruangan and j.sesi= :sesi and j.status= :status")
    List<Jadwal> cariJadwal(@Param("id") List<String> id, @Param("tahun")TahunAkademik t, @Param("hari")Hari h, @Param("ruangan")Ruangan r, @Param("sesi")String sesi,@Param("status")StatusRecord statusRecord);

    @Query("select new id.ac.tazkia.smilemahasiswa.dto.schedule.ScheduleDto(j.id,j.matakuliahKurikulum.matakuliah.namaMatakuliah,j.kelas.namaKelas,j.dosen.karyawan.namaKaryawan,j.matakuliahKurikulum.jumlahSks,j.jamMulai,j.jamSelesai,j.akses,j.ruangan.namaRuangan, j.hari.namaHari,j.matakuliahKurikulum.matakuliah.namaMatakuliahEnglish)from Jadwal j where j.prodi = :prodi and j.status not in (:id) and j.tahunAkademik= :tahun order by j.matakuliahKurikulum.matakuliah.namaMatakuliahEnglish asc")
    List<ScheduleDto> assesment(@Param("prodi") Prodi prodi, @Param("id") List<StatusRecord> statusRecord, @Param("tahun") TahunAkademik t);

    @Query("select new id.ac.tazkia.smilemahasiswa.dto.schedule.ScheduleDto(j.id,j.matakuliahKurikulum.matakuliah.namaMatakuliah,j.kelas.namaKelas,j.dosen.karyawan.namaKaryawan,j.matakuliahKurikulum.jumlahSks,j.jamMulai,j.jamSelesai,j.akses,j.ruangan.namaRuangan, j.hari.namaHari,j.matakuliahKurikulum.matakuliah.namaMatakuliahEnglish)from Jadwal j where j.dosen = :dosen and j.status =:id and j.tahunAkademik= :tahun order by j.matakuliahKurikulum.matakuliah.namaMatakuliahEnglish asc")
    List<ScheduleDto> lecturerAssesment(@Param("dosen") Dosen dosen, @Param("id") StatusRecord statusRecord, @Param("tahun") TahunAkademik t);

    @Query("select new id.ac.tazkia.smilemahasiswa.dto.schedule.ScheduleDto(j.id,j.matakuliahKurikulum.matakuliah.namaMatakuliah,j.kelas.namaKelas,j.dosen.karyawan.namaKaryawan,j.matakuliahKurikulum.jumlahSks,j.jamMulai,j.jamSelesai,j.akses,j.ruangan.namaRuangan, j.hari.namaHari,j.matakuliahKurikulum.matakuliah.namaMatakuliahEnglish)from Jadwal j where j.prodi = :prodi and j.status not in (:id) and j.tahunAkademik= :tahun and j.matakuliahKurikulum.matakuliah.namaMatakuliah like %:search% order by j.matakuliahKurikulum.matakuliah.namaMatakuliahEnglish asc")
    List<ScheduleDto> assesmentSearch(@Param("prodi") Prodi prodi, @Param("id") List<StatusRecord> statusRecord, @Param("tahun") TahunAkademik t,@Param("search")String search);

    @Query(value = "SELECT a.id AS krs, a.id_mahasiswa AS mahasiswa, b.nama,  b.nim ,COALESCE(absensi_mahasiswa,0) AS absmahasiswa ,COALESCE(absen_dosen,0) AS absdosen, COALESCE(ROUND(((absensi_mahasiswa * 100)/absen_dosen),2), 0) AS absen, COALESCE(ROUND(((((absensi_mahasiswa * 100)/absen_dosen)*f.bobot_presensi)/100),2),0) AS nilaiabsen, ROUND((COALESCE(e.sds,0) * 100)/(COALESCE(g.sds,0)*10),2) AS sds,a.nilai_uts AS uts,a.nilai_uas AS uas,a.nilai_akhir AS akhir,a.grade AS grade FROM krs_detail AS a INNER JOIN mahasiswa AS b ON a.id_mahasiswa=b.id  LEFT JOIN(SELECT COUNT(a.id)AS absensi_mahasiswa,id_krs_detail FROM presensi_mahasiswa AS a INNER JOIN sesi_kuliah AS b ON a.id_sesi_kuliah=b.id INNER JOIN  presensi_dosen AS c ON b.id_presensi_dosen=c.id WHERE a.status_presensi NOT IN ('MANGKIR','TERLAMBAT') AND c.id_jadwal=?1 AND a.status='AKTIF' GROUP BY id_krs_detail) AS c ON a.id=c.id_krs_detail LEFT JOIN(SELECT COUNT(id)AS absen_dosen,id_jadwal FROM presensi_dosen WHERE id_jadwal=?1 AND STATUS='AKTIF') d ON a.id_jadwal=d.id_jadwal LEFT JOIN(SELECT COUNT(b.id_mahasiswa)AS sds,b.id_mahasiswa,d.sds AS bobotsds FROM presensi_mahasiswa AS a INNER JOIN krs_detail AS b ON a.id_krs_detail=b.id INNER JOIN jadwal AS c ON b.id_jadwal=c.id INNER JOIN matakuliah_kurikulum AS d ON c.id_matakuliah_kurikulum = d.id INNER JOIN matakuliah AS e ON d.id_matakuliah = e.id WHERE LEFT(e.kode_matakuliah,3)='SDS' AND a.status='AKTIF' AND a.status_presensi NOT IN ('MANGKIR','TERLAMBAT') AND c.id_tahun_akademik=?2 GROUP BY a.id_mahasiswa)e ON a.id_mahasiswa=e.id_mahasiswa INNER JOIN jadwal AS f ON a.id_jadwal = f.id INNER JOIN matakuliah_kurikulum AS g ON f.id_matakuliah_kurikulum=g.id WHERE a.id_jadwal=?1 AND a.status='AKTIF'", nativeQuery = true)
    List<ScoreDto>  scoreInput(Jadwal jadwal, TahunAkademik tahunAkademik);

    @Query(value = "SELECT a.*  FROM (SELECT a.id,a.id_mahasiswa,b.nim,b.nama,c.nama_prodi FROM krs AS a INNER JOIN mahasiswa AS b ON a.id_mahasiswa=b.id INNER JOIN prodi AS c ON a.id_prodi=c.id WHERE a.id_tahun_akademik=?2 AND a.status='AKTIF' AND a.id_prodi=?1 GROUP BY id_mahasiswa)a LEFT JOIN (SELECT a.* FROM krs AS a INNER JOIN krs_detail AS b ON a.id=b.id_krs INNER JOIN jadwal AS c ON b.id_jadwal=c.id INNER JOIN matakuliah_kurikulum AS d ON c.id_matakuliah_kurikulum=d.id  INNER JOIN (SELECT * FROM matakuliah WHERE LEFT(kode_matakuliah,3)='SDS') e ON d.id_matakuliah=e.id WHERE a.id_tahun_akademik=?2 AND a.status='AKTIF' AND a.id_prodi=?1 GROUP BY a.id)b ON a.id=b.id WHERE b.id IS NULL ORDER BY a.nim",nativeQuery = true)
    List<Object[]> cariMahasiswaBelumSds(Prodi prodi, TahunAkademik tahunAkademik);
}
