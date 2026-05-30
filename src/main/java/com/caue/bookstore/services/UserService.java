package com.caue.bookstore.services;

import com.caue.bookstore.dto.UserDTO;
import com.caue.bookstore.entities.Role;
import com.caue.bookstore.entities.User;
import com.caue.bookstore.exceptions.ResourceNotFoundException;
import com.caue.bookstore.repositories.RoleRepository;
import com.caue.bookstore.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService implements UserDetailsService {

    private UserRepository repository;
    private PasswordEncoder passwordEncoder;
    private RoleRepository roleRepository;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {


        return repository.findUserByName(username).orElseThrow(() -> new ResourceNotFoundException("User not found."));


    }

    public UserDTO insert(UserDTO dto) {

        User entity = new User();
        List<Role> roles = new ArrayList<>();

        entity.setName(dto.getName());
        entity.setLastName(dto.getLastName());
        entity.setBirthdate(dto.getBirthdate());
        entity.setEmail(dto.getEmail());
        entity.setPassword(passwordEncoder.encode(dto.getPassword()));

        dto.getRoles().forEach(roleDTO -> {
            Role userRole = roleRepository.findByAuthority(roleDTO.getAuthority());
            roles.add(userRole);
        });

        entity.getRoles().addAll(roles);

        entity = repository.save(entity);

        return new UserDTO(entity);
    }
}
