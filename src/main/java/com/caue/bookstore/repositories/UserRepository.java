package com.caue.bookstore.repositories;


import com.caue.bookstore.dto.UserDTO;
import com.caue.bookstore.entities.User;
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
}
