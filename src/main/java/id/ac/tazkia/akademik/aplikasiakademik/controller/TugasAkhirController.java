package id.ac.tazkia.akademik.aplikasiakademik.controller;

import id.ac.tazkia.akademik.aplikasiakademik.dao.*;
import id.ac.tazkia.akademik.aplikasiakademik.entity.*;
import id.ac.tazkia.akademik.aplikasiakademik.service.CurrentUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Controller
public class TugasAkhirController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TugasAkhirController.class);

    @Autowired
    private MatakuliahKurikulumDao matakuliahKurikulumDao;
    @Autowired
    private KrsDetailDao krsDetailDao;
    @Autowired
    private CurrentUserService currentUserService;
    @Autowired
    private MahasiswaDao mahasiswaDao;
    @Autowired
    private NoteDao noteDao;
    @Autowired
    private TahunAkademikDao tahunAkademikDao;
    @Autowired
    private DosenDao dosenDao;
    @Autowired
    private ProdiDao prodiDao;

    @Value("${upload.note}")
    private String uploadFolder;

    @ModelAttribute("dosen")
    public Iterable<Dosen> dosen() {
        return dosenDao.findByStatusNotIn(StatusRecord.HAPUS);
    }

    @ModelAttribute("prodi")
    public Iterable<Prodi> prodi() {
        return prodiDao.findByStatus(StatusRecord.AKTIF);
    }

    @ModelAttribute("tahun")
    public Iterable<TahunAkademik> tahun() {
        return tahunAkademikDao.findByStatusNotInOrderByTahunDesc(StatusRecord.HAPUS);
    }

    @GetMapping("/tugasakhir/mahasiswa")
    public String rejected(Model model, Authentication authentication){
        User user = currentUserService.currentUser(authentication);
        Mahasiswa mahasiswa = mahasiswaDao.findByUser(user);
        Note waiting = noteDao.findByMahasiswaAndStatus(mahasiswa,StatusApprove.WAITING);
        Note approved = noteDao.findByMahasiswaAndStatus(mahasiswa,StatusApprove.APPROVED);


        if (waiting != null){
            model.addAttribute("waiting",waiting);
        }

        if (approved != null){
            model.addAttribute("approved",approved);
        }

        List<Note> rejected = noteDao.findByMahasiswaOrderByTanggalInputDesc(mahasiswa);
        if (rejected == null || rejected.isEmpty()) {

            return "tugasakhir/register";

        }
        model.addAttribute("mahasiswa" , mahasiswa);
        model.addAttribute("rejected" , rejected);

        return "tugasakhir/mahasiswa";

    }

    @GetMapping("/tugasakhir/register")
    public String register(Model model, Authentication authentication){
        User user = currentUserService.currentUser(authentication);
        Mahasiswa mahasiswa = mahasiswaDao.findByUser(user);

        Note note = noteDao.findByMahasiswaAndStatus(mahasiswa,StatusApprove.WAITING);
        Note approve = noteDao.findByMahasiswaAndStatus(mahasiswa,StatusApprove.APPROVED);
        List<Note> empty = noteDao.findByMahasiswa(mahasiswa);

        if (empty == null || empty.isEmpty()) {

            return "tugasakhir/register";

        }else {
            if (approve != null) {
                return "redirect:tugasakhir/mahasiswa";
            }

            if (approve == null && note == null) {
                return "redirect:tugasakhir/mahasiswa";
            }

            if (note != null) {
                return "redirect:tugasakhir/mahasiswa";
            }
        }
        return "tugasakhir/register";

    }

    @GetMapping("/tugasakhir/alertpage")
    public void alertPage(){

    }

    @GetMapping("/tugasakhir/konsepnote")
    public String formNote(Model model, Authentication authentication, @RequestParam(required = false) String id){
        User user = currentUserService.currentUser(authentication);
        Mahasiswa mahasiswa = mahasiswaDao.findByUser(user);

        model.addAttribute("mahasiswa", mahasiswa);

        Note validasi = noteDao.findByTahunAkademikAndMahasiswaAndStatusNotIn(tahunAkademikDao.findByStatus(StatusRecord.AKTIF),mahasiswa,StatusApprove.REJECTED);

//



        if (id == null || id.isEmpty() || !StringUtils.hasText(id)) {
            Iterable<String> matakuliahKurikulum = matakuliahKurikulumDao.CariMatakuliahKonsep(StatusRecord.AKTIF, mahasiswa.getKurikulum(), StatusRecord.AKTIF);
            System.out.println(matakuliahKurikulum);
            if (mahasiswa.getKurikulum() != null) {
                for (String mk : matakuliahKurikulum) {
                    List<KrsDetail> konsepNote = krsDetailDao.findByMatakuliahKurikulumAndMahasiswaAndStatusAndNilaiAkhirGreaterThan(matakuliahKurikulumDao.findById(mk).get(), mahasiswa, StatusRecord.AKTIF, new BigDecimal(60));


                    if (konsepNote == null || konsepNote.isEmpty()) {
                        return "redirect:alertpage";
                    }
                }
            }
            if (validasi == null){
                model.addAttribute("note",new Note());
                return "tugasakhir/konsepnote";
            }

            if (validasi != null){
                return "redirect:register";
            }
            model.addAttribute("note",new Note());
        }else {
            Iterable<String> matakuliahKurikulum = matakuliahKurikulumDao.CariMatakuliahKonsep(StatusRecord.AKTIF, mahasiswa.getKurikulum(), StatusRecord.AKTIF);
            System.out.println(matakuliahKurikulum);
            if (mahasiswa.getKurikulum() != null) {
                for (String mk : matakuliahKurikulum) {
                    List<KrsDetail> note = krsDetailDao.findByMatakuliahKurikulumAndMahasiswaAndStatusAndNilaiAkhirGreaterThan(matakuliahKurikulumDao.findById(mk).get(), mahasiswa, StatusRecord.AKTIF, new BigDecimal(60));
                    System.out.println();
                    if (note == null || note.isEmpty()) {
                        return "redirect:alertpage";
                    }
                }
                model.addAttribute("note",noteDao.findById(id).get());
            }
        }

        return "tugasakhir/konsepnote";

    }

    @PostMapping("/tugasakhir/konsepnote")
    public String uploadBukti(@Valid Note note,
                              BindingResult error, MultipartFile file,
                              Authentication currentUser) throws Exception {
        User user = currentUserService.currentUser(currentUser);
        Mahasiswa mahasiswa = mahasiswaDao.findByUser(user);

        if (!file.isEmpty() || file != null) {
            String namaFile = file.getName();
            String jenisFile = file.getContentType();
            String namaAsli = file.getOriginalFilename();
            Long ukuran = file.getSize();

//        memisahkan extensi
            String extension = "";

            int i = namaAsli.lastIndexOf('.');
            int p = Math.max(namaAsli.lastIndexOf('/'), namaAsli.lastIndexOf('\\'));

            if (i > p) {
                extension = namaAsli.substring(i + 1);
            }


            String idFile = UUID.randomUUID().toString();
            String lokasiUpload = uploadFolder + File.separator + mahasiswa.getNim();
            new File(lokasiUpload).mkdirs();
            File tujuan = new File(lokasiUpload + File.separator + idFile + "." + extension);
            file.transferTo(tujuan);


            note.setMahasiswa(mahasiswa);
            note.setFileUpload(idFile + "." + extension);
            note.setTanggalInput(LocalDate.now());
            note.setStatus(StatusApprove.WAITING);
            note.setTahunAkademik(tahunAkademikDao.findByStatus(StatusRecord.AKTIF));
            noteDao.save(note);
        }else {
            note.setMahasiswa(mahasiswa);
            note.setFileUpload(note.getFileUpload());
            note.setTanggalInput(LocalDate.now());
            note.setStatus(StatusApprove.WAITING);
            note.setTahunAkademik(tahunAkademikDao.findByStatus(StatusRecord.AKTIF));
            noteDao.save(note);
        }

        return "redirect:mahasiswa";

    }


    @GetMapping("/tugasakhir/list")
    public void list(Model model, @RequestParam(required = false) TahunAkademik tahun, Pageable pageable,
                     @RequestParam(required = false) Prodi prodi, @RequestParam(required = false) StatusApprove status){

        if (tahun != null){
            if(status != null) {
                model.addAttribute("selectedTahun",tahun);
                model.addAttribute("selectedProdi",prodi);
                model.addAttribute("status",status);
                model.addAttribute("listNote", noteDao.findByTahunAkademikAndMahasiswaIdProdiAndStatus(tahun,prodi,status,pageable));
            }else {
                model.addAttribute("selectedTahun",tahun);
                model.addAttribute("selectedProdi",prodi);
                model.addAttribute("listNote", noteDao.findByTahunAkademikAndMahasiswaIdProdiAndStatusNotIn(tahun,prodi,StatusApprove.HAPUS,pageable));
            }

        }

    }

    @GetMapping("/tugasakhir/form")
    public void form(Model model,@RequestParam(value = "id", name = "id") Note note){
        model.addAttribute("note", note);
    }

    @GetMapping("/uploaded/{note}/bukti/")
    public ResponseEntity<byte[]> tampilkanEvidence(@PathVariable Note note, Model model) throws Exception {
        String lokasiFile = uploadFolder + File.separator + note.getMahasiswa().getNim()
                + File.separator + note.getFileUpload();
        LOGGER.debug("Lokasi file bukti : {}", lokasiFile);

        try {
            HttpHeaders headers = new HttpHeaders();
            if (note.getFileUpload().toLowerCase().endsWith("jpeg") || note.getFileUpload().toLowerCase().endsWith("jpg")) {
                headers.setContentType(MediaType.IMAGE_JPEG);
            } else if (note.getFileUpload().toLowerCase().endsWith("png")) {
                headers.setContentType(MediaType.IMAGE_PNG);
            } else if (note.getFileUpload().toLowerCase().endsWith("pdf")) {
                headers.setContentType(MediaType.APPLICATION_PDF);
            } else {
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            }
            byte[] data = Files.readAllBytes(Paths.get(lokasiFile));
            return new ResponseEntity<byte[]>(data, headers, HttpStatus.OK);
        } catch (Exception err) {
            LOGGER.warn(err.getMessage(), err);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();


        }

    }

    @PostMapping("/tugasakhir/approve")
    public String Approve(Authentication authentication,@RequestParam String id,@RequestParam String keterangan){
        User user = currentUserService.currentUser(authentication);
        Note note = noteDao.findById(id).get();
        note.setStatus(StatusApprove.APPROVED);
        note.setKeterangan(keterangan);
        note.setTanggalApprove(LocalDate.now());
        note.setUserApprove(user);
        noteDao.save(note);

        return "redirect:list?tahun="+note.getTahunAkademik().getId()+"&prodi="+note.getMahasiswa().getIdProdi().getId();
    }

    @PostMapping("/tugasakhir/reject")
    public String Reject(Authentication authentication,@RequestParam String id,@RequestParam String keterangan){
        User user = currentUserService.currentUser(authentication);
        Note note = noteDao.findById(id).get();
        note.setStatus(StatusApprove.REJECTED);
        note.setKeterangan(keterangan);
        note.setTanggalReject(LocalDate.now());
        note.setUserReject(user);
        noteDao.save(note);

        return "redirect:list?tahun="+note.getTahunAkademik().getId()+"&prodi="+note.getMahasiswa().getIdProdi().getId();
    }

}