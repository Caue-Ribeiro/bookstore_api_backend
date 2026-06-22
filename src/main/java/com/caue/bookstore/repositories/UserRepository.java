package com.caue.bookstore.repositories;


import com.caue.bookstore.dto.UserDTO;
import com.caue.bookstore.entities.User;
import com.caue.bookstore.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {


    Optional<User> findByUsername(String username);

    Optional<User>  findUserByName(String username);


    @Modifying
    @Query(nativeQuery = true, value = """
                DELETE FROM bs_user 
                WHERE id = :id
            """)
    void deleteUserById(@Param("id") UUID id);


    @Query("""
           SELECT DISTINCT u
           FROM User u
""")
    Page<User> findAllUsers(Pageable pageable);

    Optional<User> findByEmail(String email);

    Optional<User> findByPasswordResetToken(String token);

    @Query(nativeQuery = true, value = """
    SELECT * 
    FROM bs_user
    WHERE email = :username AND role = :role
""")
    User findUserByAuthorities(String username, String role);

    @Query("""
    SELECT u FROM User u WHERE LOWER(u.name) LIKE LOWER(CONCAT('%',:query,'%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%',:query,'%')) OR LOWER(u.lastName) LIKE LOWER(CONCAT('%',:query,'%')) 
""")
    Page<User> searchUser(@Param("query") String query, Pageable pageable);
}
