package com.swygbro.trip.backend.domain.guideProduct.domain;

import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GuideProductRepository extends JpaRepository<GuideProduct, Long>, GuideProductCustomRepository {

    @Query("""  
            SELECT g
            FROM GuideProduct AS g
            WHERE st_contains(st_buffer(:center, :radius), g.location)
            """)
    List<GuideProduct> findAllByLocation(@Param("center") Point point,
                                         @Param("radius") int radius);
}
