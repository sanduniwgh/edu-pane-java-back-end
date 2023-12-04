package lk.ijse.dep11.to.responce;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class LecturerResponseTO {
    private Integer id;
    private String name;
    private String designation;
    private String qualifications;
    private String type;
    private String pictureUrl;
    private String linkedin;

}
