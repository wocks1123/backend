package com.swygbro.trip.backend.domain.guideProduct.application;

import com.swygbro.trip.backend.domain.guideProduct.domain.*;
import com.swygbro.trip.backend.domain.guideProduct.dto.*;
import com.swygbro.trip.backend.domain.guideProduct.exception.GuideProductNotFoundException;
import com.swygbro.trip.backend.domain.guideProduct.exception.GuideProductNotInRangeException;
import com.swygbro.trip.backend.domain.guideProduct.exception.MismatchUserFromCreatorException;
import com.swygbro.trip.backend.domain.guideProduct.exception.NotValidLocationException;
import com.swygbro.trip.backend.domain.s3.application.S3Service;
import com.swygbro.trip.backend.domain.user.domain.Language;
import com.swygbro.trip.backend.domain.user.domain.Nationality;
import com.swygbro.trip.backend.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GuideProductService {

    private final S3Service s3Service;
    private final GuideProductRepository guideProductRepository;
    private final RegionRepository regionRepository;

    // 메인 페이지
    @Transactional(readOnly = true)
    public MainPageResponse getMainPage(Double latitude, Double longitude, int page) {
        MultiPolygon polygon = regionRepository.findByName("서울특별시").getPolygon();
        Geometry geometry;
        Pageable pageable = PageRequest.of(page, 12);

        if (page >= 1) {
            Page<SearchGuideProductResponse> allGuideProducts = guideProductRepository.findAllWithMain(pageable);

            return MainPageResponse.builder().allGuideProducts(allGuideProducts).build();
        } else {
            if (latitude != null && longitude != null) {
                geometry = setPoint(latitude, longitude);
            } else geometry = polygon;

            List<SearchGuideProductResponse> nearGuideProducts = guideProductRepository.findByLocation(geometry, 30000);

            List<SearchGuideProductResponse> bestGuideProducts = guideProductRepository.findByBest(polygon);

            Page<SearchGuideProductResponse> allGuideProducts = guideProductRepository.findAllWithMain(pageable);

            return MainPageResponse.from(nearGuideProducts, bestGuideProducts, allGuideProducts);
        }
    }

    // 가이드 상품 생성
    @Transactional
    public CreateGuideProductDto createGuideProduct(User user, CreateGuideProductRequest request, MultipartFile thumb, Optional<List<MultipartFile>> images) {
        isValidLocation(request.getLatitude(), request.getLongitude());

        List<String> imageUrls = new ArrayList<>();
        images.ifPresentOrElse(list -> {
                    list.forEach(image -> {
                        imageUrls.add(s3Service.uploadImage(image));
                    });
                    imageUrls.add(0, s3Service.uploadImage(thumb));
                },
                () -> {
                    imageUrls.add(s3Service.uploadImage(thumb));
                }
        );

        GuideProduct product = GuideProduct.setGuideProduct(user, request, imageUrls);

        request.getCategories().forEach(category -> {
            product.addGuideCategory(new GuideCategory(category));
        });

        GuideProduct resultProduct = guideProductRepository.saveAndFlush(product);
        return CreateGuideProductDto.fromEntity(resultProduct);
    }

    // 가이드 상품 조회
    @Transactional(readOnly = true)
    public GuideProductDto getProduct(Long productId) {
        GuideProduct product = guideProductRepository.findDetailById(productId).orElseThrow(() -> new GuideProductNotFoundException(productId));

        return GuideProductDto.fromEntity(product);
    }

    // 가이드 상품 수정
    @Transactional
    public GuideProductDto modifyGuideProduct(User user, Long productId, ModifyGuideProductRequest edits,
                                              Optional<MultipartFile> modifyThumb, Optional<List<MultipartFile>> modifyImages) {
        GuideProduct product = guideProductRepository.findById(productId).orElseThrow(() -> new GuideProductNotFoundException(productId));

        if (product.getUser().equals(user))
            throw new MismatchUserFromCreatorException("가이드 상품을 수정할 권한이 없습니다.");

        modifyThumb.ifPresent(image -> {
            s3Service.deleteImage(product.getThumb());
            edits.setThumb(s3Service.uploadImage(image));
        });

        modifyImages.ifPresent(list -> {
            product.getImages().forEach(image -> {
                if (!edits.getImages().contains(image)) s3Service.deleteImage(image);
            });

            list.forEach(newImage -> {
                edits.getImages().add(s3Service.uploadImage(newImage));
            });
        });

        product.setGuideProduct(edits);
        product.setGuideCategory(edits.getCategories());

        GuideProduct resultProduct = guideProductRepository.saveAndFlush(product);
        return GuideProductDto.fromEntity(resultProduct);
    }

    // 가이드 상품 삭제
    @Transactional
    public void deleteGuideProduct(Long productId, User user) {
        GuideProduct product = guideProductRepository.findById(productId).orElseThrow(() -> new GuideProductNotFoundException(productId));

        if (product.getUser().equals(user)) throw new MismatchUserFromCreatorException("가이드 상품을 삭제할 권한이 없습니다.");

        s3Service.deleteImage(product.getThumb());
        product.getImages().forEach(s3Service::deleteImage);

        guideProductRepository.deleteById(productId);
    }

    // 지역, 날짜로 검색
    @Transactional(readOnly = true)
    public Page<SearchGuideProductResponse> getSearchedGuideList(SearchGuideProductRequest request, SearchCategoriesRequest categories,
                                                                 Long minPrice, Long maxPrice, int minDuration, int maxDuration,
                                                                 DayTime dayTime, Nationality nationality, List<Language> languages,
                                                                 Pageable pageable) {
        ZonedDateTime zonedDateStart;
        ZonedDateTime zonedDateEnd;
        MultiPolygon polygon;

        if (request.getRegion() != null && request.getStart() != null && request.getEnd() != null) {
            zonedDateStart = request.getStart().atStartOfDay(ZoneId.of("Asia/Seoul"));
            zonedDateEnd = ZonedDateTime.of(request.getEnd().atTime(LocalTime.MAX), ZoneId.of("Asia/Seoul"));
            polygon = regionRepository.findByName(request.getRegion()).getPolygon();
        } else {
            zonedDateStart = null;
            zonedDateEnd = null;
            polygon = regionRepository.findByName("서울특별시").getPolygon();
        }

        Page<SearchGuideProductResponse> guideProducts = guideProductRepository.findByFilter(polygon, zonedDateStart,
                zonedDateEnd, categories, minPrice, maxPrice, minDuration, maxDuration, dayTime, nationality, languages, pageable);

        if (guideProducts.isEmpty()) throw new GuideProductNotInRangeException("해당 조건에 부합하는 가이드 상품이 존재하지 않습니다.");

        return guideProducts;
    }

    @Transactional(readOnly = true)
    public Page<GuideProductDto> getGuideProductPages(Pageable pageable) {
        var guideProducts = guideProductRepository.findAll(pageable);
        return guideProducts.map(GuideProductDto::fromEntity);
    }

    // 가이드 위치 유효한지 검사
    private void isValidLocation(double latitude, double longitude) throws NotValidLocationException {
        if (latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180)
            throw new NotValidLocationException();
    }

    private Point setPoint(double latitude, double longitude) {
        GeometryFactory geometryFactory = new GeometryFactory();
        Point point = geometryFactory.createPoint(new Coordinate(longitude, latitude));
        point.setSRID(4326);

        return point;
    }
}
