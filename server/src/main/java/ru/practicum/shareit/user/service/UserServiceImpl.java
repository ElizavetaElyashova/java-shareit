package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;


    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .toList();
    }

    @Override
    public UserDto findById(long id) {
        return UserMapper.toUserDto(userRepository.findById(id).orElseThrow(NoSuchElementException::new));
    }

    @Override
    public UserDto create(UserDto userDto) {
        return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(userDto)));
    }

    @Override
    public UserDto update(UpdateUserRequest updateUserRequest) {
        UserDto userDto = findById(updateUserRequest.getId());
        if (updateUserRequest.getName() != null) {
            userDto.setName(updateUserRequest.getName());
        }
        if (updateUserRequest.getEmail() != null) {
            userDto.setEmail(updateUserRequest.getEmail());
        }
        return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(userDto)));
    }

    @Override
    public void remove(long id) {
        userRepository.deleteById(id);
    }
}
