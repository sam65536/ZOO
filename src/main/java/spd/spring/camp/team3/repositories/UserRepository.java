package spd.spring.camp.team3.repositories;

import org.springframework.data.repository.CrudRepository;
import spd.spring.camp.team3.domain.entities.User;

public interface UserRepository extends CrudRepository<User, Integer> {
    User findByUsername(String username);
}