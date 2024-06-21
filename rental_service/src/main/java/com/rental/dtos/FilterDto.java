package com.rental.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FilterDto {

    private String propertytype;

    private String constructiontype;

    private  String location;

    @Min(value = 0, message = "maxexpectedrent must be greater than or equal to 0")
    private Double maxexpectedrent;

    @Min(value = 0, message = "minexpectedrent must be greater than or equal to 0")
    private Double minexpectedrent;

    @NotNull(message = "Page is required")
    @Min(value = 1, message = "Page must be greater than or equal to 1")
    private Integer page;

    @NotNull(message = "Limit is required")
    @Min(value = 1, message = "Limit must be greater than or equal to 1")
    private Integer limit;


}
