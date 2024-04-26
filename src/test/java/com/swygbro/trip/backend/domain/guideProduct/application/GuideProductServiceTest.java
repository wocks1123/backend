package com.swygbro.trip.backend.domain.guideProduct.application;


import com.swygbro.trip.backend.domain.guideProduct.domain.GuideImage;
import com.swygbro.trip.backend.domain.guideProduct.domain.GuideProduct;
import com.swygbro.trip.backend.domain.guideProduct.domain.GuideProductRepository;
import com.swygbro.trip.backend.domain.guideProduct.dto.CreateGuideProductRequest;
import com.swygbro.trip.backend.domain.guideProduct.dto.GuideProductDto;
import com.swygbro.trip.backend.domain.guideProduct.exception.GuideProductNotFoundException;
import com.swygbro.trip.backend.domain.s3.application.S3Service;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@DisplayName("비즈니스 로직 - 가이드 상품")
@ExtendWith(MockitoExtension.class)
public class GuideProductServiceTest {

    @InjectMocks
    GuideProductService guideProductService;
    @Mock
    GuideProductRepository guideProductRepository;
    @Mock
    S3Service s3Service;

    @DisplayName("이미지 업로드 성공")
    @Test
    void createProduct() throws IOException {
        // given
        String title = "test";
        String description = "test";
        CreateGuideProductRequest request = new CreateGuideProductRequest(title, description);

        List<MultipartFile> images = new ArrayList<>();
        MockMultipartFile mockMultipartFile1 = new MockMultipartFile("image1", "test1.jpg", "image/jpg", "test image1".getBytes());
        MockMultipartFile mockMultipartFile2 = new MockMultipartFile("image2", "test2.jpg", "image/jpg", "test image2".getBytes());
        images.add(mockMultipartFile1);
        images.add(mockMultipartFile2);

        GuideProduct product = new GuideProduct(title, description);
        images.forEach(image -> {
            given(s3Service.uploadImage(image)).willReturn("test utl");
            product.addGuideImage(new GuideImage("test url"));
        });

        given(guideProductRepository.saveAndFlush(any())).willReturn(product);

        // when
        GuideProductDto result = guideProductService.createGuideProduct(request, images);

        // then
        assertThat(result.getTitle()).isEqualTo(title);
        assertThat(result.getDescription()).isEqualTo(description);
        assertThat(result.getImages().get(0).getUrl()).isEqualTo("test url");
    }

    @DisplayName("상품 불러오기 성공")
    @Test
    void getProduct() {
        // given
        int productId = 1;
        String title = "test";
        String description = "test";

        given(guideProductRepository.findById(productId)).willReturn(Optional.of(new GuideProduct(title, description)));

        // when
        GuideProductDto result = guideProductService.getProduct(productId);

        // then
        assertThat(result.getTitle()).isEqualTo(title);
        assertThat(result.getDescription()).isEqualTo(description);
    }

    @DisplayName("상품 불러오기 실패")
    @Test
    void getProduct_fail() {
        // given
        int productId = 1;

        given(guideProductRepository.findById(productId)).willReturn(Optional.empty());

        // when
        Throwable throwable = catchThrowable(() -> {
            guideProductService.getProduct(productId);
        });

        // then
        assertThat(throwable).isInstanceOf(GuideProductNotFoundException.class)
                .hasMessage("해당 (%s) 가이드 상품을 찾을 수 없습니다.".formatted(productId));
    }


}
