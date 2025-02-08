package com.authserver.Authserver.model;

public enum DependabotDismissedReason {
    FIX_STARTED("fix_started"),
    INACCURATE("inaccurate"),
    NO_BANDWIDTH("no_bandwidth"),
    NOT_USED("not_used"),
    TOLERABLE_RISK("tolerable_risk");

    private final String githubValue;
    DependabotDismissedReason(String githubValue) {
        this.githubValue = githubValue;
    }
    public String getGithubValue() {
        return githubValue;
    }
}