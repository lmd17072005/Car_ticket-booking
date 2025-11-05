package com.ra.base_spring_boot.config;

import com.ra.base_spring_boot.model.payment.CancellationPolicy;
import com.ra.base_spring_boot.model.user.Role;
import com.ra.base_spring_boot.model.user.User;
import com.ra.base_spring_boot.model.constants.RoleName;
import com.ra.base_spring_boot.model.constants.Status;
import com.ra.base_spring_boot.repository.IRoleRepository;
import com.ra.base_spring_boot.repository.IUserRepository;
import com.ra.base_spring_boot.repository.payment.ICancellationPolicyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    private final IRoleRepository roleRepository;
    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ICancellationPolicyRepository cancellationPolicyRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("Bắt đầu khởi tạo dữ liệu...");

        if (roleRepository.count() == 0) {
            log.info("Chưa có dữ liệu ROLES, tiến hành khởi tạo...");
            roleRepository.save(new Role(RoleName.ROLE_ADMIN));
            roleRepository.save(new Role(RoleName.ROLE_USER));
            log.info("Khởi tạo ROLES thành công.");
        }

        if (!userRepository.findByEmail("admin@gmail.com").isPresent()) {
            log.info("Chưa có tài khoản ADMIN, tiến hành khởi tạo...");


            Role adminRole = roleRepository.findByRoleName(RoleName.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy ROLE_ADMIN."));
            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);


            User admin = User.builder()
                    .firstName("Admin")
                    .lastName("Vivutoday")
                    .email("admin@gmail.com")
                    .password(passwordEncoder.encode("123456"))
                    .phone("0987654321")
                    .status(Status.ACTIVE)
                    .roles(roles)
                    .build();

            userRepository.save(admin);
            log.info("Khởi tạo tài khoản ADMIN thành công.");
        }
        log.info("Hoàn tất quá trình khởi tạo dữ liệu.");
    }
}
