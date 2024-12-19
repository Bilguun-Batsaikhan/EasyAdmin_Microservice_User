package com.certimeter.myapp.repository.jpa;

import com.certimeter.myapp.resourcemodel.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    User findUserById(Long id);

    User findUserByUsername(String username);

    User findUserByEmail(String email);

    Page<User> findByUsernameContaining(String username, Pageable pageable);

    Page<User> findByUsernameStartingWith(String username, Pageable pageable);

    Page<User> findByUsernameNotContaining(String username, Pageable pageable);

    Page<User> findByUsernameEndingWith(String username, Pageable pageable);

    Page<User> findByUsername(String username, Pageable pageable);

    Page<User> findByUsernameNot(String username, Pageable pageable);

    boolean existsByUsername(String username);
}
