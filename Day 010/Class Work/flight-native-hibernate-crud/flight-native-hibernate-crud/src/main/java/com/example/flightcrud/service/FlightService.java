package com.example.flightcrud.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import com.example.flightcrud.entity.Flight;

public interface FlightService {
	Flight create(Flight f);

	Flight get(Integer id);

	List<Flight> all();

	Flight update(Integer id, Flight f);

	void delete(Integer id);

	List<Flight> source(String s);

	List<Flight> route(String s, String d);

	List<Flight> priceBelow(BigDecimal p);

	List<Flight> dateRange(LocalDate a, LocalDate b);

	List<Flight> cheapest();
}
