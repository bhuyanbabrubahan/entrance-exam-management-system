// src/services/authService.js
import axios from "axios";

/* ================= BACKEND URL ================= */
const API_URL = "http://localhost:8080/api/user";

/* ================= LOGIN ================= */
export const login = async (data) => {
  try {
    const response = await axios.post(
      `${API_URL}/login`,
      data,
      {
        headers: {
          "Content-Type": "application/json",
          Accept: "application/json",
        },
      }
    );

    const resData = response.data;

    /* ================= STORE AUTH DATA ================= */
    if (resData?.token) {
      localStorage.setItem("jwtToken", resData.token);
    }

    if (resData?.role) {
      localStorage.setItem("userRole", resData.role);
    }

    if (resData?.email) {
      localStorage.setItem("userEmail", resData.email);
    }

    return resData;

  } catch (error) {
    console.error("Login error:", error.response?.data || error.message);
    throw error;
  }
};
