import useHotels from "../components/useHotels";
import HotelCards from "../components/HotelCards";
import Loading from "../components/Loading";
import ErrorAlert from "../components/ErrorAlert";

function ViewHotels() {
  const { hotels, error, loading } = useHotels();

  if (loading) {
    return <Loading />;
  }

  return (
    <div>
      <h2 className="page-title mb-3">Hotel List</h2>
      <ErrorAlert message={error} />
      <HotelCards hotels={hotels} />
    </div>
  );
}

export default ViewHotels;
