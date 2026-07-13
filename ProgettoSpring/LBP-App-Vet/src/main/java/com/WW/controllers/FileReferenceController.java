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
    public ResponseEntity<List<FileReferences>> getAllFileReferences() {
        try {
            List<FileReferences> list = fileReferencesService.visualizzaTuttiFileReferences();
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<FileReferences> getById(@PathVariable Integer id) {
        try {
            Optional<FileReferences> fr = fileReferencesService.getFileReferenceById(id);
            return fr.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadById(@PathVariable Integer id) {
        try {
            Optional<FileReferences> fileReference = fileReferencesService.getFileReferenceById(id);

            if (fileReference.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            FileReferences file = fileReference.get();
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
    public ResponseEntity<List<FileReferences>> getByOwner(@PathVariable Integer ownerId) {
        try {
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
}
