package com.qltnb.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("disabled") // Chuyển từ "dev" thành "disabled" để tắt cơ chế bypass hoàn toàn
public class DevSecurityConfig {
    // Không còn hiệu lực hoạt động trong hệ thống
}
