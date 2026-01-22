package com.example.controller.user;

import com.example.entity.User;
import com.example.service.MinioService;
import com.example.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user/profile")
public class UserProfileController {

    private final UserService userService;
    private final MinioService minioService;

    @GetMapping
    public String myProfile(OAuth2AuthenticationToken auth) {
        String username = auth.getPrincipal()
                .getAttribute("preferred_username");

        User user = userService.getOrCreateUserFromKeycloak(username);
        return "redirect:/user/profile/" + user.getId();
    }

    @GetMapping("/{id}")
    public String viewProfile(@PathVariable Long id,
                              OAuth2AuthenticationToken auth,
                              Model model) {

        Long viewerId = null;
        if (auth != null) {
            viewerId = userService.getUserIdByUsername(
                    auth.getPrincipal().getAttribute("preferred_username"));
        }

        User user = userService.getUserById(id, viewerId);
        if (user == null) return "error/403";

        model.addAttribute("user", user);
        model.addAttribute("isOwner", viewerId != null && viewerId.equals(id));

        if (user.getAvatarPath() != null) {
            model.addAttribute("avatarUrl",
                    minioService.getPresignedUrl(user.getAvatarPath()));
        }

        return "user/user-profile";
    }


    @PostMapping("/avatar")
    public String uploadAvatar(@RequestParam("avatar") MultipartFile avatar,
                               OAuth2AuthenticationToken auth) {

        String username = auth.getPrincipal()
                .getAttribute("preferred_username");

        Long userId = userService.getUserIdByUsername(username);

        userService.uploadAvatar(userId, avatar);

        return "redirect:/user/profile";
    }

}
