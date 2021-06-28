package com.tewhem.LoginProject.registration;

import com.tewhem.LoginProject.appuser.AppUser;
import com.tewhem.LoginProject.appuser.AppUserRole;
import com.tewhem.LoginProject.appuser.AppUserService;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class RegistrationService {

    private final AppUserService appUserService;
    private final EmailValidator emailValidator;

    public String register(RegistrationRequest request) {
        boolean isValidEmail = emailValidator.test(request.getEmail());

        if (!isValidEmail) {
            throw new IllegalStateException("Email is not valid.");
        }
        return appUserService.signUpUser(new AppUser(request.getFirstName(),
                request.getLastName(), request.getEmail(),
                request.getPassword(), AppUserRole.USER));
    }

}
