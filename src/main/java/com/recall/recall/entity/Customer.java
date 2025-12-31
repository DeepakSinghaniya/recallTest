package com.recall.recall.entity;

import com.recall.recall.validation.DuplicateEmail;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

@Data
@DuplicateEmail(message="Email already exist")
@Entity
@Table(name="CUSTOMER")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
    @NotBlank(message = "Name is required")
    @Column(name = "NAME")
    private String name;
    @Column(name= "EMAIL")
    @Email(message="Enter a valid email")
    @NotBlank(message = "Email is required")
    private String email;
    @CreationTimestamp
    @Column(name= "CREATED_AT", updatable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;
}
