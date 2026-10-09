package exception;

public class HotelNotFoundException extends RuntimeException {

    public HotelNotFoundException(int hotelId) {
        super("Hotel not found with ID: " + hotelId);
    }
}