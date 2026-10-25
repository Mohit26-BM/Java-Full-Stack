import { useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import type Hotel from "../components/hotel";
import HotelForm from "../components/HotelForm";
import ErrorAlert from "../components/ErrorAlert";
import { API_URL, getErrorMessage } from "../api";

function AddHotel() {
  const navigate = useNavigate();
  const [error, setError] = useState("");

  const handleSubmit = async (hotel: Omit<Hotel, "id">) => {
    try {
      await axios.post(API_URL, hotel);
      navigate("/");
    } catch (err) {
      // Stay on the page so the user can fix the input.
      setError(getErrorMessage(err, "Error adding hotel. Please try again."));
    }
  };

  return (
    <div>
      <h2 className="page-title mb-3">Add Hotel</h2>
      <ErrorAlert message={error} />
      <HotelForm initial={null} onSubmit={handleSubmit} onCancel={() => navigate("/")} />
    </div>
  );
}

export default AddHotel;
