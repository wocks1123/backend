package com.swygbro.trip.backend.domain.guideProduct.domain;

import com.swygbro.trip.backend.domain.guideProduct.dto.CreateGuideProductRequest;
import com.swygbro.trip.backend.domain.guideProduct.dto.ModifyGuideProductRequest;
import com.swygbro.trip.backend.domain.user.domain.User;
import com.swygbro.trip.backend.global.entity.BaseEntity;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/* TODO : 이미지와 양방향 매핑이 아니라 json 형태로 저장?
          index 설정,
          update시 delete???? or delete 사용?
 */
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

    @ManyToOne
    @JoinColumn(name = "host_id")
    private User user;

    @Column(length = 100, nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "text")
    private String description;

    @Column(nullable = false)
    private Long price;

    @Column(nullable = false, columnDefinition = "POINT SRID 4326")
    private Point location;

    @Column(name = "guide_start", nullable = false)
    private ZonedDateTime guideStart;

    @Column(name = "guide_end", nullable = false)
    private ZonedDateTime guideEnd;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<GuideCategory> categories;

    @Column(name = "guide_thumbnail", nullable = false)
    private String thumb;

    @Type(JsonType.class)
    @Column(name = "guide_images", columnDefinition = "longtext")
    private List<String> images;

    public static GuideProduct setGuideProduct(User user, CreateGuideProductRequest request, List<String> images) {
        GeometryFactory geometryFactory = new GeometryFactory();
        Point point = geometryFactory.createPoint(new Coordinate(request.getLongitude(), request.getLatitude()));
        point.setSRID(4326);

        if (images.size() == 1)
            return GuideProduct.builder().user(user)
                    .title(request.getTitle()).description(request.getDescription())
                    .price(request.getPrice()).location(point)
                    .guideStart(request.getGuideStart()).guideEnd(request.getGuideEnd())
                    .thumb(images.get(0)).categories(new ArrayList<>())
                    .build();
        else return GuideProduct.builder().user(user)
                .title(request.getTitle()).description(request.getDescription())
                .price(request.getPrice()).location(point)
                .guideStart(request.getGuideStart()).guideEnd(request.getGuideEnd())
                .thumb(images.get(0)).images(images.subList(1, images.size()).stream().toList())
                .categories(new ArrayList<>())
                .build();
    }

    public void addGuideCategory(GuideCategory category) {
        this.categories.add(category);
        category.setProduct(this);
    }

    public void setGuideCategory(List<GuideCategoryCode> categoryCodes) {
        for (int i = 0; i < categoryCodes.size(); i++) {
            if (i >= this.categories.size()) {
                GuideCategory category = new GuideCategory(categoryCodes.get(i));
                category.setProduct(this);
                this.categories.add(category);
            } else this.categories.get(i).setCategoryCode(categoryCodes.get(i));
        }
    }

    public void setGuideProduct(ModifyGuideProductRequest request) {
        GeometryFactory geometryFactory = new GeometryFactory();
        Point point = geometryFactory.createPoint(new Coordinate(request.getLongitude(), request.getLatitude()));
        point.setSRID(4326);

        this.title = request.getTitle();
        this.description = request.getDescription();
        this.price = request.getPrice();
        this.location = point;
        this.guideStart = request.getGuideStart();
        this.guideEnd = request.getGuideEnd();
        this.thumb = request.getThumb();
        this.images = request.getImages();
    }
}