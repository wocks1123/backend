package com.swygbro.trip.backend.domain.guideProduct.api;

import com.swygbro.trip.backend.domain.guideProduct.application.GuideProductService;
import com.swygbro.trip.backend.domain.guideProduct.dto.CreateGuideProductRequest;
import com.swygbro.trip.backend.domain.guideProduct.dto.GuideProductDto;
import com.swygbro.trip.backend.domain.guideProduct.dto.GuideProductResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class GuideProductController {

    private final GuideProductService guideProductService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public GuideProductResponse createGuideProduct(@Valid @RequestPart CreateGuideProductRequest request, @RequestPart(value = "file") List<MultipartFile> images) {
        GuideProductDto product = guideProductService.createGuideProduct(request, images);

        return GuideProductResponse.fromDto(product);
    }

    @GetMapping("/{productId}")
    public GuideProductResponse getGuideProduct(@PathVariable int productId) {
        return GuideProductResponse.fromDto(guideProductService.getProduct(productId));
    }
}
