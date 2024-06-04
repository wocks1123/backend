package com.swygbro.trip.backend.domain.user.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserLanguage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnore
    private User user;

    @Enumerated(EnumType.STRING)
    private Language language;

    public UserLanguage(User user, String language) {
        this.user = user;
        this.language = Language.valueOf(language);
    }

}
