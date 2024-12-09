package com.example.demo.dto;

import com.example.demo.util.RrnEncryptionUtil;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.demo.entity.Users;

@Getter
@Setter
public class SignupRequest {
    private String userId;
    private String password;
    private String name;
    private String regNo;

    public Users toEntity(PasswordEncoder passwordEncoder, RrnEncryptionUtil rrnEncryptionUtil) throws Exception {
        Users user = new Users();
        user.setUserId(this.userId);
        user.setPassword(passwordEncoder.encode(this.password));
        user.setName(this.name);
        user.setRegNo(rrnEncryptionUtil.encrypt(this.regNo)); // 암호화된 주민등록번호
        return user;
    }
}
