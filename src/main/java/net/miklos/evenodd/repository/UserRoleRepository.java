package net.miklos.evenodd.repository;

import net.miklos.evenodd.model.Admin;
import net.miklos.evenodd.model.UserRoles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRoleRepository extends JpaRepository<UserRoles, Integer> {

    List<UserRoles> findByUserId(Admin UserId);

}
