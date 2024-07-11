package com.swygbro.trip.backend.domain.guideProduct.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.swygbro.trip.backend.domain.guideProduct.application.GuideProductService;
import com.swygbro.trip.backend.domain.guideProduct.dto.CreateGuideProductDto;
import com.swygbro.trip.backend.domain.guideProduct.dto.CreateGuideProductRequest;
import com.swygbro.trip.backend.domain.guideProduct.dto.GuideProductDto;
import com.swygbro.trip.backend.domain.guideProduct.dto.ModifyGuideProductRequest;
import com.swygbro.trip.backend.domain.guideProduct.exception.GuideProductNotFoundException;
import com.swygbro.trip.backend.domain.guideProduct.exception.MismatchUserFromCreatorException;
import com.swygbro.trip.backend.domain.guideProduct.fixture.GuideProductRequestFixture;
import com.swygbro.trip.backend.domain.user.TestUserFactory;
import com.swygbro.trip.backend.domain.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("가이드 상품 API")
@WebMvcTest(value = GuideProductController.class)
@MockBean(JpaMetamodelMappingContext.class)
@WithMockUser("test")
public class GuideProductControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private GuideProductService guideProductService;

    private User user;

    @BeforeEach
    void setup() {
        user = TestUserFactory.createTestUser();
    }

    @DisplayName("이미지 등록 API")
    @Test
    void createProduct() throws Exception {
        CreateGuideProductRequest request = GuideProductRequestFixture.getCreateGuideProductRequest();

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String jsonProduct = mapper.writeValueAsString(request);
        MockMultipartFile product = new MockMultipartFile("request", "product", "application/json", jsonProduct.getBytes());

        List<MultipartFile> images = new ArrayList<>();
        MockMultipartFile mockMultipartFileThumb = new MockMultipartFile("thumb", "test1.jpg", "image/jpg", "test image1".getBytes());
        MockMultipartFile mockMultipartFile1 = new MockMultipartFile("file", "test1.jpg", "image/jpg", "test image1".getBytes());
        MockMultipartFile mockMultipartFile2 = new MockMultipartFile("file", "test2.jpg", "image/jpg", "test image2".getBytes());
        images.add(mockMultipartFile1);
        images.add(mockMultipartFile2);

        given(guideProductService.createGuideProduct(user, request, mockMultipartFileThumb, Optional.of(images))).willReturn(new CreateGuideProductDto());

        mockMvc.perform(
                        multipart("/api/v1/products")
                                .file(mockMultipartFileThumb)
                                .file((MockMultipartFile) images.get(0))
                                .file((MockMultipartFile) images.get(1))
                                .file(product)
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                                .with(csrf()))
                .andExpect(status().isOk())
                .andReturn();
    }

    @DisplayName("상품 조회 API")
    @Test
    void getProduct() throws Exception {
        Long productId = 1L;

        given(guideProductService.getProduct(productId)).willReturn(new GuideProductDto());

        mockMvc.perform(
                        get("/api/v1/products/" + productId)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    @DisplayName("상품 조회 API 실패")
    @Test
    void getProduct_fail() throws Exception {
        Long productId = 1L;

        doThrow(new GuideProductNotFoundException(productId)).when(guideProductService).getProduct(productId);

        mockMvc.perform(
                        get("/api/v1/products/" + productId)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn();
    }

    @DisplayName("상품 수정 API")
    @Test
    void modifyProduct() throws Exception {
        long productId = 1L;
        ModifyGuideProductRequest edit = GuideProductRequestFixture.getModifyProductRequest();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String body = mapper.writeValueAsString(edit);
        MockMultipartFile requestEdit = new MockMultipartFile("request", "product", "application/json", body.getBytes());

        List<MultipartFile> images = new ArrayList<>();
        MockMultipartFile mockMultipartFileThumb = new MockMultipartFile("thumb", "modify_test.jpg", "image/jpg", "test thumb image".getBytes());
        MockMultipartFile mockMultipartFile1 = new MockMultipartFile("file", "modify_test.jpg", "image/jpg", "test image1".getBytes());
        MockMultipartFile mockMultipartFile2 = new MockMultipartFile("file", "modify_test.jpg", "image/jpg", "test image2".getBytes());
        images.add(mockMultipartFile1);
        images.add(mockMultipartFile2);

        given(guideProductService.modifyGuideProduct(user, productId, edit, Optional.of(mockMultipartFileThumb), Optional.of(images))).willReturn(new GuideProductDto());

        MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart("/api/v1/products/" + productId);
        builder.with(new RequestPostProcessor() {
            @Override
            public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                request.setMethod("PUT");
                return request;
            }
        });
        mockMvc.perform(builder
                        .file(requestEdit)
                        .file(mockMultipartFileThumb)
                        .file((MockMultipartFile) images.get(0))
                        .file((MockMultipartFile) images.get(1))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
    }

    @DisplayName("상품 수정 시 권한이 없는 경우 수정 실패")
    @Test
    void modifyProduct_unauthorized() throws Exception {
        long productId = 1L;
        ModifyGuideProductRequest edit = GuideProductRequestFixture.getModifyProductRequest();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String body = mapper.writeValueAsString(edit);
        MockMultipartFile requestEdit = new MockMultipartFile("request", "product", "application/json", body.getBytes());

        List<MultipartFile> images = new ArrayList<>();
        MockMultipartFile mockMultipartFileThumb = new MockMultipartFile("thumb", "modify_test.jpg", "image/jpg", "test thumb image".getBytes());
        MockMultipartFile mockMultipartFile1 = new MockMultipartFile("file", "modify_test.jpg", "image/jpg", "test image1".getBytes());
        MockMultipartFile mockMultipartFile2 = new MockMultipartFile("file", "modify_test.jpg", "image/jpg", "test image2".getBytes());
        images.add(mockMultipartFile1);
        images.add(mockMultipartFile2);

        doThrow(new MismatchUserFromCreatorException("가이드 상품을 수정할 권한이 없습니다.")).when(guideProductService).modifyGuideProduct(any(), any(), any(), any(), any());

        MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart("/api/v1/products/" + productId);
        builder.with(new RequestPostProcessor() {
            @Override
            public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                request.setMethod("PUT");
                return request;
            }
        });
        mockMvc.perform(builder
                        .file(requestEdit)
                        .file(mockMultipartFileThumb)
                        .file((MockMultipartFile) images.get(0))
                        .file((MockMultipartFile) images.get(1))
                        .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @DisplayName("상품 삭제 API")
    @Test
    void deleteGuideProduct() throws Exception {
        Long productId = 1L;
        String email = "test@gmail.com";

        mockMvc.perform(delete("/api/v1/products/" + productId)
                        .param("email", email)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @DisplayName("상품 삭제 권한이 없을 경우")
    @Test
    void deleteGuideProduct_unauthorized() throws Exception {
        Long productId = 1L;
        String email = "test@gmail.com";

        doThrow(new MismatchUserFromCreatorException("가이드 상품을 삭제할 권한이 없습니다.")).when(guideProductService).deleteGuideProduct(any(), any());

        mockMvc.perform(delete("/api/v1/products/" + productId)
                        .param("email", email)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }
}
