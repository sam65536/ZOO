package spd.spring.camp.team3.services.User;

import spd.spring.camp.team3.domain.entities.User;

public interface UserService {
    Iterable<User> listAllUsers();

    User getUserById(Integer id);

    User getUserByUsername(String username);

    User saveUser(User user);

    void deleteUser(Integer id);
}
