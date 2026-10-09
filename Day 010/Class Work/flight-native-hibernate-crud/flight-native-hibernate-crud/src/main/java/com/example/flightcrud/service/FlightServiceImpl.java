package com.example.flightcrud.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import org.springframework.stereotype.Service;
import com.example.flightcrud.dao.FlightDao;
import com.example.flightcrud.entity.Flight;
import com.example.flightcrud.exception.FlightNotFoundException;

@Service
public class FlightServiceImpl implements FlightService {
	private final FlightDao dao;

	public FlightServiceImpl(FlightDao dao) {
		this.dao = dao;
	}

	public Flight create(Flight f) {
		return dao.save(f);
	}

	public Flight get(Integer id) {
		return dao.findById(id).orElseThrow(() -> new FlightNotFoundException("Flight not found with id: " + id));
	}

	public List<Flight> all() {
		return dao.findAll();
	}

	public Flight update(Integer id, Flight f) {
		get(id);
		f.setFlightId(id);
		return dao.update(f);
	}

	public void delete(Integer id) {
		if (!dao.deleteById(id))
			throw new FlightNotFoundException("Flight not found with id: " + id);
	}

	public List<Flight> source(String s) {
		return dao.findBySource(s);
	}

	public List<Flight> route(String s, String d) {
		return dao.findByRoute(s, d);
	}

	public List<Flight> priceBelow(BigDecimal p) {
		return dao.findByPriceLessThan(p);
	}

	public List<Flight> dateRange(LocalDate a, LocalDate b) {
		return dao.findByDateRange(a, b);
	}

	public List<Flight> cheapest() {
		return dao.findCheapestFlights();
	}
}
