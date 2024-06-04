package com.swygbro.trip.backend.domain.guideProduct.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter(AccessLevel.PACKAGE)
@Table(name = "guide_category")
public class GuideCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private GuideProduct product;

    @Column(name = "category_code", nullable = false)
    @Enumerated(EnumType.STRING)
    private GuideCategoryCode categoryCode;

    public GuideCategory(GuideCategoryCode code) {
        this.categoryCode = code;
    }

}
