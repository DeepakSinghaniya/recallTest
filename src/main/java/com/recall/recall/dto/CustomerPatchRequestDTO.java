package com.recall.recall.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerPatchRequestDTO {

    @Size(max = 50, message = "Name must not exceed 50 characters")
    private String name;

    @Email(message = "Enter a valid email")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String email;
}
