package com.certimetergroup.myapp.repository.jpa;

import com.certimetergroup.myapp.resourcemodel.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findUserById(Long id);

    User findUserByUsername(String username);

    User findUserByEmail(String email);

    boolean existsByUsername(String username);
}
