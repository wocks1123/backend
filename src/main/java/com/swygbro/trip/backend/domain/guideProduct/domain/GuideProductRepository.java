package com.swygbro.trip.backend.domain.guideProduct.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuideProductRepository extends JpaRepository<GuideProduct, Long>, GuideProductCustomRepository {

}
