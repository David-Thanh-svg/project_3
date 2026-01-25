package com.example.controller.user;

import com.example.entity.User;
import com.example.entity.enums.PrivacyLevel;
import com.example.repository.UserRepository;
import com.example.service.UserService;
import com.example.service.MinioService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.ui.Model;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.example.dto.UserDto;

import java.util.List;


@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final MinioService minioService;
    private final UserService userService; // ✅ THÊM DÒNG NÀY


    @GetMapping
    public String userHome(
            @AuthenticationPrincipal OidcUser oidcUser,
            Model model
    ) {

        if (oidcUser == null) {
            return "redirect:/login";
        }

        String username = oidcUser.getPreferredUsername();

        User user = userRepository
                .findByUsername(username)
                .orElseGet(() ->
                        userService.getOrCreateUserFromKeycloak(username)
                );

        model.addAttribute("username", user.getUsername());

        if (user.getAvatarPath() != null) {
            model.addAttribute("avatarUrl",
                    minioService.getPresignedUrl(user.getAvatarPath()));
        }

        return "user/user-home";
    }

    @GetMapping("/edit")
    public String editProfile(OAuth2AuthenticationToken auth, Model model) {
        String username = auth.getPrincipal()
                .getAttribute("preferred_username");

        User user = userRepository
                .findByUsername(username)
                .orElseThrow();

        model.addAttribute("user", user);
        return "user/user-profile-edit";
    }

    @PostMapping("/edit")
    public String updateProfile(@RequestParam String bio,
                                @RequestParam PrivacyLevel privacy,
                                OAuth2AuthenticationToken auth) {

        String username = auth.getPrincipal()
                .getAttribute("preferred_username");

        Long userId = userRepository
                .findByUsername(username)
                .orElseThrow()
                .getId();

        userService.updateProfile(userId, bio, privacy);
        return "redirect:/user/profile";
    }
    @GetMapping("/users/search")
    @ResponseBody
    public List<UserDto> search(@RequestParam String q) {

        return userRepository
                .findByUsernameContainingIgnoreCase(q)
                .stream()
                .map(u -> new UserDto(u.getId(), u.getUsername()))
                .toList();
    }


}
