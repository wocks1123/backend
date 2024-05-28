package com.swygbro.trip.backend.domain.review.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "review_id")
    @Setter
    private Review review;

    @Column(nullable = false, length = 250)
    private String imageUrl;

    public ReviewImage(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
