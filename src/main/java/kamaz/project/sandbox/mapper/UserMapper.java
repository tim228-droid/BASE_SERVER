package kamaz.project.sandbox.mapper;

import java.util.stream.Collectors;

import kamaz.project.sandbox.dto.UserDto;
import kamaz.project.sandbox.dto.UserLoggedDto;
import kamaz.project.sandbox.models.Permission;
import kamaz.project.sandbox.models.User;

public class UserMapper {
    public static UserDto userToUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getRole().getAuthority(),
                user.getRole().getPermissions().stream()
                .map(Permission::getAuthority)
                .collect(Collectors.toSet())
        );
    }
    public static User userDtoToUser(UserDto dto) {
        User user = new User();
        user.setUsername(dto.username());
        return user;
    }
    public static UserLoggedDto userToUserLoggedDto(User user) {
        return new UserLoggedDto(
                user.getUsername(),
                user.getRole().getAuthority(),
                user.getRole().getPermissions().stream().map(Permission::getAuthority).collect(Collectors.toSet())
        );
    }
}