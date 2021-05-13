package com.ubbcluj.sd.filestorage.requests.persistance;

import com.ubbcluj.sd.filestorage.requests.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findUserBySecret(String secret);
}
