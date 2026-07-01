package com.buildsage.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PipelineWorkflowIT {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void rejectsInvalidLogIngestion() throws Exception {
        String token = login();
        Map<String, Object> body = Map.of(
                "externalId", "bad-1",
                "branch", "main",
                "commitSha", "abc",
                "status", "FAILED",
                "startedAt", Instant.now().toString(),
                "logs", java.util.List.of());

        mockMvc.perform(post("/api/projects/20000000-0000-0000-0000-000000000001/pipeline-runs")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void idempotencyKeyReturnsExistingPipelineRun() throws Exception {
        String token = login();
        Map<String, Object> body = Map.of(
                "externalId",
                "gh-idempotent-1",
                "branch",
                "main",
                "commitSha",
                "abc123",
                "status",
                "SUCCESS",
                "startedAt",
                Instant.now().toString(),
                "logs",
                java.util.List.of("build ok"));

        String firstResponse = mockMvc.perform(post("/api/projects/20000000-0000-0000-0000-000000000001/pipeline-runs")
                        .header("Authorization", "Bearer " + token)
                        .header("Idempotency-Key", "pipeline-delivery-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String secondResponse = mockMvc.perform(post("/api/projects/20000000-0000-0000-0000-000000000001/pipeline-runs")
                        .header("Authorization", "Bearer " + token)
                        .header("Idempotency-Key", "pipeline-delivery-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(objectMapper.readTree(secondResponse).get("data").get("id").asText())
                .isEqualTo(objectMapper
                        .readTree(firstResponse)
                        .get("data")
                        .get("id")
                        .asText());
    }

    @Test
    void searchesLogsByTextAndLineRange() throws Exception {
        String token = login();
        Map<String, Object> body = Map.of(
                "externalId",
                "gh-search-1",
                "branch",
                "main",
                "commitSha",
                "abc123",
                "status",
                "FAILED",
                "startedAt",
                Instant.now().toString(),
                "logs",
                java.util.List.of("compile started", "npm ERR dependency conflict", "docker build skipped"));

        String ingestResponse = mockMvc.perform(post("/api/projects/20000000-0000-0000-0000-000000000001/pipeline-runs")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        String runId =
                objectMapper.readTree(ingestResponse).get("data").get("id").asText();

        mockMvc.perform(get("/api/pipeline-runs/" + runId + "/logs?q=dependency&fromLine=2&toLine=2")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalItems").value(1))
                .andExpect(jsonPath("$.data.items[0].lineNumber").value(2));
    }

    @Test
    void queuedAnalysisWorkflowCompletesAndPersistsResult() throws Exception {
        String token = login();
        Map<String, Object> body = Map.of(
                "externalId",
                "gh-queued-1",
                "branch",
                "main",
                "commitSha",
                "abc123",
                "status",
                "FAILED",
                "startedAt",
                Instant.now().toString(),
                "finishedAt",
                Instant.now().toString(),
                "jobs",
                java.util.List.of(Map.of("name", "unit-tests", "stage", "test", "status", "FAILED")),
                "logs",
                java.util.List.of(
                        "stage=test", "AssertionError: expected 200 but got 500", "There were failing tests"));

        String ingestResponse = mockMvc.perform(post("/api/projects/20000000-0000-0000-0000-000000000001/pipeline-runs")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        String runId =
                objectMapper.readTree(ingestResponse).get("data").get("id").asText();

        mockMvc.perform(post("/api/pipeline-runs/" + runId + "/analyze").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("QUEUED"));

        AtomicReference<JsonNode> analysis = new AtomicReference<>();
        Awaitility.await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            String response = mockMvc.perform(
                            get("/api/pipeline-runs/" + runId + "/analysis").header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
            analysis.set(objectMapper.readTree(response));
            assertThat(analysis.get().get("data").get("status").asText()).isEqualTo("COMPLETED");
        });
        assertThat(analysis.get().get("data").get("failureType").asText()).isEqualTo("TEST_FAILURE");
        assertThat(analysis.get().get("data").get("reviewState").asText()).isEqualTo("REVIEW_REQUIRED");

        mockMvc.perform(get("/api/pipeline-runs/" + runId + "/analysis/queue")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.analysisStatus").value("COMPLETED"))
                .andExpect(jsonPath("$.data.jobType").value("LOG_ANALYSIS"));
    }

    private String login() throws Exception {
        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"admin@buildsage.dev\",\"password\":\"password\"}"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        JsonNode root = objectMapper.readTree(response);
        assertThat(root.get("success").asBoolean()).isTrue();
        return root.get("data").get("token").asText();
    }
}
