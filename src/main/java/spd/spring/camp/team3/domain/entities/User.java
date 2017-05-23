package spd.spring.camp.team3.domain.entities;

import lombok.Getter;
import lombok.Setter;
import spd.spring.camp.team3.domain.enums.UserRole;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String username;
    private String email;
    private String password;
    
    @Enumerated
    private UserRole role;
}