import { useState } from "react";
import type Hotel from "./hotel";
import { fallbackImage, getHotelImage } from "./imagePaths";

interface Props {
  hotel: Hotel;
  className?: string;
}

function HotelImage({ hotel, className }: Props) {
  const [failed, setFailed] = useState(false);

  return (
    <img
      src={failed ? fallbackImage : getHotelImage(hotel)}
      alt={`${hotel.name}, ${hotel.city}`}
      className={className}
      loading="lazy"
      onError={() => setFailed(true)}
    />
  );
}

export default HotelImage;
