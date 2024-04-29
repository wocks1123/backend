package com.swygbro.trip.backend.domain.guideProduct.application;

import com.swygbro.trip.backend.domain.guideProduct.domain.GuideCategory;
import com.swygbro.trip.backend.domain.guideProduct.domain.GuideImage;
import com.swygbro.trip.backend.domain.guideProduct.domain.GuideProduct;
import com.swygbro.trip.backend.domain.guideProduct.domain.GuideProductRepository;
import com.swygbro.trip.backend.domain.guideProduct.dto.GuideProductDto;
import com.swygbro.trip.backend.domain.guideProduct.dto.GuideProductRequest;
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

@Service
@RequiredArgsConstructor
public class GuideProductService {

    private final S3Service s3Service;
    private final GuideProductRepository guideProductRepository;
    private final UserRepository userRepository;

    // 가이드 상품 생성
    @Transactional
    public GuideProductDto createGuideProduct(GuideProductRequest request, List<MultipartFile> images) {
        User user = getUser(request.getAccount());

        isValidLocation(request.getLongitude(), request.getLatitude());
        GuideProduct product = new GuideProduct(user, request.getTitle(), request.getDescription(),
                request.getPrice(), request.getLongitude(), request.getLatitude(), request.getGuideStart(), request.getGuideEnd());

        images.forEach(image -> {
            product.addGuideImage(new GuideImage(s3Service.uploadImage(image)));
        });

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
    public GuideProductDto modifyGuideProduct(Long productId, GuideProductRequest edits) {
        GuideProduct product = guideProductRepository.findById(productId).orElseThrow(() -> new GuideProductNotFoundException(productId));
        User user = getUser(edits.getAccount());

        if (product.getUser() != user) throw new MismatchUserFromCreatorException();
        product.setGuideProduct(edits);
        product.setGuideImage(edits.getCategories());

        GuideProduct resultProduct = guideProductRepository.saveAndFlush(product);
        return GuideProductDto.fromEntity(resultProduct);
    }

    // 가이드 상품 생성 시 필요한 호스트 정보 불러오가
    private User getUser(String account) {
        return userRepository.findByAccount(account).orElseThrow(() -> new UserNotFoundException(account));
    }

    // 가이드 위치 유효한지 검사
    private void isValidLocation(double longitude, double latitude) throws NotValidLocationException {
        if (longitude < -90 || longitude > 90 || latitude < -180 || latitude > 180)
            throw new NotValidLocationException();
    }

}
