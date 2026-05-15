package com.creativeshouse.userservice.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorSummaryDto {
    private Long id;
    private String email;
    private String name; // Handles both fullName (Creative) or businessName (Business)
    private String userType; // "CREATIVE" or "BUSINESS"
}
