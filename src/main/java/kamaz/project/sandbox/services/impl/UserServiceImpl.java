package kamaz.project.sandbox.services.impl;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import kamaz.project.sandbox.dto.UserDto;
import kamaz.project.sandbox.exception.AppException;
import kamaz.project.sandbox.exception.ResourceNotFoundException;
import kamaz.project.sandbox.mapper.UserMapper;
import kamaz.project.sandbox.models.Role;
import kamaz.project.sandbox.models.User;
import kamaz.project.sandbox.repositories.RoleRepository;
import kamaz.project.sandbox.repositories.UserRepository;
import kamaz.project.sandbox.services.UserService;
import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<UserDto> getUsers() {
        return userRepository.findAll().stream()
        .map(UserMapper::userToUserDto).toList();
    }
    @Override
    public UserDto create(UserDto userDto) {
        User user = UserMapper.userDtoToUser(userDto);

        Role role = roleRepository.findByName(userDto.role()).orElseThrow(
                () -> new ResourceNotFoundException( "Role not found")
        );

        user.setRole(role);
        user.setPassword(passwordEncoder.encode(userDto.password()));

        return UserMapper.userToUserDto(userRepository.save(user));
    }
    @Override
    public UserDto getUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new AppException(HttpStatus.NOT_FOUND, "User not found")
        );
        return UserMapper.userToUserDto(user);
    }
    @Override
    public UserDto getUser(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new AppException(HttpStatus.NOT_FOUND, "User not found")
        );
        return UserMapper.userToUserDto(user);
    }
    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new AppException(HttpStatus.NOT_FOUND, "User not found")
        );
        Role role = roleRepository.findByName(userDto.role()).orElseThrow(
                () -> new AppException(HttpStatus.NOT_FOUND, "Role not found")
        );

        user.setUsername(userDto.username());
        user.setPassword(passwordEncoder.encode(userDto.password()));
        user.setRole(role);

        return UserMapper.userToUserDto(userRepository.save(user));
    }
    @Override
    public String deleteUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new AppException(HttpStatus.NOT_FOUND, "User not found")
        );

        userRepository.delete(user);

        return String.format("User with %d deleted successfully", userId);
    }
    
}