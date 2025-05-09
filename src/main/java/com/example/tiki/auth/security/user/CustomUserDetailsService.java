package com.example.tiki.auth.security.user;

import com.example.tiki.auth.domain.User;
import com.example.tiki.auth.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AuthRepository authRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = authRepository.findByEmail(email);
        if(user.isPresent()){
            return new CustomUserDetails(user.get());
        }
        throw new UsernameNotFoundException("없는 계정입니다.");
    }
}
