package dao;

import entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.Query;

public interface HotelDao extends JpaRepository<Hotel, Integer> {

    List<Hotel> findByHotelChain(String hotelChain);

    @Query("SELECT h FROM Hotel h WHERE h.city = :city")
	List<Hotel> findByCity(String city);

    List<Hotel> findByCityAndHotelChain(
            String city,
            String hotelChain);

    List<Hotel> findByPricePerNight(BigDecimal pricePerNight);

    List<Hotel> findByPricePerNightLessThan(BigDecimal pricePerNight);

    List<Hotel> findByAvailableDate(LocalDate availableDate);

    List<Hotel> findByAvailableDateAfter(LocalDate date);

    @Query("SELECT h.id FROM Hotel h WHERE h.hotelChain = :hotelChain")
	boolean existsByHotelChain(String hotelChain);
}