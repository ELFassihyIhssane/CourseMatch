package ma.kaoutar.userservice.controller;

import ma.kaoutar.userservice.dto.AuthRequest;
import ma.kaoutar.userservice.security.JwtService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtService jwtService;

    public AuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public String login(@RequestBody AuthRequest request) {
        return jwtService.generateToken(request.getUsername());
    }

}
