package com.ubbcluj.sd.filestorage.requests.persistance;

import com.ubbcluj.sd.filestorage.requests.domain.StoredFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<StoredFile, Integer> {
}
