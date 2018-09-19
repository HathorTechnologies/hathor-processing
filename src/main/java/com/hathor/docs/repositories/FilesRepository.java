package com.hathor.docs.repositories;

import com.hathor.docs.entities.FileData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface FilesRepository extends JpaRepository<FileData, Integer> {

    Optional<FileData> findByDataIdAndFileId(UUID dataId, UUID fileId);

    Optional<FileData> findByFileIdAndDataIdIsNull(UUID id);

    List<FileData> findByDataIdIsNull();

    List<FileData> findByDataIdInAndFileIdIn(Set<UUID> dataIds, Set<UUID> fileIds);
}
