package com.example.controller.home;

import com.example.entity.User;
import com.example.repository.UserRepository;
import com.example.service.MinioService;
import com.example.service.PostService;
import com.example.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Controller
@RequestMapping("/")
public class HomeController {

    private final UserRepository userRepository;
    private final MinioService minioService;
    private final PostService postService;     // ✅ THÊM
    private final UserService userService;

    @GetMapping("/home")
    public String home(
            @AuthenticationPrincipal OidcUser oidcUser,
            Model model
    ) {
        String username = oidcUser.getPreferredUsername();

        User user = userRepository
                .findByUsername(username)
                .orElseThrow();

        Long userId = user.getId();

        // 👇 FEED
        model.addAttribute(
                "posts",
                postService.getFeed(userId)
        );

        // 👇 USER INFO
        model.addAttribute("username", user.getUsername());

        if (user.getAvatarPath() != null) {
            model.addAttribute(
                    "avatarUrl",
                    minioService.getPresignedUrl(user.getAvatarPath())
            );
        }

        model.addAttribute("minio", minioService); // 👈 RẤT QUAN TRỌNG

        return "home/feed";
    }

    @GetMapping("user/home")
    public String userHome() {
        return "user/user-home";
    }
}



