package com.swygbro.trip.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.swygbro.trip.backend.domain.guideProduct.domain.DayTime;
import com.swygbro.trip.backend.domain.guideProduct.domain.GuideCategoryCode;
import com.swygbro.trip.backend.domain.guideProduct.dto.CreateGuideProductRequest;
import com.swygbro.trip.backend.domain.guideProduct.dto.ModifyGuideProductRequest;
import com.swygbro.trip.backend.domain.guideProduct.dto.SearchCategoriesRequest;
import com.swygbro.trip.backend.domain.guideProduct.dto.SearchGuideProductRequest;
import com.swygbro.trip.backend.domain.reservation.dto.ReservationSearchCriteria;
import com.swygbro.trip.backend.domain.reservation.dto.SavePaymentRequest;
import com.swygbro.trip.backend.domain.reservation.dto.SaveReservationRequest;
import com.swygbro.trip.backend.domain.review.dto.CreateReviewRequest;
import com.swygbro.trip.backend.domain.user.domain.Gender;
import com.swygbro.trip.backend.domain.user.domain.Language;
import com.swygbro.trip.backend.domain.user.domain.Nationality;
import com.swygbro.trip.backend.domain.user.dto.CreateUserRequest;
import com.swygbro.trip.backend.domain.user.dto.LoginRequest;
import com.swygbro.trip.backend.domain.user.dto.UpdateUserRequest;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = {"/user.sql", "/guideProduct.sql", "/review.sql", "/reservation.sql"})
@WithUserDetails("example1@email.com") // 1L
@ActiveProfiles("test")
class IntegratedTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // USER
    @Test
    @DisplayName("[USER][POST] /api/v1/auth/signup 회원가입 성공(200)")
    void givenUser_whenJoin_thenSuccess() throws Exception {
        // given
        CreateUserRequest request = new CreateUserRequest(
                "testemail@gmail.com",
                "testNickname",
                "testName",
                "+01012345678",
                "경기도 화성시",
                Nationality.KOR,
                "1999-01-01",
                Gender.Male,
                "testpassword1",
                "testpassword1"
        );

        // when & then
        mockMvc.perform(
                        post("/api/v1/auth/signup")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .with(csrf()))
                .andExpect(status().isOk())
                .andReturn();
    }

    @ParameterizedTest
    @DisplayName("[USER][POST] /api/v1/auth/signup 회원가입 정보 중복(409)")
    @CsvSource({
            // 이메일 중복
            "example1@email.com, testNickname, +01012345678",
            // 닉네임 중복
            "testemail@gmail.com, example_nickname1, +01012345678",
            // 전화번호 중복
            "testemail@gmail.com, testNickname, +01234567801"
    })
    void givenUser_whenJoin_thenDuplicated(String email, String nickname, String phone) throws Exception {
        // given
        CreateUserRequest request = new CreateUserRequest(
                email,
                nickname,
                "testName",
                phone,
                "경기도 화성시",
                Nationality.KOR,
                "1999-01-01",
                Gender.Male,
                "testpassword1",
                "testpassword1"
        );

        // when & then
        mockMvc.perform(
                        post("/api/v1/auth/signup")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .with(csrf()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value("CONFLICT"))
                .andReturn();
    }

    @Test
    @DisplayName("[USER][POST] /api/v1/auth/login 로그인 성공(200)")
    void givenEmailAndPW_whenLogin_thenSuccess() throws Exception {
        // given
        LoginRequest request = new LoginRequest(
                "example1@email.com",
                "password123"
        );

        // when & then
        mockMvc.perform(
                        post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .with(csrf()))
                .andExpect(status().isOk())
                .andReturn();
    }

    @ParameterizedTest
    @DisplayName("[USER][POST] /api/v1/auth/login 잘못된 이메일과 패스워드로 로그인 실패(400)")
    @CsvSource({
            // 정확한 정보
            //"test1@gmail.com, password123!",
            // 존재하지 않는 이메일
            "wrong@gmail.com, password123",
            // 틀린 비밀번호
            "example1@example.com, password1234",
    })
    void givenEmailAndPW_whenLogin_thenBadRequest(String email, String password) throws Exception {
        // given
        LoginRequest request = new LoginRequest(email, password);

        // when & then
        mockMvc.perform(
                        post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .with(csrf()))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    @DisplayName("[USER][GET] /api/v1/users/{userId} 유저 조회 성공(200)")
    void givenUserId_whenSearch_thenSuccess() throws Exception {
        // given
        long userId = 1L;

        // when & then
        mockMvc.perform(
                        get("/api/v1/users/" + userId)
                                .with(csrf()))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @DisplayName("[USER][GET] /api/v1/users/{userId} 유저 조회 실패(404)")
    void givenUserId_whenSearch_thenNotFound() throws Exception {
        // given
        long userId = 100L;

        // when & then
        mockMvc.perform(
                        get("/api/v1/users/" + userId)
                                .with(csrf()))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    @DisplayName("[USER][PUT] /api/v1/users/{userId} 유저 정보 수정 성공(200)")
    void givenModifiedUser_whenModify_thenSuccess() throws Exception {
        // given
        long userId = 1L;
        UpdateUserRequest request = UpdateUserRequest
                .builder()
                .profile("자기 소개")
                .nickname("modified_nickName")
                .languages(List.of(Language.ko.name(), Language.en.name()))
                .build();
        objectMapper.registerModule(new JavaTimeModule());
        String jsonProduct = objectMapper.writeValueAsString(request);
        MockMultipartFile modifiedRequest = new MockMultipartFile("dto", "modify", MediaType.APPLICATION_JSON_VALUE, jsonProduct.getBytes());
        MockMultipartFile multipartFile = new MockMultipartFile("file", "modify.jpg", MediaType.IMAGE_JPEG_VALUE, "modify image".getBytes());

        MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart("/api/v1/users/" + userId);
        builder.with(new RequestPostProcessor() {
            @Override
            public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                request.setMethod("PUT");
                return request;
            }
        });

        // when & then
        mockMvc.perform(
                        builder
                                .file(modifiedRequest)
                                .file(multipartFile)
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                                .with(csrf()))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @DisplayName("[USER][PUT] /api/v1/users/{userId} 유저 정보 수정 권한이 없어서 실패(403)")
    void givenWrongUser_whenModify_thenForbidden() throws Exception {
        // given
        long userId = 3L;
        UpdateUserRequest request = UpdateUserRequest
                .builder()
                .profile("자기 소개")
                .nickname("modified_nickName")
                .languages(List.of(Language.ko.name(), Language.en.name()))
                .build();
        objectMapper.registerModule(new JavaTimeModule());
        String jsonProduct = objectMapper.writeValueAsString(request);
        MockMultipartFile modifiedRequest = new MockMultipartFile("dto", "modify", MediaType.APPLICATION_JSON_VALUE, jsonProduct.getBytes());
        MockMultipartFile multipartFile = new MockMultipartFile("file", "modify.jpg", MediaType.IMAGE_JPEG_VALUE, "modify image".getBytes());

        MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart("/api/v1/users/" + userId);
        builder.with(new RequestPostProcessor() {
            @Override
            public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                request.setMethod("PUT");
                return request;
            }
        });

        // when & then
        mockMvc.perform(
                        builder
                                .file(modifiedRequest)
                                .file(multipartFile)
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                                .with(csrf()))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    @DisplayName("[USER][PUT] /api/v1/users/{userId} 입력 양식 오류로 수정 실패(400)")
    void givenWrongData_whenModify_thenBadRequest() throws Exception {
        // given
        long userId = 1L;
        UpdateUserRequest request = UpdateUserRequest.builder().build();
        objectMapper.registerModule(new JavaTimeModule());
        String jsonProduct = objectMapper.writeValueAsString(request);
        MockMultipartFile modifiedRequest = new MockMultipartFile("dto", "modify", "application/json", jsonProduct.getBytes());
        MockMultipartFile multipartFile = new MockMultipartFile("file", "modify.jpg", "image/jpg", "modify image".getBytes());

        MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart("/api/v1/users/" + userId);
        builder.with(new RequestPostProcessor() {
            @Override
            public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                request.setMethod("PUT");
                return request;
            }
        });

        // when & then
        mockMvc.perform(
                        builder
                                .file(modifiedRequest)
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                                .with(csrf()))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    // PRODUCT
    @DisplayName("[PRODUCT][GET] /api/v1/search/main 위치 정보 없는 메인 페이지 조회 성공(200)")
    @Test
    void given_whenMain_thenSuccess() throws Exception {
        // given & when
        ResultActions result = mockMvc.perform(
                get("/api/v1/search/main")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf()));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.bestGuideProducts").isArray())
                .andExpect(jsonPath("$.nearGuideProducts").isArray())
                .andExpect(jsonPath("$.allGuideProducts.content").isArray());
    }

    @DisplayName("[PRODUCT][GET] /api/v1/search/main 위치 정보가 있는 메인 페이지 조회 성공(200)")
    @Test
    void givenPoint_whenMain_thenSuccess() throws Exception {
        // given
        Double latitude = 37.5665;
        Double longitude = 126.9780;

        // when
        ResultActions result = mockMvc.perform(
                get("/api/v1/search/main")
                        .param("latitude", latitude.toString())
                        .param("longitude", longitude.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf()));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.bestGuideProducts").isArray())
                .andExpect(jsonPath("$.nearGuideProducts").isArray())
                .andExpect(jsonPath("$.allGuideProducts.content").isArray());
    }

    @DisplayName("[PRODUCT][POST] /api/v1/products 가이드 상품 등록 성공(200)")
    @Test
    void givenProduct_whenCreate_thenSuccess() throws Exception {
        // given
        CreateGuideProductRequest request = new CreateGuideProductRequest(
                "테스트 상품 제목",
                "테스트 상품 설명",
                10000L,
                "테스트 가이드 위치 이름",
                37.0,
                127.0,
                LocalDate.of(2024, 6, 1),
                LocalDate.of(2024, 6, 3),
                LocalTime.of(12, 0, 0),
                LocalTime.of(18, 0, 0),
                3,
                List.of(GuideCategoryCode.DINING, GuideCategoryCode.OUTDOOR)
        );

        objectMapper.registerModule(new JavaTimeModule());
        String jsonProduct = objectMapper.writeValueAsString(request);
        InputStream inputStream1 = getClass().getResourceAsStream("/test.png");
        InputStream inputStream2 = getClass().getResourceAsStream("/test.png");
        InputStream inputStream3 = getClass().getResourceAsStream("/test.png");
        MockMultipartFile createRequest = new MockMultipartFile("request", "request", MediaType.APPLICATION_JSON_VALUE, jsonProduct.getBytes());
        MockMultipartFile thumbImage = new MockMultipartFile("thumb", "thumb.png", MediaType.IMAGE_PNG_VALUE, IOUtils.toByteArray(inputStream1));
        MockMultipartFile testImage1 = new MockMultipartFile("file", "file1.png", MediaType.IMAGE_PNG_VALUE, IOUtils.toByteArray(inputStream2));
        MockMultipartFile testImage2 = new MockMultipartFile("file", "file2.png", MediaType.IMAGE_PNG_VALUE, IOUtils.toByteArray(inputStream3));

        // when & then
        mockMvc.perform(
                        multipart("/api/v1/products")
                                .file(createRequest)
                                .file(thumbImage)
                                .file(testImage1)
                                .file(testImage2)
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                                .with(csrf()))
                .andExpect(status().isOk())
                .andReturn();
    }

    @DisplayName("[PRODUCT][POST] /api/v1/products 잘못된 위치로 가이드 상품 등록 실패(400)")
    @Test
    void givenWrongPoint_whenCreate_thenBadRequest() throws Exception {
        // given
        CreateGuideProductRequest request = new CreateGuideProductRequest(
                "테스트 상품 제목",
                "테스트 상품 설명",
                10000L,
                "테스트 가이드 위치 이름",
                127.0,
                38.0,
                LocalDate.of(2024, 6, 1),
                LocalDate.of(2024, 6, 3),
                LocalTime.of(12, 0, 0),
                LocalTime.of(18, 0, 0),
                3,
                List.of(GuideCategoryCode.DINING, GuideCategoryCode.OUTDOOR)
        );

        objectMapper.registerModule(new JavaTimeModule());
        String jsonProduct = objectMapper.writeValueAsString(request);
        InputStream inputStream1 = getClass().getResourceAsStream("/test.png");
        MockMultipartFile createRequest = new MockMultipartFile("request", "request", MediaType.APPLICATION_JSON_VALUE, jsonProduct.getBytes());
        MockMultipartFile thumbImage = new MockMultipartFile("thumb", "thumb.png", MediaType.IMAGE_PNG_VALUE, IOUtils.toByteArray(inputStream1));

        // when & then
        mockMvc.perform(
                        multipart("/api/v1/products")
                                .file(createRequest)
                                .file(thumbImage)
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                                .with(csrf()))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @DisplayName("[PRODUCT][POST] /api/v1/products 지원하지 않는 이미지로 가이드 상품 등록 실패(415)")
    @Test
    void givenWrongImage_whenCreate_thenUnsupportedMediaType() throws Exception {
        // given
        CreateGuideProductRequest request = new CreateGuideProductRequest(
                "테스트 상품 제목",
                "테스트 상품 설명",
                10000L,
                "테스트 가이드 위치 이름",
                37.0,
                127.0,
                LocalDate.of(2024, 6, 1),
                LocalDate.of(2024, 6, 3),
                LocalTime.of(12, 0, 0),
                LocalTime.of(18, 0, 0),
                3,
                List.of(GuideCategoryCode.DINING, GuideCategoryCode.OUTDOOR)
        );

        objectMapper.registerModule(new JavaTimeModule());
        String jsonProduct = objectMapper.writeValueAsString(request);
        MockMultipartFile createRequest = new MockMultipartFile("request", "request", MediaType.APPLICATION_JSON_VALUE, jsonProduct.getBytes());
        MockMultipartFile thumbImage = new MockMultipartFile("thumb", "thumb.png", MediaType.IMAGE_PNG_VALUE, "wrong".getBytes());

        // when & then
        mockMvc.perform(
                        multipart("/api/v1/products")
                                .file(createRequest)
                                .file(thumbImage)
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                                .with(csrf()))
                .andExpect(status().isUnsupportedMediaType())
                .andReturn();
    }

    @DisplayName("[PRODUCT][GET] /api/v1/products/{productId} 가이드 상품 조회 성공(200)")
    @Test
    void givenId_whenGet_thenSuccess() throws Exception {
        // given
        Long productId = 1L;

        // when & then
        mockMvc.perform(
                        get("/api/v1/products/" + productId)
                                .with(csrf()))
                .andExpect(status().isOk())
                .andReturn();
    }

    @DisplayName("[PRODUCT][GET] /api/v1/products/{productId} 가이드 상품 조회 실패(404)")
    @Test
    void givenId_whenGet_thenNotFound() throws Exception {
        // given
        Long wrongProductId = 1000L;

        // when & then
        mockMvc.perform(
                        get("/api/v1/products/" + wrongProductId)
                                .with(csrf()))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @DisplayName("[PRODUCT][PUT] /api/v1/products/{productId} 가이드 상품 수정 성공(이미지도 수정할 경우)(200)")
    @Test
    void givenModifiedProductAndImages_whenModify_thenSuccess() throws Exception {
        // given
        Long productId = 1L;
        ModifyGuideProductRequest request = new ModifyGuideProductRequest(
                "modified title",
                "modified description",
                50000L,
                "modified locationName",
                25.0,
                110.0,
                LocalDate.of(2024, 6, 5),
                LocalDate.of(2024, 6, 10),
                LocalTime.of(8, 0, 0),
                LocalTime.of(13, 0, 0),
                2,
                List.of(GuideCategoryCode.ART_CULTURE, GuideCategoryCode.ENTERTAINMENT),
                null,
                new ArrayList<>()
        );

        objectMapper.registerModule(new JavaTimeModule());
        String jsonProduct = objectMapper.writeValueAsString(request);
        InputStream inputStream1 = getClass().getResourceAsStream("/test.png");
        InputStream inputStream2 = getClass().getResourceAsStream("/test.png");
        MockMultipartFile modifiedRequest = new MockMultipartFile("request", "request", MediaType.APPLICATION_JSON_VALUE, jsonProduct.getBytes());
        MockMultipartFile modifiedThumbImage = new MockMultipartFile("thumb", "thumb.png", MediaType.IMAGE_PNG_VALUE, IOUtils.toByteArray(inputStream1));
        MockMultipartFile modifiedImage = new MockMultipartFile("file", "image.png", MediaType.IMAGE_PNG_VALUE, IOUtils.toByteArray(inputStream2));

        // when & then
        MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart("/api/v1/products/" + productId);
        builder.with(new RequestPostProcessor() {
            @Override
            public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                request.setMethod("PUT");
                return request;
            }
        });

        mockMvc.perform(
                        builder
                                .file(modifiedRequest)
                                .file(modifiedThumbImage)
                                .file(modifiedImage)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .with(csrf()))
                .andExpect(status().isOk())
                .andReturn();
    }

    @DisplayName("[PRODUCT][PUT] /api/v1/products/{productId} 가이드 상품 수정 성공(이미지 수정 x)(200)")
    @Test
    void givenModifiedProduct_whenModify_thenSuccess() throws Exception {
        // given
        Long productId = 1L;
        ModifyGuideProductRequest request = new ModifyGuideProductRequest(
                "modified title",
                "modified description",
                50000L,
                "modified locationName",
                25.0,
                110.0,
                LocalDate.of(2024, 6, 5),
                LocalDate.of(2024, 6, 10),
                LocalTime.of(8, 0, 0),
                LocalTime.of(13, 0, 0),
                2,
                List.of(GuideCategoryCode.ART_CULTURE, GuideCategoryCode.ENTERTAINMENT),
                "test thumb url",
                List.of("test image1 url", "test image2 url")
        );

        objectMapper.registerModule(new JavaTimeModule());
        String jsonProduct = objectMapper.writeValueAsString(request);
        MockMultipartFile modifiedRequest = new MockMultipartFile("request", "request", MediaType.APPLICATION_JSON_VALUE, jsonProduct.getBytes());

        // when & then
        MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart("/api/v1/products/" + productId);
        builder.with(new RequestPostProcessor() {
            @Override
            public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                request.setMethod("PUT");
                return request;
            }
        });

        mockMvc.perform(
                        builder
                                .file(modifiedRequest)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .with(csrf()))
                .andExpect(status().isOk())
                .andReturn();
    }

    @DisplayName("[PRODUCT][PUT] /api/v1/products/{productId} 가이드 상품 수정 권한이 없어 실패(401)")
    @Test
    void givenModifiedProduct_whenModify_thenUnauthorized() throws Exception {
        // given
        Long productId = 2L;
        ModifyGuideProductRequest request = new ModifyGuideProductRequest(
                "modified title",
                "modified description",
                50000L,
                "modified locationName",
                25.0,
                110.0,
                LocalDate.of(2024, 6, 5),
                LocalDate.of(2024, 6, 10),
                LocalTime.of(8, 0, 0),
                LocalTime.of(13, 0, 0),
                2,
                List.of(GuideCategoryCode.ART_CULTURE, GuideCategoryCode.ENTERTAINMENT),
                "test thumb url",
                List.of("test image1 url", "test image2 url")
        );

        objectMapper.registerModule(new JavaTimeModule());
        String jsonProduct = objectMapper.writeValueAsString(request);
        MockMultipartFile modifiedRequest = new MockMultipartFile("request", "request", MediaType.APPLICATION_JSON_VALUE, jsonProduct.getBytes());

        // when & then
        MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart("/api/v1/products/" + productId);
        builder.with(new RequestPostProcessor() {
            @Override
            public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                request.setMethod("PUT");
                return request;
            }
        });

        mockMvc.perform(
                        builder
                                .file(modifiedRequest)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @DisplayName("[PRODUCT][PUT] /api/v1/products/{productId} 가이드 상품이 존재하지 않아 수정 실패(404)")
    @Test
    void givenModifiedProduct_whenModify_thenNotFound() throws Exception {
        // given
        Long productId = 100L;
        ModifyGuideProductRequest request = new ModifyGuideProductRequest(
                "modified title",
                "modified description",
                50000L,
                "modified locationName",
                25.0,
                110.0,
                LocalDate.of(2024, 6, 5),
                LocalDate.of(2024, 6, 10),
                LocalTime.of(8, 0, 0),
                LocalTime.of(13, 0, 0),
                2,
                List.of(GuideCategoryCode.ART_CULTURE, GuideCategoryCode.ENTERTAINMENT),
                "test thumb url",
                List.of("test image1 url", "test image2 url")
        );

        objectMapper.registerModule(new JavaTimeModule());
        String jsonProduct = objectMapper.writeValueAsString(request);
        MockMultipartFile modifiedRequest = new MockMultipartFile("request", "request", MediaType.APPLICATION_JSON_VALUE, jsonProduct.getBytes());

        // when & then
        MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart("/api/v1/products/" + productId);
        builder.with(new RequestPostProcessor() {
            @Override
            public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                request.setMethod("PUT");
                return request;
            }
        });

        mockMvc.perform(
                        builder
                                .file(modifiedRequest)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .with(csrf()))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @DisplayName("[PRODUCT][PUT] /api/v1/products/{productId} 가이드 상품 수정 시 지원하지 않는 이미지로 실패(404)")
    @Test
    void givenModifiedProductAndWrongImage_whenModify_thenUnsupported() throws Exception {
        // given
        Long productId = 1L;
        ModifyGuideProductRequest request = new ModifyGuideProductRequest(
                "modified title",
                "modified description",
                50000L,
                "modified locationName",
                25.0,
                110.0,
                LocalDate.of(2024, 6, 5),
                LocalDate.of(2024, 6, 10),
                LocalTime.of(8, 0, 0),
                LocalTime.of(13, 0, 0),
                2,
                List.of(GuideCategoryCode.ART_CULTURE, GuideCategoryCode.ENTERTAINMENT),
                null,
                new ArrayList<>()
        );

        objectMapper.registerModule(new JavaTimeModule());
        String jsonProduct = objectMapper.writeValueAsString(request);
        MockMultipartFile modifiedRequest = new MockMultipartFile("request", "request", MediaType.APPLICATION_JSON_VALUE, jsonProduct.getBytes());
        MockMultipartFile modifiedThumbImage = new MockMultipartFile("thumb", "thumb.png", MediaType.IMAGE_PNG_VALUE, "text".getBytes());

        // when & then
        MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart("/api/v1/products/" + productId);
        builder.with(new RequestPostProcessor() {
            @Override
            public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                request.setMethod("PUT");
                return request;
            }
        });

        mockMvc.perform(
                        builder
                                .file(modifiedRequest)
                                .file(modifiedThumbImage)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .with(csrf()))
                .andExpect(status().isUnsupportedMediaType())
                .andReturn();
    }

    @DisplayName("[PRODUCT][DELETE] /api/v1/products/{productId} 가이드 상품 삭제 성공(200)")
    @Test
    void givenId_whenDelete_thenSuccess() throws Exception {
        // given
        Long productId = 1L;

        // when & then
        mockMvc.perform(
                        delete("/api/v1/products/" + productId)
                                .with(csrf()))
                .andExpect(status().isOk())
                .andReturn();
    }

    @DisplayName("[PRODUCT][DELETE] /api/v1/products/{productId} 가이드 상품 삭제 권한이 없어 실패(401)")
    @Test
    void givenId_whenDelete_thenUnauthorized() throws Exception {
        // given
        Long productId = 2L;

        // when & then
        mockMvc.perform(
                        delete("/api/v1/products/" + productId)
                                .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @DisplayName("[PRODUCT][DELETE] /api/v1/products/{productId} 가이드 상품 존재하지 않아 삭제 실패(401)")
    @Test
    void givenId_whenDelete_thenNotFound() throws Exception {
        // given
        Long productId = 100L;

        // when & then
        mockMvc.perform(
                        delete("/api/v1/products/" + productId)
                                .with(csrf()))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @DisplayName("[PRODUCT][GET] /api/v1/search 가이드 상품 지역, 날짜로 검색(200)")
    @Test
    void givenRegionAndDate_whenSearch_thenSuccess() throws Exception {
        // given
        SearchGuideProductRequest request = new SearchGuideProductRequest(
                "서울특별시",
                LocalDate.of(2024, 5, 11),
                LocalDate.of(2024, 5, 12)
        );

        // when
        ResultActions result = mockMvc.perform(
                get("/api/v1/search")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @DisplayName("[PRODUCT][GET] /api/v1/search 가이드 상품 근처(20km) 검색(200)")
    @Test
    void givenPointAndNear_whenSearch_thenSuccess() throws Exception {
        // given
        SearchCategoriesRequest request = new SearchCategoriesRequest(
                37.123,
                -122.456,
                GuideCategoryCode.NEAR
        );

        // when
        ResultActions result = mockMvc.perform(
                get("/api/v1/search")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @DisplayName("[PRODUCT][GET] /api/v1/search 가이드 상품 카테고리 검색(200)")
    @Test
    void givenCategory_whenSearch_thenSuccess() throws Exception {
        // given
        SearchCategoriesRequest request = new SearchCategoriesRequest(
                null,
                null,
                GuideCategoryCode.OUTDOOR
        );

        // when
        ResultActions result = mockMvc.perform(
                get("/api/v1/search")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @DisplayName("[PRODUCT][GET] /api/v1/search 가이드 상품 필터 검색(200)")
    @Test
    void givenFilter_whenSearch_thenSuccess() throws Exception {
        // given & when
        ResultActions result = mockMvc.perform(
                get("/api/v1/search")
                        .param("min", "10000")
                        .param("max", "50000")
                        .param("minD", "1")
                        .param("maxD", "5")
                        .param("dayT", DayTime.MORNING.toString())
                        .param("host", "false")
                        .param("lan", Language.ko.toString())
                        .with(csrf()));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @DisplayName("[PRODUCT][GET] /api/v1/search 가이드 상품 지역,날짜 + 필터 검색(200)")
    @Test
    void givenRegionAndDateAndFilter_whenSearch_thenSuccess() throws Exception {
        // given
        SearchGuideProductRequest request = new SearchGuideProductRequest(
                "서울특별시",
                LocalDate.of(2024, 5, 11),
                LocalDate.of(2024, 5, 12)
        );

        // when
        ResultActions result = mockMvc.perform(
                get("/api/v1/search")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("min", "10000")
                        .param("max", "50000")
                        .param("minD", "1")
                        .param("maxD", "5")
                        .param("dayT", DayTime.MORNING.toString())
                        .param("host", "false")
                        .param("lan", Language.ko.toString())
                        .with(csrf()));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @DisplayName("[PRODUCT][GET] /api/v1/search 가이드 상품 지역,날짜 + 카테고리 검색(200)")
    @Test
    void givenRegionAndDateAndCategory_whenSearch_thenSuccess() throws Exception {
        // given
        SearchGuideProductRequest request1 = new SearchGuideProductRequest(
                "서울특별시",
                LocalDate.of(2024, 5, 11),
                LocalDate.of(2024, 5, 12)
        );

        SearchCategoriesRequest request2 = new SearchCategoriesRequest(
                null,
                null,
                GuideCategoryCode.OUTDOOR
        );

        // when
        ResultActions result = mockMvc.perform(
                get("/api/v1/search")
                        .content(objectMapper.writeValueAsString(request1))
                        .content(objectMapper.writeValueAsString(request2))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @DisplayName("[PRODUCT][GET] /api/v1/search 가이드 상품 카테고리 + 필터 검색(200)")
    @Test
    void givenCategoryAndFilter_whenSearch_thenSuccess() throws Exception {
        // given
        SearchCategoriesRequest request = new SearchCategoriesRequest(
                null,
                null,
                GuideCategoryCode.OUTDOOR
        );

        // when
        ResultActions result = mockMvc.perform(
                get("/api/v1/search")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("min", "10000")
                        .param("max", "50000")
                        .param("minD", "1")
                        .param("maxD", "5")
                        .param("dayT", DayTime.MORNING.toString())
                        .param("host", "false")
                        .param("lan", Language.ko.toString())
                        .with(csrf()));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @DisplayName("[PRODUCT][GET] /api/v1/search 가이드 상품 지역, 날짜 + 카테고리 + 필터 검색(200)")
    @Test
    void givenRegionAndDateAndCategoryAndFilter_whenSearch_thenSuccess() throws Exception {
        // given
        SearchGuideProductRequest request1 = new SearchGuideProductRequest(
                "서울특별시",
                LocalDate.of(2024, 5, 11),
                LocalDate.of(2024, 5, 12)
        );

        SearchCategoriesRequest request2 = new SearchCategoriesRequest(
                null,
                null,
                GuideCategoryCode.OUTDOOR
        );

        // when
        ResultActions result = mockMvc.perform(
                get("/api/v1/search")
                        .content(objectMapper.writeValueAsString(request1))
                        .content(objectMapper.writeValueAsString(request2))
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("min", "10000")
                        .param("max", "50000")
                        .param("minD", "1")
                        .param("maxD", "5")
                        .param("dayT", DayTime.MORNING.toString())
                        .param("host", "false")
                        .param("lan", Language.ko.toString())
                        .with(csrf()));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    // REVIEW
    @DisplayName("[REVIEW][GET] /api/v1/reviews/{reviewId} 리뷰 조회(200)")
    @Test
    void givenReviewId_whenGetReview_thenSuccess() throws Exception {
        // given
        Long reviewId = 1L;

        // when & then
        mockMvc.perform(
                        get("/api/v1/reviews/" + reviewId)
                                .with(csrf()))
                .andExpect(status().isOk())
                .andReturn();
    }

    @DisplayName("[REVIEW][POST] /api/v1/reviews 리뷰 생성(200)")
    @Test
    void givenReview_whenCreate_thenSuccess() throws Exception {
        // given
        CreateReviewRequest request = CreateReviewRequest
                .builder()
                .reservationId(1L)
                .content("재미있어요")
                .rating(5)
                .build();

        objectMapper.registerModule(new JavaTimeModule());
        String jsonProduct = objectMapper.writeValueAsString(request);
        InputStream inputStream1 = getClass().getResourceAsStream("/test.png");
        MockMultipartFile createRequest = new MockMultipartFile("dto", "request", MediaType.APPLICATION_JSON_VALUE, jsonProduct.getBytes());
        MockMultipartFile reviewImage = new MockMultipartFile("file", "thumb.png", MediaType.IMAGE_PNG_VALUE, IOUtils.toByteArray(inputStream1));

        // when & then
        mockMvc.perform(
                        multipart("/api/v1/reviews")
                                .file(createRequest)
                                .file(reviewImage)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .with(csrf()))
                .andExpect(status().isOk())
                .andReturn();
    }

    @DisplayName("[REVIEW][POST] /api/v1/reviews 리뷰 생성시 잘못된 입력값으로 실패(400)")
    @Test
    void givenWrongReview_whenCreate_thenBadRequest() throws Exception {
        // given
        CreateReviewRequest request = CreateReviewRequest
                .builder()
                .content("재미있어요")
                .rating(5)
                .build();

        objectMapper.registerModule(new JavaTimeModule());
        String jsonProduct = objectMapper.writeValueAsString(request);
        InputStream inputStream1 = getClass().getResourceAsStream("/test.png");
        MockMultipartFile createRequest = new MockMultipartFile("dto", "request", MediaType.APPLICATION_JSON_VALUE, jsonProduct.getBytes());
        MockMultipartFile reviewImage = new MockMultipartFile("file", "thumb.png", MediaType.IMAGE_PNG_VALUE, IOUtils.toByteArray(inputStream1));

        // when & then
        mockMvc.perform(
                        multipart("/api/v1/reviews")
                                .file(createRequest)
                                .file(reviewImage)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .with(csrf()))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    // RESERVATION
    @DisplayName("[RESERVATION][POST] /api/v1/reservation/client/save 예약 정보 저장(200)")
    @Test
    void givenNewReservation_whenCreate_thenSuccess() throws Exception {
        // given
        SaveReservationRequest request = SaveReservationRequest
                .builder()
                .productId(1L)
                .guideStart(ZonedDateTime.now())
                .guideEnd(ZonedDateTime.now())
                .personnel(1)
                .price(20000)
                .build();

        // when & then
        mockMvc.perform(
                        post("/api/v1/reservation/client/save")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .with(csrf()))
                .andExpect(status().isOk())
                .andReturn();
    }

    @DisplayName("[RESERVATION][POST] /api/v1/reservation/client/save 예약 정보 저장시 상품 id가 존재하지 않아 실패(409)")
    @Test
    void givenWrongId_whenCreateReservation_thenSuccess() throws Exception {
        // given
        SaveReservationRequest request = SaveReservationRequest
                .builder()
                .productId(100L)
                .guideStart(ZonedDateTime.now())
                .guideEnd(ZonedDateTime.now())
                .personnel(1)
                .price(20000)
                .build();

        // when
        ResultActions result = mockMvc.perform(
                post("/api/v1/reservation/client/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()));

        // then
        result.andExpect(content().string("예약 정보 저장에 실패했습니다."));
    }

    @DisplayName("[RESERVATION][POST] /api/v1/reservation/client/cancel/{merchant_uid} 여행객이 예약 취소 성공(200)")
    @Test
    void givenMerchantUid_whenCancelReservation_thenSuccess() throws Exception {
        // given
        String merchantUid = "merchant_uid_1";

        // when & then
        mockMvc.perform(
                        post("/api/v1/reservation/client/cancel/" + merchantUid)
                                .with(csrf()))
                .andExpect(status().isOk())
                .andReturn();
    }

    @DisplayName("[RESERVATION][POST] /api/v1/reservation/client/list 예약 리스트 조회 성공(200)")
    @Test
    void givenCriteria_whenGetReservationList_thenSuccess() throws Exception {
        // given
        ReservationSearchCriteria request = ReservationSearchCriteria
                .builder()
                .isPast(false)
                .statusFilter(1)
                .offset(0)
                .pageSize(5)
                .build();

        // when
        ResultActions result = mockMvc.perform(
                post("/api/v1/reservation/client/list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()));

        // then
        result.andExpect(status().isOk());
    }

    @DisplayName("[RESERVATION][POST] /api/v1/reservation/guide/confirm/{merchant_uid} 가이드가 예약 확정 성공(200)")
    @Test
    void givenMerchantUid_whenConfirmReservation_thenSuccess() throws Exception {
        // given
        String merchantUid = "merchant_uid_1";

        // when & then
        mockMvc.perform(
                        post("/api/v1/reservation/guide/confirm/" + merchantUid)
                                .with(csrf()))
                .andExpect(status().isOk())
                .andReturn();
    }

    @DisplayName("[RESERVATION][POST] /api/v1/reservation/guide/cancel/{merchant_uid} 가이드가 예약 취소 성공(200)")
    @Test
    void givenMerchantUid_whenGuideCancelReservation_thenSuccess() throws Exception {
        // given
        String merchantUid = "merchant_uid_1";

        // when & then
        mockMvc.perform(
                        post("/api/v1/reservation/guide/cancel/" + merchantUid)
                                .with(csrf()))
                .andExpect(status().isOk())
                .andReturn();
    }

    @DisplayName("[RESERVATION][GET] /api/v1/reservation/{merchant_uid} 예약 정보 조회(200)")
    @Test
    void givenMerchantUid_whenGetReservation_thenSuccess() throws Exception {
        // given
        String merchantUid = "merchant_uid_1";

        // when & then
        mockMvc.perform(
                        get("/api/v1/reservation/" + merchantUid)
                                .with(csrf()))
                .andExpect(status().isOk())
                .andReturn();
    }

    // PAYMENT
    @DisplayName("[PAYMENT][POST] /api/v1/reservation/client/payment 결제 정보 저장 성공(200)")
    @Test
    void givenPayment_whenSavePayment_thenSuccess() throws Exception {
        // given
        SavePaymentRequest request = SavePaymentRequest
                .builder()
                .merchantUid("merchant_uid_1")
                .productId(1L)
                .impUid("imp_uid_7")
                .paidAt(20240524032530L)
                .price(10000)
                .personnel(1)
                .build();

        // when & then
        mockMvc.perform(
                        post("/api/v1/reservation/client/payment")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .with(csrf()))
                .andExpect(status().isOk())
                .andReturn();
    }

    // CHAT
    @DisplayName("[CHAT][POST] /api/v1/chat/room 채팅방 생성 성공(200)")
    @Test
    void givenEmail_whenCreateChat_thenSuccess() throws Exception {
        // given
        MultiValueMap<String, String> chatRoomRequest = new LinkedMultiValueMap<>();
        chatRoomRequest.add("chatRoomRequest", "test2@gmail.com");

        // when & then
        mockMvc.perform(
                        post("/api/v1/chat/room")
                                .contentType(MediaType.APPLICATION_JSON)
                                .params(chatRoomRequest)
                                .with(csrf()))
                .andExpect(status().isOk())
                .andReturn();
    }
}
