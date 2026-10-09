package com.example.flightcrud.dao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import com.example.flightcrud.entity.Flight;

public interface FlightDao {
	Flight save(Flight f);

	Optional<Flight> findById(Integer id);

	List<Flight> findAll();

	Flight update(Flight f);

	boolean deleteById(Integer id);

	List<Flight> findBySource(String s);

	List<Flight> findByRoute(String s, String d);

	List<Flight> findByPriceLessThan(BigDecimal p);

	List<Flight> findByDateRange(LocalDate a, LocalDate b);

	List<Flight> findCheapestFlights();
}
