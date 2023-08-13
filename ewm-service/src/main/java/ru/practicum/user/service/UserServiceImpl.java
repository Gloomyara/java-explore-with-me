package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.user.dto.NewUserDto;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;
import ru.practicum.util.pagerequest.PageRequester;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getUsersAdmin(Set<Long> ids, Integer from, Integer size) {
        return toDto(userRepository.findAllByIdIn(ids, new PageRequester(from, size, Sort.by("id"))).toList());
    }

    @Override
    public UserDto saveNewUserAdmin(NewUserDto newUserDto) {
        return toDto(userRepository.save(toEntity(newUserDto)));
    }

    @Override
    public void deleteUserAdmin(Long userId) {
        userRepository.deleteById(userId);
    }

    private User toEntity(NewUserDto newUserDto) {
        return UserMapper.INSTANCE.toEntity(newUserDto);
    }

    private UserDto toDto(User user) {
        return UserMapper.INSTANCE.toDto(user);
    }

    private List<UserDto> toDto(List<User> user) {
        return UserMapper.INSTANCE.toDto(user);
    }
}
