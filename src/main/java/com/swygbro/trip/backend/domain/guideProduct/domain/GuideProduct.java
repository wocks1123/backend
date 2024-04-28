package com.swygbro.trip.backend.domain.guideProduct.domain;

import com.swygbro.trip.backend.domain.user.domain.User;
import com.swygbro.trip.backend.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/* TODO : 이미지와 양방향 매핑이 아니라 json 형태로 저장?
          index 설정,
          update시 delete???? or delete 사용?
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "guide_product")
public class GuideProduct extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "host_id")
    private User user;

    @Column(length = 100, nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Long price;

    @Column(nullable = false)
    private Point location;

    @Column(name = "guide_start", nullable = false)
    private ZonedDateTime guideStart;

    @Column(name = "guide_end", nullable = false)
    private ZonedDateTime guideEnd;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<GuideCategory> categories = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<GuideImage> images = new ArrayList<>();


    public GuideProduct(User user, String title, String description, Long price, double longitude, double latitude, ZonedDateTime guideStart, ZonedDateTime guideEnd) {

        GeometryFactory geometryFactory = new GeometryFactory();
        Point point = geometryFactory.createPoint(new Coordinate(latitude, longitude));
        point.setSRID(4326);

        this.user = user;
        this.title = title;
        this.description = description;
        this.price = price;
        this.location = point;
        this.guideStart = guideStart;
        this.guideEnd = guideEnd;
    }

    public void addGuideImage(GuideImage image) {
        this.images.add(image);
        image.setProduct(this);
    }

    public void addGuideCategory(GuideCategory category) {
        this.categories.add(category);
        category.setProduct(this);
    }
}
