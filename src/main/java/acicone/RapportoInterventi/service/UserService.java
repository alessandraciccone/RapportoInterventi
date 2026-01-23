package acicone.RapportoInterventi.service;

import acicone.RapportoInterventi.entities.Role;
import acicone.RapportoInterventi.entities.User;

import acicone.RapportoInterventi.exceptions.BadRequestExceptions;
import acicone.RapportoInterventi.exceptions.NotFoundException;
import acicone.RapportoInterventi.payloads.UserResponseDTO;
import acicone.RapportoInterventi.payloads.UserUpdateDTO;
import acicone.RapportoInterventi.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ======================
    // GET ALL USERS
    // ======================
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    // ======================
    // GET USER BY ID
    // ======================
    public UserResponseDTO getUserById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Utente non trovato"));
        return mapToResponseDTO(user);
    }

    // ======================
    // SEARCH USERS (admin)
    // ======================
    public List<UserResponseDTO> searchUsers(String query) {
        List<User> users = userRepository
                .findByNomeContainingIgnoreCaseOrCognomeContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        query, query, query
                );

        return users.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    // ======================
    // UPDATE USER
    // ======================
    @Transactional
    public UserResponseDTO updateUser(UUID userId, UserUpdateDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Utente non trovato"));

        // Aggiorna solo i campi non nulli
        if (dto.nome() != null && !dto.nome().isBlank()) {
            user.setNome(dto.nome());
        }

        if (dto.cognome() != null && !dto.cognome().isBlank()) {
            user.setCognome(dto.cognome());
        }

        if (dto.email() != null && !dto.email().isBlank()) {
            // Verifica che la nuova email non sia già usata
            if (!user.getEmail().equals(dto.email()) &&
                    userRepository.existsByEmail(dto.email())) {
                throw new BadRequestExceptions("Email già in uso");
            }
            user.setEmail(dto.email());
        }

        User updated = userRepository.save(user);
        return mapToResponseDTO(updated);
    }

    // ======================
    // PROMOTE TO ADMIN
    // ======================
    @Transactional
    public UserResponseDTO promoteToAdmin(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Utente non trovato"));

        if (user.getRole() == Role.ADMIN) {
            throw new BadRequestExceptions("L'utente è già admin");
        }

        user.setRole(Role.ADMIN);
        User updated = userRepository.save(user);
        return mapToResponseDTO(updated);
    }

    // ======================
    // DEMOTE TO USER
    // ======================
    @Transactional
    public UserResponseDTO demoteToUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Utente non trovato"));

        if (user.getRole() == Role.USER) {
            throw new BadRequestExceptions("L'utente è già un utente normale");
        }

        user.setRole(Role.USER);
        User updated = userRepository.save(user);
        return mapToResponseDTO(updated);
    }

    // ======================
    // DELETE USER
    // ======================
    @Transactional
    public void deleteUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Utente non trovato"));

        // Verifica che non ci siano rapporti associati
        if (user.getReports() != null && !user.getReports().isEmpty()) {
            throw new BadRequestExceptions(
                    "Impossibile eliminare l'utente: ci sono " +
                            user.getReports().size() + " rapporti associati"
            );
        }

        userRepository.delete(user);
    }

    // ======================
    // GET USER ENTITY (for internal use)
    // ======================
    public User getUserEntity(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Utente non trovato"));
    }

    // ======================
    // HELPER METHOD
    // ======================
    private UserResponseDTO mapToResponseDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getNome(),
                user.getCognome(),
                user.getEmail(),
                user.getRole().name()
        );
    }
}