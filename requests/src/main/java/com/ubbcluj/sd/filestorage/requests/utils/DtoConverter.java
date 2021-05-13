package com.ubbcluj.sd.filestorage.requests.utils;

import com.ubbcluj.sd.filestorage.requests.domain.StoredFile;
import com.ubbcluj.sd.filestorage.requests.dto.FileDTO;

public class DtoConverter {

    private DtoConverter() {}

    public static FileDTO fileToDTO(StoredFile file) {
        Integer parentDirId = file.getParentDir() == null ? null : file.getParentDir().getId();
        FileDTO dto = FileDTO.builder()
                .id(file.getId())
                .name(file.getName())
                .parentId(parentDirId)
                .publicAccess(file.isPublicAccess())
                .size(file.getSize())
                .build();
        return dto;
    }
}
