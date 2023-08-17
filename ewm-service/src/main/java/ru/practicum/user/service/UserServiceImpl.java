package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.user.dto.NewUserDto;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.repository.UserRepository;
import ru.practicum.util.pager.Pager;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper mapper = UserMapper.INSTANCE;

    @Override
    public List<UserDto> getUsersAdmin(Set<Long> ids, Integer from, Integer size) {
        return mapper.toDto(userRepository.findAllByIdIn(ids, new Pager(from, size, Sort.by("id"))).toList());
    }

    @Override
    public UserDto saveNewUserAdmin(NewUserDto dto) {
        return mapper.toDto(userRepository.save(mapper.toEntity(dto)));
    }

    @Override
    public void deleteUserAdmin(Long userId) {
        userRepository.deleteById(userId);
    }

}
