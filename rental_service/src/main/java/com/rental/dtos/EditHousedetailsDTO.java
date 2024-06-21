package com.rental.dtos;
import java.time.Instant;
import java.util.List;
import com.rental.model.Image;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditHousedetailsDTO {
    @NotBlank(message = "Title is required")
    @Size(min = 3, message = "Title should've minimum 3 characters")
    @Size(max = 250, message = "Title can't exceed 250 characters")
    private String title;

    @NotBlank(message = "Address is required")
    @Size(max = 250, message = "Address can't exceed 250 characters")
    private String address;

    @NotBlank(message = "Street address is required")
    @Size(max = 250, message = "Street Address can't exceed 250 characters")
    private String streetAddress;

    @NotBlank(message = "Apartment name is required")
    @Size(max = 250, message = "Apartment Name can't exceed 250 characters")
    private String apartmentName;

    private String propertytype;

    @NotNull(message = "Sqft is required")
    @Min(value = 1, message = "Invalid value for Sqft")
    private Double sqft;

    @NotNull(message = "Garage is required")
    @Min(value = 0, message = "Invalid value Garage")
    private Double garage;

    @NotNull(message = "Bedroom is required")
    @Min(value = 0, message = "Invalid value for Bedrooms")
    private Double bedrooms;

    @NotNull(message = "Bathrooms is required")
    @Min(value = 0, message = "Invalid value for Bathrooms")
    private Double bathrooms;

    private String constructiontype;

    @NotNull(message = "Year built is required")
    @Min(value = 1, message = "Invalid valid for Yearbuilt")
    private Integer yearbuilt;

    @NotNull(message = "Expected rent is required")
    @Min(value = 1, message = "Invalid value for Expected Rent")
    private Double expectedrent;

    @NotNull(message = "Deposit is required")
    @Min(value = 1, message = "Invalid value for Deposit")
    private Double deposit;

    private String rentType;

    private List<String> utilities;

    @NotNull(message = "Availability from date is required")
    private Instant availabilityfrom;

    @NotNull(message = "Availability till is required")
    private Instant availabilitytill;


    @NotBlank(message = "Description is required")
    @Size(max = 500, message = "Description can't exceed 500 characters")
    private String description;

    private String smokingpolicy;

    private double houselatitude;

    private double houselongitude;

    private String vegetarianpreference;

    private String petsFriendly;

    private List<String> addaminities;

    private List<Image> addimages;


}
