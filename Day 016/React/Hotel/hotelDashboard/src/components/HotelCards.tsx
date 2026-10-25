import type Hotel from "./hotel";
import HotelImage from "./HotelImage";

const formatPrice = (price: number) =>
  `₹${new Intl.NumberFormat("en-IN").format(price)}`;

function HotelCards({ hotels }: { hotels: Hotel[] }) {
  if (hotels.length === 0) {
    return (
      <div className="alert alert-themed" role="alert">
        No hotels found.
      </div>
    );
  }

  return (
    <div className="row row-cols-1 row-cols-md-2 row-cols-xl-3 g-4">
      {hotels.map((hotel) => (
        <div className="col" key={hotel.id}>
          <div className="card card-themed shadow-sm h-100 overflow-hidden">
            <div className="ratio ratio-16x9">
              <HotelImage hotel={hotel} className="hotel-photo" />
            </div>

            <div className="card-body">
              <div className="d-flex justify-content-between align-items-start gap-2">
                <h5 className="card-title mb-1">{hotel.name}</h5>
                <span className="badge badge-rating text-nowrap">{hotel.rating} ★</span>
              </div>
              <p className="text-secondary mb-2">
                {hotel.city} · {hotel.hotelChain}
              </p>
              <p className="mb-0">{hotel.roomType}</p>
            </div>

            <div className="card-footer d-flex justify-content-between bg-transparent">
              <span className="fw-semibold">{formatPrice(hotel.pricePerNight)} / night</span>
              <small className="text-secondary align-self-center">
                From {hotel.availableDate}
              </small>
            </div>
          </div>
        </div>
      ))}
    </div>
  );
}

export default HotelCards;
