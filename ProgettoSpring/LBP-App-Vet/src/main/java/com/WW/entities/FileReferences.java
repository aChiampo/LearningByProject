package com.WW.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "FILE_REFERENCE")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class FileReferences {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;
    @Column(name = "Original_File_Name", nullable = false)
    private String originalFileName;
    @Column(name = "Stored_File_Name", nullable = false)
    private String storedFileName;
    @Column(name = "Mime_type", nullable = false)
    private String mimeType;
    @Column(name = "SSize", nullable = false)
    private long ssize;
    @Column(name = "Storage_Path", nullable = false)
    private String storagePath;
    @ManyToOne
    @JoinColumn(name = "Owner", nullable = false)
    private Utente owner;
    @Column(name = "Upload_Date", nullable = false)
    private LocalDateTime uploadDate;
    @Column(name = "Is_Deleted", nullable = false)
    private boolean isDeleted;
}
