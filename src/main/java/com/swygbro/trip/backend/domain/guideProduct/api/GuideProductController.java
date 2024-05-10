package com.swygbro.trip.backend.domain.guideProduct.api;

import com.swygbro.trip.backend.domain.guideProduct.application.GuideProductService;
import com.swygbro.trip.backend.domain.guideProduct.domain.DayTime;
import com.swygbro.trip.backend.domain.guideProduct.domain.GuideCategoryCode;
import com.swygbro.trip.backend.domain.guideProduct.dto.CreateGuideProductRequest;
import com.swygbro.trip.backend.domain.guideProduct.dto.GuideProductDto;
import com.swygbro.trip.backend.domain.guideProduct.dto.ModifyGuideProductRequest;
import com.swygbro.trip.backend.domain.guideProduct.dto.SearchGuideProductResponse;
import com.swygbro.trip.backend.domain.user.domain.User;
import com.swygbro.trip.backend.global.document.ForbiddenResponse;
import com.swygbro.trip.backend.global.document.InvalidTokenResponse;
import com.swygbro.trip.backend.global.exception.ApiErrorResponse;
import com.swygbro.trip.backend.global.jwt.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class GuideProductController {

    private final GuideProductService guideProductService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @SecurityRequirement(name = "access-token")
    @Operation(summary = "가이드 상품 등록", description = """
            # 가이드 상품 등록
                        
            가이드 상품을 등록합니다.
                        
            상품 제목, 상품 설명, 가이드 비용, 가이드 위치(위도, 경도), 가이드 시작/종료 날짜/시간, 카테고리, 대표 이미지, 이미지를 입력합니다.
                        
            이미지는 MultipartFile 타입이며 대표 이미지는 `thumb`, 그 외 이미지는 `file` key로 입력하시면 됩니다.<br>
            대표 이미지는 Not Null 이며 그 외 이미지는 Null 가능 입니다.
                        
            각 필드의 제약 조건은 다음과 같습니다.
            | 필드명 | 설명 | 제약조건 | null 가능 | 예시 |
            |--------|------|----------|----------|------|
            |title| 상품 제목 | 한글 기준 최대 30자, 영어 기준 최대 100자 | N | 신나는 서울 투어 |
            |description| 상품 설명 | 한글 기준 21000자, 영어 기준 65535  | N | 서울 *** 여행 가이드 합니다. |
            |price| 가이드 비용 | 한국 재화 기준 | N | 10000 |
            |longitude| 가이드 위치(위도) | -90.0 이상, 90.0 이하 | N | 37.2 |
            |latitude| 가이드 위치(경도) | -180.0 이상, 180.0 이하 | N | 127.5 |
            |guideStart| 가이드 시작 날짜/시간 | yyyy-MM-dd HH:mm:ss 패턴 | N | 2024-05-01 12:00:00 |
            |guideEnd| 가이드 종료 날짜/시간 | yyyy-MM-dd HH:mm:ss 패턴 | N | 2024-05-01 14:00:00 |
            |categories| 카테고리 | DINING,TOUR,OUTDOOR,ENTERTAINMENT,ART_CULTURE,SPORTS_FITNESS 중 여러개, 한개도 가능 | N | ["DINING", "OUTDOOR"]] |
                        
            ## 응답
                        
            - 상품 등록 성공 시 `200` 코드와 함께 상품 등록 정보를 json 형태로 반환합니다.
            - 상품 등록 중 가이드 위치가 잘못된 경우, `400` 에러를 반환합니다.
            - 상품 등록 중 유저 정보가 존재하지 않은 경우, `404` 에러를 반환합니다.
            - 상품 등록 중 이미지 저장에 실패하면 `500` 에러를 반환합니다.
            """, tags = "Guide Products")
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
    @ForbiddenResponse
    @InvalidTokenResponse
    public GuideProductDto createGuideProduct(@CurrentUser User user,
                                              @Valid @RequestPart CreateGuideProductRequest request,
                                              @RequestPart(value = "thumb") MultipartFile thumbImage,
                                              @RequestPart(value = "file", required = false) Optional<List<MultipartFile>> images) {
        return guideProductService.createGuideProduct(user.getEmail(), request, thumbImage, images);
    }

    @GetMapping("/{productId}")
    @Operation(summary = "가이드 상품 조회", description = """
            # 가이드 상품 조회
                        
            특정 가이드 상품을 조회합니다.
                        
            상품 조회 시 상품 고유 id를 path에 입력합니다.
                        
            각 필드의 제약 조건은 다음과 같습니다.
            | 필드명 | 설명 | 제약조건 | 예시 |
            |--------|------|----------|----------|
            |productId| 가이드 상품 고유 id | 숫자 | 1 |
                        
            ## 응답
                        
            - 상품 조회 성공 시 `200` 코드와 함께 상품 정보를 json 형태로 반환합니다.
            - 상품 조회 시 상품이 존재하지 않는 경우, `404` 에러를 반환합니다.
            """, tags = "Guide Products")
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

    @PutMapping(value = "/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @SecurityRequirement(name = "access-token")
    @Operation(summary = "가이드 상품 정보 수정", description = """
            # 가이드 상품 수정
                        
            가이드 상품을 수정합니다.
                        
            상품 제목, 상품 설명, 가이드 비용, 가이드 위치(위도, 경도), 가이드 시작/종료 날짜/시간, 카테고리, 대표 이미지, 이미지를 입력합니다.
                        
            이미지는 MultipartFile 타입이며 대표 이미지는 `thumb`, 그 외 이미지는 `file` key로 입력하시면 됩니다.<br>
            대표 이미지, 그 외 이미지 모두 수정가능하며 대표 이미지를 수정할 경우 새로운 대표 이미지를 입력해주시고 기존 대표 이미지 url은 빼주시면 됩니다.<br>
            그 외 이미지를 수정할 경우 새로운 이미지를 입력해주시고 새로운 이미지로 수정된 기존 이미지 url은 빼주시고 나머지 이미지 url을 입력해주시면 됩니다.<br>
            이미지 수정 없이 가이드 상품 정보만 수정 가능합니다.
                        
            각 필드의 제약 조건은 다음과 같습니다.
            | 필드명 | 설명 | 제약조건 | null 가능 | 예시 |
            |--------|------|----------|----------|------|
            |title| 상품 제목 | 한글 기준 최대 30자, 영어 기준 최대 100자 | N | 신나는 서울 투어 |
            |description| 상품 설명 | 한글 기준 21000자, 영어 기준 65535  | N | 서울 *** 여행 가이드 합니다. |
            |price| 가이드 비용 | 한국 재화 기준 | N | 10000 |
            |longitude| 가이드 위치(위도) | -90.0 이상, 90.0 이하 | N | 37.2 |
            |latitude| 가이드 위치(경도) | -180.0 이상, 180.0 이하 | N | 127.5 |
            |guideStart| 가이드 시작 날짜/시간 | yyyy-MM-dd HH:mm:ss 패턴 | N | 2024-05-01 12:00:00 |
            |guideEnd| 가이드 종료 날짜/시간 | yyyy-MM-dd HH:mm:ss 패턴 | N | 2024-05-01 14:00:00 |
            |categories| 카테고리 | DINING,TOUR,OUTDOOR,ENTERTAINMENT,ART_CULTURE,SPORTS_FITNESS 중 여러개, 한개도 가능 | N | ["DINING", "OUTDOOR"]] |
            |thumb| 대표 이미지 url | 문자열 | Y | https://S3저장소URL/저장위치/난수화된 이미지이름.이미지 타입 |
            |images| 이미지 url 리스트 | 문자열 | Y | ["https://S3저장소URL/저장위치/난수화된 이미지이름.이미지 타입", "..."] |
                        
            ## 응답
                        
            - 상품 수정 성공 시 `200` 코드와 함께 상품 수정 정보를 json 형태로 반환합니다.
            - 상품 수정 중 권한이 없을 경우(수정하려는 유저의 정보와 상품 작성자의 정보가 다를 경우), `401` 에러를 반환합니다.
            - 상품 수정 중 상품 정보나 유저 정보가 존재하지 않을 경우, `404` 에러를 반환합니다.
            """, tags = "Guide Products")
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
    @ForbiddenResponse
    @InvalidTokenResponse
    public GuideProductDto modifyGuideProduct(@CurrentUser User user,
                                              @PathVariable Long productId,
                                              @Valid @RequestPart ModifyGuideProductRequest request,
                                              @RequestPart(value = "thumb", required = false) Optional<MultipartFile> modifyThumbImage,
                                              @RequestPart(value = "file", required = false) Optional<List<MultipartFile>> modifyImages) {
        return guideProductService.modifyGuideProduct(user.getEmail(), productId, request, modifyThumbImage, modifyImages);
    }

    @DeleteMapping("/{productId}")
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @SecurityRequirement(name = "access-token")
    @Operation(summary = "가이드 상품 삭제", description = """
            # 가이드 상품 삭제
                        
            특정 가이드 상품을 삭제합니다.
                        
            상품 삭제 시 상품 고유 id를 path에 입력하고 유저 email을 param으로 넘겨줍니다.
                        
            각 필드의 제약 조건은 다음과 같습니다.
            | 필드명 | 설명 | 제약조건 | null 가능 | 예시 |
            |--------|------|----------|----------|------|
            |productId| 가이드 상품 고유 id | 숫자 | N | 1 |
                        
            ## 응답
                        
            - 상품 삭제 성공 시 `200` 코드와 함께 성공 메시지를 반환합니다.
            - 상품 삭제 시 권한이 없을 경우(삭제하려는 유저의 정보와 상품 작성자의 정보가 다를 경우), `401` 에러를 반환합니다.
            - 상품 삭제 시 상품이 존재하지 않는 경우, `404` 에러를 반환합니다.
            """, tags = "Guide Products")
    @ApiResponse(
            responseCode = "200",
            description = "가이드 상품 삭제 성공",
            content = @Content(
                    mediaType = MediaType.TEXT_PLAIN_VALUE,
                    schema = @Schema(implementation = String.class)
            )
    )
    @ApiResponse(
            responseCode = "401",
            description = "가이드 상품을 삭제할 권한이 없어서 삭제 실패",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiErrorResponse.class),
                    examples = @ExampleObject(
                            name = "삭제 권한 없음",
                            value = "{ \"status\" : \"UNAUTHORIZED\", \"message\" : \"가이드 상품을 삭할 권한이 없습니다.\"}"
                    )
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "상품 정보 및 유저 정보가 존재하지 않아서 삭제 실패",
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
    @ForbiddenResponse
    @InvalidTokenResponse
    public String deleteGuideProduct(@CurrentUser User user,
                                     @PathVariable Long productId) {
        guideProductService.deleteGuideProduct(productId, user.getEmail());

        return "삭제에 성공했습니다.";
    }

    @GetMapping("/location")
    @Operation(summary = "범위 내 가이드 상품 불어오기", description = """
            # 범위 내 가이드 상품 불어오기
                        
            유저의 현재 위치 기준 30km 범위 내에 존재하는 가이드 상품 리스트를 불러옵니다.
                        
            각 필드의 제약 조건은 다음과 같습니다.
            | 필드명 | 설명 | 제약조건 | null 가능 | 예시 |
            |--------|------|----------|----------|------|
            |longitude| 현재 위치(위도) | -90.0 이상, 90.0 이하 | N | 37.435 |
            |latitude| 현재 위치(경도) | -180.0 이상, 180.0 이하 | N | 230.253 |
                        
            ## 응답
                        
            - 범위 내 가이드 상품이 존재할 경우 `200` 코드와 함께 가이드 상품 리스트를 반환합니다.
            - 범위 내 가이드 상품이 존재하지 않을 경우 `404` 에러를 반환합니다.
            """, tags = "Search Guide Products")
    @ApiResponse(
            responseCode = "200",
            description = "범위 내 가이드 상품 불러오기 성공",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = SearchGuideProductResponse.class)
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "범위 내 가이드 상품이 존재하지 않음",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiErrorResponse.class),
                    examples = @ExampleObject(
                            name = "가이드 상품이 존재하지 않음",
                            value = "{ \"status\" : \"NOT_FOUND\", \"message\" : \"주변에 가이드 상품이 존재하지 않습니다.\"}"
                    )
            )
    )
    public List<SearchGuideProductResponse> getGuideListIn(@RequestParam double longitude,
                                                           @RequestParam double latitude) {
        return guideProductService.getGuideListIn(longitude, latitude);
    }

    @GetMapping("/login/search")
    @PreAuthorize("isAuthenticated() and hasRole('USER') and #user.id == principal.id")
    @SecurityRequirement(name = "access-token")
    @Operation(summary = "로그인 했을 경우 검색 + 필터 ", description = """
            # 지역 + 날짜로 검색
                        
            지역과 날짜를 입력하면 두 조건에 만족하는 가이드 상품들을 검색한다.
                        
            각 필드의 제약 조건은 다음과 같습니다.
            | 필드명 | 설명 | 제약조건 | null 가능 | 예시 |
            |--------|------|----------|----------|------|
            |region| 한국 지역 선택 | 서울특별시, 경기도, 광주광역시, 세종특별자치시, 부산광역시, 울산광역시, 대구광역시, 제주특별자치도, 인천광역시, 전라북도, 전라남도, 충청남도, 충청북도, 강원도, 경상북도, 경상남도, 대전광역시만 가능 | N | 서울특별시 |
            |start| 범위 시작 날짜 | yyyy-MM-dd, 00:00:00시간부터 | N | 2024-05-01 |
            |end| 범위 종료 날짜 | yyyy-MM-dd, 23:59:99시간까지 | N | 2024-05-02 |
                        
            # 상세 조건으로 필터
                        
            카테고리, 가격 범위, 소요 시간, 시간대, 같은 국적 여부를 이용해 추가 검색을 합니다.
                        
            각 필드의 제약 조건은 다음과 같습니다.
            | 필드명 | 설명 | 제약조건 | null 가능 | 예시 |
            |--------|------|----------|----------|------|
            |category| 카테고리 선택 | (중복 가능) DINING,TOUR,OUTDOOR,ENTERTAINMENT,ART_CULTURE,SPORTS_FITNESS | Y | DINING |
            |min| 최소 가격 범위 | 한국 재화 기준 | Y (default = 0) | 10000 |
            |max| 최대 가격 범위 | 한국 재화 기준 | Y (default = 200000)| 30000 |
            |minD| 최소 소요 시간 | 시간 단위 | Y (default = 1) | 2 |
            |maxD| 최대 소요 시간 | 시간 단위 | Y (default = 24) | 5 |
            |dayT| 시간대 | DAWN(0 ~ 6), MORNING(7 ~ 11), LUNCH (12 ~ 17), EVENING (18 ~ 23) | Y (default = ALL) | LUNCH |
            |host| 같은 국적 여부 | 같을 경우 true, 다를 경우 false | Y (default = false) | false |
                      
            # 상황
            1. 지역 + 날짜로만 검색 가능
            2. 1번에서의 검색 결과에서 카테고리 선택으로 검색 가능
            3. 1번 + 2번 검색 결과에서 세부 조건으로 검색 가능
            4. 1번에서의 검색 결과에서 세부 조건으로 검색 가능
              
            ## 응답
                        
            - 검색 조건 내 가이드 상품이 존재할 경우 `200` 코드와 함께 가이드 상품 리스트를 반환합니다.
            - 검색 조건 내 가이드 상품이 존재하지 않을 경우 `404` 에러를 반환합니다.
            """, tags = "Search Guide Products")
    @ApiResponse(
            responseCode = "200",
            description = "범위 내 가이드 상품 불러오기 성공",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = SearchGuideProductResponse.class)
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "범위 내 가이드 상품이 존재하지 않음",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiErrorResponse.class),
                    examples = @ExampleObject(
                            name = "가이드 상품이 존재하지 않음",
                            value = "{ \"status\" : \"NOT_FOUND\", \"message\" : \"해당 조건에 부합하는 가이드 상품이 존재하지 않습니다.\"}"
                    )
            )
    )
    @ForbiddenResponse
    @InvalidTokenResponse
    public List<SearchGuideProductResponse> getSearchedGuideListWithLogin(@CurrentUser User user,
                                                                          @RequestParam String region,
                                                                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
                                                                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
                                                                          @RequestParam(value = "category", required = false) List<GuideCategoryCode> categories,
                                                                          @RequestParam(value = "min", required = false, defaultValue = "0") Long minPrice,
                                                                          @RequestParam(value = "max", required = false, defaultValue = "200000") Long maxPrice,
                                                                          @RequestParam(value = "minD", required = false, defaultValue = "1") int minDuration,
                                                                          @RequestParam(value = "maxD", required = false, defaultValue = "24") int maxDuration,
                                                                          @RequestParam(value = "dayT", required = false, defaultValue = "ALL") DayTime dayTime,
                                                                          @RequestParam(value = "host", required = false, defaultValue = "false") boolean same) {
        if (!same)
            return guideProductService.getSearchedGuideList(region, start, end, categories, minPrice, maxPrice, minDuration, maxDuration, dayTime, null);
        return guideProductService.getSearchedGuideList(region, start, end, categories, minPrice, maxPrice, minDuration, maxDuration, dayTime, user.getNationality());
    }

    @GetMapping("/search")
    @Operation(summary = "로그인 안 했을 경우 검색 + 필터 ", description = """
            # 지역 + 날짜로 검색
                        
            지역과 날짜를 입력하면 두 조건에 만족하는 가이드 상품들을 검색한다.
                        
            각 필드의 제약 조건은 다음과 같습니다.
            | 필드명 | 설명 | 제약조건 | null 가능 | 예시 |
            |--------|------|----------|----------|------|
            |region| 한국 지역 선택 | 서울특별시, 경기도, 광주광역시, 세종특별자치시, 부산광역시, 울산광역시, 대구광역시, 제주특별자치도, 인천광역시, 전라북도, 전라남도, 충청남도, 충청북도, 강원도, 경상북도, 경상남도, 대전광역시만 가능 | N | 서울특별시 |
            |start| 범위 시작 날짜 | yyyy-MM-dd, 00:00:00시간부터 | N | 2024-05-01 |
            |end| 범위 종료 날짜 | yyyy-MM-dd, 23:59:99시간까지 | N | 2024-05-02 |
                        
            # 상세 조건으로 필터
                        
            카테고리, 가격 범위, 소요 시간, 시간대, 같은 국적 여부를 이용해 추가 검색을 합니다.
                        
            각 필드의 제약 조건은 다음과 같습니다.
            | 필드명 | 설명 | 제약조건 | null 가능 | 예시 |
            |--------|------|----------|----------|------|
            |category| 카테고리 선택 | (중복 가능) DINING,TOUR,OUTDOOR,ENTERTAINMENT,ART_CULTURE,SPORTS_FITNESS | Y | DINING |
            |min| 최소 가격 범위 | 한국 재화 기준 | Y (default = 0) | 10000 |
            |max| 최대 가격 범위 | 한국 재화 기준 | Y (default = 200000)| 30000 |
            |minD| 최소 소요 시간 | 시간 단위 | Y (default = 1) | 2 |
            |maxD| 최대 소요 시간 | 시간 단위 | Y (default = 24) | 5 |
            |dayT| 시간대 | DAWN(0 ~ 6), MORNING(7 ~ 11), LUNCH (12 ~ 17), EVENING (18 ~ 23) | Y (default = ALL) | LUNCH |
                      
            # 상황
            1. 지역 + 날짜로만 검색 가능
            2. 1번에서의 검색 결과에서 카테고리 선택으로 검색 가능
            3. 1번 + 2번 검색 결과에서 세부 조건으로 검색 가능
            4. 1번에서의 검색 결과에서 세부 조건으로 검색 가능
              
            ## 응답
                        
            - 검색 조건 내 가이드 상품이 존재할 경우 `200` 코드와 함께 가이드 상품 리스트를 반환합니다.
            - 검색 조건 내 가이드 상품이 존재하지 않을 경우 `404` 에러를 반환합니다.
            """, tags = "Search Guide Products")
    @ApiResponse(
            responseCode = "200",
            description = "범위 내 가이드 상품 불러오기 성공",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = SearchGuideProductResponse.class)
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "범위 내 가이드 상품이 존재하지 않음",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiErrorResponse.class),
                    examples = @ExampleObject(
                            name = "가이드 상품이 존재하지 않음",
                            value = "{ \"status\" : \"NOT_FOUND\", \"message\" : \"해당 조건에 부합하는 가이드 상품이 존재하지 않습니다.\"}"
                    )
            )
    )
    public List<SearchGuideProductResponse> getSearchedGuideList(@RequestParam String region,
                                                                 @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
                                                                 @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
                                                                 @RequestParam(value = "category", required = false) List<GuideCategoryCode> categories,
                                                                 @RequestParam(value = "min", required = false, defaultValue = "0") Long minPrice,
                                                                 @RequestParam(value = "max", required = false, defaultValue = "200000") Long maxPrice,
                                                                 @RequestParam(value = "minD", required = false, defaultValue = "1") int minDuration,
                                                                 @RequestParam(value = "maxD", required = false, defaultValue = "24") int maxDuration,
                                                                 @RequestParam(value = "dayT", required = false, defaultValue = "ALL") DayTime dayTime) {
        return guideProductService.getSearchedGuideList(region, start, end, categories, minPrice, maxPrice, minDuration, maxDuration, dayTime, null);
    }
}
