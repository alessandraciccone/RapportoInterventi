package acicone.RapportoInterventi.controller;

import acicone.RapportoInterventi.entities.Role;
import acicone.RapportoInterventi.entities.User;
import acicone.RapportoInterventi.payloads.*;
import acicone.RapportoInterventi.repositories.UserRepository;
import acicone.RapportoInterventi.security.CustomUserDetailsService;
import acicone.RapportoInterventi.security.JwtTool;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTool jwtTool;
    private final CustomUserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtTool jwtTool,
                          CustomUserDetailsService userDetailsService,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtTool = jwtTool;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // --- LOGIN ---
    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody @Valid LoginRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        var userDetails = userDetailsService.loadUserByUsername(request.email());
        String role = userDetails.getAuthorities().iterator().next().getAuthority();
        String token = jwtTool.generateToken(
                java.util.UUID.fromString(userRepository.findByEmail(request.email()).get().getId().toString()),
                role
        );

        return new LoginResponseDTO(token);
    }

    // --- REGISTRAZIONE ---
    @PostMapping("/register")
    public RegistrationResponseDTO register(@RequestBody @Valid RegistrationRequestDTO request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new RuntimeException("Email già registrata"); // qui puoi usare le tue eccezioni custom
        }

        User user = new User();
        user.setNome(request.nome());
        user.setCognome(request.cognome());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.USER);// ruolo di

        userRepository.save(user);

        return new RegistrationResponseDTO("Utente registrato con successo", user.getId().toString());
    }
}
