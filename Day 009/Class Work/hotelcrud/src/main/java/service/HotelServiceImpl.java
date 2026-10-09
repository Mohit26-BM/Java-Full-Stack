
package service;

import exception.HotelNotFoundException;
import dao.HotelDao;
import entity.Hotel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class HotelServiceImpl implements HotelService {

    private static final Logger logger =
            LoggerFactory.getLogger(HotelServiceImpl.class);

    @Autowired
    private HotelDao hotelDao;

    @Override
    public List<Hotel> getHotels() {
        logger.info("Fetching all hotels");

        List<Hotel> hotels = hotelDao.findAll();

        logger.info("Successfully fetched {} hotels", hotels.size());
        return hotels;
    }

    @Override
    public Hotel findHotel(int hotelId) {
        logger.info("Searching for hotel with ID: {}", hotelId);

        Hotel hotel = hotelDao.findById(hotelId)
                .orElseThrow(() -> {
                    logger.warn("Hotel not found with ID: {}", hotelId);
                    return new HotelNotFoundException(hotelId);
                });

        logger.info("Hotel found with ID: {}", hotelId);
        return hotel;
    }

    @Override
    public Hotel addHotel(Hotel hotel) {
        logger.info("Adding hotel: {}", hotel.getName());

        Hotel savedHotel = hotelDao.save(hotel);

        logger.info("Hotel added successfully with ID: {}",
                savedHotel.getId());

        return savedHotel;
    }

    @Override
    public Hotel updateHotel(Hotel hotel) {
        logger.info("Updating hotel with ID: {}", hotel.getId());

        if (hotel.getId() == null) {
            logger.warn("Cannot update hotel without an ID");
            throw new IllegalArgumentException(
                    "Hotel ID is required for update");
        }

        if (!hotelDao.existsById(hotel.getId())) {
            logger.warn("Cannot update. Hotel not found with ID: {}",
                    hotel.getId());
            throw new HotelNotFoundException(hotel.getId());
        }

        Hotel updatedHotel = hotelDao.save(hotel);

        logger.info("Hotel updated successfully with ID: {}",
                updatedHotel.getId());

        return updatedHotel;
    }

    @Override
    public boolean removeHotel(int hotelId) {
        logger.info("Attempting to delete hotel with ID: {}", hotelId);

        if (!hotelDao.existsById(hotelId)) {
            logger.warn("Cannot delete. Hotel not found with ID: {}",
                    hotelId);
            throw new HotelNotFoundException(hotelId);
        }

        hotelDao.deleteById(hotelId);

        logger.info("Hotel deleted successfully with ID: {}", hotelId);
        return true;
    }

    @Override
    public List<Hotel> searchByHotelChain(String hotelChain) {
        logger.info("Searching hotels by chain: {}", hotelChain);

        List<Hotel> hotels = hotelDao.findByHotelChain(hotelChain);

        logger.info("Found {} hotels for chain: {}",
                hotels.size(), hotelChain);

        return hotels;
    }

    @Override
    public List<Hotel> searchByCity(String city) {
        logger.info("Searching hotels in city: {}", city);

        List<Hotel> hotels = hotelDao.findByCity(city);

        logger.info("Found {} hotels in city: {}",
                hotels.size(), city);

        return hotels;
    }

    @Override
    public List<Hotel> searchByCityAndHotelChain(
            String city, String hotelChain) {

        logger.info("Searching hotels in city: {} and chain: {}",
                city, hotelChain);

        List<Hotel> hotels =
                hotelDao.findByCityAndHotelChain(city, hotelChain);

        logger.info("Found {} hotels in city: {} and chain: {}",
                hotels.size(), city, hotelChain);

        return hotels;
    }

    @Override
    public List<Hotel> searchByPrice(BigDecimal pricePerNight) {
        logger.info("Searching hotels with price: {}", pricePerNight);

        List<Hotel> hotels =
                hotelDao.findByPricePerNight(pricePerNight);

        logger.info("Found {} hotels with price: {}",
                hotels.size(), pricePerNight);

        return hotels;
    }

    @Override
    public List<Hotel> searchByPriceLessThan(BigDecimal pricePerNight) {
        logger.info("Searching hotels with price less than: {}",
                pricePerNight);

        List<Hotel> hotels =
                hotelDao.findByPricePerNightLessThan(pricePerNight);

        logger.info("Found {} hotels with price less than: {}",
                hotels.size(), pricePerNight);

        return hotels;
    }

    @Override
    public List<Hotel> searchByAvailableDate(LocalDate availableDate) {
        logger.info("Searching hotels available on: {}", availableDate);

        List<Hotel> hotels =
                hotelDao.findByAvailableDate(availableDate);

        logger.info("Found {} hotels available on: {}",
                hotels.size(), availableDate);

        return hotels;
    }

    @Override
    public List<Hotel> searchByAvailableDateAfter(LocalDate date) {
        logger.info("Searching hotels available after: {}", date);

        List<Hotel> hotels =
                hotelDao.findByAvailableDateAfter(date);

        logger.info("Found {} hotels available after: {}",
                hotels.size(), date);

        return hotels;
    }

    @Override
    public boolean existsByHotelChain(String hotelChain) {
        logger.debug("Checking whether hotel chain exists: {}",
                hotelChain);

        boolean exists = hotelDao.existsByHotelChain(hotelChain);

        logger.debug("Hotel chain '{}' exists: {}", hotelChain, exists);
        return exists;
    }
}
