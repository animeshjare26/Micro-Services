package com.creativeshouse.userservice.controllers;

import com.creativeshouse.userservice.dtos.response.AuthorSummaryDto;
import com.creativeshouse.userservice.security.service.UserService;
import com.creativeshouse.userservice.utils.GenericResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}/summary")
    public ResponseEntity<GenericResponse<AuthorSummaryDto>> getUserSummary(@PathVariable Long id) {
        AuthorSummaryDto summary = userService.getUserSummaryById(id);
        return ResponseEntity.ok(GenericResponse.success(summary));
    }
}
