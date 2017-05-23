package spd.spring.camp.team3.domain.entities;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import spd.spring.camp.team3.domain.enums.AnimalGender;
import spd.spring.camp.team3.domain.enums.AnimalType;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "animals")
public class Animal implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated
    private AnimalGender gender;

    @Enumerated
    private AnimalType type;

    private String name;
    private Integer age;
    private String imagePath;

    @Lob
    private byte[] image;
}