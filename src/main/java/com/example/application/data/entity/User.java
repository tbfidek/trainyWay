package com.example.application.data.entity;

import com.example.application.data.Role;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;

@Entity
@Table(name = "users")
public class User extends AbstractEntity {

    private String username;

    private String email;
    private String passwordSalt;
    private String passwordHash;
    private Role role;

    public User(){}

    public User(String username, String email, String password, Role role){
        this.username = username;
        this.email = email;
        this.role = role;
        this.passwordSalt = RandomStringUtils.randomAlphabetic(32);
        this.passwordHash = DigestUtils.sha1Hex(password + passwordSalt);
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public boolean checkPassword(String password){
        return DigestUtils.sha1Hex(password + passwordSalt).equals(passwordHash);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordSalt() {
        return passwordSalt;
    }

    public void setPasswordSalt(String passwordSalt) {
        this.passwordSalt = passwordSalt;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
