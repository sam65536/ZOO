package spd.spring.camp.team3.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import spd.spring.camp.team3.config.SecurityConfig;
import spd.spring.camp.team3.domain.entities.User;
import spd.spring.camp.team3.domain.enums.UserRole;
import spd.spring.camp.team3.exceptions.UserNotFoundException;
import spd.spring.camp.team3.security.CustomUserDetails;
import spd.spring.camp.team3.security.CustomUserDetailsService;
import spd.spring.camp.team3.security.annotations.AllowedForAdmin;
import spd.spring.camp.team3.services.User.UserService;

@Controller
@RequestMapping(value = "/users")
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;

    @Autowired
    public UserController(UserService userService, AuthenticationManager authenticationManager, CustomUserDetailsService customUserDetailsService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.customUserDetailsService = customUserDetailsService;
    }

    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    @AllowedForAdmin
    public String admin(Model model) {
        model.addAttribute("users", userService.listAllUsers());
        return "users/admin-dashboard";
    }

    @RequestMapping(value = "/new", method = RequestMethod.GET)
    public String signUp(Model model) {
        model.addAttribute("user", new User());
        return "users/create";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String signUp(@ModelAttribute User user) {
        try {
            user.setRole(UserRole.ROLE_USER);
            String password = user.getPassword();
            user.setPassword(SecurityConfig.encoder.encode(user.getPassword()));
            userService.saveUser(user);
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getUsername());
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
            authenticationManager.authenticate(auth);
            SecurityContextHolder.getContext().setAuthentication(auth);
            return "redirect:/users/me";
        } catch (Exception e) {
            return "error";
        }
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login() {
        return "login";
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    @AllowedForAdmin
    public String showUserInfo(@PathVariable("id") int id, Model model) {
        User user = userService.getUserById(id);
        if (user == null) {
            throw new UserNotFoundException();
        }
        model.addAttribute("user", user);
        return "users/show";
    }

    @RequestMapping(value = "/me", method = RequestMethod.GET)
    public String showActiveProfile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails myUser = (CustomUserDetails) authentication.getPrincipal();
        User user = userService.getUserById(myUser.getUser().getId());
        model.addAttribute("user", user);
        return "users/show";
    }


    @RequestMapping(value = "{id}/remove", method = RequestMethod.GET)
    @AllowedForAdmin
    public String remove(@PathVariable("id") Integer id) {
        userService.deleteUser(id);
        return "redirect:/users/admin";
	}

    @RequestMapping(value = "{id}/edit", method = RequestMethod.GET)
    @AllowedForAdmin
    public String edit(@PathVariable("id") Integer id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "users/edit";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    @AllowedForAdmin
    public String editSave(@ModelAttribute User user) {
        userService.saveUser(user);
        return "redirect:/users/admin";
    }

    @GetMapping(value = "/checkUsername")
    public ResponseEntity<?> checkUsername(@RequestParam("username") String username) {
        User user = userService.getUserByUsername(username);
        ResponseEntity<?> result = (user == null) ? new ResponseEntity<>(HttpStatus.OK) : new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return result;
    }
}