package spd.spring.camp.team3.security.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.access.prepost.PreAuthorize;

@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@PreAuthorize(AllowedForSystemUsers.condition)
public @interface AllowedForSystemUsers {
    String condition = "hasAnyRole(T(spd.spring.camp.team3.domain.enums.UserRole).values())";
}