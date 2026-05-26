package kamaz.project.sandbox;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import kamaz.project.sandbox.models.Role;
import kamaz.project.sandbox.models.User;
import kamaz.project.sandbox.repositories.RoleRepository;
import kamaz.project.sandbox.repositories.UserRepository;
import lombok.RequiredArgsConstructor;

@SpringBootApplication
@RequiredArgsConstructor
public class DemoApplication implements ApplicationRunner {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        createRolesIfNotExists();
        createUsersIfNotExists();
    }

    private void createUsersIfNotExists() {
        if (!userRepository.findAll().isEmpty()) return;

        Role roleAdmin = roleRepository.findByName("ADMIN").orElseThrow();
        Role roleUser = roleRepository.findByName("USER").orElseThrow();
        Role roleManager = roleRepository.findByName("MANAGER").orElseThrow();

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

        System.out.println("Пользователи созданы: admin/admin, manager/manager, user/user");
    }

    private void createRolesIfNotExists() {
        if (roleRepository.count() > 0) return;

        Role userRole = Role.builder().name("USER").build();
        Role managerRole = Role.builder().name("MANAGER").build();
        Role adminRole = Role.builder().name("ADMIN").build();

        roleRepository.save(userRole);
        roleRepository.save(managerRole);
        roleRepository.save(adminRole);

        System.out.println("Роли созданы: USER, MANAGER, ADMIN");
    }
}