import "bootstrap/dist/css/bootstrap.min.css";
import { useState } from "react";
import "./HotelList.css";

type Hotel = {
  id: number;
  name: string;
  city: string;
  hotelChain: string;
  roomType: string;
  pricePerNight: number;
  rating: number;
  availableDate: string; // LocalDate arrives as "yyyy-MM-dd"
};

function HotelList() {
  const [hotels, setHotels] = useState<Hotel[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  async function loadHotels() {
    setLoading(true);
    setError("");

    try {
      const response = await fetch("http://localhost:8080/hotels");

      if (!response.ok) {
        throw new Error(`Request failed: ${response.status}`);
      }

      const data: Hotel[] = await response.json();
      setHotels(data);
    } catch (error) {
      setError(
        error instanceof Error ? error.message : "An unknown error occurred."
      );
    } finally {
      setLoading(false);
    }
  }

  return (
    <main className="container my-4 hotel-page">
      <h1 className="text-2xl mb-3">Hotels</h1>
      <button
        className="btn hotel-btn mb-3"
        onClick={loadHotels}
        disabled={loading}
      >
        {loading ? "Loading hotels..." : "Load Hotels"}
      </button>

      {error && (
        <div className="alert theme-danger" role="alert">
          Error: {error}
        </div>
      )}

      <div className="table-responsive">
      <table className="table table-bordered align-middle hotel-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>City</th>
            <th>Chain</th>
            <th>Room Type</th>
            <th>Price / Night</th>
            <th>Rating</th>
            <th>Available From</th>
          </tr>
        </thead>
        <tbody>
          {hotels.length > 0 ? (
            hotels.map((hotel) => (
              <tr key={hotel.id}>
                <td>{hotel.id}</td>
                <td>{hotel.name}</td>
                <td>{hotel.city}</td>
                <td>{hotel.hotelChain}</td>
                <td>{hotel.roomType}</td>
                <td>{hotel.pricePerNight}</td>
                <td>{hotel.rating}</td>
                <td>{hotel.availableDate}</td>
              </tr>
            ))
          ) : (
            <tr>
              <td colSpan={8} className="text-center">No hotels loaded</td>
            </tr>
          )}
        </tbody>
      </table>
      </div>
    </main>
  );
}

export default HotelList;
