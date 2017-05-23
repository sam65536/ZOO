package spd.spring.camp.team3.services.Animal;

import spd.spring.camp.team3.domain.entities.Animal;

public interface AnimalService {
    Iterable<Animal> listAllAnimals();

    Animal getAnimalById(Integer id);

    Animal saveAnimal(Animal animal);

    void deleteAnimal(Integer id);
}