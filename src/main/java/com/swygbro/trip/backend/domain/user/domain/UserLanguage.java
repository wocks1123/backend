package com.swygbro.trip.backend.domain.user.domain;


import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class UserLanguage {

    @Id
    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private Language language;
    
}
