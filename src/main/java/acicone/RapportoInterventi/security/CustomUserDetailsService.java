package acicone.RapportoInterventi.security;

import acicone.RapportoInterventi.entities.User;
import acicone.RapportoInterventi.entities.Role;
import acicone.RapportoInterventi.repositories.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato con email: " + email));

        return buildUserDetails(user);
    }

    public UserDetails loadUserById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato con id: " + userId));

        return buildUserDetails(user);
    }

    // --- Helper per costruire UserDetails a partire da User ---
    private UserDetails buildUserDetails(User user) {
        Role role = user.getRole(); // Enum Role {USER, ADMIN}
        String roleName = "ROLE_" + role.name(); // Spring Security vuole ROLE_USER / ROLE_ADMIN

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority(roleName))
        );
    }
}
