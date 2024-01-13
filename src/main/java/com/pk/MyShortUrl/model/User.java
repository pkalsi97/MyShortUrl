package com.pk.MyShortUrl.model;
// Reduces boilerplate code.
import lombok.Data;
import lombok.NoArgsConstructor;
// for annotations of flied to mongo db.
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
// For validation of input fields
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Document(collection = "users")
@Data
@NoArgsConstructor
public class User {
    // auto generate a unique ID to every user (Primary Key)
    @Id
    private String id;

    //username should be unique and cant be left blank
    @Indexed(unique = true)
    @NotBlank(message = "Username is required.")
    private String username;

    //Email must be unique and a valid email.
    @Indexed(unique = true)
    @NotBlank(message = "Email is required.")
    @Email(message = "Must be a valid email address.")
    private String email;

    //password must be 8 characters long.
    @NotBlank(message = "Password is required.")
    @Size(min = 8, message = "Password must be at least 8 characters long.")
    private String password;

    private int urlLimit = 5;
    private int activeUrlCount;

    //Prevent password from appearing in any type of json request/being exposed to api
    @JsonIgnore
    public String getPassword() {
        return password;
    }
}

/*
* @Document -> This class will be used to store data in the database(MongoDB).
* @Data -> lombok getters,setters etc.
* @NoArgsConstructor -> lombok generate a no argument constructor for this class.
* @Id is used to annotate a flied as primary key.
* @Indexed  -> annotates this filed must be unique across the database.
* @NotBlank -> annotates flied can be empty.
* @Email -> annotates the flied to be a valid email address.
* @Size -> annotates the minimum length of the filed.
* Use of jackson ->@JsonIgnore prevents serialization and deserialization of the field password.
* */
