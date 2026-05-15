package com.creativeshouse.post_service.client;

import com.creativeshouse.post_service.dtos.response.AuthorSummaryDto;
import com.creativeshouse.post_service.utils.GenericResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "USER-SERVICE", fallback = UserClientFallback.class)
public interface UserClient {

    @GetMapping("/api/users/{id}/summary")
    GenericResponse<AuthorSummaryDto> getUserSummary(@PathVariable("id") Long id);
}
