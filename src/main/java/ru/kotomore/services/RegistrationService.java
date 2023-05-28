package ru.kotomore.services;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.kotomore.dto.UserDTO;
import ru.kotomore.exceptions.UserAlreadyExistException;
import ru.kotomore.models.User;
import ru.kotomore.repositories.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RegistrationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @CacheEvict(value = "user_details")
    public Optional<User> saveUser(UserDTO userDTO) {
        String email = userDTO.getEmail();
        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistException(email);
        }

        User newUser = modelMapper.map(userDTO, User.class);
        newUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        newUser = userRepository.save(newUser);
        return Optional.of(newUser);
    }
}
