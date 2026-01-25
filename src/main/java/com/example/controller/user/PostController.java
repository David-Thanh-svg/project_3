package com.example.controller.user;

import com.example.entity.Post;
import com.example.entity.enums.PrivacyLevel;
import com.example.service.PostService;
import com.example.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
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
    public String deletePost(@PathVariable Long id,
                             OAuth2AuthenticationToken authentication) {

        String username =
                authentication.getPrincipal()
                        .getAttribute("preferred_username");

        Long userId = userService.getUserIdByUsername(username);

        postService.deletePost(id, userId);

        return "redirect:/home";
    }
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id,
                               Model model,
                               OAuth2AuthenticationToken auth) {

        Long userId = userService.getUserIdByUsername(
                auth.getPrincipal().getAttribute("preferred_username")
        );

        Post post = postService.getPostForEdit(id, userId);

        model.addAttribute("post", post);
        model.addAttribute("allUsers", userService.findAll());

        return "post/edit";
    }



    @PostMapping("/{id}/edit")
    public String editPost(@PathVariable Long id,

                           @RequestParam String content,

                           @RequestParam PrivacyLevel privacy,

                           @RequestParam(required = false)
                           List<Long> tagUserIds,

                           @RequestParam(required = false)
                           List<Long> deleteMediaIds,

                           @RequestParam(required = false)
                           List<MultipartFile> newFiles,

                           Authentication authentication
    ) {

        String username = authentication.getName();

        Long userId =
                userService.getUserIdByUsername(username);

        postService.updatePost(
                id,
                userId,
                content,
                privacy,
                newFiles,
                deleteMediaIds,
                tagUserIds
        );

        return "redirect:/home";
    }

    @PostMapping("/posts/{id}/repost")
    public String repost(@PathVariable Long id,
                         @RequestParam(required = false) String content) {

        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        String username = auth.getName();

        Long userId =
                userService.getUserIdByUsername(username);

        postService.repost(userId, id, content);

        return "redirect:/";
    }


}
