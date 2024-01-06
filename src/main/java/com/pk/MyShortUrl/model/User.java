package com.pk.MyShortUrl.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;


@Document(collection = "users")
@Data
@NoArgsConstructor
public class User {

    @Id
    private String id;

    @Indexed(unique = true)
    @NotBlank(message = "Username is required.")
    private String username;

    @Indexed(unique = true)
    @NotBlank(message = "Email is required.")
    @Email(message = "Must be a valid email address.")
    private String email;

    @NotBlank(message = "Password is required.")
    @Size(min = 8, message = "Password must be at least 8 characters long.")
    private String password;

    private int urlLimit = 5; // Default URL limit

    // Lombok will handle getters and setters for all fields

    // Use JsonIgnore to prevent password from being serialized
    @JsonIgnore
    public String getPassword() {
        return password;
    }
}
