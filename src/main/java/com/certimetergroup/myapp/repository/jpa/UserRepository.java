package com.certimetergroup.myapp.repository.jpa;

import com.certimetergroup.myapp.resourcemodel.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findUserById(Long id);

    User findUserByUsernameAndPassword(String username, String password);

    User findUserByEmailAndPassword(String email, String password);
}
