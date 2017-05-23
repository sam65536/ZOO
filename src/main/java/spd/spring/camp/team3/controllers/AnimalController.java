package spd.spring.camp.team3.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import spd.spring.camp.team3.domain.entities.Animal;
import spd.spring.camp.team3.security.annotations.AllowedForSystemUsers;
import spd.spring.camp.team3.services.Animal.AnimalService;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.file.Paths;
import java.util.List;

@Controller
@RequestMapping("/animals")
public class AnimalController {

    private final int ANIMALS_LIST_PAGE_SIZE = 10;

    private final AnimalService animalService;
    private final Environment environment;

    @Autowired
    public AnimalController(AnimalService animalService, Environment environment) {
        this.animalService = animalService;
        this.environment = environment;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String animalsListRedirect(HttpServletRequest request) {
        request.getSession().setAttribute("animals", null);
        return "redirect:/animals/page/1";
    }

    @RequestMapping(value = "/page/{pageNumber}", method = RequestMethod.GET)
    public String showPagedAnimalsPage(HttpServletRequest request, @PathVariable Integer pageNumber, Model model) {
        PagedListHolder<?> pagedListHolder = (PagedListHolder<?>) request.getSession().getAttribute("animals");
        if (pagedListHolder == null) {
            pagedListHolder = new PagedListHolder((List) animalService.listAllAnimals());
            pagedListHolder.setPageSize(ANIMALS_LIST_PAGE_SIZE);
        } else {
            final int goToPage = pageNumber - 1;
            if (goToPage <= pagedListHolder.getPageCount() && goToPage >=0) {
                pagedListHolder.setPage(goToPage);
            }
        }
        request.getSession().setAttribute("animals", pagedListHolder);

        int current = pagedListHolder.getPage() + 1;
        int begin = Math.max(1, current - ANIMALS_LIST_PAGE_SIZE);
        int end = Math.min(begin + 5, pagedListHolder.getPageCount());
        int totalPageCount = pagedListHolder.getPageCount();
        String baseUrl = "/animals/page/";

        model.addAttribute("beginIndex", begin);
        model.addAttribute("endIndex", end);
        model.addAttribute("currentIndex", current);
        model.addAttribute("totalPageCount", totalPageCount);
        model.addAttribute("baseUrl", baseUrl);
        model.addAttribute("animals", pagedListHolder.getPageList());

        return "animals/index";
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public String showAnimal(@PathVariable Integer id, Model model) {
        model.addAttribute("animal", animalService.getAnimalById(id));
        return "animals/show";
    }

    @RequestMapping(value = "/new", method = RequestMethod.GET)
    @AllowedForSystemUsers
    public String showNewAnimalForm(Model model) {
        model.addAttribute("animal", new Animal());
        return "animals/create";
    }

    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> uploadImage (@RequestParam("imagePath") MultipartFile uploadFile) {
        String filename = uploadFile.getOriginalFilename();
        String directory = environment.getProperty("zoo.paths.uploadedFiles");
        String filepath = Paths.get(directory, filename).toString();
        try ( BufferedOutputStream stream =
                      new BufferedOutputStream(new FileOutputStream(new File(filepath))) ) {

            stream.write(uploadFile.getBytes());
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/images/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> viewImage(@PathVariable Integer id) {
        Animal animal = animalService.getAnimalById(id);
        byte[] content = animal.getImage();
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(content, headers, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST)
    public String save(@ModelAttribute Animal animal) {
        animalService.saveAnimal(animal);
        return "redirect:/animals/" + animal.getId();
    }

    @RequestMapping("/edit/{id}")
    @AllowedForSystemUsers
    public String edit(@PathVariable Integer id, Model model) {
        model.addAttribute("animal", animalService.getAnimalById(id));
        return "animals/edit";
    }

    @RequestMapping(value = "{animal_id}", method = RequestMethod.POST)
    @AllowedForSystemUsers
    public String editSave(@ModelAttribute("animal") Animal animal) {
        animalService.saveAnimal(animal);
        return "redirect:/animals/{animal_id}";
    }

    @RequestMapping("/delete/{id}")
    @AllowedForSystemUsers
    public String delete(@PathVariable Integer id) {
        animalService.deleteAnimal(id);
        return "redirect:/animals";
    }
}