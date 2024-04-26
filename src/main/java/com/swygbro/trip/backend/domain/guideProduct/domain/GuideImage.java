package com.swygbro.trip.backend.domain.guideProduct.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class GuideImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private GuideProduct product;

    @Column(name = "image_url", length = 250)
    private String url;

    public GuideImage(String url) {
        this.url = url;
    }

    public void setProduct(GuideProduct product) {
        this.product = product;
    }
}
