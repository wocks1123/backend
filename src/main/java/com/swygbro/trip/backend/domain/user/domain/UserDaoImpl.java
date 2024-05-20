package com.swygbro.trip.backend.domain.user.domain;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.swygbro.trip.backend.domain.guideProduct.domain.QGuideProduct;
import com.swygbro.trip.backend.domain.review.domain.QReview;
import com.swygbro.trip.backend.domain.user.dto.UserDetailDto;
import com.swygbro.trip.backend.domain.user.dto.UserProfileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Repository
@RequiredArgsConstructor
public class UserDaoImpl implements UserDao {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<UserProfileDto> getUserProfile(Long userId) {

        QUser qUser = QUser.user;
        QUserLanguage qUserLanguage = QUserLanguage.userLanguage;
        QGuideProduct qGuideProduct = QGuideProduct.guideProduct;
        QReview qReview = QReview.review;

        var user = queryFactory
                .select(qUser)
                .from(qUser)
                .leftJoin(qUser.userLanguages, qUserLanguage).fetchJoin()
                .where(qUser.id.eq(userId))
                .fetchOne();

        if (user == null) {
            return Optional.empty();
        }

        var guideProducts = queryFactory
                .select(qGuideProduct)
                .from(qGuideProduct)
                .where(qGuideProduct.user.eq(user))
                .orderBy(qGuideProduct.guideStart.desc())
                .limit(4) // 임의 지정
                .fetch();


        var reviewInfo = queryFactory
                .select(qReview.count(), qReview.rating.avg())
                .from(qReview).leftJoin(qGuideProduct).on(qGuideProduct.eq(qReview.guideProduct))
                .where(qGuideProduct.user.eq(qUser))
                .groupBy(qUser)
                .where(qUser.id.eq(userId));

        var reviewRes = reviewInfo.fetchFirst();
        int reviewCount = 0;
        float reviewRatingAvg = 0.0f;
        if (reviewRes != null) {
            reviewCount = reviewRes.get(0, Long.class).intValue();
            reviewRatingAvg = reviewRes.get(1, Double.class).floatValue();
        }

        return Optional.of(UserProfileDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .name(user.getName())
                .profile(user.getProfile())
                .profileImageUrl(user.getProfileImageUrl())
                .languages(user.getUserLanguages().stream().map(UserLanguage::getLanguage).toList())
                .createdAt(user.getCreatedAt())
                .guideProducts(guideProducts.stream().map(UserProfileDto.SimpleGuideProductDto::fromEntity).toList())
                .reviewCount(reviewCount)
                .reviewRating(Float.parseFloat(String.format("%.1f", reviewRatingAvg)))
                .build());
    }

    @Override
    public Page<UserDetailDto> findUsersByFilter(Pageable pageable,
                                                 String email,
                                                 String nickname,
                                                 String name,
                                                 String phone,
                                                 String location,
                                                 Nationality nationality,
                                                 LocalDate birthdate,
                                                 Gender gender,
                                                 SignUpType signUpType) {
        QUser qUser = QUser.user;
        BooleanBuilder builder = createBooleanBuilder(email,
                nickname,
                name,
                phone,
                location,
                nationality,
                birthdate,
                gender,
                signUpType);

        List<UserDetailDto> users = queryFactory
                .select(Projections.fields(UserDetailDto.class,
                        qUser.id,
                        qUser.email,
                        qUser.nickname,
                        qUser.name,
                        qUser.phone,
                        qUser.location,
                        qUser.nationality,
                        qUser.birthdate,
                        qUser.gender,
                        qUser.signUpType,
                        qUser.createdAt,
                        qUser.updatedAt
                ))
                .from(qUser)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = Optional.ofNullable(queryFactory.select(qUser.count())
                .from(qUser)
                .where(builder)
                .fetchOne()).orElse(0L);

        return new PageImpl<>(users, pageable, total);
    }

    private BooleanBuilder createBooleanBuilder(String email,
                                                String nickname,
                                                String name,
                                                String phone,
                                                String location,
                                                Nationality nationality,
                                                LocalDate birthdate,
                                                Gender gender,
                                                SignUpType signUpType) {
        QUser qUser = QUser.user;
        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.hasText(email)) {
            builder.and(qUser.email.eq(email));
        }

        if (StringUtils.hasText(nickname)) {
            builder.and(qUser.nickname.eq(nickname));
        }

        if (StringUtils.hasText(name)) {
            builder.and(qUser.name.eq(name));
        }

        if (StringUtils.hasText(phone)) {
            builder.and(qUser.phone.eq(phone));
        }

        if (StringUtils.hasText(location)) {
            builder.and(qUser.location.eq(location));
        }

        if (nationality != null) {
            builder.and(qUser.nationality.eq(nationality));
        }

        if (birthdate != null) {
            builder.and(qUser.birthdate.eq(birthdate));
        }

        if (gender != null) {
            builder.and(qUser.gender.eq(gender));
        }

        if (signUpType != null) {
            builder.and(qUser.signUpType.eq(signUpType));
        }

        return builder;
    }


}
