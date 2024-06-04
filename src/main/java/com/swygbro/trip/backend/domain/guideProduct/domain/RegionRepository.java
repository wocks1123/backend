package com.swygbro.trip.backend.domain.guideProduct.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RegionRepository extends JpaRepository<Region, String> {

    Region findByName(String name);
}
