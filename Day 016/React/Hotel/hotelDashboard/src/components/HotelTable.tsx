import type { ReactNode } from "react";
import type Hotel from "./hotel";
import HotelImage from "./HotelImage";

interface Props {
  hotels: Hotel[];
  // Optional extra column (e.g. Edit / Delete) rendered for each row.
  renderActions?: (hotel: Hotel) => ReactNode;
}

const formatPrice = (price: number) =>
  `₹${new Intl.NumberFormat("en-IN").format(price)}`;

function HotelTable({ hotels, renderActions }: Props) {
  if (hotels.length === 0) {
    return (
      <div className="alert alert-themed" role="alert">
        No hotels found.
      </div>
    );
  }

  return (
    <div className="card card-themed shadow-sm">
      <div className="table-responsive">
        <table className="table table-themed table-striped table-hover align-middle mb-0">
          <thead>
            <tr>
              <th>Photo</th>
              <th>ID</th>
              <th>Name</th>
              <th>City</th>
              <th>Hotel Chain</th>
              <th>Room Type</th>
              <th>Price Per Night</th>
              <th>Rating</th>
              <th>Available Date</th>
              {renderActions && <th>Actions</th>}
            </tr>
          </thead>

          <tbody>
            {hotels.map((hotel) => (
              <tr key={hotel.id}>
                <td>
                  <HotelImage hotel={hotel} className="hotel-thumb" />
                </td>
                <td>{hotel.id}</td>
                <td className="fw-semibold">{hotel.name}</td>
                <td>{hotel.city}</td>
                <td>{hotel.hotelChain}</td>
                <td>{hotel.roomType}</td>
                <td>{formatPrice(hotel.pricePerNight)}</td>
                <td>
                  <span className="badge badge-rating">{hotel.rating} ★</span>
                </td>
                <td>{hotel.availableDate}</td>
                {renderActions && <td className="text-nowrap">{renderActions(hotel)}</td>}
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

export default HotelTable;
