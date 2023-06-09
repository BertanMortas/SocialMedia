package com.bilgeadam.repository.entity;

import com.bilgeadam.repository.enums.EStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class UserProfile extends Base implements Serializable {
    @Id
    private String id;
    private Long authId;
    private String username;
    private String password;
    private String email;
    private String phone;
    private String avatar;
    private String info;
    private String address;
    @Builder.Default
    private EStatus status = EStatus.PENDING;
    private List<String> follows = new ArrayList<>();
    private List<String> followers = new ArrayList<>();
}
