package com.swygbro.trip.backend.domain.guideProduct.api;

import com.swygbro.trip.backend.domain.guideProduct.application.GuideProductService;
import com.swygbro.trip.backend.domain.guideProduct.dto.GuideProductDto;
import com.swygbro.trip.backend.domain.guideProduct.dto.GuideProductRequest;
import com.swygbro.trip.backend.global.exception.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "GuideProducts", description = "가이드 상품 API")
public class GuideProductController {

    private final GuideProductService guideProductService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "가이드 상품 등록", description = "가이드 상품에 대한 정보, 이미지를 통해 상품을 등록합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "가이드 상품 등록 성공",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = GuideProductDto.class)
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "가이드 위치가 유효하지 않아서 등록 실패",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiErrorResponse.class),
                    examples = @ExampleObject(
                            name = "가이드 위치가 유효하지 않음",
                            value = "{ \"status\" : \"BAD_REQUEST\", \"message\" : \"잘못된 위치입니다. 올바르게 입력해 주세요.\"}"
                    )
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "유저 정보가 존재하지 않아서 등록 실패",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiErrorResponse.class),
                    examples = @ExampleObject(
                            name = "유저 조회 실패",
                            value = "{ \"status\" : \"NOT_FOUND\", \"message\" : \"사용자를 찾을 수 없습니다. : {account}.\"}"
                    )
            )
    )
    @ApiResponse(
            responseCode = "500",
            description = "S3 에러로 인한 이미지 업로드 실패, 상품 등록 실패",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiErrorResponse.class),
                    examples = @ExampleObject(
                            name = "이미지 업로드 실패",
                            value = "{ \"status\" : \"INTERNAL_SERVER_ERROR\", \"message\" : \"이미지 업로드에 실패했습니다.\"}"
                    )
            )
    )
    public GuideProductDto createGuideProduct(@Valid @RequestPart GuideProductRequest request, @RequestPart(value = "file") List<MultipartFile> images) {
        return guideProductService.createGuideProduct(request, images);
    }

    @GetMapping("/{productId}")
    @Operation(summary = "가이드 상품 조회", description = "가이드 상품 번호로 상품 정보를 조회합니다.")
    @ApiResponse(
            responseCode = "200",
            description = "가이드 상품 조회 성공",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = GuideProductDto.class)
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "가이드 상품 정보가 존재하지 않아서 조회 실패",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiErrorResponse.class),
                    examples = @ExampleObject(
                            name = "가이드 상품 조회 실패",
                            value = "{ \"status\" : \"NOT_FOUND\", \"message\" : \"해당 {productId} 가이드 상품을 찾을 수 없습니다.\"}"
                    )
            )
    )
    public GuideProductDto getGuideProduct(@PathVariable Long productId) {
        return guideProductService.getProduct(productId);
    }

    @PutMapping("/{productId}")
    @Operation(summary = "가이드 상품 정보 수정", description = "이미지와 유저 정보를 제외한 모든 정보를 수정할 수 있다. ")
    @ApiResponse(
            responseCode = "200",
            description = "가이드 상품 정보 수정 성공",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = GuideProductDto.class)
            )
    )
    @ApiResponse(
            responseCode = "401",
            description = "가이드 상품을 수정할 권한이 없어서 수정 실패",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiErrorResponse.class),
                    examples = @ExampleObject(
                            name = "수정 권한 없음",
                            value = "{ \"status\" : \"UNAUTHORIZED\", \"message\" : \"가이드 상품을 수정할 권한이 없습니다.\"}"
                    )
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "상품 정보 및 유저 정보가 존재하지 않아서 수정 실패",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiErrorResponse.class),
                    examples = {
                            @ExampleObject(
                                    name = "가이드 상품 조회 실패",
                                    value = "{ \"status\" : \"NOT_FOUND\", \"message\" : \"해당 {productId} 가이드 상품을 찾을 수 없습니다.\"}"
                            ),
                            @ExampleObject(
                                    name = "유저 조회 실패",
                                    value = "{ \"status\" : \"NOT_FOUND\", \"message\" : \"사용자를 찾을 수 없습니다. : {account}.\"}"
                            )
                    }

            )
    )
    public GuideProductDto modifyGuideProduct(@PathVariable Long productId, @Valid @RequestBody GuideProductRequest edit) {
        return guideProductService.modifyGuideProduct(productId, edit);
    }

    @DeleteMapping("/{productId}")
    public String deleteGuideProduct(@PathVariable Long productId) {
        guideProductService.deleteGuideProduct(productId);

        return "삭제에 성공했습니다.";
    }
}
