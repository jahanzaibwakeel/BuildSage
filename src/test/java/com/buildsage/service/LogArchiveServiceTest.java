package com.buildsage.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class LogArchiveServiceTest {
    private final LogArchiveService service = new LogArchiveService();

    @Test
    void detectsSupportedArchiveProviders() {
        assertThat(service.storageProvider("s3://bucket/log.txt")).isEqualTo("S3");
        assertThat(service.storageProvider("gs://bucket/log.txt")).isEqualTo("GCS");
        assertThat(service.storageProvider("minio://bucket/log.txt")).isEqualTo("MINIO");
        assertThat(service.storageProvider("https://logs.example.com/log.txt")).isEqualTo("HTTP");
        assertThat(service.storageProvider(null)).isEqualTo("NONE");
    }
}
