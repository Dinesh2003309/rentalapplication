package com.rental.dtos;

import java.time.Instant;
import java.util.List;


import com.rental.model.Image;
import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HouseDetailsDTO {
	
	@NotBlank(message = "Title is required")
    @Size(max = 250, message = "Title can't exceed 250 characters")
    private String title;

    @NotBlank(message = "Address is required")
    @Size(max = 250, message = "Address can't exceed 250 characters")
    private String address;

    @NotBlank(message = "Street address is required")
    @Size(max = 250, message = "Street Address can't exceed 250 characters")
    private String streetAddress;

    @NotBlank(message = "Apartment Name is required")
    @Size(max = 250, message = "Apartment Name can't exceed 250 characters")
    private String apartmentName;

    @DecimalMin(value = "-90.0", message = "House Latitude should be between -90 and 90")
    @DecimalMax(value = "90.0", message = "House Latitude should be between -90 and 90")
    @Column(insertable=false, updatable=false)
    private double houselatitude;

    @DecimalMin(value = "-180.0", message = "House Longitude should be between -180 and 180")
    @DecimalMax(value = "180.0", message = "House Longitude should be between -180 and 180")
    @Column(insertable=false, updatable=false)
    private double houselongitude;


    @NotNull(message = "Property type is required")
    private String propertytype;

    @NotNull(message = "Sqft is required")
    @Min(value = 1, message = "Invalid value for Sqft")
    private double sqft;

    @NotNull(message = "Garage is required")
    @Min(value = 0, message = "Invalid value Garage")
    private double garage;

    @NotNull(message = "Bedroom is required")
    @Min(value = 0, message = "Invalid value for Bedrooms")
    private double bedrooms;

    @NotNull(message = "Bathrooms is required")
    @Min(value = 0, message = "Invalid value for Bathrooms")
    private double bathrooms;

    @NotNull(message = "Construction type is required")
    private String constructiontype;

    @NotNull(message = "Year built is required")
    @Min(value = 1, message = "Invalid valid for Yearbuilt")
    private Integer yearbuilt;

    @NotNull(message = "Expected rent is required")
    @Min(value = 1, message = "Invalid value for Expected Rent")
    private double expectedrent;

    @NotNull(message = "Deposit is required")
    @Min(value = 1, message = "Invalid value for Deposit")
    private double deposit;

    private String rentType;

    private List<String> utilities;

    @NotNull(message = "Availability from date is required")
    private Instant availabilityfrom;

    @NotNull(message = "Availability till date is required")
    private Instant availabilitytill;

    @NotBlank(message = "Description is required")
    @Size(max = 500, message = "Description can't exceed 500 characters")
    private String description;

    private String smokingpolicy;

    private String vegetarianpreference;

    private String petsFriendly;

    private List<String> addaminities;

    private List<Image> addimages;


}
