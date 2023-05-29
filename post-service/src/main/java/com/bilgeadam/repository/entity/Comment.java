package com.bilgeadam.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Comment extends Base {
    @Id
    private String id;
    private String userId;
    private String postId;
    private String username;
    private String comment;
    private List<String> subCommentIds = new ArrayList<>();
    private List<String> commentLikeIds = new ArrayList<>();
    private List<String> commentDislikeIds = new ArrayList<>();
}
