import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import axios from "../../../utils/axios";
import "./adminCandidates.css";

/* ================= CENTRALIZED STATUS CONFIG ================= */
const STATUS_CONFIG = {
  PENDING: { label: "Pending", color: "#f59e0b" },
  COMPLETED: { label: "Completed", color: "#16a34a" },
  SUBMITTED: { label: "Submitted", color: "#2563eb" },
  APPROVED: { label: "Approved", color: "#14b8a6" },
  REJECTED: { label: "Rejected", color: "#dc2626" },
  IN_PROGRESS: { label: "In Progress", color: "#f97316" },
  NOT_STARTED: { label: "Not Started", color: "#6b7280" },
};

const AdminCandidates = () => {
  const [candidates, setCandidates] = useState([]);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [statusCounts, setStatusCounts] = useState({});
  const [statusFilter, setStatusFilter] = useState("");
  const [searchText, setSearchText] = useState("");

  const recordsPerPage = 10;

  /* ================= FETCH CANDIDATES ================= */
  const fetchCandidates = async (page = 0, status = "", search = "") => {
    try {
      const res = await axios.get(
        `/api/admin/candidates?page=${page}&size=${recordsPerPage}&sort=applicationNumber,desc` +
          (status ? `&status=${status}` : "") +
          (search ? `&search=${encodeURIComponent(search)}` : "")
      );

      setCandidates(res.data.content || []);
      setTotalPages(res.data.totalPages || 1);
    } catch (err) {
      console.error("Error fetching candidates:", err);
      setCandidates([]);
      setTotalPages(1);
    }
  };

  /* ================= FETCH STATUS COUNTS ================= */
  const fetchStatusCounts = async () => {
    try {
      const res = await axios.get("/api/admin/candidates/status-counts");
      setStatusCounts(res.data || {});
    } catch (err) {
      console.error("Error fetching status counts:", err);
      setStatusCounts({});
    }
  };

  /* ================= EFFECT ================= */
  useEffect(() => {
    fetchCandidates(currentPage, statusFilter, searchText);
    fetchStatusCounts();
  }, [currentPage, statusFilter, searchText]);

  useEffect(() => {
    setCurrentPage(0);
  }, [statusFilter, searchText]);

  /* ================= PAGINATION ================= */
  const goNext = () =>
    currentPage < totalPages - 1 && setCurrentPage((p) => p + 1);
  const goPrev = () =>
    currentPage > 0 && setCurrentPage((p) => p - 1);

  /* ================= DYNAMIC STATUS RENDER ================= */
  const renderStatus = (status) => {
    if (!status) return "-";

    const config = STATUS_CONFIG[status];

    return (
      <span
        className="status-badge"
        style={{
          backgroundColor: config?.color || "#9ca3af",
        }}
      >
        {config?.label || status.replace("_", " ")}
      </span>
    );
  };

  return (
    <div className="candidates-container">
      <h2 className="page-title">Candidates List</h2>

      {/* ================= FILTER & SEARCH ================= */}
      <div className="filter-bar">
        <input
          type="text"
          placeholder="Search by name/email/app no"
          value={searchText}
          onChange={(e) => setSearchText(e.target.value)}
        />

        <select
          value={statusFilter}
          onChange={(e) => setStatusFilter(e.target.value)}
        >
          <option value="">All Status</option>
          {Object.keys(STATUS_CONFIG).map((key) => (
            <option key={key} value={key}>
              {STATUS_CONFIG[key].label}
            </option>
          ))}
        </select>
      </div>

      {/* ================= DYNAMIC STATUS LEGEND ================= */}
      <div className="status-legend">
        {Object.keys(STATUS_CONFIG).map((key) => (
          <span
            key={key}
            className={`status-badge ${
              statusFilter === key ? "active" : ""
            }`}
            style={{
              backgroundColor: STATUS_CONFIG[key].color,
              cursor: "pointer",
            }}
            onClick={() =>
              setStatusFilter(statusFilter === key ? "" : key)
            }
          >
            {STATUS_CONFIG[key].label} (
            {statusCounts[key] || 0})
          </span>
        ))}
      </div>

      {/* ================= TABLE ================= */}
      <div className="table-wrapper">
        <table className="candidates-table">
          <thead>
            <tr>
              <th>#</th>
              <th>Name</th>
              <th>Email</th>
              <th>Mobile</th>
              <th>Personal</th>
              <th>Education</th>
              <th>Documents</th>
              <th>Payment</th>
              <th>Action</th>
            </tr>
          </thead>

          <tbody>
  {candidates.length ? (
    candidates.map((c, idx) => (
      <tr key={c.userId || idx}>
        <td>{currentPage * recordsPerPage + idx + 1}</td>
        <td>{c.fullName}</td>
        <td>{c.email}</td>
        <td>{c.mobileNumber || "-"}</td>
        <td>{renderStatus(c.personalStatus)}</td>
        <td>{renderStatus(c.educationStatus)}</td>
        <td>{renderStatus(c.documentStatus)}</td>
        <td>{renderStatus(c.paymentStatus)}</td>
        <td>
          {/* ✅ Use userId as the correct unique identifier */}
          {c.userId ? (
            <Link
              to={`/admin/candidates/${c.userId}`}
              className="view-btn"
            >
              View
            </Link>
          ) : (
            <span style={{ color: "red" }}>No ID</span>
          )}
        </td>
      </tr>
    ))
  ) : (
    <tr>
      <td colSpan="9" style={{ textAlign: "center" }}>
        No candidates found
      </td>
    </tr>
  )}
</tbody>
        </table>
      </div>

      {/* ================= PAGINATION ================= */}
      <div className="pagination">
        <button
          onClick={goPrev}
          disabled={currentPage === 0}
          className="page-btn"
        >
          Previous
        </button>

        <span className="page-info">
          Page {currentPage + 1} of {totalPages || 1}
        </span>

        <button
          onClick={goNext}
          disabled={currentPage >= totalPages - 1}
          className="page-btn"
        >
          Next
        </button>
      </div>
    </div>
  );
};

export default AdminCandidates;