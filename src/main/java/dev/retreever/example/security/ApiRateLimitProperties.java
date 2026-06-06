package dev.retreever.example.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "retreever.example.rate-limit")
public class ApiRateLimitProperties {

    private final Limit request = new Limit(20, 500);
    private final Limit upload = new Limit(2, 20);

    public Limit getRequest() {
        return request;
    }

    public Limit getUpload() {
        return upload;
    }

    public static class Limit {

        private int perMinute;
        private int perDay;

        public Limit() {
        }

        public Limit(int perMinute, int perDay) {
            this.perMinute = perMinute;
            this.perDay = perDay;
        }

        public int getPerMinute() {
            return perMinute;
        }

        public void setPerMinute(int perMinute) {
            this.perMinute = perMinute;
        }

        public int getPerDay() {
            return perDay;
        }

        public void setPerDay(int perDay) {
            this.perDay = perDay;
        }
    }
}
