package com.swygbro.trip.backend.domain.guideProduct.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.MultiPolygon;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Region {

    @Id
    @Column(length = 100)
    private String name;

    @Column(nullable = false, columnDefinition = "MULTIPOLYGON SRID 4326")
    private MultiPolygon polygon;
}
