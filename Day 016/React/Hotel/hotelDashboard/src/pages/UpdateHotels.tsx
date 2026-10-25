import { Link } from "react-router-dom";
import useHotels from "../components/useHotels";
import HotelTable from "../components/HotelTable";
import Loading from "../components/Loading";
import ErrorAlert from "../components/ErrorAlert";

function UpdateHotels() {
  const { hotels, error, loading } = useHotels();

  if (loading) {
    return <Loading />;
  }

  return (
    <div>
      <h2 className="page-title mb-1">Update Hotel</h2>
      <p className="text-secondary">Pick a hotel to edit.</p>
      <ErrorAlert message={error} />
      <HotelTable
        hotels={hotels}
        renderActions={(hotel) => (
          <Link to={`/update/${hotel.id}`} className="btn btn-accent btn-sm">
            Edit
          </Link>
        )}
      />
    </div>
  );
}

export default UpdateHotels;
