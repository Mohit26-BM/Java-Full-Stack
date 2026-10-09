package com.example.flightcrud.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.example.flightcrud.entity.Flight;
import com.example.flightcrud.service.FlightService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

@RestController
@RequestMapping("/api/flights")
@Validated
public class FlightController {
	private final FlightService service;

	public FlightController(FlightService service) {
		this.service = service;
	}

	@PostMapping
	public ResponseEntity<Flight> create(@Valid @RequestBody Flight f) {
		return ResponseEntity.status(HttpStatus.CREATED).body(service.create(f));
	}

	@GetMapping
	public List<Flight> all() {
		return service.all();
	}

	@GetMapping("/{id}")
	public Flight get(@PathVariable @Positive Integer id) {
		return service.get(id);
	}

	@PutMapping("/{id}")
	public Flight update(@PathVariable @Positive Integer id, @Valid @RequestBody Flight f) {
		return service.update(id, f);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable @Positive Integer id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/search/source")
	public List<Flight> source(@RequestParam String source) {
		return service.source(source);
	}

	@GetMapping("/search/route")
	public List<Flight> route(@RequestParam String source, @RequestParam String destination) {
		return service.route(source, destination);
	}

	@GetMapping("/search/price")
	public List<Flight> price(@RequestParam BigDecimal maxPrice) {
		return service.priceBelow(maxPrice);
	}

	@GetMapping("/search/date-range")
	public List<Flight> dates(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
		return service.dateRange(startDate, endDate);
	}

	@GetMapping("/search/cheapest")
	public List<Flight> cheapest() {
		return service.cheapest();
	}
}
