package com.guide.touristweb.controller;

import com.guide.touristmodel.entity.Role;
import com.guide.touristmodel.entity.User;
import com.guide.touristrepository.repository.RoleRepository;
import com.guide.touristrepository.repository.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final BCryptPasswordEncoder encoder;

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("userForm", new UserForm());
        return "register";
    }

    @PostMapping("/register")
    public String processRegister(@ModelAttribute("userForm") @Valid UserForm userForm,
                                  BindingResult bindingResult,
                                  Model model) {
        if (bindingResult.hasErrors()) {
            return "register";
        }

        if (userRepo.findByUsername(userForm.getUsername()) != null) {
            model.addAttribute("error", "Username already exists.");
            return "register";
        }

        if (!userForm.getPassword().equals(userForm.getConfirmPassword())) {
            model.addAttribute("error", "Passwords do not match.");
            return "register";
        }

        var user = new User();
        user.setUsername(userForm.getUsername());
        user.setPassword(encoder.encode(userForm.getPassword()));

        Role roleUser = roleRepo.findByName("USER");
        if (roleUser == null) {
            roleUser = new Role();
            roleUser.setName("USER");
            roleUser = roleRepo.save(roleUser);
        }
        user.getRoles().add(roleUser);
        userRepo.save(user);

        return "redirect:/login";
    }

    @GetMapping("/profile")
    public String profile() {
        return "profile";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    public static class UserForm {
        @NotBlank(message = "Username is required")
        private String username;

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        private String password;

        @NotBlank(message = "Confirm Password is required")
        private String confirmPassword;

        public String getUsername() {
            return username;
        }
        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }
        public void setPassword(String password) {
            this.password = password;
        }

        public String getConfirmPassword() {
            return confirmPassword;
        }
        public void setConfirmPassword(String confirmPassword) {
            this.confirmPassword = confirmPassword;
        }
    }
}