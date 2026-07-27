package com.WW.controllers;

import com.WW.entities.FileReferences;
import com.WW.entities.Utente;
import com.WW.services.FileReferencesService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/file-reference")
@CrossOrigin(origins = "*")
public class FileReferenceController {

    private final FileReferencesService fileReferencesService;

    public FileReferenceController(FileReferencesService fileReferencesService) {
        this.fileReferencesService = fileReferencesService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<List<FileReferences>> getAllFileReferences() {
        try {
            List<FileReferences> list = fileReferencesService.visualizzaTuttiFileReferences();
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPTIONIST', 'CLIENTE')")
    public ResponseEntity<FileReferences> getById(@PathVariable Integer id, Authentication authentication) {
        try {
            Optional<FileReferences> fr = fileReferencesService.getFileReferenceById(id);
            if (fr.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            if (!canAccessFile(fr.get(), authentication)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.ok(fr.get());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}/download")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPTIONIST', 'CLIENTE')")
    public ResponseEntity<Resource> downloadById(@PathVariable Integer id, Authentication authentication) {
        try {
            Optional<FileReferences> fileReference = fileReferencesService.getFileReferenceById(id);

            if (fileReference.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            FileReferences file = fileReference.get();
            if (!canAccessFile(file, authentication)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            Path storagePath = Paths.get(file.getStoragePath()).toAbsolutePath().normalize();
            Path filePath = Files.isDirectory(storagePath)
                    ? storagePath.resolve(file.getStoredFileName()).normalize()
                    : storagePath;

            if (!Files.exists(filePath) || !Files.isReadable(filePath)) {
                return ResponseEntity.notFound().build();
            }

            Resource resource = new UrlResource(filePath.toUri());

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(file.getMimeType()))
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + file.getOriginalFileName() + "\"")
                    .body(resource);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/owner/{ownerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPTIONIST', 'CLIENTE')")
    public ResponseEntity<List<FileReferences>> getByOwner(@PathVariable Integer ownerId, Authentication authentication) {
        try {
            if (!canAccessOwner(ownerId, authentication)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            Utente owner = new Utente();
            owner.setId(ownerId);
            List<FileReferences> list = fileReferencesService.findByOwner(owner);
            return ResponseEntity.ok(list);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/before")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<List<FileReferences>> getBefore(@RequestParam LocalDateTime date) {
        try {
            List<FileReferences> list = fileReferencesService.findByUploadDateBefore(date);
            return ResponseEntity.ok(list);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/after")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<List<FileReferences>> getAfter(@RequestParam LocalDateTime date) {
        try {
            List<FileReferences> list = fileReferencesService.findByUploadDateAfter(date);
            return ResponseEntity.ok(list);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FileReferences> createFileReference(@RequestBody FileReferences fileReferences) {
        try {
            FileReferences created = fileReferencesService.salvaFileReference(fileReferences);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FileReferences> updateFileReference(@PathVariable Integer id, @RequestBody FileReferences fileReferences) {
        try {
            fileReferences.setId(id);
            FileReferences updated = fileReferencesService.aggiornaFileReference(fileReferences);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteFileReference(@PathVariable Integer id) {
        try {
            fileReferencesService.eliminaFileReference(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private boolean canAccessFile(FileReferences fileReference, Authentication authentication) {
        if (hasAnyRole(authentication, "ROLE_ADMIN", "ROLE_RECEPTIONIST", "ROLE_VETERINARIO")) {
            return true;
        }

        return fileReference.getOwner() != null && canAccessOwner(fileReference.getOwner().getId(), authentication);
    }

    private boolean canAccessOwner(Integer ownerId, Authentication authentication) {
        if (hasAnyRole(authentication, "ROLE_ADMIN", "ROLE_RECEPTIONIST", "ROLE_VETERINARIO")) {
            return true;
        }

        return hasAnyRole(authentication, "ROLE_CLIENTE") && Integer.valueOf(authentication.getName()).equals(ownerId);
    }

    private boolean hasAnyRole(Authentication authentication, String... roles) {
        List<String> requestedRoles = List.of(roles);
        return authentication != null && authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(requestedRoles::contains);
    }
}
