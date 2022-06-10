package ru.yandex.practicum.catsgram.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.service.PostService;
import java.util.List;

@RestController
public class PostController {
    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/posts")
    public List<Post> findAll(
            @RequestParam(value = "sort", defaultValue = "desc", required = false) String sort,
            @RequestParam(value = "page", defaultValue = "1", required = false) int page,
            @RequestParam(value = "size", defaultValue = "10", required = false) int size) {

        if(!(sort.equals("asc") || sort.equals("desc"))){
            throw new IllegalArgumentException();
        }
        if(page <= 0 || size <= 0){
            throw new IllegalArgumentException();
        }
        return postService.findAll(sort, page, size);
    }

    @GetMapping("/post/{postId}")
    public Post getPostById(@PathVariable int postId) {
        return postService.getPostByID(postId);
    }

    @PostMapping("/post")
    public Post create(@RequestBody Post post) {
        return postService.create(post);
    }
}