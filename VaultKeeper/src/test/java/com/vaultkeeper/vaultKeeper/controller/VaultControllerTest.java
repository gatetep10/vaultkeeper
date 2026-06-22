package com.vaultkeeper.vaultKeeper.controller;

import com.vaultkeeper.vaultKeeper.config.SecurityConfig;
import com.vaultkeeper.vaultKeeper.model.VaultEntry;
import com.vaultkeeper.vaultKeeper.repository.VaultEntryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VaultController.class)
@Import(SecurityConfig.class)
public class VaultControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VaultEntryRepository vaultEntryRepository;

    @Test
    @WithMockUser(username = "24RP00849")
    public void testShowVault_ReturnsVaultPageWithEntries() throws Exception {
        VaultEntry entry1 = new VaultEntry("Gmail", "MySecret123", "24RP00849");
        entry1.setId(1L);
        VaultEntry entry2 = new VaultEntry("Facebook", "Pass456", "24RP00849");
        entry2.setId(2L);
        List<VaultEntry> entries = Arrays.asList(entry1, entry2);

        when(vaultEntryRepository.findByUsername("24RP00849")).thenReturn(entries);

        mockMvc.perform(get("/vault"))
                .andExpect(status().isOk())
                .andExpect(view().name("vault"))
                .andExpect(model().attributeExists("entries"));
    }

    @Test
    @WithMockUser(username = "24RP00849")
    public void testShowAddForm_ReturnsAddEntryPage() throws Exception {
        mockMvc.perform(get("/vault/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("add-entry"))
                .andExpect(model().attributeExists("vaultEntry"));
    }

    @Test
    @WithMockUser(username = "24RP00849")
    public void testAddEntry_ValidEntry_RedirectsToVault() throws Exception {
        mockMvc.perform(post("/vault/add")
                .with(csrf())
                .param("platformName", "Instagram")
                .param("secretWord", "MySecret123")
                .param("username", "24RP00849"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/vault"));
    }

    @Test
    @WithMockUser(username = "24RP00849")
    public void testAddEntry_InvalidSecretShort_ReturnsFormWithErrors() throws Exception {
        mockMvc.perform(post("/vault/add")
                .with(csrf())
                .param("platformName", "Instagram")
                .param("secretWord", "abc")
                .param("username", "24RP00849"))
                .andExpect(status().isOk())
                .andExpect(view().name("add-entry"))
                .andExpect(model().hasErrors());
    }

    @Test
    public void testUnauthenticatedAccess_RedirectsToLogin() throws Exception {
        mockMvc.perform(get("/vault"))
                .andExpect(status().is3xxRedirection());
    }
}