package com.WW.services;

import com.WW.entities.FileReferences;
import com.WW.entities.Utente;
import com.WW.repositories.FileReferencesRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FileReferencesService {

    private final FileReferencesRepo fileReferencesRepo;

    public FileReferencesService(FileReferencesRepo fileReferencesRepo) {
        this.fileReferencesRepo = fileReferencesRepo;
    }

    /**
     * Visualizza tutti i file reference
     */
    public List<FileReferences> visualizzaTuttiFileReferences() {
        return fileReferencesRepo.findAll();
    }

    /**
     * Ricerca per owner
     */
    public List<FileReferences> findByOwner(Utente owner) {
        if (owner == null || owner.getId() == null || owner.getId() <= 0) {
            throw new IllegalArgumentException("Owner non valido");
        }
        return fileReferencesRepo.findByOwner(owner);
    }

    /**
     * Ricerca file caricati prima di uploadDate
     */
    public List<FileReferences> findByUploadDateBefore(LocalDateTime uploadDate) {
        if (uploadDate == null) {
            throw new IllegalArgumentException("La data di filtro non può essere null");
        }
        return fileReferencesRepo.findByUploadDateBefore(uploadDate);
    }

    /**
     * Ricerca file caricati dopo uploadDate
     */
    public List<FileReferences> findByUploadDateAfter(LocalDateTime uploadDate) {
        if (uploadDate == null) {
            throw new IllegalArgumentException("La data di filtro non può essere null");
        }
        return fileReferencesRepo.findByUploadDateAfter(uploadDate);
    }

    /**
     * Recupera per ID
     */
    public Optional<FileReferences> getFileReferenceById(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("L'ID non può essere null o negativo");
        }
        return fileReferencesRepo.findById(id);
    }

    /**
     * Salva nuova FileReference
     */
    public FileReferences salvaFileReference(FileReferences fileReferences) {
        validateFileReferenceForSave(fileReferences);
        return fileReferencesRepo.save(fileReferences);
    }

    /**
     * Aggiorna FileReference esistente
     */
    public FileReferences aggiornaFileReference(FileReferences fileReferences) {
        if (fileReferences == null) {
            throw new IllegalArgumentException("FileReference non può essere null");
        }
        if (fileReferences.getId() == null || fileReferences.getId() <= 0) {
            throw new IllegalArgumentException("L'ID è obbligatorio per l'aggiornamento");
        }
        if (!fileReferencesRepo.existsById(fileReferences.getId())) {
            throw new IllegalArgumentException("FileReference con ID " + fileReferences.getId() + " non trovato");
        }
        validateFileReferenceForSave(fileReferences);
        return fileReferencesRepo.save(fileReferences);
    }

    /**
     * Elimina FileReference
     */
    public void eliminaFileReference(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("L'ID non può essere null o negativo");
        }
        if (!fileReferencesRepo.existsById(id)) {
            throw new IllegalArgumentException("FileReference con ID " + id + " non trovato");
        }
        fileReferencesRepo.deleteById(id);
    }

    private void validateFileReferenceForSave(FileReferences f) {
        if (f == null) {
            throw new IllegalArgumentException("FileReference non può essere null");
        }
        if (f.getOriginalFileName() == null || f.getOriginalFileName().trim().isEmpty()) {
            throw new IllegalArgumentException("OriginalFileName è obbligatorio");
        }
        if (f.getStoredFileName() == null || f.getStoredFileName().trim().isEmpty()) {
            throw new IllegalArgumentException("StoredFileName è obbligatorio");
        }
        if (f.getMimeType() == null || f.getMimeType().trim().isEmpty()) {
            throw new IllegalArgumentException("MimeType è obbligatorio");
        }
        if (f.getSsize() <= 0) {
            throw new IllegalArgumentException("La dimensione del file deve essere maggiore di zero");
        }
        if (f.getStoragePath() == null || f.getStoragePath().trim().isEmpty()) {
            throw new IllegalArgumentException("StoragePath è obbligatorio");
        }
        if (f.getOwner() == null || f.getOwner().getId() == null || f.getOwner().getId() <= 0) {
            throw new IllegalArgumentException("Owner è obbligatorio e deve avere un ID valido");
        }
        if (f.getUploadDate() == null) {
            throw new IllegalArgumentException("UploadDate è obbligatoria");
        }
    }
}
