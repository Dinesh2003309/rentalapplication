package com.rental.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaginationDto {

    @NotNull(message = "Page is required")
    @Min(value = 1, message = "Page must be greater than or equal to 1")
    private Integer page;

    @NotNull(message = "Limit is required")
    @Min(value = 1, message = "Limit must be greater than or equal to 1")
    private Integer limit;
}