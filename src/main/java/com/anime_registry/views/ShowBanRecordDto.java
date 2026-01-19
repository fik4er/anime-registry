package com.anime_registry.views;

import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
public class ShowBanRecordDto implements Serializable {
    private Integer id;
    private LocalDate banDate;
    private String reason;
    private String legalDocumentUrl;
    private String sourceRegistryNumber;
    public ShowBanRecordDto() {}
}