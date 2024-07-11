package com.swygbro.trip.backend.domain.guideProduct.application;


import com.swygbro.trip.backend.domain.guideProduct.domain.GuideProduct;
import com.swygbro.trip.backend.domain.guideProduct.domain.GuideProductRepository;
import com.swygbro.trip.backend.domain.guideProduct.dto.CreateGuideProductDto;
import com.swygbro.trip.backend.domain.guideProduct.dto.CreateGuideProductRequest;
import com.swygbro.trip.backend.domain.guideProduct.dto.GuideProductDto;
import com.swygbro.trip.backend.domain.guideProduct.dto.ModifyGuideProductRequest;
import com.swygbro.trip.backend.domain.guideProduct.exception.GuideProductNotFoundException;
import com.swygbro.trip.backend.domain.guideProduct.exception.MismatchUserFromCreatorException;
import com.swygbro.trip.backend.domain.guideProduct.fixture.GuideProductFixture;
import com.swygbro.trip.backend.domain.guideProduct.fixture.GuideProductRequestFixture;
import com.swygbro.trip.backend.domain.s3.application.S3Service;
import com.swygbro.trip.backend.domain.user.TestUserFactory;
import com.swygbro.trip.backend.domain.user.domain.User;
import com.swygbro.trip.backend.domain.user.domain.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
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

    private User user;

    @BeforeEach
    void setup() {
        user = TestUserFactory.createTestUser();
    }

    @DisplayName("게시물 생성 성공")
    @Test
    void createProduct() {
        // given
        CreateGuideProductRequest request = GuideProductRequestFixture.getCreateGuideProductRequest();
        GuideProduct product = GuideProductFixture.getGuideProduct(request);
        User user = product.getUser();

        List<MultipartFile> images = new ArrayList<>();
        MockMultipartFile mockMultipartFileThumb = new MockMultipartFile("thumb", "thumb.jpg", "image/jpg", "test thumb image".getBytes());
        MockMultipartFile mockMultipartFile1 = new MockMultipartFile("image1", "test1.jpg", "image/jpg", "test image1".getBytes());
        MockMultipartFile mockMultipartFile2 = new MockMultipartFile("image2", "test2.jpg", "image/jpg", "test image2".getBytes());
        images.add(mockMultipartFile1);
        images.add(mockMultipartFile2);

        given(userRepository.findByEmail(any())).willReturn(Optional.of(user));
        given(s3Service.uploadImage(any())).willReturn("test url");
        given(guideProductRepository.saveAndFlush(any())).willReturn(product);

        // when
        CreateGuideProductDto result = guideProductService.createGuideProduct(user, request, mockMultipartFileThumb, Optional.of(images));

        // then
        assertThat(result.getUserId()).isEqualTo(product.getUser().getId());
        assertThat(result.getTitle()).isEqualTo(product.getTitle());
        assertThat(result.getDescription()).isEqualTo(product.getDescription());
        assertThat(result.getPrice()).isEqualTo(product.getPrice());
        assertThat(result.getLatitude()).isEqualTo(product.getLocation().getX());
        assertThat(result.getLongitude()).isEqualTo(product.getLocation().getY());
        assertThat(result.getGuideStart().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))).isEqualTo(product.getGuideStart().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        assertThat(result.getGuideStart().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))).isEqualTo(product.getGuideStart().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        assertThat(result.getCategories().get(0).name()).isEqualTo(product.getCategories().stream().toList().get(0).getCategoryCode().name());
        assertThat(result.getThumb()).isEqualTo(product.getThumb());
        assertThat(result.getImages().get(0)).isEqualTo(product.getImages().get(0));
    }

    @DisplayName("상품 불러오기 성공")
    @Test
    void getProduct() {
        // given
        Long productId = 1L;
        CreateGuideProductRequest request = GuideProductRequestFixture.getCreateGuideProductRequest();
        GuideProduct product = GuideProductFixture.getGuideProduct(request);

        given(guideProductRepository.findById(productId)).willReturn(Optional.of(product));

        // when
        GuideProductDto result = guideProductService.getProduct(productId);

        // then
        assertThat(result.getUserId()).isEqualTo(product.getUser().getId());
        assertThat(result.getTitle()).isEqualTo(product.getTitle());
        assertThat(result.getDescription()).isEqualTo(product.getDescription());
        assertThat(result.getPrice()).isEqualTo(product.getPrice());
        assertThat(result.getLatitude()).isEqualTo(product.getLocation().getX());
        assertThat(result.getLongitude()).isEqualTo(product.getLocation().getY());
        assertThat(result.getGuideStart().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))).isEqualTo(product.getGuideStart().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        assertThat(result.getGuideStart().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))).isEqualTo(product.getGuideStart().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        assertThat(result.getCategories().get(0).name()).isEqualTo(product.getCategories().stream().toList().get(0).getCategoryCode().name());
        assertThat(result.getThumb()).isEqualTo(product.getThumb());
        assertThat(result.getImages().get(0)).isEqualTo(product.getImages().get(0));
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
        Long productId = 1L;
        CreateGuideProductRequest request = GuideProductRequestFixture.getCreateGuideProductRequest();
        GuideProduct product = GuideProductFixture.getGuideProduct(request);
        User user = product.getUser();
        ModifyGuideProductRequest edit = GuideProductRequestFixture.getModifyProductRequest();

        List<MultipartFile> images = new ArrayList<>();
        MockMultipartFile mockMultipartFileThumb = new MockMultipartFile("thumb", "modify_test.jpg", "image/jpg", "test thumb image".getBytes());
        MockMultipartFile mockMultipartFile1 = new MockMultipartFile("image1", "modify_test.jpg", "image/jpg", "test image1".getBytes());
        MockMultipartFile mockMultipartFile2 = new MockMultipartFile("image2", "modify_test.jpg", "image/jpg", "test image2".getBytes());
        images.add(mockMultipartFile1);
        images.add(mockMultipartFile2);

        given(guideProductRepository.findById(productId)).willReturn(Optional.of(product));
        given(userRepository.findByEmail(any())).willReturn(Optional.of(user));
        given(s3Service.uploadImage(any())).willReturn("modify url");
        given(guideProductRepository.saveAndFlush(any())).willReturn(product);

        // when
        GuideProductDto result = guideProductService.modifyGuideProduct(user, productId, edit, Optional.of(mockMultipartFileThumb), Optional.of(images));

        // then
        assertThat(result.getTitle()).isEqualTo(edit.getTitle());
        assertThat(result.getDescription()).isEqualTo(edit.getDescription());
        assertThat(result.getThumb()).isEqualTo("modify url");
        assertThat(result.getImages().get(0)).isEqualTo("modify url");
    }

    @DisplayName("상품 내용 수정시 권한 없음")
    @Test
    void modifyProduct_unauthorized() {
        // given
        Long productId = 1L;
        CreateGuideProductRequest request = GuideProductRequestFixture.getCreateGuideProductRequest();
        GuideProduct product = GuideProductFixture.getGuideProduct(request);
        ModifyGuideProductRequest edit = GuideProductRequestFixture.getModifyProductRequest();
        User user = User.builder().email("wrong@email.com").build();

        List<MultipartFile> images = new ArrayList<>();
        MockMultipartFile mockMultipartFileThumb = new MockMultipartFile("thumb", "modify_test.jpg", "image/jpg", "test thumb image".getBytes());
        MockMultipartFile mockMultipartFile1 = new MockMultipartFile("image1", "modify_test.jpg", "image/jpg", "test image1".getBytes());
        MockMultipartFile mockMultipartFile2 = new MockMultipartFile("image2", "modify_test.jpg", "image/jpg", "test image2".getBytes());
        images.add(mockMultipartFile1);
        images.add(mockMultipartFile2);

        given(guideProductRepository.findById(any())).willReturn(Optional.of(product));
        given(userRepository.findByEmail(any())).willReturn(Optional.of(user));

        // when
        Throwable throwable = catchThrowable(() -> {
            guideProductService.modifyGuideProduct(user, productId, edit, Optional.of(mockMultipartFileThumb), Optional.of(images));
        });

        // then
        assertThat(throwable).isInstanceOf(MismatchUserFromCreatorException.class);
        assertThat(throwable).hasMessage("가이드 상품을 수정할 권한이 없습니다.");
    }

    @DisplayName("상품 삭제 성공")
    @Test
    void deleteProduct() {
        // given
        Long productId = 1L;
        String email = "test@gmail.com";
        CreateGuideProductRequest request = GuideProductRequestFixture.getCreateGuideProductRequest();
        GuideProduct product = GuideProductFixture.getGuideProduct(request);
        User user = product.getUser();

        given(guideProductRepository.findById(productId)).willReturn(Optional.of(product));
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));

        // when && then
        Assertions.assertDoesNotThrow(() -> guideProductService.deleteGuideProduct(productId, user));
    }

    @DisplayName("상품 삭제 권한이 없을 경우")
    @Test
    void deleteProduct_unauthorized() {
        // given
        Long productId = 1L;
        String email = "test@gmail.com";
        CreateGuideProductRequest request = GuideProductRequestFixture.getCreateGuideProductRequest();
        GuideProduct product = GuideProductFixture.getGuideProduct(request);
        User user = User.builder().email("wrong@email.com").build();

        given(guideProductRepository.findById(productId)).willReturn(Optional.of(product));
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));

        // when
        Throwable throwable = catchThrowable(() -> {
            guideProductService.deleteGuideProduct(productId, user);
        });

        // then
        assertThat(throwable).isInstanceOf(MismatchUserFromCreatorException.class);
        assertThat(throwable).hasMessage("가이드 상품을 수정할 권한이 없습니다.");
    }
}
