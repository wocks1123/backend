package com.swygbro.trip.backend.domain.user.domain;

import com.swygbro.trip.backend.global.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

@Entity
@Getter
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String account;

    private String email;

    private String password;

    protected User() {
    }

    public User(String account, String email, String password) {
        this.account = account;
        this.email = email;
        this.password = password;
    }

}
