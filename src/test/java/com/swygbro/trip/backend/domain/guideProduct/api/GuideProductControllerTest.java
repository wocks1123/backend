package com.swygbro.trip.backend.domain.guideProduct.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.swygbro.trip.backend.domain.guideProduct.application.GuideProductService;
import com.swygbro.trip.backend.domain.guideProduct.domain.GuideProduct;
import com.swygbro.trip.backend.domain.guideProduct.dto.GuideProductDto;
import com.swygbro.trip.backend.domain.guideProduct.dto.GuideProductRequest;
import com.swygbro.trip.backend.domain.guideProduct.exception.GuideProductNotFoundException;
import com.swygbro.trip.backend.domain.guideProduct.exception.MismatchUserFromCreatorException;
import com.swygbro.trip.backend.domain.guideProduct.fixture.GuideProductFixture;
import com.swygbro.trip.backend.domain.guideProduct.fixture.GuideProductRequestFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("가이드 상품 API")
@WebMvcTest(GuideProductController.class)
@MockBean(JpaMetamodelMappingContext.class)
public class GuideProductControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private GuideProductService guideProductService;

    @DisplayName("이미지 등록 API")
    @Test
    void createProduct() throws Exception {
        GuideProductRequest request = GuideProductRequestFixture.getGuideProductRequest();

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String jsonProduct = mapper.writeValueAsString(request);
        MockMultipartFile product = new MockMultipartFile("request", "product", "application/json", jsonProduct.getBytes());

        List<MultipartFile> images = new ArrayList<>();
        MockMultipartFile mockMultipartFile1 = new MockMultipartFile("file", "test1.jpg", "image/jpg", "test image1".getBytes());
        MockMultipartFile mockMultipartFile2 = new MockMultipartFile("file", "test2.jpg", "image/jpg", "test image2".getBytes());
        images.add(mockMultipartFile1);
        images.add(mockMultipartFile2);

        given(guideProductService.createGuideProduct(request, images)).willReturn(new GuideProductDto());

        mockMvc.perform(
                        multipart("/api/v1/products")
                                .file((MockMultipartFile) images.get(0))
                                .file((MockMultipartFile) images.get(1))
                                .file(product)
                                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
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
        GuideProductRequest edit = GuideProductRequestFixture.getGuideProductRequest();
        edit.setTitle("modify title");
        edit.setDescription("modify description");
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String body = mapper.writeValueAsString(edit);
        GuideProduct product = GuideProductFixture.getGuideProduct();

        given(guideProductService.modifyGuideProduct(productId, edit)).willReturn(GuideProductDto.fromEntity(product));

        mockMvc.perform(
                        put("/api/v1/products/" + productId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        verify(guideProductService, times(1)).modifyGuideProduct(productId, edit);
        verifyNoMoreInteractions(guideProductService);
    }

    @DisplayName("상품 수정 시 권한이 없는 경우 수정 실패")
    @Test
    void modifyProduct_unauthorized() throws Exception {
        long productId = 1L;
        GuideProductRequest edit = GuideProductRequestFixture.getGuideProductRequest();
        edit.setTitle("modify title");
        edit.setDescription("modify description");
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String body = mapper.writeValueAsString(edit);

        doThrow(new MismatchUserFromCreatorException()).when(guideProductService).modifyGuideProduct(productId, edit);

        mockMvc.perform(
                        put("/api/v1/products/" + productId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}
