package dev.retreever.example.security;

public record RateLimitDecision(
        boolean allowed,
        int limit,
        int current,
        String windowLabel,
        String actionLabel
) {

    public static RateLimitDecision allowed(int limit, int current, String windowLabel, String actionLabel) {
        return new RateLimitDecision(true, limit, current, windowLabel, actionLabel);
    }

    public static RateLimitDecision rejected(int limit, int current, String windowLabel, String actionLabel) {
        return new RateLimitDecision(false, limit, current, windowLabel, actionLabel);
    }

    public String rejectionReason() {
        return "Too many " + actionLabel + " requests for this device and IP. Limit is "
                + limit + " per " + windowLabel + ".";
    }
}
