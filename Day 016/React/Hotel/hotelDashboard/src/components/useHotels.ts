import { useState, useEffect } from "react";
import axios from "axios";
import type Hotel from "./hotel";
import { API_URL, getErrorMessage } from "../api";

// Loads all hotels; call reload() after a change to refresh the list.
function useHotels() {
  const [hotels, setHotels] = useState<Hotel[]>([]);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);
  const [reloadKey, setReloadKey] = useState(0);

  useEffect(() => {
    const loadHotels = async () => {
      try {
        const response = await axios.get<Hotel[]>(API_URL);
        setHotels(response.data);
        setError("");
      } catch (err) {
        setError(getErrorMessage(err, "Error fetching hotels. Please try again."));
      } finally {
        setLoading(false);
      }
    };

    loadHotels();
  }, [reloadKey]);

  const reload = () => setReloadKey((key) => key + 1);

  return { hotels, error, setError, loading, reload };
}

export default useHotels;
