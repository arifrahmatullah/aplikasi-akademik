package id.ac.tazkia.smilemahasiswa.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
    @Entity
    @Data
    public class Prodi {

        @Id
        @GeneratedValue(generator = "uuid" )
        @GenericGenerator(name = "uuid", strategy = "uuid2")
        private String id;

        @ManyToOne
        @JoinColumn(name = "id_jurusan")
        private Jurusan idJurusan;

        @ManyToOne
        @JoinColumn(name = "id_jenjang")
        private Jenjang idJenjang;

        private String kodeProdi;

        private String namaProdi;

        private String keterangan;


        @Enumerated(EnumType.STRING)
        private StatusRecord status = StatusRecord.AKTIF;

//        @ManyToMany(fetch = FetchType.EAGER)
//        @JoinTable(name = "prodi_program",
//                joinColumns=@JoinColumn(name = "id_prodi"),
//                inverseJoinColumns = @JoinColumn(name = "id_program"))
//        private Set<Program> programs= new HashSet<>();

}
