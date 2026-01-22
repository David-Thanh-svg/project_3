package com.example.controller.user;

import com.example.entity.enums.PrivacyLevel;
import com.example.service.PostService;
import com.example.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final UserService userService;

    @GetMapping("/create")
    public String showCreateForm() {
        return "post/create";
    }

    @GetMapping
    public String postsRedirect() {
        return "redirect:/home";
    }

    @PostMapping("/create")
    public String create(
            @RequestParam String content,
            @RequestParam PrivacyLevel privacy,
            @RequestParam(required = false) List<MultipartFile> files,
            @RequestParam(required = false) List<Long> tags,
            OAuth2AuthenticationToken auth
    ) {
        Long userId = userService.getUserIdByUsername(
                auth.getPrincipal().getAttribute("preferred_username")
        );

        postService.createPost(userId, content, privacy, files, tags);
        return "redirect:/home";
    }

    @PostMapping("/{id}/repost")
    public String repost(@PathVariable Long id,
                         @RequestParam(required = false) String content,
                         OAuth2AuthenticationToken auth) {

        Long userId = userService.getUserIdByUsername(
                auth.getPrincipal().getAttribute("preferred_username")
        );

        postService.repost(userId, id, content);
        return "redirect:/home";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id,
                         OAuth2AuthenticationToken auth) {

        Long userId = userService.getUserIdByUsername(
                auth.getPrincipal().getAttribute("preferred_username")
        );

        postService.deletePost(id, userId);
        return "redirect:/feed";
    }
}
