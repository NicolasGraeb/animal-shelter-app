package app.pet.services;

import app.pet.models.User;
import app.pet.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User alice;

    @BeforeEach
    void setUp() {
        alice = new User();
        alice.setId(1L);
        alice.setUsername("alice");
        alice.setEmail("alice@example.com");
        alice.setPasswordHash("hash");
        alice.setRole(null);
    }

    @Test
    void getAllUsers_returnsAll() {
        when(userRepository.findAll()).thenReturn(List.of(alice));
        var result = userService.getAllUsers();
        assertThat(result).containsExactly(alice);
        verify(userRepository).findAll();
    }

    @Test
    void getUserById_existing_returnsUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(alice));
        var result = userService.getUserById(1L);
        assertThat(result).isSameAs(alice);
    }

    @Test
    void getUserById_missing_throws() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.getUserById(2L))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("User not found with id: 2");
    }

    @Test
    void createUser_savesAndReturns() {
        when(userRepository.save(alice)).thenReturn(alice);
        var result = userService.createUser(alice);
        assertThat(result).isSameAs(alice);
        verify(userRepository).save(alice);
    }

    @Test
    void getUserByUsername_existing_returnsUser() {
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(alice));
        var result = userService.getUserByUsername("alice");
        assertThat(result).isSameAs(alice);
    }

    @Test
    void getUserByUsername_missing_throws() {
        when(userRepository.findByUsername("bob")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.getUserByUsername("bob"))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("User not found with username: bob");
    }

    @Test
    void updateUser_existing_updatesAndSaves() {
        User updates = new User();
        updates.setUsername("alice2");
        updates.setEmail("a2@example.com");
        updates.setPasswordHash("newhash");
        updates.setRole(null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(alice));
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var result = userService.updateUser(1L, updates);

        assertThat(result.getUsername()).isEqualTo("alice2");
        assertThat(result.getEmail()).isEqualTo("a2@example.com");
        assertThat(result.getPasswordHash()).isEqualTo("newhash");
        verify(userRepository).save(alice);
    }

    @Test
    void deleteUser_existing_deletes() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(alice));
        userService.deleteUser(1L);
        verify(userRepository).delete(alice);
    }
}
