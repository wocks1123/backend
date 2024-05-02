package com.swygbro.trip.backend.domain.guideProduct.application;

import com.swygbro.trip.backend.domain.guideProduct.domain.GuideCategory;
import com.swygbro.trip.backend.domain.guideProduct.domain.GuideProduct;
import com.swygbro.trip.backend.domain.guideProduct.domain.GuideProductRepository;
import com.swygbro.trip.backend.domain.guideProduct.dto.CreateGuideProductRequest;
import com.swygbro.trip.backend.domain.guideProduct.dto.GuideProductDto;
import com.swygbro.trip.backend.domain.guideProduct.dto.ModifyGuideProductRequest;
import com.swygbro.trip.backend.domain.guideProduct.exception.GuideProductNotFoundException;
import com.swygbro.trip.backend.domain.guideProduct.exception.MismatchUserFromCreatorException;
import com.swygbro.trip.backend.domain.guideProduct.exception.NotValidLocationException;
import com.swygbro.trip.backend.domain.s3.application.S3Service;
import com.swygbro.trip.backend.domain.user.domain.User;
import com.swygbro.trip.backend.domain.user.domain.UserRepository;
import com.swygbro.trip.backend.domain.user.excepiton.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GuideProductService {

    private final S3Service s3Service;
    private final GuideProductRepository guideProductRepository;
    private final UserRepository userRepository;

    // 가이드 상품 생성
    @Transactional
    public GuideProductDto createGuideProduct(CreateGuideProductRequest request, MultipartFile thumb, List<MultipartFile> images) {
        User user = getUser(request.getEmail());

        isValidLocation(request.getLongitude(), request.getLatitude());

        List<String> imageUrls = images.stream().map(s3Service::uploadImage).collect(Collectors.toList());
        imageUrls.add(0, s3Service.uploadImage(thumb));
        GuideProduct product = GuideProduct.setGuideProduct(user, request, imageUrls);

        request.getCategories().forEach(category -> {
            product.addGuideCategory(new GuideCategory(category));
        });

        GuideProduct resultProduct = guideProductRepository.saveAndFlush(product);
        return GuideProductDto.fromEntity(resultProduct);
    }

    // TODO : fetch join 사용으로 쿼리문 단축
    // 가이드 상품 조회
    @Transactional(readOnly = true)
    public GuideProductDto getProduct(Long productId) {
        GuideProduct product = guideProductRepository.findById(productId).orElseThrow(() -> new GuideProductNotFoundException(productId));

        return GuideProductDto.fromEntity(product);
    }

    // 가이드 상품 수정
    @Transactional
    public GuideProductDto modifyGuideProduct(Long productId, ModifyGuideProductRequest edits, Optional<MultipartFile> modifyThumb, Optional<List<MultipartFile>> modifyImages) {
        GuideProduct product = guideProductRepository.findById(productId).orElseThrow(() -> new GuideProductNotFoundException(productId));
        User user = getUser(edits.getEmail());

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

        if (product.getUser() != user) throw new MismatchUserFromCreatorException();
        product.setGuideProduct(edits);
        product.setGuideCategory(edits.getCategories());

        GuideProduct resultProduct = guideProductRepository.saveAndFlush(product);
        return GuideProductDto.fromEntity(resultProduct);
    }

    // 가이드 상품 삭제
    @Transactional
    public void deleteGuideProduct(Long productId) {
        GuideProduct product = guideProductRepository.findById(productId).orElseThrow(() -> new GuideProductNotFoundException(productId));

        s3Service.deleteImage(product.getThumb());
        product.getImages().forEach(s3Service::deleteImage);

        guideProductRepository.deleteById(productId);
    }

    // 가이드 상품 생성 시 필요한 호스트 정보 불러오가
    private User getUser(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(email));
    }

    // 가이드 위치 유효한지 검사
    private void isValidLocation(double longitude, double latitude) throws NotValidLocationException {
        if (longitude < -90 || longitude > 90 || latitude < -180 || latitude > 180)
            throw new NotValidLocationException();
    }
}
