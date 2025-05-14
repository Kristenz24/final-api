package com.mingoy.social_media_be;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/mingoy/api")
@CrossOrigin(origins = "*")
public class SocMedController {

    @Autowired
    private SocMedRepository socMedRepository;

    // GET ALL
    @GetMapping(path = "/posts")
    public @ResponseBody Iterable<SocMed> getAllPosts() {
        return socMedRepository.findAll();
    }

    // POST (Single post)
    @PostMapping(path = "/posts")
    public ResponseEntity<SocMed> createPost(@RequestBody SocMed post) {
        try {
            // Set default values
            if (post.getTime() == null) {
                post.setTime(LocalDateTime.now());
            }
            if (post.getLikeCount() == 0) {
                post.setLikeCount(0);
            }
            SocMed savedPost = socMedRepository.save(post);
            return new ResponseEntity<>(savedPost, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // BULK POST (Multiple posts)
    @PostMapping(path = "/posts/bulk")
    public ResponseEntity<List<SocMed>> createPosts(@RequestBody List<SocMed> posts) {
        try {
            // Set default values for each post
            posts.forEach(post -> {
                if (post.getTime() == null) {
                    post.setTime(LocalDateTime.now());
                }
                if (post.getLikeCount() == 0) {
                    post.setLikeCount(0);
                }
            });

            List<SocMed> savedPosts = (List<SocMed>) socMedRepository.saveAll(posts);
            return new ResponseEntity<>(savedPosts, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET BY ID
    @GetMapping("/posts/{id}")
    public ResponseEntity<SocMed> getPostById(@PathVariable Long id) {
        Optional<SocMed> postData = socMedRepository.findById(id);
        return postData.map(post -> new ResponseEntity<>(post, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // UPDATE BY ID
    @PutMapping("/posts/{id}")
    public ResponseEntity<SocMed> updatePost(@PathVariable Long id, @RequestBody SocMed post) {
        Optional<SocMed> postData = socMedRepository.findById(id);

        if (postData.isPresent()) {
            SocMed existingPost = postData.get();
            if (post.getDescription() != null) {
                existingPost.setDescription(post.getDescription());
            }
            if (post.getImage() != null) {
                existingPost.setImage(post.getImage());
            }
            if (post.getName() != null) {
                existingPost.setName(post.getName());
            }
            existingPost.setBookmarked(post.isBookmarked());
            existingPost.setLikeCount(post.getLikeCount());
            // Don't update time on edit to preserve original post time

            return new ResponseEntity<>(socMedRepository.save(existingPost), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // DELETE BY ID
    @DeleteMapping("/posts/{id}")
    public ResponseEntity<HttpStatus> deletePost(@PathVariable Long id) {
        try {
            socMedRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Additional endpoint for toggling bookmarks
    @PatchMapping("/posts/{id}/bookmark")
    public ResponseEntity<SocMed> toggleBookmark(@PathVariable Long id) {
        Optional<SocMed> postData = socMedRepository.findById(id);
        if (postData.isPresent()) {
            SocMed post = postData.get();
            post.setBookmarked(!post.isBookmarked());
            return new ResponseEntity<>(socMedRepository.save(post), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // Updated like endpoint (now just increments/decrements count without boolean check)
    @PatchMapping("/posts/{id}/like")
    public ResponseEntity<SocMed> toggleLike(@PathVariable Long id) {
        Optional<SocMed> postData = socMedRepository.findById(id);
        if (postData.isPresent()) {
            SocMed post = postData.get();
            post.setLikeCount(post.getLikeCount() + 1);
            return new ResponseEntity<>(socMedRepository.save(post), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}