import { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import axios from "axios";
import type Hotel from "../components/hotel";
import HotelForm from "../components/HotelForm";
import Loading from "../components/Loading";
import ErrorAlert from "../components/ErrorAlert";
import { API_URL, getErrorMessage } from "../api";

function EditHotel() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [hotel, setHotel] = useState<Hotel | null>(null);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const loadHotel = async () => {
      try {
        const response = await axios.get<Hotel>(`${API_URL}/${id}`);
        setHotel(response.data);
      } catch (err) {
        setError(getErrorMessage(err, "Error fetching hotel. Please try again."));
      } finally {
        setLoading(false);
      }
    };

    loadHotel();
  }, [id]);

  const handleSubmit = async (updated: Omit<Hotel, "id"> & { id?: number }) => {
    try {
      await axios.post(`${API_URL}/update`, updated);
      navigate("/");
    } catch (err) {
      setError(getErrorMessage(err, "Error updating hotel. Please try again."));
    }
  };

  if (loading) {
    return <Loading />;
  }

  return (
    <div>
      <h2 className="page-title mb-3">Edit Hotel #{id}</h2>
      <ErrorAlert message={error} />
      {hotel && (
        <HotelForm
          initial={hotel}
          onSubmit={handleSubmit}
          onCancel={() => navigate("/update")}
        />
      )}
    </div>
  );
}

export default EditHotel;
