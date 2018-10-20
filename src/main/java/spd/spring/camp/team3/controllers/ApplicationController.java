package spd.spring.camp.team3.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import spd.spring.camp.team3.security.CustomUserDetails;
import spd.spring.camp.team3.domain.enums.UserRole;

@Controller
public class ApplicationController {

    @RequestMapping("/")
    public String root() {
        return "redirect:/animals";
    }

    @RequestMapping("/signedin")
    public String signedIn(Authentication authentication) {
        CustomUserDetails principal = (authentication != null) ? (CustomUserDetails) authentication.getPrincipal() : null;
        if (principal != null) {
            String authority = principal.getAuthorities().iterator().next().getAuthority();
            switch (UserRole.valueOf(authority)) {
                case ROLE_ADMIN:
                    return "redirect:/users/admin";
                case ROLE_USER:
                    return "redirect:/users/me";
            }
        }
        return "/";
    }
}