package com.vaultkeeper.vaultKeeper.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "vault_entries")
public class VaultEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Platform name is required")
    @Column(nullable = false)
    private String platformName;

    @NotBlank(message = "Secret word is required")
    @Size(min = 4, message = "Secret word must be at least 4 characters")
    @Column(nullable = false)
    private String secretWord;

    @Column(nullable = false)
    private String username;

    public VaultEntry() {}

    public VaultEntry(String platformName, String secretWord, String username) {
        this.platformName = platformName;
        this.secretWord = secretWord;
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public String getSecretWord() {
        return secretWord;
    }

    public void setSecretWord(String secretWord) {
        this.secretWord = secretWord;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}