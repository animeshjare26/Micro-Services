package com.creativeshouse.post_service.client;

import com.creativeshouse.post_service.dtos.response.AuthorSummaryDto;
import com.creativeshouse.post_service.utils.GenericResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserClientFallback implements UserClient {

    @Override
    public GenericResponse<AuthorSummaryDto> getUserSummary(Long id) {
        log.error("User Service is down! Fallback triggered for userId: {}", id);
        
        AuthorSummaryDto fallbackAuthor = new AuthorSummaryDto();
        fallbackAuthor.setId(id);
        fallbackAuthor.setName("Unknown User");
        fallbackAuthor.setUserType("UNKNOWN");
        fallbackAuthor.setEmail("");

        return GenericResponse.success(fallbackAuthor, "Fallback Author Provided");
    }
}
