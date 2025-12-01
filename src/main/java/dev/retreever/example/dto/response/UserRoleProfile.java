package dev.retreever.example.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.time.Instant;

/**
 * Container for role polymorphism (Jackson + OpenAPI)
 */
public final class UserRoleProfile {

    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.PROPERTY,
            property = "role" // "seller" | "customer" | "admin"
    )
    @JsonSubTypes({
            @JsonSubTypes.Type(value = SellerRole.class, name = "seller"),
            @JsonSubTypes.Type(value = CustomerRole.class, name = "customer"),
            @JsonSubTypes.Type(value = AdminRole.class, name = "admin")
    })
    public sealed interface Role permits SellerRole, CustomerRole, AdminRole {
    }

    public record SellerRole(
            @JsonProperty("bio") String bio,
            @JsonProperty("selling_since") Instant sellingSince
    ) implements Role {
    }

    public record CustomerRole() implements Role {
    }

    public record AdminRole() implements Role {
    }
}
