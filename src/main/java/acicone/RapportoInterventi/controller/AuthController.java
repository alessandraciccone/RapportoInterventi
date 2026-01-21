package acicone.RapportoInterventi.controller;

import acicone.RapportoInterventi.payloads.LoginRequestDTO;
import acicone.RapportoInterventi.payloads.LoginResponseDTO;
import acicone.RapportoInterventi.payloads.RegistrationRequestDTO;
import acicone.RapportoInterventi.payloads.RegistrationResponseDTO;
import acicone.RapportoInterventi.service.AuthService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

private final AuthService authService;
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody @Valid LoginRequestDTO request) {
        return authService.login(request);
    }

    @PostMapping("/register")
    public RegistrationResponseDTO register(
            @RequestBody @Valid RegistrationRequestDTO request
    ) {
        return authService.register(request);
    }
}
