package kamaz.project.sandbox.mapper;

import kamaz.project.sandbox.dto.UserDto;
import kamaz.project.sandbox.dto.UserLoggedDto;
import kamaz.project.sandbox.models.User;

public class UserMapper {

    public static UserDto userToUserDto(User user) {
        if (user == null) return null;
        
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getRole() != null ? user.getRole().getName() : null,
                null
        );
    }

    public static UserLoggedDto userToUserLoggedDto(User user) {
        if (user == null) return null;
        
        return new UserLoggedDto(
                user.getUsername(),
                user.getRole() != null ? user.getRole().getName() : null,
                null
        );
    }

    // НОВЫЙ МЕТОД (используется в UserServiceImpl)
    public static User userDtoToUser(UserDto dto) {
        if (dto == null) return null;
        
        User user = new User();
        user.setId(dto.id());
        user.setUsername(dto.username());
        user.setPassword(dto.password());
        // роль устанавливается отдельно в сервисе
        return user;
    }
}