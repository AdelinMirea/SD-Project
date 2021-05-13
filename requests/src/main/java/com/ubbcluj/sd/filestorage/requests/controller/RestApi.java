package com.ubbcluj.sd.filestorage.requests.controller;

import com.google.api.client.util.IOUtils;
import com.jlefebure.spring.boot.minio.MinioException;
import com.jlefebure.spring.boot.minio.MinioService;
import com.ubbcluj.sd.filestorage.requests.domain.FileType;
import com.ubbcluj.sd.filestorage.requests.domain.StoredFile;
import com.ubbcluj.sd.filestorage.requests.domain.User;
import com.ubbcluj.sd.filestorage.requests.dto.FileDTO;
import com.ubbcluj.sd.filestorage.requests.persistance.FileRepository;
import com.ubbcluj.sd.filestorage.requests.persistance.UserRepository;
import com.ubbcluj.sd.filestorage.requests.utils.DtoConverter;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.Store;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;

import javax.print.DocFlavor;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api")
@CrossOrigin
@Slf4j
public class RestApi {

    @Autowired
    private MinioService minioService;

    private FileRepository fileRepository;
    private UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setFileRepository(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @GetMapping("/files")
    public ResponseEntity<List<FileDTO>> getFilesFromRoot(@PathParam(value = "dirId") Integer dirId,
                                                          @RequestHeader Map<String, String> headers) {
        log.info("Entering get files with headers" + headers);
        Optional<User> userOptional = getUserFromHeaders(headers);
        if (!userOptional.isPresent()) {
            log.info("No user found!");
            return new ResponseEntity<>(NOT_FOUND);
        }
        User user = userOptional.get();
        if (dirId == null) {
            log.info("Sending all files");
            List<FileDTO> filesToReturn = user.getStoredFiles().stream()
                    .filter(file -> file.getParentDir() == null)
                    .map(DtoConverter::fileToDTO)
                    .collect(Collectors.toList());
            return new ResponseEntity<>(filesToReturn, OK);
        }

        log.info("Filtering files with dir id: " + dirId);
        Optional<StoredFile> directory = user.getStoredFiles()
                .stream()
                .filter(file -> file.getType().equals(FileType.DIRECTORY) && file.getId() == dirId)
                .findFirst();
        if (directory.isPresent()) {
            List<FileDTO> filesToRemove = directory.get().getChildFiles().stream()
                    .map(DtoConverter::fileToDTO).collect(Collectors.toList());
            return new ResponseEntity<>(filesToRemove, OK);
        }

        return new ResponseEntity<>(NOT_FOUND);
    }

    @GetMapping("/files/{id}/parent")
    public ResponseEntity<FileDTO> getParentFolder(@PathVariable int id,
                                                   @RequestHeader Map<String, String> header) {
        Optional<User> userOptional = getUserFromHeaders(header);
        if (!userOptional.isPresent()) {
            return new ResponseEntity<>(NOT_FOUND);
        }
        User user = userOptional.get();
        List<StoredFile> storedFiles = user.getStoredFiles();
        Optional<StoredFile> fileWithId = storedFiles.stream().filter(file -> file.getId() == id).findFirst();
        if (fileWithId.isPresent()) {
            StoredFile parent = fileWithId.get().getParentDir();
            return new ResponseEntity<>(DtoConverter.fileToDTO(parent), OK);
        }
        return new ResponseEntity<>(NOT_FOUND);
    }


    @GetMapping("/files/{id}")
    public ResponseEntity<FileDTO> getFileById(@PathVariable int id,
                                               @RequestHeader Map<String, String> header) {
        Optional<User> userOptional = getUserFromHeaders(header);
        if (!userOptional.isPresent()) {
            return new ResponseEntity<>(NOT_FOUND);
        }
        User user = userOptional.get();
        List<StoredFile> storedFiles = user.getStoredFiles();
        Optional<StoredFile> usersFile = storedFiles.stream().filter(file -> file.getId() == id).findFirst();
        if (usersFile.isPresent()) {
            return new ResponseEntity<>(DtoConverter.fileToDTO(usersFile.get()), OK);
        }
        Optional<StoredFile> fileById = fileRepository.findById(id);
        if (fileById.isPresent() && fileById.get().isPublicAccess()) {
            return new ResponseEntity<>(DtoConverter.fileToDTO(fileById.get()), OK);
        }
        return new ResponseEntity<>(FORBIDDEN);
    }

    @GetMapping("/files/{id}/download")
    public void getObject(@PathVariable("id") int id,
                          @RequestHeader Map<String, String> header,
                          HttpServletResponse response) throws MinioException, IOException {
        log.info("Downloading file...");
        Optional<User> userOptional = getUserFromHeaders(header);
        if (!userOptional.isPresent()) {
            throw new NoSuchFileException("No file!");
        }
        User user = userOptional.get();
        Optional<StoredFile> first = user.getStoredFiles().stream().filter(file -> file.getId() == id).findFirst();
        if (first.isPresent()){
            String name = first.get().getName();
            sendFileWithId(id, response, name);
            return;
        }
        Optional<StoredFile> fileById = fileRepository.findById(id);
        if (fileById.isPresent() && fileById.get().isPublicAccess()) {
            String name = fileById.get().getName();
            sendFileWithId(id, response, name);
        }
        throw new NoSuchFileException("No file!");
    }

    @PostMapping("/files")
    public ResponseEntity<?> uploadFile(@RequestHeader Map<String, String> header,
                                      @RequestParam("file") MultipartFile file) throws URISyntaxException, IOException, MinioException {
        log.info("Uploading file...");
        Optional<User> userOptional = getUserFromHeaders(header);
        if (!userOptional.isPresent()) {
            return new ResponseEntity<>(NOT_FOUND);
        }
        User user = userOptional.get();

        StoredFile newFile = new StoredFile();
        newFile.setName(file.getOriginalFilename());
        newFile.setOwner(user);
        newFile.setPublicAccess(false);
        newFile.setSize(file.getSize());
        newFile.setType(FileType.FILE);
        StoredFile savedFile = fileRepository.save(newFile);
        Path path = Paths.get(".", String.valueOf(savedFile.getId()));
        String contentType = URLConnection.guessContentTypeFromName(savedFile.getName());
        minioService.upload(path, file.getInputStream(), contentType);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private void sendFileWithId(int id, HttpServletResponse response, String name) throws MinioException, IOException {
        InputStream inputStream = minioService.get(Paths.get(".", String.valueOf(id)));

        // Set the content type and attachment header.
        response.addHeader("Content-disposition", "attachment;filename=" + name);
        response.setContentType(URLConnection.guessContentTypeFromName(name));

        // Copy the stream to the response's output stream.
        IOUtils.copy(inputStream, response.getOutputStream());
        response.flushBuffer();
    }

    private Optional<User> getUserFromHeaders(Map<String, String> headers) {
        String auth = headers.get("auth");
        if (auth == null) {
            return Optional.empty();
        }
        return userRepository.findUserBySecret(auth);
    }
}
