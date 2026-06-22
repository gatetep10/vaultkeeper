package com.vaultkeeper.vaultKeeper.controller;

import com.vaultkeeper.vaultKeeper.model.VaultEntry;
import com.vaultkeeper.vaultKeeper.repository.VaultEntryRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class VaultController {

    @Autowired
    private VaultEntryRepository vaultEntryRepository;

    @GetMapping("/vault")
    public String showVault(Model model, Principal principal, 
                            @CookieValue(value = "lastPlatform", required = false) String lastPlatformCookie,
                            @RequestParam(value = "q", required = false) String keyword,
                            HttpSession session) {
        
        String username = principal.getName();
        List<VaultEntry> entries;
        
        if (lastPlatformCookie != null && !lastPlatformCookie.isEmpty()) {
            try {
                String decoded = URLDecoder.decode(lastPlatformCookie, StandardCharsets.UTF_8);
                session.setAttribute("prefillPlatform", decoded);
                model.addAttribute("prefillPlatform", decoded);
            } catch (Exception e) {
                model.addAttribute("prefillPlatform", lastPlatformCookie);
            }
        }
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            entries = vaultEntryRepository.findByUsernameAndPlatformNameContainingIgnoreCase(username, keyword);
            model.addAttribute("searchKeyword", keyword);
        } else {
            entries = vaultEntryRepository.findByUsername(username);
        }
        
        List<VaultEntry> maskedEntries = new ArrayList<>();
        for (VaultEntry entry : entries) {
            VaultEntry masked = new VaultEntry();
            masked.setId(entry.getId());
            masked.setPlatformName(entry.getPlatformName());
            masked.setUsername(entry.getUsername());
            String secret = entry.getSecretWord();
            if (secret != null && secret.length() > 2) {
                String maskedSecret = "*".repeat(secret.length() - 2) + secret.substring(secret.length() - 2);
                masked.setSecretWord(maskedSecret);
            } else {
                masked.setSecretWord(secret);
            }
            maskedEntries.add(masked);
        }
        
        model.addAttribute("entries", maskedEntries);
        return "vault";
    }

    @GetMapping("/vault/add")
    public String showAddForm(Model model, Principal principal,
                              @CookieValue(value = "lastPlatform", required = false) String lastPlatformCookie,
                              HttpSession session) {
        
        VaultEntry entry = new VaultEntry();
        
        if (lastPlatformCookie != null && !lastPlatformCookie.isEmpty()) {
            try {
                String decoded = URLDecoder.decode(lastPlatformCookie, StandardCharsets.UTF_8);
                entry.setPlatformName(decoded);
            } catch (Exception e) {
                entry.setPlatformName(lastPlatformCookie);
            }
        } else {
            String sessionPlatform = (String) session.getAttribute("lastPlatform");
            if (sessionPlatform != null && !sessionPlatform.isEmpty()) {
                entry.setPlatformName(sessionPlatform);
            }
        }
        
        model.addAttribute("vaultEntry", entry);
        return "add-entry";
    }

    @PostMapping("/vault/add")
    public String addEntry(@Valid @ModelAttribute("vaultEntry") VaultEntry vaultEntry,
                           BindingResult result, Principal principal,
                           HttpServletResponse response, HttpSession session,
                           RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            return "add-entry";
        }
        
        String username = principal.getName();
        vaultEntry.setUsername(username);
        
        vaultEntryRepository.save(vaultEntry);
        
        try {
            String encodedPlatform = URLEncoder.encode(vaultEntry.getPlatformName(), StandardCharsets.UTF_8);
            Cookie cookie = new Cookie("lastPlatform", encodedPlatform);
            cookie.setMaxAge(7 * 24 * 60 * 60);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            response.addCookie(cookie);
        } catch (Exception e) {
            Cookie cookie = new Cookie("lastPlatform", vaultEntry.getPlatformName().replace(" ", "_"));
            cookie.setMaxAge(7 * 24 * 60 * 60);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            response.addCookie(cookie);
        }
        
        session.setAttribute("lastPlatform", vaultEntry.getPlatformName());
        
        redirectAttributes.addFlashAttribute("successMessage", "Entry added successfully!");
        return "redirect:/vault";
    }

    @GetMapping("/vault/{id}/edit")
    public String showEditForm(@PathVariable("id") Long id, Model model, Principal principal,
                               RedirectAttributes redirectAttributes) {
        
        VaultEntry entry = vaultEntryRepository.findById(id).orElse(null);
        
        if (entry == null || !entry.getUsername().equals(principal.getName())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Entry not found");
            return "redirect:/vault";
        }
        
        model.addAttribute("vaultEntry", entry);
        return "edit-entry";
    }

    @PostMapping("/vault/{id}/edit")
    public String updateEntry(@PathVariable("id") Long id,
                              @Valid @ModelAttribute("vaultEntry") VaultEntry updatedEntry,
                              BindingResult result, Principal principal,
                              RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            return "edit-entry";
        }
        
        VaultEntry existingEntry = vaultEntryRepository.findById(id).orElse(null);
        
        if (existingEntry == null || !existingEntry.getUsername().equals(principal.getName())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Entry not found");
            return "redirect:/vault";
        }
        
        existingEntry.setPlatformName(updatedEntry.getPlatformName());
        existingEntry.setSecretWord(updatedEntry.getSecretWord());
        vaultEntryRepository.save(existingEntry);
        
        redirectAttributes.addFlashAttribute("successMessage", "Entry updated successfully!");
        return "redirect:/vault";
    }

    @PostMapping("/vault/{id}/delete")
    public String deleteEntry(@PathVariable("id") Long id, Principal principal,
                              RedirectAttributes redirectAttributes) {
        
        VaultEntry entry = vaultEntryRepository.findById(id).orElse(null);
        
        if (entry != null && entry.getUsername().equals(principal.getName())) {
            vaultEntryRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Entry deleted successfully!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Entry not found");
        }
        
        return "redirect:/vault";
    }
}