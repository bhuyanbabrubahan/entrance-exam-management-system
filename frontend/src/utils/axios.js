import axios from "axios";
import { getToken, logout } from "./auth";

/* =====================================================
   AXIOS INSTANCE (GLOBAL)
===================================================== */
const axiosInstance = axios.create({
  baseURL: "http://localhost:8080",
  headers: {
    "Content-Type": "application/json",
  },
});

/* =====================================================
   REQUEST INTERCEPTOR → ATTACH JWT
===================================================== */
axiosInstance.interceptors.request.use(
  (config) => {
    const token = getToken();

    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
  },
  (error) => Promise.reject(error)
);

/* =====================================================
   RESPONSE INTERCEPTOR → HANDLE AUTH ERRORS
===================================================== */
axiosInstance.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      logout(); // auto logout on token expiry
    }
    return Promise.reject(error);
  }
);

export default axiosInstance;
