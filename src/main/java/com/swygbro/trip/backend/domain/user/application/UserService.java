package com.swygbro.trip.backend.domain.user.application;

import com.swygbro.trip.backend.domain.guideProduct.domain.GuideProductRepository;
import com.swygbro.trip.backend.domain.s3.application.S3Service;
import com.swygbro.trip.backend.domain.user.domain.SignUpType;
import com.swygbro.trip.backend.domain.user.domain.User;
import com.swygbro.trip.backend.domain.user.domain.UserDao;
import com.swygbro.trip.backend.domain.user.domain.UserRepository;
import com.swygbro.trip.backend.domain.user.dto.CreateUserRequest;
import com.swygbro.trip.backend.domain.user.dto.UpdateUserRequest;
import com.swygbro.trip.backend.domain.user.dto.UserInfoDto;
import com.swygbro.trip.backend.domain.user.dto.UserProfileDto;
import com.swygbro.trip.backend.domain.user.excepiton.PasswordNotMatchException;
import com.swygbro.trip.backend.domain.user.excepiton.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final GuideProductRepository guideProductRepository;
    private final UserDao userDao;
    private final UserValidationService userValidationService;
    private final S3Service s3Service;

    @Transactional
    public UserInfoDto createUser(CreateUserRequest dto) {
        userValidationService.checkUniqueUser(dto);

        if (!dto.getPassword().equals(dto.getPasswordCheck())) {
            throw new PasswordNotMatchException();
        }

        User createdUser = userRepository.save(
                new User(dto, SignUpType.Local, passwordEncoder.encode(dto.getPassword()))
        );

        return UserInfoDto.builder()
                .id(createdUser.getId())
                .email(createdUser.getEmail())
                .nickname(createdUser.getNickname())
                .name(createdUser.getName())
                .birthdate(createdUser.getBirthdate())
                .gender(createdUser.getGender())
                .nationality(createdUser.getNationality())
                .build();
    }

    @Transactional(readOnly = true)
    public UserProfileDto getUserProfile(Long userId) {
        return userDao.getUserProfile(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional
    public void updateUser(Long id, UpdateUserRequest dto, MultipartFile imageFile) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (imageFile != null) {
            user.setProfileImageUrl(s3Service.uploadImage(imageFile));
        }

        user.update(dto);
    }

    @Transactional(readOnly = true)
    public Page<User> getUserPages(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

}
