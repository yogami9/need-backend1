package com.needbackend_app.needapp.administrator.superadmin;

import com.needbackend_app.needapp.user.enums.Role;
import com.needbackend_app.needapp.user.model.NeedUser;
import com.needbackend_app.needapp.user.repository.NeedUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SuperAdminSeeder implements CommandLineRunner {

    private final NeedUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!userRepository.existsByEmail("superadmin@need.com")) {
            NeedUser user = new NeedUser();
            user.setFirstName("Super");
            user.setLastName("Admin");
            user.setEmail("superadmin@need.com");
            user.setPhone("08000000000");
            user.setPassword(passwordEncoder.encode("SuperSecure123"));
            user.setRole(Role.SUPERADMIN);
            userRepository.save(user);
            log.info("SuperAdmin seeded: {} {} <{}>", user.getFirstName(), user.getLastName(), user.getEmail());
        } else {
            log.info("SuperAdmin already exists. No action taken.");
        }
    }
}