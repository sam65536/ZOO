package spd.spring.camp.team3.exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "No such User")
public class UserNotFoundException extends RuntimeException {
}