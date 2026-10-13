package com.company.boot.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * OPTIONAL EXTENSION: nested DTO.
 * JSON:  "address": { "street": "...", "city": "...", "state": "...", "pincode": "560001" }
 * Used in both the request and the response.
 */
public class AddressDto {

    private String street;

    @NotBlank(message = "address.city is required")
    private String city;

    private String state;

    @Pattern(regexp = "\\d{6}", message = "address.pincode must be exactly 6 digits")
    private String pincode;

    public AddressDto() {
    }

    public AddressDto(String street, String city, String state, String pincode) {
        this.street = street;
        this.city = city;
        this.state = state;
        this.pincode = pincode;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }
}
