package dev.ivanov.social_network.user_service.services;

import dev.ivanov.social_network.user_service.dto.PageDto;
import dev.ivanov.social_network.user_service.dto.UserDto;
import dev.ivanov.social_network.user_service.entities.User;
import dev.ivanov.social_network.user_service.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@NoArgsConstructor
@AllArgsConstructor
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void createUser(String userId) {
        User user = User.builder()
                .id(userId)
                .deleted(false)
                .build();
        userRepository.save(user);
        log.trace("user {} created", userId);
    }

    @Transactional
    public void deleteUser(String userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setDeleted(true);
            userRepository.save(user);
        }
        log.trace("user {} deleted", userId);
    }

    @Transactional
    public PageDto<UserDto> search(String query, Integer page, Integer size) {
        Page<User> userPage;

        if (query.isEmpty())
            userPage = userRepository.findAll(PageRequest.of(page, size));
        else
            userPage = userRepository.findUsersByNicknameOrNameAndSurnameOrEmailOrPhone(query,
                PageRequest.of(page, size));

        size = userPage.getSize();
        page = userPage.getNumber();
        Integer totalPages = userPage.getTotalPages();
        Long totalElements = userPage.getTotalElements();

        List<UserDto> data = userPage.getContent().stream().map(u ->
                UserDto.builder()
                        .id(u.getId())
                        .nickname(u.getNickname())
                        .name(u.getName())
                        .surname(u.getSurname())
                        .email(u.getEmail())
                        .avatarImageId(u.getAvatar().getId())
                        .phone(u.getPhone())
                        .build()
                ).toList();

        PageDto<UserDto> userDtoPageDto = new PageDto<>();
        userDtoPageDto.setData(data);
        userDtoPageDto.setPage(page);
        userDtoPageDto.setSize(size);
        userDtoPageDto.setTotalPages(totalPages);
        userDtoPageDto.setTotalElements(totalElements);

        return userDtoPageDto;

    }
}
