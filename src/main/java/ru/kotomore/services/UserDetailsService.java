package ru.kotomore.services;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.kotomore.models.User;
import ru.kotomore.repositories.UserRepository;
import ru.kotomore.security.UserDetails;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Cacheable(value = "user_details")
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(s);

        if (user.isEmpty())
            throw new UsernameNotFoundException(String.format("Пользователь - %s не найден", s));

        return new UserDetails(user.get());
    }
}
