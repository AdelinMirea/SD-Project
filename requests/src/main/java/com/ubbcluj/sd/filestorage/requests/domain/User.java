package com.ubbcluj.sd.filestorage.requests.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String email;
    private String password;
    private String secret;

    @OneToMany
    @JoinColumn(name = "file_id")
    private List<StoredFile> storedFiles = new ArrayList<>();
}
