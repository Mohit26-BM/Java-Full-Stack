package com.example.flightcrud.dao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.flightcrud.entity.Flight;

@Repository
public class FlightDaoImpl implements FlightDao {
	
	//@Autowired
	private final SessionFactory sf;

	public FlightDaoImpl(SessionFactory sf) {
		this.sf = sf;
	}

	public Flight save(Flight f) {
		Transaction tx = null;
		try (Session s = sf.openSession()) {
			tx = s.beginTransaction();
			s.persist(f);
			tx.commit();
			return f;
		} catch (RuntimeException e) {
			rollback(tx);
			throw e;
		}
	}

	public Optional<Flight> findById(Integer id) {
		try (Session s = sf.openSession()) {
			return Optional.ofNullable(s.get(Flight.class, id));
		}
	}

	public List<Flight> findAll() {
		try (Session s = sf.openSession()) {
			return s.createQuery("from Flight f order by f.flightId", Flight.class).getResultList();
		}
	}

	public Flight update(Flight f) {
		Transaction tx = null;
		try (Session s = sf.openSession()) {
			tx = s.beginTransaction();
			Flight x = s.get(Flight.class, f.getFlightId());
			if (x == null)
				throw new IllegalArgumentException("Flight not found: " + f.getFlightId());
			
			x.setProvider(f.getProvider());
			x.setSource(f.getSource());
			x.setDestination(f.getDestination());
			x.setPrice(f.getPrice());
			x.setDateOfTravel(f.getDateOfTravel());
			tx.commit();
			return x;
		} catch (RuntimeException e) {
			rollback(tx);
			throw e;
		}
	}

	public boolean deleteById(Integer id) {
		Transaction tx = null;
		try (Session s = sf.openSession()) {
			tx = s.beginTransaction();
			Flight f = s.get(Flight.class, id);
			if (f == null) {
				tx.rollback();
				return false;
			}
			s.remove(f);
			tx.commit();
			return true;
		} catch (RuntimeException e) {
			rollback(tx);
			throw e;
		}
	}

	public List<Flight> findBySource(String source) {
		try (Session s = sf.openSession()) {
			return s.createQuery("from Flight f where lower(f.source)=lower(:source) order by f.price", Flight.class)
					.setParameter("source", source).getResultList();
		}
	}

	public List<Flight> findByRoute(String source, String destination) {
		try (Session s = sf.openSession()) {
			return s.createQuery(
					"from Flight f where lower(f.source)=lower(:source) and lower(f.destination)=lower(:destination) order by f.price",
					Flight.class).setParameter("source", source).setParameter("destination", destination)
					.getResultList();
		}
	}

	public List<Flight> findByPriceLessThan(BigDecimal price) {
		try (Session s = sf.openSession()) {
			return s.createQuery("from Flight f where f.price < :price order by f.price", Flight.class)
					.setParameter("price", price).getResultList();
		}
	}

	public List<Flight> findByDateRange(LocalDate a, LocalDate b) {
		try (Session s = sf.openSession()) {
			return s.createQuery("from Flight f where f.dateOfTravel between :a and :b order by f.dateOfTravel",
					Flight.class).setParameter("a", a).setParameter("b", b).getResultList();
		}
	}

	public List<Flight> findCheapestFlights() {
		try (Session s = sf.openSession()) {
			return s.createQuery("from Flight f where f.price=(select min(f2.price) from Flight f2)", Flight.class)
					.getResultList();
		}
	}

	private void rollback(Transaction tx) {
		if (tx != null)
			try {
				tx.rollback();
			} catch (Exception ignored) {
			}
	}
}
