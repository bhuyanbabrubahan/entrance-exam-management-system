// src/services/adminApi.js

const BASE_URL = "http://localhost:8080/api/admin";

/* ================= TOKEN ================= */
const getToken = () => localStorage.getItem("jwtToken");

/* ================= LOGOUT ================= */
export const logout = () => {
  localStorage.removeItem("jwtToken");
  localStorage.removeItem("userRole");
  localStorage.removeItem("userEmail");
  window.location.replace("/login?type=admin");
};

/* ================= AUTH HEADER ================= */
const authHeader = () => {
  const token = getToken();

  if (!token) {
    logout();
    return {};
  }

  return {
    Authorization: `Bearer ${token}`,
    "Content-Type": "application/json",
  };
};

/* ================= GENERIC FETCH ================= */
const apiFetch = async (url, options = {}) => {
  const res = await fetch(url, {
    headers: authHeader(),
    ...options,
  });

  if (res.status === 401 || res.status === 403) {
    logout();
    throw new Error("Unauthorized access");
  }

  if (!res.ok) {
    const text = await res.text();
    throw new Error(text || "Request failed");
  }

  return res.json();
};

/* =====================================================
   DASHBOARD
===================================================== */
export const getAdminDashboardStats = () =>
  apiFetch(`${BASE_URL}/dashboard/stats`);

/* =====================================================
   CANDIDATES (With Pagination + Filters)
===================================================== */
export const getCandidates = ({
  page = 0,
  size = 10,
  sort = "applicationNumber,desc",
  status = "",
  search = "",
}) =>
  apiFetch(
    `${BASE_URL}/candidates?page=${page}&size=${size}&sort=${sort}&status=${status}&search=${search}`
  );

export const getCandidateById = (id) =>
  apiFetch(`${BASE_URL}/candidates/${id}`);

export const updateCandidateStatus = (id, status) =>
  apiFetch(`${BASE_URL}/candidates/${id}/status`, {
    method: "PUT",
    body: JSON.stringify({ status }),
  });

export const deleteCandidate = (id) =>
  apiFetch(`${BASE_URL}/candidates/${id}`, {
    method: "DELETE",
  });

/* =====================================================
   AUDIT LOGS
===================================================== */
export const getAuditLogs = (page = 0) =>
  apiFetch(`${BASE_URL}/audit-logs?page=${page}`);

/* =====================================================
   CORRECTION WINDOW
===================================================== */
export const getCorrectionWindow = () =>
  apiFetch(`${BASE_URL}/correction-window`);

export const saveCorrectionWindow = (data) =>
  apiFetch(`${BASE_URL}/correction-window`, {
    method: "POST",
    body: JSON.stringify(data),
  });

/* =====================================================
   NEWS MANAGEMENT
===================================================== */
export const getAllNews = () =>
  apiFetch(`${BASE_URL}/news`);

export const createNews = (newsData) =>
  apiFetch(`${BASE_URL}/news`, {
    method: "POST",
    body: JSON.stringify(newsData),
  });

export const deleteNews = (id) =>
  apiFetch(`${BASE_URL}/news/${id}`, {
    method: "DELETE",
  });

  