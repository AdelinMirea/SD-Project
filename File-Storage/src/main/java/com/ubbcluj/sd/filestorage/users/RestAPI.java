package com.ubbcluj.sd.filestorage.users;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api")
@Slf4j
@CrossOrigin
public class RestAPI {

    private UserRepository userRepository;

    public RestAPI(){
        log.info("LOADED!!!!!");
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers(){
        log.info("Get all users");
        List<UserDTO> dtos = userRepository.findAll().stream().map(user -> new UserDTO(user.getEmail(), user.getPassword())).collect(Collectors.toList());
        return new ResponseEntity<>(dtos, OK);
    }

    @PostMapping("/users")
    public ResponseEntity<UserDTO> addUser(@RequestBody UserDTO userDTO) {
        if (userDTO == null) {
            return new ResponseEntity<>(NOT_ACCEPTABLE);
        }
        log.info("Add user " + userDTO.toString());
        Optional<User> userByEmail = userRepository.findByEmail(userDTO.getEmail());
        if (userByEmail.isPresent()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        User user = new User();
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        user.setSecret(String.valueOf(System.nanoTime()));

        userRepository.save(user);
        return new ResponseEntity<>(userDTO, OK);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserDTO userDTO) {
        if (userDTO == null) {
            return new ResponseEntity<>(NOT_ACCEPTABLE);
        }
        log.info("Logging in " + userDTO.toString());
        Optional<User> userByEmail = userRepository.findByEmail(userDTO.getEmail()).filter(user -> user.getPassword().equals(userDTO.getPassword()));
        return userByEmail.map(user -> new ResponseEntity<>(user.getSecret(), OK))
                .orElseGet(() -> new ResponseEntity<>(NOT_FOUND));
    }
}
