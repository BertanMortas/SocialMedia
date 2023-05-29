package com.bilgeadam.repository;

import com.bilgeadam.repository.entity.Dislike;
import com.bilgeadam.repository.entity.Post;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IDislikeRepository extends MongoRepository<Dislike,String> {
    Optional<Dislike> findByUserIdAndPostId(String id, String postId);
}
