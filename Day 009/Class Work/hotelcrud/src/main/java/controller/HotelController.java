package controller;

import entity.Hotel;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import service.HotelService;

import java.util.List;

@RestController
@RequestMapping("/hotels")
public class HotelController {

    @Autowired
    HotelService hotelService;

    @GetMapping("/city/{city}")
    public List<Hotel> searchByCity(@PathVariable String city) {
        return hotelService.searchByCity(city);
    }

    @GetMapping("/chain/{hotelChain}")
    public List<Hotel> searchByHotelChain(@PathVariable String hotelChain) {
        return hotelService.searchByHotelChain(hotelChain);
    }

    @DeleteMapping("/{hotelId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public boolean removeHotel(@PathVariable int hotelId) {
        return hotelService.removeHotel(hotelId);
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping("/update")
    public Hotel updateHotel(@Valid @RequestBody Hotel hotel) {
        return hotelService.updateHotel(hotel);
    }

    @GetMapping("/{hotelId}")
    public Hotel getHotelById(@PathVariable int hotelId) {
        return hotelService.findHotel(hotelId);
    }

    @GetMapping
    public List<Hotel> getAllHotels() {
        return hotelService.getHotels();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Hotel addHotel(@Valid @RequestBody Hotel hotel) {
        return hotelService.addHotel(hotel);
    }
}