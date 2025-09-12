package com.yash.ResourceManagement.Entity;


import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
@Getter
@Setter
public class UserEntity
{
    @Id
    private String id;

    @Indexed(unique = true)
    @NonNull
    private String email;

    @NonNull
    private String password;


}
