package spd.spring.camp.team3.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import spd.spring.camp.team3.domain.entities.User;
import spd.spring.camp.team3.domain.enums.UserRole;
import spd.spring.camp.team3.exceptions.UserNotFoundException;
import spd.spring.camp.team3.repositories.UserRepository;

import java.util.HashSet;
import java.util.Set;

@Service
@Qualifier("customUserDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String name) throws UserNotFoundException {
        try {
            User domainUser = userRepository.findByUsername(name);
            UserRole role = domainUser.getRole();
            Set<GrantedAuthority> authorities = new HashSet<>();
            authorities.add(new SimpleGrantedAuthority(role.toString()));
            return new CustomUserDetails(domainUser, authorities);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}