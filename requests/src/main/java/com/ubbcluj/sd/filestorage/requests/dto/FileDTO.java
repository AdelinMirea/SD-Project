package com.ubbcluj.sd.filestorage.requests.dto;

import com.ubbcluj.sd.filestorage.requests.domain.FileType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileDTO {
    private int id;
    private FileType type;
    private String name;
    private double size;
    private boolean publicAccess;
    private Integer parentId;
}
