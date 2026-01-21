package acicone.RapportoInterventi.service;

import acicone.RapportoInterventi.entities.Role;
import acicone.RapportoInterventi.entities.User;
import acicone.RapportoInterventi.exceptions.BadRequestExceptions;
import acicone.RapportoInterventi.payloads.LoginRequestDTO;
import acicone.RapportoInterventi.payloads.LoginResponseDTO;
import acicone.RapportoInterventi.payloads.RegistrationRequestDTO;
import acicone.RapportoInterventi.payloads.RegistrationResponseDTO;
import acicone.RapportoInterventi.repositories.UserRepository;
import acicone.RapportoInterventi.security.JwtTool;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTool jwtTool;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtTool jwtTool
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTool = jwtTool;
    }


    // LOGIN
    public LoginResponseDTO login(LoginRequestDTO request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadRequestExceptions("Credenziali non valide"));

        String accessToken = jwtTool.generateToken(
                user.getId(),
                user.getRole().name()
        );

        String refreshToken = jwtTool.generateRefreshToken(user.getId());

        return new LoginResponseDTO(
                accessToken,
                refreshToken,
                user.getRole().name(),
                user.getId()
        );
    }


    // REGISTRAZIONE

    public RegistrationResponseDTO register(RegistrationRequestDTO request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new BadRequestExceptions("Email già registrata");
        }

        User user = new User();
        user.setNome(request.nome());
        user.setCognome(request.cognome());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.USER);

        userRepository.save(user);

        return new RegistrationResponseDTO(
                user.getId(),
                user.getEmail(),
                "Utente registrato con successo"
        );
    }
}
