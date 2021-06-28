package com.tewhem.LoginProject.appuser;

import java.time.LocalDateTime;
import java.util.UUID;

import com.tewhem.LoginProject.registration.token.ConfirmationToken;
import com.tewhem.LoginProject.registration.token.ConfirmationTokenService;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AppUserService implements UserDetailsService {

    private static final String USER_NOT_FOUND = "user with email %s not found";
    private final AppUserRepository appUserRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ConfirmationTokenService confirmationTokenService;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {
        return appUserRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format(USER_NOT_FOUND, email)));
    }

    public String signUpUser(AppUser appUser) {
        boolean userExists =
                appUserRepository.findByEmail(appUser.getEmail()).isPresent();

        if (userExists) {
            throw new IllegalStateException("Email already taken.");
        }
        String encodedPassword =
                bCryptPasswordEncoder.encode(appUser.getPassword());

        appUser.setPassword(encodedPassword);

        appUserRepository.save(appUser);

        String token = UUID.randomUUID().toString();

        ConfirmationToken confirmationToken =
                new ConfirmationToken(token, LocalDateTime.now(),
                        LocalDateTime.now().plusMinutes(15), appUser);

        confirmationTokenService.saveConfirmationToken(confirmationToken);
        // TODO : Send Email
        return token;
    }

    public int enableAppUser(String email) {
        return appUserRepository.enableAppUser(email);
    }
}
