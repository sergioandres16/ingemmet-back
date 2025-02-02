package org.saeta.ingemmetback.services;

import org.saeta.ingemmetback.entities.User;
import org.saeta.ingemmetback.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * Crea un usuario en la base de datos (registrar).
     * @throws RuntimeException si username/email ya existen.
     */
    public User registerUser(String username, String email, String rawPassword) {
        // Verificar duplicados
        if(userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("El username ya existe: " + username);
        }
        if(userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("El email ya existe: " + email);
        }

        // Hashear password
        String hashed = passwordEncoder.encode(rawPassword);

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(hashed);

        return userRepository.save(user);
    }

    /**
     * Busca un usuario por username
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Comprueba credenciales para login
     */
    public User login(String usernameOrEmail, String rawPassword) {
        // Buscar user por username o email
        Optional<User> userOpt = userRepository.findByUsername(usernameOrEmail);
        if(!userOpt.isPresent()) {
            // Intentar por email si no está por username
            userOpt = userRepository.findByEmail(usernameOrEmail);
            if(!userOpt.isPresent()) {
                throw new RuntimeException("Usuario no encontrado");
            }
        }

        User user = userOpt.get();
        // Validar password con BCrypt
        if(!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            throw new RuntimeException("Contraseña inválida");
        }
        return user;
    }
}