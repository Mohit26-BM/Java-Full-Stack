package service;

import entity.Hotel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface HotelService {

    List<Hotel> getHotels();

    Hotel findHotel(int hotelId);

    Hotel addHotel(Hotel hotel);

    Hotel updateHotel(Hotel hotel);

    boolean removeHotel(int hotelId);

    List<Hotel> searchByHotelChain(String hotelChain);

    List<Hotel> searchByCity(String city);

    List<Hotel> searchByCityAndHotelChain(
            String city,
            String hotelChain);

    List<Hotel> searchByPrice(BigDecimal pricePerNight);

    List<Hotel> searchByPriceLessThan(BigDecimal pricePerNight);

    List<Hotel> searchByAvailableDate(LocalDate availableDate);

    List<Hotel> searchByAvailableDateAfter(LocalDate date);

    boolean existsByHotelChain(String hotelChain);
}