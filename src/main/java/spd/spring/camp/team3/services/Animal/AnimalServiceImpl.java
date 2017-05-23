package spd.spring.camp.team3.services.Animal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import spd.spring.camp.team3.domain.entities.Animal;
import spd.spring.camp.team3.repositories.AnimalRepository;

import java.io.*;

@Service
public class AnimalServiceImpl implements AnimalService {

    private final Environment environment;
    private AnimalRepository animalRepository;

    @Autowired
    public AnimalServiceImpl(Environment environment) {
        this.environment = environment;
    }

    @Autowired
    public void setAnimalRepository(AnimalRepository animalRepository) {
        this.animalRepository = animalRepository;
    }

    @Override
    public Iterable<Animal> listAllAnimals() {
        return animalRepository.findAll();
    }

    @Override
    public Animal getAnimalById(Integer id) {
        return animalRepository.findOne(id);
    }

    @Override
    public Animal saveAnimal(Animal animal) {
        String directory = environment.getProperty("zoo.paths.uploadedFiles");
        String filepath = directory + animal.getImagePath();
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             InputStream inputStream = new BufferedInputStream(new FileInputStream(new File(filepath))) ) {
            int data;
            while ( (data = inputStream.read()) != -1 ) {
                outputStream.write(data);
            }
            byte[] content = outputStream.toByteArray();
            animal.setImage(content);
        } catch (Exception e) {}
        return animalRepository.save(animal);
    }

    @Override
    public void deleteAnimal(Integer id) {
        animalRepository.delete(id);
    }
}