package com.caue.bookstore.services;

import com.caue.bookstore.dto.UserDTO;
import com.caue.bookstore.entities.User;
import com.caue.bookstore.exceptions.DatabaseException;
import com.caue.bookstore.exceptions.ResourceNotFoundException;
import com.caue.bookstore.repositories.UserRepository;
import com.caue.bookstore.utils.UserMapper;
import org.jetbrains.annotations.NotNull;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.UUID;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    private final String NOT_FOUND_MSG = "User not found.";

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    @NotNull
    @Override
    public UserDetails loadUserByUsername(@NotNull String username) throws UsernameNotFoundException {


        return repository.findByEmail(username).orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_MSG));


    }

    @Transactional(readOnly = true)
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        return repository.findAllUsers(pageable).map(UserDTO::new);
    }

    @Transactional(readOnly = true)
    public UserDTO getUserById(UUID id) {
        User entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_MSG));

        return new UserDTO(entity);
    }

    @Transactional
    public UserDTO insert(UserDTO dto) {

        User entity = new User();


        dtoToEntity(dto, entity);


        entity = repository.save(entity);

        return new UserDTO(entity);
    }


    @Transactional
    public UserDTO editUser(UUID id, UserDTO dto) {

        User userEntity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_MSG));

        userMapper.updateUserFromDto(dto,userEntity);

        userEntity = repository.save(userEntity);

        return new UserDTO(userEntity);
    }


    @Transactional
    public void deleteUser(UUID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(NOT_FOUND_MSG);
        }
        try {

            repository.deleteUserById(id);
            repository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Data integrity has been violated.");
        }
    }

    private void dtoToEntity(UserDTO dto, User entity) {
        entity.setName(dto.getName());
        entity.setLastName(dto.getLastName());
        entity.setBirthdate(dto.getBirthdate());
        entity.setEmail(dto.getEmail());
        entity.setRole(dto.getRole());

        if (dto.getPassword() != null) {
            entity.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

    }

}

