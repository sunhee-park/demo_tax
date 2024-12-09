package com.example.demo.service;

import com.example.demo.dto.SignupRequest;
import com.example.demo.entity.Users;
import com.example.demo.exception.CustomException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.repository.UsersRepository;
import com.example.demo.security.JwtUtil;
import com.example.demo.util.RrnEncryptionUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsersService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationService authenticationService;
    private final RrnEncryptionUtil rrnEncryptionUtil;

    public UsersService(UsersRepository usersRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, AuthenticationService authenticationService, RrnEncryptionUtil rrnEncryptionUtil) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationService = authenticationService;
        this.rrnEncryptionUtil = rrnEncryptionUtil;
    }

    public Users signup(SignupRequest request) throws Exception {

        if (usersRepository.findByUserId(request.getUserId()).isPresent()) {
            throw new CustomException(ErrorCode.USER_ALREADY_EXISTS);
        }

        return usersRepository.save(request.toEntity(passwordEncoder, rrnEncryptionUtil));
    }

    public String login(String userId, String password) {
        // 인증 수행
        Authentication authentication = authenticationService.authenticateUser(userId, password);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // access token 발급
        return jwtUtil.generateToken(userId);
    }
}
