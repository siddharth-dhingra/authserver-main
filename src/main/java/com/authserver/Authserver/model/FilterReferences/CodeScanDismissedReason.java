package com.authserver.Authserver.model.FilterReferences;

public enum CodeScanDismissedReason {
    FALSE_POSITIVE("false positive"),
    WONT_FIX("won't fix"),
    USED_IN_TESTS("used in tests");

    private final String githubValue;
    CodeScanDismissedReason(String githubValue) {
        this.githubValue = githubValue;
    }
    public String getGithubValue() {
        return githubValue;
    }
}