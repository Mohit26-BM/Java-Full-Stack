import { useState } from "react";
import type { FormEvent } from "react";
import axios from "axios";
import type Hotel from "../components/hotel";
import HotelTable from "../components/HotelTable";
import ErrorAlert from "../components/ErrorAlert";
import { API_URL, getErrorMessage } from "../api";

type SearchBy = "city" | "chain";

function SearchHotels() {
  const [searchBy, setSearchBy] = useState<SearchBy>("city");
  const [searchText, setSearchText] = useState("");
  const [hotels, setHotels] = useState<Hotel[]>([]);
  const [searched, setSearched] = useState(false);
  const [error, setError] = useState("");

  const handleSearch = async (e: FormEvent) => {
    e.preventDefault();
    const text = searchText.trim();
    if (!text) {
      return;
    }

    try {
      const response = await axios.get<Hotel[]>(
        `${API_URL}/${searchBy}/${encodeURIComponent(text)}`
      );
      setHotels(response.data);
      setError("");
    } catch (err) {
      setError(getErrorMessage(err, "Error searching hotels. Please try again."));
    } finally {
      setSearched(true);
    }
  };

  return (
    <div>
      <h2 className="page-title mb-3">Search Hotels</h2>

      <form onSubmit={handleSearch} className="card card-themed shadow-sm mb-4">
        <div className="card-body">
          <div className="row g-2">
            <div className="col-12 col-md-3">
              <select
                className="form-select"
                aria-label="Search by"
                value={searchBy}
                onChange={(e) => setSearchBy(e.target.value as SearchBy)}
              >
                <option value="city">City</option>
                <option value="chain">Hotel Chain</option>
              </select>
            </div>
            <div className="col-12 col-md">
              <input
                className="form-control"
                placeholder={`Search by ${searchBy === "city" ? "city" : "hotel chain"}`}
                value={searchText}
                onChange={(e) => setSearchText(e.target.value)}
              />
            </div>
            <div className="col-12 col-md-auto">
              <button type="submit" className="btn btn-accent w-100">
                Search
              </button>
            </div>
          </div>
        </div>
      </form>

      <ErrorAlert message={error} />
      {searched && !error && <HotelTable hotels={hotels} />}
    </div>
  );
}

export default SearchHotels;
