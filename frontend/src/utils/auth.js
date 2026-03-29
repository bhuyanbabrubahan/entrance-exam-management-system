// src/utils/auth.js

/* ================= TOKEN ================= */
export const getToken = () => {
  return localStorage.getItem("jwtToken");
};

/* ================= ROLE ================= */
export const getRole = () => {
  return localStorage.getItem("userRole");
};

/* ================= AUTH CHECK ================= */
export const isAuthenticated = () => {
  return !!getToken();
};

/* ================= ROLE CHECKS ================= */
export const isAdmin = () => {
  return getRole() === "ROLE_ADMIN";
};

export const isUser = () => {
  return getRole() === "ROLE_USER";
};

/* ================= LOGOUT ================= */
export const logout = () => {
  localStorage.removeItem("jwtToken");
  localStorage.removeItem("userRole");
  localStorage.removeItem("userEmail");

  window.location.replace("/login");
};
