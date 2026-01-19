package com.anime_registry.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "ban_record")
@Getter
@Setter
public class BanRecord extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "anime_id", nullable = false)
    private Anime anime;

    @Column(name = "ban_date")
    private LocalDate banDate;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Column(name = "legal_document_url")
    private String legalDocumentUrl;

    @Column(name = "source_registry_number")
    private String sourceRegistryNumber;

    public BanRecord() {}
}