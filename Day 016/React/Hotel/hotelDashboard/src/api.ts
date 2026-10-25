import axios from "axios";
import { clearToken, getToken } from "./auth";

export const API_URL = "http://localhost:8080/hotels";
export const LOGIN_URL = "http://localhost:8080/auth/login";

// Send the login token with every request.
axios.interceptors.request.use((config) => {
  const token = getToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// An expired or invalid token means the user must log in again.
// (A 401 from the login call itself just means wrong credentials.)
axios.interceptors.response.use(
  (response) => response,
  (error) => {
    if (
      axios.isAxiosError(error) &&
      error.response?.status === 401 &&
      error.config?.url !== LOGIN_URL
    ) {
      clearToken();
      window.location.assign("/login");
    }
    return Promise.reject(error);
  }
);

// Turns a backend error ({ "error": "..." } or { field: message }) into text.
export const getErrorMessage = (err: unknown, fallback: string): string => {
  if (axios.isAxiosError(err) && err.response?.data) {
    const data = err.response.data;
    if (typeof data === "object") {
      return Object.values(data).join(", ");
    }
  }
  return fallback;
};
