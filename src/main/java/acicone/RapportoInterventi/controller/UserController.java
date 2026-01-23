package acicone.RapportoInterventi.controller;

import acicone.RapportoInterventi.payloads.UserResponseDTO;
import acicone.RapportoInterventi.payloads.UserUpdateDTO;
import acicone.RapportoInterventi.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ======================
    // GET ALL USERS (solo admin)
    // ======================
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<UserResponseDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    // ======================
    // GET USER BY ID
    // ======================
    @GetMapping("/{userId}")
    @PreAuthorize("isAuthenticated()")
    public UserResponseDTO getUserById(@PathVariable UUID userId) {
        return userService.getUserById(userId);
    }

    // ======================
    // SEARCH USERS (solo admin)
    // ======================
    @GetMapping("/search")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<UserResponseDTO> searchUsers(@RequestParam String query) {
        return userService.searchUsers(query);
    }

    // ======================
    // UPDATE USER
    // ======================
    @PutMapping("/{userId}")
    @PreAuthorize("isAuthenticated()")
    public UserResponseDTO updateUser(
            @PathVariable UUID userId,
            @RequestBody @Valid UserUpdateDTO dto) {
        return userService.updateUser(userId, dto);
    }

    // ======================
    // PROMOTE USER TO ADMIN (solo admin)
    // ======================
    @PostMapping("/{userId}/promote")
    @PreAuthorize("hasAuthority('ADMIN')")
    public UserResponseDTO promoteToAdmin(@PathVariable UUID userId) {
        return userService.promoteToAdmin(userId);
    }

    // ======================
    // DEMOTE ADMIN TO USER (solo admin)
    // ======================
    @PostMapping("/{userId}/demote")
    @PreAuthorize("hasAuthority('ADMIN')")
    public UserResponseDTO demoteToUser(@PathVariable UUID userId) {
        return userService.demoteToUser(userId);
    }

    // ======================
    // DELETE USER (solo admin)
    // ======================
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable UUID userId) {
        userService.deleteUser(userId);
    }
}