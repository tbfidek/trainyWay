package com.example.application.data.service;

import com.example.application.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {

    User getByUsername(String username);

    User getByEmail(String email);


    @Query(value = "SELECT * from \"create_reset_query\"(?1, ?2)", nativeQuery = true)
    void createResetQuery(String email, String code);

    @Query(value = "SELECT count(*) from resetcodes WHERE code = ?1", nativeQuery = true)
    Integer checkResetCode(String code);

    @Query(value = "SELECT * from \"delete_query\"(?1)", nativeQuery = true)
    void deleteResetQuery(String code);

    @Query(value = "SELECT email FROM resetcodes where code = ?1", nativeQuery = true)
    String getEmailByCode(String code);
}
