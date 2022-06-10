package ru.yandex.practicum.catsgram.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.exception.*;
import ru.yandex.practicum.catsgram.model.*;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostService {
    private final Map<Integer,Post> posts = new HashMap<>();
    private final UserService userService;
    private static final Logger log = LoggerFactory.getLogger(PostService.class);
    private static int counter;

    @Autowired
    public PostService (UserService userService) {
        this.userService = userService;
    }

    public List<Post> findAll(String sort, int page, int size) {
        List<Post> list = new ArrayList<>(posts.values());

        Comparator<Post> postComparator = new Comparator<>() {
            @Override
            public int compare(Post post1, Post post2) {
                Instant creationDate1 = post1.getCreationDate();
                Instant creationDate2 = post2.getCreationDate();
                return creationDate1.compareTo(creationDate2);
            }
        };
        if (sort.equals("asc")) {
            list.sort(postComparator);
        } else {
            list.sort(postComparator.reversed());
        }

        int fromIndex = (page - 1) * size;
        log.debug("Текущее количество постов: {}, запрошены с {} по {}.", list.size(), fromIndex + 1, fromIndex + size);
        if (fromIndex >= list.size()) {
            return new ArrayList<>();
        }
        int toIndex = fromIndex + size;
        if (toIndex >  list.size()) {
            toIndex = list.size();
        }
        log.debug("Выданы посты с {} по {}.", fromIndex + 1, toIndex);
        return list.subList(fromIndex, toIndex);
    }

    public List<Post> findAllByUserEmail(String email, Integer size, String sort) {
        return posts.values().stream().filter(p -> email.equals(p.getAuthor())).sorted((p0, p1) -> {
            int comp = p0.getCreationDate().compareTo(p1.getCreationDate()); //прямой порядок сортировки
            if(sort.equals("desc")){
                comp = -1 * comp; //обратный порядок сортировки
            }
            return comp;
        }).limit(size).collect(Collectors.toList());
    }

    public Post create(Post post) {
        String author = post.getAuthor();
        if (author == null || userService.findUserByEmail(author) == null) {
            throw new UserNotFoundException(String.format("Пользователь %s не найден", author));
        }
        post.setId(++counter);
        posts.put(post.getId(),post);
        log.trace("Добавляем комментарий: {}, ID {}", post.getDescription(), post.getId());
        return post;
    }

    public Post getPostByID (int postId) {
        Post post = posts.get(postId);
        if (post == null) {
            throw new PostNotFoundException(String.format("Пост № %d не найден", postId));
        }
        return post;
    }
}
