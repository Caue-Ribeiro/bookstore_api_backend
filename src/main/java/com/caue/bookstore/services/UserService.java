package com.caue.bookstore.services;

import com.caue.bookstore.dto.UserDTO;
import com.caue.bookstore.entities.User;
import com.caue.bookstore.enums.UserRole;
import com.caue.bookstore.exceptions.DatabaseException;
import com.caue.bookstore.exceptions.InvalidResetTokenException;
import com.caue.bookstore.exceptions.ResourceNotFoundException;
import com.caue.bookstore.repositories.UserRepository;
import com.caue.bookstore.utils.PasswordValidator;
import com.caue.bookstore.utils.UserMapper;

import jakarta.validation.constraints.NotNull;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final AuditLogService auditLogService;

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final long LOCK_DURATION_MS = 15 * 60 * 1000; 
    private static final long RESET_TOKEN_VALIDITY_MS = 60 * 60 * 1000; 

    private final String NOT_FOUND_MSG = "User not found.";

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder, UserMapper userMapper, AuditLogService auditLogService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.auditLogService = auditLogService;
    }

    @NotNull
    @Override
    public UserDetails loadUserByUsername(@NotNull String username) throws UsernameNotFoundException {
        User userEntity = repository.findByEmail(username).orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_MSG));

        return checkUserDeletion(userEntity);
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        return repository.findAllUsers(pageable).map(UserDTO::new);
    }

    @Transactional(readOnly = true)
    public UserDTO getUserById(UUID id) {
        User userEntity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_MSG));


        User user = checkUserDeletion(userEntity);

        return new UserDTO(user);

    }

    @Transactional(readOnly = true)
    public Page<UserDTO> searchUser(String query, Pageable pageable){

        return repository.searchUser(query, pageable).map(UserDTO::new);
    }

    @Transactional
    public UserDTO insert(UserDTO dto) {
        
        if (dto.getPassword() != null) {
            PasswordValidator.validatePasswordStrength(dto.getPassword());
        }

        User entity = new User();
        dtoToEntity(dto, entity);
        entity = repository.save(entity);

        return new UserDTO(entity);
    }

    @Transactional
    public void handleSuccessfulLogin(User user) {
        user.setFailedLoginAttempts(0);
        user.setLastLogin(System.currentTimeMillis());
        user.setIsLocked(false);
        user.setLockExpirationTime(null);
        repository.save(user);

        
        auditLogService.logAction(user, "LOGIN", "Successful login");
    }

    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Map<String,String> handleFailedLoginAttempt(User user) {
        Map<String,String> responseMsg = new HashMap<>();

        int attempts = (user.getFailedLoginAttempts() != null ? user.getFailedLoginAttempts() : 0) + 1;
        user.setFailedLoginAttempts(attempts);

        if (attempts >= MAX_FAILED_ATTEMPTS) {
            user.setIsLocked(true);
            user.setLockExpirationTime(System.currentTimeMillis() + LOCK_DURATION_MS);
            repository.saveAndFlush(user);
            auditLogService.logAction(user, "LOGIN_FAILED", "Account locked after " + MAX_FAILED_ATTEMPTS + " failed attempts");
            
            
            responseMsg.put("status", "Account locked for "+ (LOCK_DURATION_MS / 1000/ 60 + " minutes due to " +
                    "too many" +
                    " failed login attempts"));

            return responseMsg;

        }

        repository.saveAndFlush(user);
        auditLogService.logAction(user, "LOGIN_FAILED", "Failed login attempt (" + attempts + "/" + MAX_FAILED_ATTEMPTS + ")");
        responseMsg.put("message", "Failed login attempt (" +attempts+"/" + MAX_FAILED_ATTEMPTS+")");

        return responseMsg;
    }

    
    @Transactional
    public void requestPasswordReset(String email) {
        User user = repository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User with email " + email + " not found."));

        String resetToken = PasswordValidator.generateResetToken();
        user.setPasswordResetToken(resetToken);
        user.setResetTokenExpiration(System.currentTimeMillis() + RESET_TOKEN_VALIDITY_MS);
        repository.save(user);

        auditLogService.logAction(user, "PASSWORD_RESET_REQUESTED", "Password reset token generated");

        
        System.out.println("Password reset token for user " + email + ": " + resetToken);
    }

    
    @Transactional
    public void resetPasswordWithToken(String token, String newPassword) {
        
        PasswordValidator.validatePasswordStrength(newPassword);

        User user = repository.findByPasswordResetToken(token)
                .orElseThrow(() -> new InvalidResetTokenException("Invalid or expired reset token."));

        
        if (PasswordValidator.isTokenExpired(user.getResetTokenExpiration())) {
            throw new InvalidResetTokenException("Reset token has expired. Please request a new password reset.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordResetToken(null);
        user.setResetTokenExpiration(null);
        user.setFailedLoginAttempts(0);
        user.setIsLocked(false);
        user.setLockExpirationTime(null);

        repository.save(user);
        auditLogService.logAction(user, "PASSWORD_RESET", "Password successfully reset");
    }

    @Transactional
    public UserDTO editUser(UUID id, UserDTO dto) {
        User userEntity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_MSG));

        if (dto.getPassword() != null) {
            PasswordValidator.validatePasswordStrength(dto.getPassword());
            dto.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        userMapper.updateUserFromDto(dto, userEntity);


        userEntity = repository.save(userEntity);

        auditLogService.logAction(userEntity, "USER_UPDATED", "User profile updated");
        return new UserDTO(userEntity);
    }

    @Transactional
    public void deleteUser(UUID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(NOT_FOUND_MSG);
        } else if (repository.getReferenceById(id).getEmail().equals("caue@email.com")) {
            throw new DatabaseException("Main administrator can not be deleted");
        }
        try {
            User user = repository.getReferenceById(id);
            auditLogService.logAction(user, "ACCOUNT_DELETED", "User account deleted");
            repository.deleteUserById(id);
            repository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Data integrity has been violated.");
        }
    }

    //HELPER METHODS

    private void dtoToEntity(UserDTO dto, User entity) {
        entity.setName(dto.getName());
        entity.setLastName(dto.getLastName());
        entity.setBirthdate(dto.getBirthdate());
        entity.setEmail(dto.getEmail());

        if (dto.getRole() != null) {
            entity.setRole(dto.getRole());
        } else {
            entity.setRole(UserRole.CLIENT);
        }

        if (dto.getPassword() != null) {
            entity.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        
        entity.setFailedLoginAttempts(0);
        entity.setIsLocked(false);
    }

    private User checkUserDeletion(User user){

        if (user.getDeleted_at() != null) {
            throw new ResourceNotFoundException(NOT_FOUND_MSG);
        }
        return user;

    }
}

