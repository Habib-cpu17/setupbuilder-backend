package com.setupbuilder.dto;

import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
        @Size(max = 100) String displayName,
        @Size(max = 500) String bio,
        @Size(max = 100) String location,
        @Size(max = 200) String websiteUrl,
        @Size(max = 500) String avatarUrl,
        @Size(max = 500) String bannerUrl
) {}