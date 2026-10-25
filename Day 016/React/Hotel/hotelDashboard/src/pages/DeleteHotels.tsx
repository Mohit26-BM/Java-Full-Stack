import axios from "axios";
import type Hotel from "../components/hotel";
import useHotels from "../components/useHotels";
import HotelTable from "../components/HotelTable";
import Loading from "../components/Loading";
import ErrorAlert from "../components/ErrorAlert";
import { API_URL, getErrorMessage } from "../api";

function DeleteHotels() {
  const { hotels, error, setError, loading, reload } = useHotels();

  const handleDelete = async (hotel: Hotel) => {
    if (!window.confirm(`Delete "${hotel.name}"?`)) {
      return;
    }

    try {
      await axios.delete(`${API_URL}/${hotel.id}`);
      reload();
    } catch (err) {
      setError(getErrorMessage(err, "Error deleting hotel. Please try again."));
    }
  };

  if (loading) {
    return <Loading />;
  }

  return (
    <div>
      <h2 className="page-title mb-1">Delete Hotel</h2>
      <p className="text-secondary">Deleting a hotel cannot be undone.</p>
      <ErrorAlert message={error} />
      <HotelTable
        hotels={hotels}
        renderActions={(hotel) => (
          <button className="btn btn-delete btn-sm" onClick={() => handleDelete(hotel)}>
            Delete
          </button>
        )}
      />
    </div>
  );
}

export default DeleteHotels;
