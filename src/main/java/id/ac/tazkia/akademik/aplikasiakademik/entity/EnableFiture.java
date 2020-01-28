package id.ac.tazkia.akademik.aplikasiakademik.entity;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@Entity
public class EnableFiture {
    @Id
    @GeneratedValue(generator = "uuid" )
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "id_mahasiswa")
    private Mahasiswa mahasiswa;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "id_tahun_akademik")
    private TahunAkademik tahunAkademik;

    @Enumerated(EnumType.STRING)
    private StatusRecord fitur;

    private String enable;

    private String keterangan;


}
