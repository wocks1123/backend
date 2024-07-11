package com.swygbro.trip.backend.domain.user.domain;

import com.swygbro.trip.backend.domain.user.dto.CreateGoogleUserRequest;
import com.swygbro.trip.backend.domain.user.dto.CreateUserRequest;
import com.swygbro.trip.backend.domain.user.dto.GoogleUserInfo;
import com.swygbro.trip.backend.domain.user.dto.UpdateUserRequest;
import com.swygbro.trip.backend.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    @Column(length = 100)
    private String profile = "";

    @Setter
    @Column(length = 100, nullable = false)
    private String profileImageUrl = "https://metthew-s3.s3.us-east-2.amazonaws.com/guide/default_profile4x.png";

    @Column(unique = true, length = 20)
    private String phone;

    @Column(length = 100)
    private String location;

    @Enumerated(EnumType.STRING)
    private Nationality nationality;

    @Temporal(TemporalType.DATE)
    private LocalDate birthdate;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserLanguage> userLanguages = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private SignUpType signUpType;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    // 이메일로 회원가입
    public User(CreateUserRequest dto, String encodedPassword) {
        this.email = dto.getEmail();
        this.nickname = dto.getNickname();
        this.name = dto.getName();
        this.phone = dto.getPhone();
        this.location = dto.getLocation();
        this.nationality = dto.getNationality();
        this.birthdate = dto.getBirthdate();
        this.gender = dto.getGender();
        this.password = encodedPassword;
        this.userRole = UserRole.USER;
        this.signUpType = SignUpType.Local;
    }

    public User(CreateGoogleUserRequest dto, GoogleUserInfo userInfo) {
        this.email = userInfo.getEmail();
        this.nickname = dto.getNickname();
        this.name = dto.getName();
        this.phone = dto.getPhone();
        this.location = dto.getLocation();
        this.nationality = dto.getNationality();
        this.birthdate = LocalDate.parse(dto.getBirthdate());
        this.gender = dto.getGender();
        this.userRole = UserRole.USER;
        this.profileImageUrl = userInfo.getPicture();
        this.signUpType = SignUpType.Google;
    }

    public void update(UpdateUserRequest dto) {
        this.profile = dto.getProfile();

        if (dto.getNickname() != null) {
            this.nickname = dto.getNickname();
        }

        if (dto.getLanguages() != null) {
            this.userLanguages.clear();
            dto.getLanguages().forEach(language -> this.userLanguages.add(new UserLanguage(this, language)));
        }

    }

}
