package net.miklos.evenodd.repository;

import net.miklos.evenodd.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AdminRepository extends JpaRepository<Admin, Integer> {

    Admin findByUserName(String username);

    Admin findByAdminID(String adminID);

}
