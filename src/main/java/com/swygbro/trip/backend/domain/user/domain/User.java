package com.swygbro.trip.backend.domain.user.domain;

import com.swygbro.trip.backend.domain.user.dto.CreateUserRequest;
import com.swygbro.trip.backend.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
@AllArgsConstructor
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 100, nullable = false)
    private String email;

    @Column(unique = true, length = 20, nullable = false)
    private String nickname;

    @Column(length = 20, nullable = false)
    private String name;

    @Column(unique = true, length = 20, nullable = false)
    private String phone;

    @Enumerated(EnumType.STRING)
    private Nationality nationality;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private SignUpType signUpType;

    @Enumerated
    private UserRole userRole;

    public User(CreateUserRequest dto, SignUpType signUpType, String encodedPassword) {
        this.email = dto.getEmail();
        this.nickname = dto.getNickname();
        this.name = dto.getName();
        this.phone = dto.getPhone();
        this.nationality = dto.getNationality();
        this.gender = dto.getGender();
        this.password = encodedPassword;
        this.signUpType = signUpType;
        this.userRole = UserRole.USER;
    }

}
