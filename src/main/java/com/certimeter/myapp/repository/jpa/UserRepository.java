package com.certimeter.myapp.repository.jpa;

import com.certimeter.myapp.resourcemodel.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findUserById(Long id);

    User findUserByUsername(String username);

    User findUserByEmail(String email);

    Page<User> findByUsernameContaining(String username, Pageable pageable);

    boolean existsByUsername(String username);
}
