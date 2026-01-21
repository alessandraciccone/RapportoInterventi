package acicone.RapportoInterventi.service;

import acicone.RapportoInterventi.entities.User;
import acicone.RapportoInterventi.exceptions.NotFoundException;
import acicone.RapportoInterventi.payloads.UserResponseDTO;
import acicone.RapportoInterventi.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> new UserResponseDTO(
                        user.getId(),
                        user.getNome(),
                        user.getCognome(),
                        user.getEmail(),
                        user.getRole().name()
                )).collect(Collectors.toList());
    }

    public UserResponseDTO getUserById(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Utente non trovato"));
        return new UserResponseDTO(
                user.getId(),
                user.getNome(),
                user.getCognome(),
                user.getEmail(),
                user.getRole().name()

        );
    }

    public void deleteUser(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Utente non trovato");
        }
        userRepository.deleteById(userId);
    }
}