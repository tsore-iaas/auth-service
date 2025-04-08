package com.tsore.user.service;

import com.tsore.user.config.RabbitMQConfig;
import com.tsore.user.dto.LoginRequest;
import com.tsore.user.dto.LoginResponse;
import com.tsore.user.dto.RegisterRequest;
import com.tsore.user.entity.AuthProvider;
import com.tsore.user.entity.User;
import com.tsore.user.repository.UserRepository;
import com.tsore.user.security.JwtUtil;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        if (user.getProvider() == AuthProvider.LOCAL &&
                !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Mot de passe incorrect");
        }

        User userWithoutPassword = new User();
        userWithoutPassword.setId(user.getId());
        userWithoutPassword.setEmail(user.getEmail());
        userWithoutPassword.setName(user.getName());
        userWithoutPassword.setProvider(user.getProvider());
        userWithoutPassword.setRoleType(user.getRoleType());
        
        String token = jwtUtil.generateToken(user.getEmail());
        return new LoginResponse(userWithoutPassword, token);
    }

    public LoginResponse signup(RegisterRequest request) {
        if (!userRepository.findByEmail(request.getEmail()).isEmpty()) {
            throw new RuntimeException("Utilisateur avec cet email existe déjà");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User newUser = new User();
        newUser.setEmail(request.getEmail());
        newUser.setPassword(encodedPassword);
        newUser.setProvider(AuthProvider.LOCAL);
        newUser.setName(request.getName());
        newUser.setRoleType(request.getRoleType());
        String token = jwtUtil.generateToken(newUser.getEmail());
        User savedUser = userRepository.save(newUser);

        // On envoie un message à RabbitMQ pour créer l'utilisateur dans les autres
        // services
        HashMap<String, String> d = new HashMap<>();
        d.put("email", savedUser.getEmail());
        d.put("id", String.valueOf(savedUser.getId()));
        this.rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, "user.create.key", savedUser);
        return new LoginResponse(savedUser, token);
    }

    public Long getUserIdFromToken(String token) {
        String email = jwtUtil.extractEmail(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        return user.getId();
    }
}
