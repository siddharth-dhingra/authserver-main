package com.authserver.Authserver.model;

public enum SecretScanResolution {
    FALSE_POSITIVE("false_positive"),
    WONT_FIX("wont_fix"),
    REVOKED("revoked"),
    USED_IN_TESTS("used_in_tests");

    private final String githubValue;
    SecretScanResolution(String githubValue) {
        this.githubValue = githubValue;
    }
    public String getGithubValue() {
        return githubValue;
    }
}