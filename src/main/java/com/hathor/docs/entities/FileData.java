package com.hathor.docs.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Builder
@DynamicInsert
@Table(name = "files")
@AllArgsConstructor
@NoArgsConstructor
public class FileData {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "files_id_seq")
    @SequenceGenerator(name = "files_id_seq", sequenceName = "files_id_seq", allocationSize = 1)
    private Long id;

    private UUID dataId;
    private UUID fileId;
    private String originalName;
    private BigDecimal sizeKb;
    private String mimeType;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}
