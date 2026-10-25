import type Hotel from "./hotel";

const IMAGE_DIR = "/hotel-images";
const FALLBACK_COUNT = 6;

// Hotels have no image field in the backend yet, so pick a photo by name.
// First match wins, so more specific names go first.
const nameRules: [RegExp, string][] = [
  [/taj mahal palace/i, "taj-mahal-palace"],
  [/taj palace/i, "taj-palace-delhi"],
  [/oberoi/i, "oberoi-mumbai"],
  [/chola/i, "itc-grand-chola"],
  [/marriott/i, "marriott-bengaluru"],
];

export const fallbackImage = `${IMAGE_DIR}/fallback-1.jpg`;

// Other hotels get one of the generic photos, chosen by id so it stays stable.
export function getHotelImage(hotel: Hotel): string {
  const rule = nameRules.find(([pattern]) => pattern.test(hotel.name));
  if (rule) {
    return `${IMAGE_DIR}/${rule[1]}.jpg`;
  }
  return `${IMAGE_DIR}/fallback-${(hotel.id % FALLBACK_COUNT) + 1}.jpg`;
}
