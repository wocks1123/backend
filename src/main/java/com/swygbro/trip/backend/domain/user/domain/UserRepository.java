package com.swygbro.trip.backend.domain.user.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByAccount(String account);

    boolean existsByAccount(String account);

    boolean existsByNickname(String nickname);

    boolean existsByName(String name);

    boolean existsByPhone(String phone);

    boolean existsByEmail(String email);
}
