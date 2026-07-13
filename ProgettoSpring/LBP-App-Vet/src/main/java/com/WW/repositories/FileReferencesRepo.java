package com.WW.repositories;

import com.WW.entities.FileReferences;
import com.WW.entities.Utente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FileReferencesRepo extends JpaRepository<FileReferences, Integer> {
    List<FileReferences> findByIsDeletedFalse();
    Optional<FileReferences> findByIdAndIsDeletedFalse(Integer id);

    List<FileReferences> findByOwner(Utente owner);
    List<FileReferences> findByOwnerAndIsDeletedFalse(Utente owner);

    List<FileReferences> findByUploadDateBefore(LocalDateTime uploadDate);
    List<FileReferences> findByUploadDateBeforeAndIsDeletedFalse(LocalDateTime uploadDate);

    List<FileReferences> findByUploadDateAfter(LocalDateTime uploadDate);
    List<FileReferences> findByUploadDateAfterAndIsDeletedFalse(LocalDateTime uploadDate);
}
