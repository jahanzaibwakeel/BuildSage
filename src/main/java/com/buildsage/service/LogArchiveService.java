package com.buildsage.service;

import com.buildsage.domain.PipelineRun;
import com.buildsage.dto.PipelineDtos.LogArchiveResponse;
import org.springframework.stereotype.Service;

@Service
public class LogArchiveService {
    public LogArchiveResponse describe(PipelineRun run) {
        String archiveUri = run.getLogArchiveUri();
        return new LogArchiveResponse(
                run.getId(),
                archiveUri != null && !archiveUri.isBlank(),
                storageProvider(archiveUri),
                archiveUri,
                "SHA-256",
                run.getLogDigestSha256(),
                run.getLogLineCount());
    }

    public String storageProvider(String archiveUri) {
        if (archiveUri == null || archiveUri.isBlank()) {
            return "NONE";
        }
        String normalized = archiveUri.toLowerCase();
        if (normalized.startsWith("s3://")) {
            return "S3";
        }
        if (normalized.startsWith("gs://")) {
            return "GCS";
        }
        if (normalized.startsWith("minio://")) {
            return "MINIO";
        }
        if (normalized.startsWith("http://") || normalized.startsWith("https://")) {
            return "HTTP";
        }
        return "UNKNOWN";
    }
}
