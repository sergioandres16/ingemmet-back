package org.saeta.ingemmetback.controllers;

import org.saeta.ingemmetback.dto.AuthRequest;
import org.saeta.ingemmetback.dto.AuthResponse;
import org.saeta.ingemmetback.dto.RegisterRequest;
import org.saeta.ingemmetback.entities.User;
import org.saeta.ingemmetback.security.JwtUtil;
import org.saeta.ingemmetback.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    /**
     * Endpoint para registro de nuevos usuarios
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        // Crea usuario en la base de datos
        User newUser = userService.registerUser(
                request.getUsername(),
                request.getEmail(),
                request.getPassword()
        );
        // Genera un token JWT
        String token = jwtUtil.generateToken(newUser);
        // Retorna el token junto con el nombre y el correo
        return ResponseEntity.ok(new AuthResponse(token, newUser.getUsername(), newUser.getEmail()));
    }

    /**
     * Endpoint para login
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        // Verifica las credenciales
        User user = userService.login(request.getUsernameOrEmail(), request.getPassword());
        // Genera el token JWT
        String token = jwtUtil.generateToken(user);
        // Retorna el token junto con el nombre y el correo
        return ResponseEntity.ok(new AuthResponse(token, user.getUsername(), user.getEmail()));
    }

    /**
     * Endpoint para verificar un token (opcional)
     */
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        // authHeader = "Bearer <token>"
        if (!authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Token inválido");
        }
        String token = authHeader.substring(7);
        if (jwtUtil.validateToken(token)) {
            return ResponseEntity.ok("Token válido");
        } else {
            return ResponseEntity.status(401).body("Token inválido o expirado");
        }
    }
}