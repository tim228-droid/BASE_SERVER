package kamaz.project.sandbox.services;

import java.util.List;

import kamaz.project.sandbox.dto.UserDto;

public interface UserService {
    List<UserDto> getUsers();

    UserDto create(UserDto userDto);

    UserDto getUser(Long userId);
    UserDto getUser(String username);

    UserDto updateUser(Long userId, UserDto userDto);

    String deleteUser(Long userId);
    
}