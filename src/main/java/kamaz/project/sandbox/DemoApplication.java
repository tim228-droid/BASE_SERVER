package kamaz.project.sandbox;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import kamaz.project.sandbox.enums.Roles;
import kamaz.project.sandbox.models.Permission;
import kamaz.project.sandbox.models.Role;
import kamaz.project.sandbox.models.User;
import kamaz.project.sandbox.repositories.PermissionRepository;
import kamaz.project.sandbox.repositories.RoleRepository;
import kamaz.project.sandbox.repositories.UserRepository;
import lombok.RequiredArgsConstructor;

@SpringBootApplication
@RequiredArgsConstructor
public class DemoApplication implements ApplicationRunner {
	private final UserRepository userRepository;
    private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;
    private final PermissionRepository permissionRepository;
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
	@Override
    public void run(ApplicationArguments args) throws Exception {
        createPermissionsIfNotExists();
        createRolesIfNotExists();
        createUsersIfNotExists();
    }
	public void createUsersIfNotExists() {
        if(!userRepository.findAll().isEmpty())
            return;

        Role roleAdmin =  roleRepository.findByName(Roles.ADMIN.name()).orElseThrow();
        Role roleUser = roleRepository.findByName(Roles.USER.name()).orElseThrow();
        Role roleManager = roleRepository.findByName(Roles.MANAGER.name()).orElseThrow();

        User admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin"))
                .role(roleAdmin)
                .build();
		userRepository.save(admin);
        User user = User.builder()
                .username("user")
                .password(passwordEncoder.encode("user"))
                .role(roleUser)
                .build();
		userRepository.save(user);
        User manager = User.builder()
                .username("manager")
                .password(passwordEncoder.encode("manager"))
                .role(roleManager)
                .build();
		userRepository.save(manager);
    }
    private void createRolesIfNotExists() {
        if (roleRepository.count() == 0) {
            Role userRole = Role.builder().name("USER").build();
            Role moderatorRole = Role.builder().name("MODERATOR").build();
            Role managerRole = Role.builder().name("MANAGER").build();
            Role adminRole = Role.builder().name("ADMIN").build();

            // Сохраняем роли
            roleRepository.save(userRole);
            roleRepository.save(managerRole);
            roleRepository.save(moderatorRole);
            roleRepository.save(adminRole);

            // Получаем разрешения из БД
            Permission productRead = permissionRepository.findByResourceAndOperation("product", "read").orElseThrow();
            Permission productWrite = permissionRepository.findByResourceAndOperation("product", "write").orElseThrow();
            Permission productDelete = permissionRepository.findByResourceAndOperation("product", "delete").orElseThrow();

            // Назначаем разрешения ролям
            userRole.setPermissions(new HashSet<>(Set.of(productRead)));

            managerRole.setPermissions(new HashSet<>(Set.of(productRead, productWrite)));

            adminRole.setPermissions(new HashSet<>(Set.of(productRead, productWrite, productDelete)));

            // Сохраняем обновлённые роли с разрешениями
            roleRepository.save(userRole);
            roleRepository.save(managerRole);
            roleRepository.save(adminRole);
        }
    }
    private void createPermissionsIfNotExists() {
        if (permissionRepository.count() == 0) {
            permissionRepository.saveAll(List.of(
                    new Permission("product", "read"),
                    new Permission("product", "write"),
                    new Permission("product", "delete")
            ));
        }
    }
}
