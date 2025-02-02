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
        // Crea usuario en la BD
        User newUser = userService.registerUser(
                request.getUsername(),
                request.getEmail(),
                request.getPassword()
        );
        // Generamos un token JWT
        String token = jwtUtil.generateToken(newUser);
        return ResponseEntity.ok(new AuthResponse(token));
    }

    /**
     * Endpoint para login
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        // Verificamos credenciales
        User user = userService.login(request.getUsernameOrEmail(), request.getPassword());
        // Generamos token
        String token = jwtUtil.generateToken(user);
        return ResponseEntity.ok(new AuthResponse(token));
    }

    /**
     * Endpoint para verificar un token (opcional)
     */
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        // authHeader = "Bearer <token>"
        if(!authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Token inválido");
        }
        String token = authHeader.substring(7);
        if(jwtUtil.validateToken(token)) {
            return ResponseEntity.ok("Token válido");
        } else {
            return ResponseEntity.status(401).body("Token inválido o expirado");
        }
    }
}