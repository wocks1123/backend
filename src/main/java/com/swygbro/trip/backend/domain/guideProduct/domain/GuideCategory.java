package com.swygbro.trip.backend.domain.guideProduct.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "guide_category")
public class GuideCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private GuideProduct product;

    @Column(name = "category_code")
    @Enumerated(EnumType.STRING)
    private GuideCategoryCode categoryCode;

    public GuideCategory(GuideCategoryCode code) {
        this.categoryCode = code;
    }

    public void setProduct(GuideProduct product) {
        this.product = product;
    }
}
