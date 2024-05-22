package com.swygbro.trip.backend.domain.guideProduct.domain;

import com.swygbro.trip.backend.domain.guideProduct.dto.CreateGuideProductRequest;
import com.swygbro.trip.backend.domain.guideProduct.dto.ModifyGuideProductRequest;
import com.swygbro.trip.backend.domain.review.domain.Review;
import com.swygbro.trip.backend.domain.user.domain.User;
import com.swygbro.trip.backend.global.entity.BaseEntity;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "guide_product")
@Getter
public class GuideProduct extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id")
    private User user;

    @Column(length = 100, nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "text")
    private String description;

    @Column(nullable = false)
    private Long price;

    @Column(name = "location_name", length = 100, nullable = false)
    private String locationName;

    @Column(nullable = false, columnDefinition = "POINT SRID 4326")
    private Point location;

    @Column(name = "guide_start", nullable = false)
    private ZonedDateTime guideStart;

    @Column(name = "guide_end", nullable = false)
    private ZonedDateTime guideEnd;

    @Column(name = "guide_start_time", nullable = false)
    private LocalTime guideStartTime;

    @Column(name = "guide_end_time", nullable = false)
    private LocalTime guideEndTime;

    @Column(name = "guide_time", nullable = false)
    private int guideTime;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<GuideCategory> categories;

    @Column(name = "guide_thumbnail", nullable = false)
    private String thumb;

    @Type(JsonType.class)
    @Column(name = "guide_images", columnDefinition = "longtext")
    private List<String> images;

    @OneToMany(mappedBy = "guideProduct", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private Set<Review> reviews;

    public static GuideProduct setGuideProduct(User user, CreateGuideProductRequest request, List<String> images) {
        GeometryFactory geometryFactory = new GeometryFactory();
        Point point = geometryFactory.createPoint(new Coordinate(request.getLongitude(), request.getLatitude()));
        point.setSRID(4326);

        LocalDateTime startDate = request.getGuideStart().atStartOfDay();
        LocalDateTime endDate = request.getGuideEnd().atTime(23, 59, 59);

        return GuideProduct.builder().user(user)
                .title(request.getTitle()).description(request.getDescription())
                .price(request.getPrice()).locationName(request.getLocationName()).location(point)
                .guideStart(ZonedDateTime.of(startDate, ZoneId.of("Asia/Seoul"))).guideEnd(ZonedDateTime.of(endDate, ZoneId.of("Asia/Seoul")))
                .guideStartTime(request.getGuideStartTime()).guideEndTime(request.getGuideEndTime())
                .guideTime(request.getGuideTime()).thumb(images.get(0))
                .images(images.size() == 1 ? new ArrayList<>() : images.subList(1, images.size()).stream().toList())
                .categories(new LinkedHashSet<>())
                .build();
    }

    public void addGuideCategory(GuideCategory category) {
        this.categories.add(category);
        category.setProduct(this);
    }

    public void setGuideCategory(List<GuideCategoryCode> categoryCodes) {
        this.categories.clear();
        categoryCodes.forEach(categoryCode -> {
            GuideCategory category = new GuideCategory(categoryCode);
            this.categories.add(category);
            category.setProduct(this);
        });
    }

    public void setGuideProduct(ModifyGuideProductRequest request) {
        GeometryFactory geometryFactory = new GeometryFactory();
        Point point = geometryFactory.createPoint(new Coordinate(request.getLongitude(), request.getLatitude()));
        point.setSRID(4326);

        LocalDateTime startDate = request.getGuideStart().atStartOfDay();
        LocalDateTime endDate = request.getGuideEnd().atTime(23, 59, 59);

        this.title = request.getTitle();
        this.description = request.getDescription();
        this.price = request.getPrice();
        this.locationName = request.getLocationName();
        this.location = point;
        this.guideStart = ZonedDateTime.of(startDate, ZoneId.of("Asia/Seoul"));
        this.guideEnd = ZonedDateTime.of(endDate, ZoneId.of("Asia/Seoul"));
        this.guideStartTime = request.getGuideStartTime();
        this.guideEndTime = request.getGuideEndTime();
        this.guideTime = request.getGuideTime();
        this.thumb = request.getThumb();
        this.images = request.getImages();
    }
}