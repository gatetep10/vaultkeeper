package com.vaultkeeper.vaultKeeper.repository;

import com.vaultkeeper.vaultKeeper.model.VaultEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VaultEntryRepository extends JpaRepository<VaultEntry, Long> {
    List<VaultEntry> findByUsername(String username);
    List<VaultEntry> findByUsernameAndPlatformNameContainingIgnoreCase(String username, String keyword);
}