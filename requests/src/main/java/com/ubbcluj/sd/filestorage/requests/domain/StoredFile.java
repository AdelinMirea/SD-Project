package com.ubbcluj.sd.filestorage.requests.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class StoredFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private FileType type;
    private String name;
    private double size;
    private boolean publicAccess;

    @ManyToOne
    @JoinColumn(name = "file_id")
    private User owner;

    @ManyToOne
    @JoinColumn(name = "child_id")
    private StoredFile parentDir;

    @OneToMany
    @JoinColumn(name = "child_id")
    private List<StoredFile> childFiles = new ArrayList<>();

    public StoredFile() {
        publicAccess = false;
    }
}
