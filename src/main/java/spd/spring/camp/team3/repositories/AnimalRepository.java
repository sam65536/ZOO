package spd.spring.camp.team3.repositories;

import org.springframework.data.repository.CrudRepository;
import spd.spring.camp.team3.domain.entities.Animal;

public interface AnimalRepository extends CrudRepository<Animal, Integer> {
}