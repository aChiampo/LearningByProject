package com.WW.repositories;

import com.WW.entities.FileReferences;
import com.WW.entities.Utente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface FileReferencesRepo extends JpaRepository<FileReferences, Integer> {

    List<FileReferences> findByOwner(Utente owner);

    List<FileReferences> findByUploadDateBefore(LocalDateTime uploadDate);

    List<FileReferences> findByUploadDateAfter(LocalDateTime uploadDate);
}
