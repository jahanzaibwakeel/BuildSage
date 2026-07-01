package com.buildsage.security;

import com.buildsage.exception.ForbiddenException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class WebhookSignatureService {
    private final String githubSecret;

    public WebhookSignatureService(@Value("${webhooks.github.secret:}") String githubSecret) {
        this.githubSecret = githubSecret;
    }

    public void verifyGithubSignature(String payload, String signatureHeader) {
        if (githubSecret == null || githubSecret.isBlank()) {
            throw new ForbiddenException("GitHub webhook secret is not configured");
        }
        if (signatureHeader == null || !signatureHeader.startsWith("sha256=")) {
            throw new ForbiddenException("Invalid GitHub webhook signature");
        }
        String expected = "sha256=" + hmacSha256(payload);
        if (!MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.UTF_8), signatureHeader.getBytes(StandardCharsets.UTF_8))) {
            throw new ForbiddenException("Invalid GitHub webhook signature");
        }
    }

    private String hmacSha256(String payload) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(githubSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return HexFormat.of().formatHex(mac.doFinal(payload.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new ForbiddenException("Unable to verify GitHub webhook signature");
        }
    }
}
