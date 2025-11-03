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

        if (cancellationPolicyRepository.count() == 0) {
            log.info("Khởi tạo các chính sách hủy vé mặc định...");

            // Mốc 1: Hủy trước 24 giờ (1440 phút), hoàn 100%
            CancellationPolicy policy1 = new CancellationPolicy();
            policy1.setDescriptions("Hoàn 100% nếu hủy trước 24 giờ");
            policy1.setCancellationTimeLimit(1440);
            policy1.setRefundPercentage(100);
            policy1.setRoute(null); // Quy tắc chung
            cancellationPolicyRepository.save(policy1);

            // Mốc 2: Hủy trước 12 giờ (720 phút), hoàn 70%
            CancellationPolicy policy2 = new CancellationPolicy();
            policy2.setDescriptions("Hoàn 70% nếu hủy trước 12 giờ");
            policy2.setCancellationTimeLimit(720);
            policy2.setRefundPercentage(70);
            policy2.setRoute(null);
            cancellationPolicyRepository.save(policy2);

            // Mốc 3: Hủy trước 1 giờ (60 phút), hoàn 30%
            CancellationPolicy policy3 = new CancellationPolicy();
            policy3.setDescriptions("Hoàn 30% nếu hủy trước 1 giờ");
            policy3.setCancellationTimeLimit(60);
            policy3.setRefundPercentage(30);
            policy3.setRoute(null);
            cancellationPolicyRepository.save(policy3);

            log.info("-> Khởi tạo các chính sách hủy vé thành công.");
        }

        log.info("Hoàn tất quá trình khởi tạo dữ liệu.");
    }
}
