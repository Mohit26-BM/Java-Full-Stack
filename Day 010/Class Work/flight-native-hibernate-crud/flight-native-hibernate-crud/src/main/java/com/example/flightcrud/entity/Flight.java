package com.example.flightcrud.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "flights")
public class Flight implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "flight_id")
	private Integer flightId;
	
	@NotBlank(message = "Provider is mandatory")
	@Column(nullable = false, length = 50)
	private String provider;
	
	@NotBlank(message = "Source is mandatory")
	@Column(nullable = false, length = 50)
	private String source;
	
	@NotBlank(message = "Destination is mandatory")
	@Column(nullable = false, length = 50)
	private String destination;
	
	@NotNull(message = "Price is mandatory")
	@Positive(message = "Price must be greater than zero")
	@DecimalMin(value = "100.00", message = "Price must be at least 100")
	@Digits(integer = 8, fraction = 2)
	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal price;
	
	@NotNull(message = "Date of travel is mandatory")
	@FutureOrPresent(message = "Date of travel cannot be in the past")
	@Column(name = "date_of_travel", nullable = false)
	private LocalDate dateOfTravel;
	

	public Flight() {
	}

	public Flight(String provider, String source, String destination, BigDecimal price, LocalDate dateOfTravel) {
		this.provider = provider;
		this.source = source;
		this.destination = destination;
		this.price = price;
		this.dateOfTravel = dateOfTravel;
	}

	public Integer getFlightId() {
		return flightId;
	}

	public void setFlightId(Integer v) {
		flightId = v;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String v) {
		provider = v;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String v) {
		source = v;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String v) {
		destination = v;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal v) {
		price = v;
	}

	public LocalDate getDateOfTravel() {
		return dateOfTravel;
	}

	public void setDateOfTravel(LocalDate v) {
		dateOfTravel = v;
	}
}
