package spd.spring.camp.team3.bootstrap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import spd.spring.camp.team3.config.SecurityConfig;
import spd.spring.camp.team3.repositories.AnimalRepository;
import spd.spring.camp.team3.repositories.UserRepository;

import java.io.*;
import java.nio.file.Paths;

@Component
public class MvcLoader implements CommandLineRunner {

    private final Environment environment;
    private final UserRepository userRepository;
    private final AnimalRepository animalRepository;

    @Autowired
    public MvcLoader(UserRepository userRepository, Environment environment, AnimalRepository animalRepository) {
        this.userRepository = userRepository;
        this.environment = environment;
        this.animalRepository = animalRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        userRepository.findAll().forEach(user -> {
            String password = user.getPassword();
            user.setPassword(SecurityConfig.encoder.encode(password));
            userRepository.save(user);
        });
        animalRepository.findAll().forEach(animal -> {
            String directory = environment.getProperty("zoo.paths.uploadedFiles");
            String filepath = Paths.get(directory, animal.getImagePath()).toString();
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                 InputStream inputStream = new BufferedInputStream(new FileInputStream(new File(filepath)))) {
                int data;
                while ( (data = inputStream.read()) != -1 ) {
                    outputStream.write(data);
                }
                byte [] content = outputStream.toByteArray();
                animal.setImage(content);
                animalRepository.save(animal);
            } catch (Exception e) {}
        });
    }
}