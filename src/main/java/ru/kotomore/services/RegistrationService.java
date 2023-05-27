package ru.kotomore.services;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.kotomore.dto.UserDTO;
import ru.kotomore.models.User;
import ru.kotomore.repositories.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RegistrationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    public Optional<User> saveUser(UserDTO userDTO) {
        User savedUser;
        Optional<User> oldUser = userRepository.findByEmail(userDTO.getEmail());
        if (oldUser.isEmpty()) {
            savedUser = modelMapper.map(userDTO, User.class);
            savedUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        } else {
            savedUser = oldUser.get();
            savedUser.setEmail(userDTO.getEmail());
            savedUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            savedUser.setUsername(userDTO.getUsername());
        }

        savedUser = userRepository.save(savedUser);
        return Optional.of(savedUser);
    }
}
