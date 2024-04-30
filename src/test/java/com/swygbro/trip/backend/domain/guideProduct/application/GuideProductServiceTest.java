package com.swygbro.trip.backend.domain.guideProduct.application;


import com.swygbro.trip.backend.domain.guideProduct.domain.GuideCategory;
import com.swygbro.trip.backend.domain.guideProduct.domain.GuideImage;
import com.swygbro.trip.backend.domain.guideProduct.domain.GuideProduct;
import com.swygbro.trip.backend.domain.guideProduct.domain.GuideProductRepository;
import com.swygbro.trip.backend.domain.guideProduct.dto.GuideProductDto;
import com.swygbro.trip.backend.domain.guideProduct.dto.GuideProductRequest;
import com.swygbro.trip.backend.domain.guideProduct.exception.GuideProductNotFoundException;
import com.swygbro.trip.backend.domain.guideProduct.exception.MismatchUserFromCreatorException;
import com.swygbro.trip.backend.domain.guideProduct.fixture.GuideProductFixture;
import com.swygbro.trip.backend.domain.guideProduct.fixture.GuideProductRequestFixture;
import com.swygbro.trip.backend.domain.s3.application.S3Service;
import com.swygbro.trip.backend.domain.user.domain.User;
import com.swygbro.trip.backend.domain.user.domain.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.time.format.DateTimeFormatter;
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
    UserRepository userRepository;
    @Mock
    S3Service s3Service;

    @DisplayName("게시물 생성 성공")
    @Test
    void createProduct() {
        // given
        GuideProductRequest request = GuideProductRequestFixture.getGuideProductRequest();
        GuideProduct product = GuideProductFixture.getGuideProduct();
        User user = product.getUser();

        List<MultipartFile> images = new ArrayList<>();
        MockMultipartFile mockMultipartFile1 = new MockMultipartFile("image1", "test1.jpg", "image/jpg", "test image1".getBytes());
        MockMultipartFile mockMultipartFile2 = new MockMultipartFile("image2", "test2.jpg", "image/jpg", "test image2".getBytes());
        images.add(mockMultipartFile1);
        images.add(mockMultipartFile2);

        given(userRepository.findByAccount(any())).willReturn(Optional.of(user));

        images.forEach(image -> {
            given(s3Service.uploadImage(image)).willReturn("test utl");
            product.addGuideImage(new GuideImage("test url"));
        });

        request.getCategories().forEach(category -> {
            product.addGuideCategory(new GuideCategory(category));
        });

        given(guideProductRepository.saveAndFlush(any())).willReturn(product);

        // when
        GuideProductDto result = guideProductService.createGuideProduct(request, images);

        // then
        assertThat(result.getAccount()).isEqualTo(product.getUser().getAccount());
        assertThat(result.getTitle()).isEqualTo(product.getTitle());
        assertThat(result.getDescription()).isEqualTo(product.getDescription());
        assertThat(result.getPrice()).isEqualTo(product.getPrice());
        assertThat(result.getLatitude()).isEqualTo(product.getLocation().getX());
        assertThat(result.getLongitude()).isEqualTo(product.getLocation().getY());
        assertThat(result.getGuideStart().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))).isEqualTo(product.getGuideStart().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        assertThat(result.getGuideStart().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))).isEqualTo(product.getGuideStart().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        assertThat(result.getCategories().get(0).name()).isEqualTo(product.getCategories().get(0).getCategoryCode().name());
        assertThat(result.getImages().get(0).getUrl()).isEqualTo(product.getImages().get(0).getUrl());
    }

    @DisplayName("상품 불러오기 성공")
    @Test
    void getProduct() {
        // given
        Long productId = 1L;

        GuideProduct product = GuideProductFixture.getGuideProduct();
        given(guideProductRepository.findById(productId)).willReturn(Optional.of(product));

        // when
        GuideProductDto result = guideProductService.getProduct(productId);

        // then
        assertThat(result.getAccount()).isEqualTo(product.getUser().getAccount());
        assertThat(result.getTitle()).isEqualTo(product.getTitle());
        assertThat(result.getDescription()).isEqualTo(product.getDescription());
        assertThat(result.getPrice()).isEqualTo(product.getPrice());
        assertThat(result.getLatitude()).isEqualTo(product.getLocation().getX());
        assertThat(result.getLongitude()).isEqualTo(product.getLocation().getY());
        assertThat(result.getGuideStart().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))).isEqualTo(product.getGuideStart().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        assertThat(result.getGuideStart().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))).isEqualTo(product.getGuideStart().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        assertThat(result.getCategories().get(0).name()).isEqualTo(product.getCategories().get(0).getCategoryCode().name());
        assertThat(result.getImages().get(0).getUrl()).isEqualTo(product.getImages().get(0).getUrl());
    }

    @DisplayName("상품 불러오기 실패")
    @Test
    void getProduct_fail() {
        // given
        Long productId = 1L;

        given(guideProductRepository.findById(productId)).willReturn(Optional.empty());

        // when
        Throwable throwable = catchThrowable(() -> {
            guideProductService.getProduct(productId);
        });

        // then
        assertThat(throwable).isInstanceOf(GuideProductNotFoundException.class)
                .hasMessage("해당 (%s) 가이드 상품을 찾을 수 없습니다.".formatted(productId));
    }

    @DisplayName("상품 내용 수정 성공")
    @Test
    void modifyProduct() {
        // given
        GuideProductRequest edit = GuideProductRequestFixture.getGuideProductRequest();
        edit.setTitle("modify title");
        edit.setDescription("modify description");
        GuideProduct product = GuideProductFixture.getGuideProduct();
        User user = product.getUser();

        given(guideProductRepository.findById(any())).willReturn(Optional.of(product));
        given(userRepository.findByAccount(any())).willReturn(Optional.of(user));

        product.setGuideProduct(edit);
        product.setGuideCategory(edit.getCategories());
        given(guideProductRepository.saveAndFlush(any())).willReturn(product);

        // when
        GuideProductDto result = guideProductService.modifyGuideProduct(product.getId(), edit);

        // then
        assertThat(result.getTitle()).isEqualTo(product.getTitle());
        assertThat(result.getDescription()).isEqualTo(product.getDescription());
        assertThat(result.getCategories().get(0)).isEqualTo(product.getCategories().get(0).getCategoryCode());
    }

    @DisplayName("상품 내용 수정시 권한 없음")
    @Test
    void modifyProduct_unauthorized() {
        // given
        GuideProductRequest edit = GuideProductRequestFixture.getGuideProductRequest();
        edit.setTitle("modify title");
        edit.setDescription("modify description");
        GuideProduct product = GuideProductFixture.getGuideProduct();
        User user = new User("wrong_account", "testemail", "testpassword");

        given(guideProductRepository.findById(any())).willReturn(Optional.of(product));
        given(userRepository.findByAccount(any())).willReturn(Optional.of(user));

        // when
        Throwable throwable = catchThrowable(() -> {
            guideProductService.modifyGuideProduct(product.getId(), edit);
        });

        // then
        assertThat(throwable).isInstanceOf(MismatchUserFromCreatorException.class);
        assertThat(throwable).hasMessage("가이드 상품을 수정할 권한이 없습니다.");
    }
}
