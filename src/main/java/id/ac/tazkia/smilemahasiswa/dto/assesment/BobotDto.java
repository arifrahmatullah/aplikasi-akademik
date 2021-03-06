package id.ac.tazkia.smilemahasiswa.dto.assesment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BobotDto {
    private String id;
    private String nama;
    private BigDecimal bobot;
}
