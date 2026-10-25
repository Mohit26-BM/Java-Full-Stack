import { useState } from "react";
import type { FormEvent, InputHTMLAttributes } from "react";
import type Hotel from "./hotel";

interface Props {
  initial: Hotel | null;
  onSubmit: (hotel: Omit<Hotel, "id"> & { id?: number }) => Promise<void>;
  onCancel: () => void;
}

interface FormState {
  name: string;
  city: string;
  hotelChain: string;
  roomType: string;
  pricePerNight: string;
  rating: string;
  availableDate: string;
}

interface Field {
  key: keyof FormState;
  label: string;
  input?: InputHTMLAttributes<HTMLInputElement>;
}

const fields: Field[] = [
  { key: "name", label: "Name" },
  { key: "city", label: "City" },
  { key: "hotelChain", label: "Hotel Chain" },
  { key: "roomType", label: "Room Type" },
  {
    key: "pricePerNight",
    label: "Price Per Night",
    input: { type: "number", min: "0.01", step: "0.01" },
  },
  {
    key: "rating",
    label: "Rating (0-5)",
    input: { type: "number", min: "0", max: "5", step: "0.1" },
  },
  { key: "availableDate", label: "Available Date", input: { type: "date" } },
];

const toFormState = (hotel: Hotel | null): FormState => ({
  name: hotel?.name ?? "",
  city: hotel?.city ?? "",
  hotelChain: hotel?.hotelChain ?? "",
  roomType: hotel?.roomType ?? "",
  pricePerNight: hotel ? String(hotel.pricePerNight) : "",
  rating: hotel ? String(hotel.rating) : "",
  availableDate: hotel?.availableDate ?? "",
});

function HotelForm({ initial, onSubmit, onCancel }: Props) {
  const [form, setForm] = useState<FormState>(toFormState(initial));
  const [saving, setSaving] = useState(false);

  const handleChange = (field: keyof FormState, value: string) => {
    setForm((prev) => ({ ...prev, [field]: value }));
  };

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setSaving(true);
    try {
      await onSubmit({
        ...(initial ? { id: initial.id } : {}),
        name: form.name.trim(),
        city: form.city.trim(),
        hotelChain: form.hotelChain.trim(),
        roomType: form.roomType.trim(),
        pricePerNight: Number(form.pricePerNight),
        rating: Number(form.rating),
        availableDate: form.availableDate,
      });
    } finally {
      setSaving(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="card card-themed shadow-sm">
      <div className="card-body">
        <div className="row g-3">
          {fields.map(({ key, label, input }) => (
            <div className="col-12 col-md-6 col-lg-4" key={key}>
              <div className="form-floating">
                <input
                  id={`hotel-${key}`}
                  className="form-control"
                  placeholder={label}
                  value={form[key]}
                  onChange={(e) => handleChange(key, e.target.value)}
                  required
                  {...input}
                />
                <label htmlFor={`hotel-${key}`}>{label}</label>
              </div>
            </div>
          ))}
        </div>

        <div className="d-flex gap-2 mt-4">
          <button type="submit" className="btn btn-main" disabled={saving}>
            {saving ? "Saving..." : initial ? "Update Hotel" : "Add Hotel"}
          </button>
          <button
            type="button"
            className="btn btn-outline-secondary"
            onClick={onCancel}
            disabled={saving}
          >
            Cancel
          </button>
        </div>
      </div>
    </form>
  );
}

export default HotelForm;
