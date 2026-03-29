import { useEffect, useState } from "react";
import "./UserRequestedAdminCorrection.css";
import axios from "axios";
const AdminCorrectionRequests = () => {

  const [requests, setRequests] = useState([]);
  const [search, setSearch] = useState("");
  const [statusFilter, setStatusFilter] = useState("ALL");
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [selectedDocs, setSelectedDocs] = useState(null);
  const [remarks, setRemarks] = useState({});
  const [jumpPage, setJumpPage] = useState("");
  const [loading, setLoading] = useState(false);
  const [historyData, setHistoryData] = useState(null);
  const [showHistory, setShowHistory] = useState(false);

  const token = localStorage.getItem("jwtToken");

  // ✅ NEW STATE FOR STATS
   const [stats, setStats] = useState({
      totalUsers: 0,
      totalRequests: 0,
    });

    useEffect(() => {
      if (token) {
        fetchCorrectionCountStats();
      }
    }, [token]);
    
  /* ================= User Correction count stats ================= */ 
      const fetchCorrectionCountStats = async () => {
        try {
          const res = await fetch(
            "http://localhost:8080/api/admin/user-requested-admin-correction/correction-count-stats",
            {
              headers: {
                Authorization: `Bearer ${token}`,
                "Content-Type": "application/json",
              },
            }
          );

          if (!res.ok) {
            throw new Error("Failed to fetch stats");
          }

          const data = await res.json();
          console.log("Stats API Response:", data);

          setStats(data);

        } catch (error) {
          console.error("Stats Error:", error);
          setStats({ totalUsers: 0, totalRequests: 0 });
        }
      };

  /* ================= History Fetch Function ================= */ 
  const fetchHistory = async (userId, fieldName) => {
    try {
      const res = await fetch(
        `http://localhost:8080/api/admin/user-requested-admin-correction/history/${userId}/${encodeURIComponent(fieldName)}`,
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );

      if (!res.ok) {
        throw new Error("History fetch failed");
      }

      const data = await res.json();

      if (Array.isArray(data)) {
        setHistoryData(data);
      } else {
        setHistoryData([]);
      }

      setShowHistory(true);

    } catch (err) {
      console.error("History Error:", err);
      setHistoryData([]);
      setShowHistory(true);
    }
  };

  /* ================= FETCH ================= */
  const fetchRequests = async () => {
    try {
      setLoading(true);

      const res = await fetch(
        `http://localhost:8080/api/admin/user-requested-admin-correction/all?page=${page}&size=10&status=${statusFilter}&search=${search}`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      if (!res.ok) throw new Error("Failed");

      const data = await res.json();

      setRequests(Array.isArray(data.content) ? data.content : []);
      setTotalPages(data.totalPages || 0);

    } catch (err) {
      console.error(err);
      setRequests([]);
    } finally {
      setLoading(false);
    }
  };

  // EXISTING USEEFFECT
      useEffect(() => {
        if (token) fetchRequests();
      }, [page, search, statusFilter]);
  /* ================= AUTO REFRESH REQUESTS ================= */
    {/*useEffect(() => {
      const interval = setInterval(() => {
        fetchRequests();
        fetchCorrectionCountStats();
      }, 120000); // every 10 seconds

      return () => clearInterval(interval);
    }, []); */}

  /* ================= APPROVE / REJECT ================= */
  const handleAction = async (id, action) => {

    const remark = remarks[id];

    if (!remark?.trim()) {
      alert("Admin remark required");
      return;
    }

    try {
      const res = await fetch(
        `http://localhost:8080/api/admin/user-requested-admin-correction/${id}/${action}`,
        {
          method: "PUT",
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
          body: JSON.stringify({ adminRemark: remark }),
        }
      );

      if (!res.ok) {
        alert("Operation failed");
        return;
      }

      alert(`Request ${action.toUpperCase()} Successfully`);
      fetchRequests();
      fetchCorrectionCountStats();

    } catch {
      alert("Operation failed");
    }
  };

   /* ================= DEACTIVATE BASIC WINDOW ================= */

   
  const deactivateWindow = async (id) => {
  try {

    const response = await axios.put(
      `http://localhost:8080/api/admin/basic-window/deactivate/${id}`,
      {},
      {
        headers: {
          Authorization: `Bearer ${token}`
        }
      }
    );

    alert("Window Deactivated Successfully");

    fetchRequests();
    fetchCorrectionCountStats();

  } catch (error) {

    console.error("Deactivate error:", error);

    alert("Window deactivation failed");

  }
};

  /* ================= ACTIVATE BASIC WINDOW ================= */
      const activateWindow = async (id) => {

  try {
    const res = await fetch(
      `http://localhost:8080/api/admin/basic-window/open/${id}`,
      {
        method: "PUT",
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }
    );

    if (!res.ok) {
      alert("Window activation failed");
      return;
    }

    alert("Basic Details Edit Window Activated Successfully");

    // 🔥 IMPORTANT REFRESH
    await fetchRequests();
    await fetchCorrectionCountStats();

  } catch (err) {
    console.error(err);
    alert("Window activation failed");
  }
};

  /* ================= JUMP PAGE ================= */
  const handleJump = () => {
    const p = Number(jumpPage) - 1;
    if (p >= 0 && p < totalPages) {
      setPage(p);
    }
    setJumpPage("");
  };

  return (
    <div className="admin-correction-wrapper">

      <h2 className="page-title">🛡 User Requested Correction Window</h2>

      {/* ================= FILTER BAR ================= */}
      <div className="filter-bar">

        {/* ✅ CORRECTION STATS SECTION */}
        <div className="correction-stats-container">
            <div className="correction-stat-box correction-users-box">
                <span className="correction-stat-label">Total Users:</span>
                <span className="correction-stat-value">
                  {stats?.totalUsers ?? 0}
                </span>
              </div>

              <div className="correction-stat-box correction-requests-box">
                  <span className="correction-stat-label">Total Requests:</span>
                  <span className="correction-stat-value">
                    {stats?.totalRequests ?? 0}
                </span>
            </div>
        </div>

        <div className="search-box">
          <input
            type="text"
            placeholder="🔍 Search Application No / Name"
            value={search}
            onChange={(e) => {
              setSearch(e.target.value);
              setPage(0);
            }}
          />
        </div>

        <div className="select-box">
          <select
            value={statusFilter}
            onChange={(e) => {
              setStatusFilter(e.target.value);
              setPage(0);
            }}
          >
            <option value="ALL">All Status</option>
            <option value="REQUESTED">Requested</option>
            <option value="APPROVED">Approved</option>
            <option value="REJECTED">Rejected</option>
            <option value="COMPLETED">Completed</option>
          </select>
        </div>

      </div>

      {/* ================= TABLE ================= */}
      <div className="table-scroll">
        <table className="admin-table">

          <thead>
            <tr>
              <th>App No</th>
              <th>Name</th>
              <th>Field</th>
              <th>Original Value</th>
              <th>Requested data</th>
              <th>Reason</th>
              <th>Attempts</th>
              <th>Requested At</th>
              <th>Status</th>
              <th>Documents</th>
              <th>Window Start Time</th>
              <th>Window End Time</th>
              <th>Correction Status</th>
              <th>Action</th>
            </tr>
          </thead>

          <tbody>
            {loading ? (
              <tr>
                <td colSpan="10">Loading...</td>
              </tr>
            ) : !requests || requests.length === 0 ? (
              <tr>
                <td colSpan="10" className="no-data">
                  No Requests Found
                </td>
              </tr>
            ) : (
              requests.map(req => (
                <tr key={req.id}>

                  <td>{req.applicationNumber}</td>
                  <td>{req.userName}</td>
                  <td>{req.fieldName}</td>
                  <td>{req.oldValue || "—"}</td>
                  <td>{req.requestedValue}</td>
                  <td>{req.reason}</td>
                  <td>{req.attemptNumber}</td>
                  <td>
                    {req.requestedAt
                      ? new Date(req.requestedAt).toLocaleString("en-IN")
                      : "—"}
                  </td>

                  <td className={`status ${req.status?.toLowerCase()}`}>
                    {req.status}
                  </td>

                  {/* DOCUMENTS */}
                  <td>
                    {req.documentPaths?.length > 0 ? (
                      <button
                        className="view-doc-btn"
                        onClick={() => setSelectedDocs(req.documentPaths)}
                      >
                        View All
                      </button>
                    ) : "—"}
                  </td>
                  <td>
                    {req.windowStart
                      ? new Date(req.windowStart).toLocaleString("en-IN")
                      : "—"}
                  </td>

                  <td>
                    {req.windowEnd
                      ? new Date(req.windowEnd).toLocaleString("en-IN")
                      : "—"}
                  </td>
                  <td>

{/* COMPLETED */}
{(req.correctionCompleted || req.status === "COMPLETED") && (
<span className="completed-label">
✅ User Updated
<br />
{req.userUpdatedAt && (
<small>
{new Date(req.userUpdatedAt).toLocaleString("en-IN")}
</small>
)}
</span>
)}

{/* WINDOW ACTIVE */}
{req.status === "APPROVED" &&
!req.correctionCompleted &&
req.windowActive && (
<span className="pending-label">
⏳ Waiting For User
</span>
)}

{/* WINDOW EXPIRED */}
{req.status === "APPROVED" &&
!req.correctionCompleted &&
!req.windowActive &&
req.windowEnd && new Date(req.windowEnd) <= new Date() && (
<span className="expired-label">
⛔ User not updated, update time over
</span>
)}

{/* WINDOW NOT STARTED */}
{req.status === "APPROVED" &&
!req.correctionCompleted &&
!req.windowStart && (
<span className="pending-label">
—
</span>
)}

</td>

                  <td>
                        <div className="action-cell">

                      
                      {/* ================= REQUESTED ================= */}
                      {req.status === "REQUESTED" && (
                        <>
                          <textarea
                            className="remark-box"
                            placeholder="Enter admin remark"
                            value={remarks[req.id] || ""}
                            onChange={(e) =>
                              setRemarks({
                                ...remarks,
                                [req.id]: e.target.value,
                              })
                            }
                          />

                          <div className="approve-reject-row">
                            <button
                              className="approve-btn"
                              onClick={() => handleAction(req.id, "approve")}
                            >
                              Approve
                            </button>

                            <button
                              className="reject-btn"
                              onClick={() => handleAction(req.id, "reject")}
                            >
                              Reject
                            </button>
                          </div>
                        </>
                      )}

                      {/* ================= REJECTED ================= */}
                      {req.status === "REJECTED" && (
                        <div className="window-info">
                          <span className="expired-label">
                            ❌ Request Rejected
                          </span>

                          {req.reviewedAt && (
                            <div className="expiry-text">
                              Rejected At: {new Date(req.reviewedAt).toLocaleString("en-IN")}
                            </div>
                          )}
                        </div>
                      )}

                      {/* ================= WINDOW MANAGEMENT ================= */}
{(() => {

  const now = new Date();
  const windowStart = req.windowStart ? new Date(req.windowStart) : null;
  const windowEnd = req.windowEnd ? new Date(req.windowEnd) : null;

  const isExpired = windowEnd && windowEnd <= now;
  const isActive = req.windowActive;

  /* ================= CORRECTION COMPLETED ================= */
  if (req.correctionCompleted || req.status === "COMPLETED") {
    return (
      <div className="window-info">

        <span className="completed-label">
          ✅ User Updated requested field & Correction Completed
        </span>

        {req.userUpdatedAt && (
          <div className="expiry-text">
            Updated At:{" "}
            {new Date(req.userUpdatedAt).toLocaleString("en-IN")}
          </div>
        )}

      </div>
    );
  }

  /* ================= ADMIN DEACTIVATED ================= */
  if (req.deActivatedByAdmin) {
    return (
      <div className="window-info">

        <div className="deactivated-text">
          ⛔ Window Deactivated by Admin at{" "}
          {req.deActivatedAt
            ? new Date(req.deActivatedAt).toLocaleString("en-IN")
            : "—"}
        </div>

        <button
          className="activate-window-btn"
          onClick={() => activateWindow(req.id)}
        >
          Activate Window
        </button>

      </div>
    );
  }

  /* ================= APPROVED WINDOW MANAGEMENT ================= */
  if (req.status === "APPROVED") {

    /* ===== WINDOW ACTIVE ===== */
    if (isActive) {
      return (
        <div className="window-info">

          <span className="window-active-label">
            🟢 Window Active
          </span>

          {windowStart && windowEnd && (
            <div className="expiry-text">
              {windowStart.toLocaleString("en-IN")} →{" "}
              {windowEnd.toLocaleString("en-IN")}
            </div>
          )}

          <button
            className="deactivate-window-btn"
            onClick={() => deactivateWindow(req.id)}
          >
            Deactivate
          </button>

        </div>
      );
    }

    /* ===== WINDOW EXPIRED ===== */
    if (isExpired) {
      return (
        <div className="window-info">

          <span className="expired-label">
            ⛔ Correction Window Auto Expired
          </span>

          {windowEnd && (
            <div className="expiry-text">
              Expired At: {windowEnd.toLocaleString("en-IN")}
            </div>
          )}

          <button
            className="activate-window-btn"
            onClick={() => activateWindow(req.id)}
          >
            Activate Window
          </button>

        </div>
      );
    }

    /* ===== WINDOW NOT CREATED ===== */
    return (
      <div className="window-info">

        <span className="pending-label">
          ⏳ Window Not Activated
        </span>

        <button
          className="activate-window-btn"
          onClick={() => activateWindow(req.id)}
        >
          Activate Window
        </button>

      </div>
    );
  }

  return null;

})()}

                      {/* ================= SECONDARY ACTIONS ================= */}
                      <div className="secondary-actions">

                        <button
                          className="view-remark-btn"
                          onClick={() => alert(req.adminRemark || "No Remark")}
                        >
                          View Remark
                        </button>

                        <button
                          className="history-btn"
                          onClick={() => fetchHistory(req.userId, req.fieldName)}
                        >
                          History ({req.attemptNumber || 0})
                        </button>

                        <button
                          className="profile-btn"
                          onClick={() =>
                            window.open(`/admin/user-profile/${req.userId}`, "_blank")
                          }
                        >
                          Profile
                        </button>

                      </div>
                    

                        </div>
                      </td>


                </tr>
              ))
            )}
          </tbody>

        </table>
      </div>

      {/* ================= PAGINATION ================= */}
      <div className="pagination-bar">

        <button
          className="page-btn"
          disabled={page === 0}
          onClick={() => setPage(page - 1)}
        >
          ◀ Prev
        </button>

        <div className="page-info">
          Page {totalPages === 0 ? 0 : page + 1} of {totalPages}
        </div>

        <button
          className="page-btn"
          disabled={page + 1 >= totalPages}
          onClick={() => setPage(page + 1)}
        >
          Next ▶
        </button>

        <div className="jump-box">
          <span>Jump</span>
          <input
            type="number"
            value={jumpPage}
            onChange={(e) => setJumpPage(e.target.value)}
          />
          <button className="page-btn go-btn" onClick={handleJump}>
            Go
          </button>
        </div>

      </div>

      {/* ================= DOCUMENT MODAL ================= */}
{selectedDocs && (
  <div
    className="modal-overlay"
    onClick={() => setSelectedDocs(null)}
  >
    <div
      className="modal-content"
      onClick={(e) => e.stopPropagation()}
    >
      {selectedDocs.map((doc, index) => {
        const fileUrl = doc.startsWith("http")
          ? doc
          : `http://localhost:8080/${doc.replace(/^\/+/, "")}`;

        const isImage = /\.(jpg|jpeg|png)$/i.test(fileUrl);
        const isPdf = /\.pdf$/i.test(fileUrl);

        return (
          <div key={index} className="doc-preview-item">

            {/* IMAGE PREVIEW */}
            {isImage && (
              <>
                <img
                  src={fileUrl}
                  alt="document"
                  className="doc-image"
                />

                <div style={{ marginTop: "8px" }}>
                  <a href={fileUrl} download>
                    <button className="download-btn">
                      📥 Download Image
                    </button>
                  </a>
                </div>
              </>
            )}

            {/* PDF PREVIEW */}
            {isPdf && (
              <>
                <iframe
                  src={fileUrl}
                  width="100%"
                  height="400px"
                  title="PDF Preview"
                />

                <div style={{ marginTop: "8px" }}>
                  <a
                    href={fileUrl}
                    target="_blank"
                    rel="noopener noreferrer"
                  >
                    <button className="download-btn">
                      📥 Open / Download PDF
                    </button>
                  </a>
                </div>
              </>
            )}

            {/* OTHER FILE TYPES */}
            {!isImage && !isPdf && (
              <div>
                <a
                  href={fileUrl}
                  target="_blank"
                  rel="noopener noreferrer"
                >
                  <button className="download-btn">
                    📥 Download Document
                  </button>
                </a>
              </div>
            )}
          </div>
        );
      })}
    </div>
  </div>
)}


      {/* ================= HISTORY TIMELINE POPUP UI ================= */}
{showHistory && Array.isArray(historyData) && (
  <div className="modal-overlay" onClick={() => setShowHistory(false)}>
    <div
      className="modal-content history-modal"
      onClick={(e) => e.stopPropagation()}
    >
      <h3>Correction Timeline</h3>

      {historyData.length === 0 ? (
        <p>No History Found</p>
      ) : (
        <>
          {historyData.map((h) => (
            <div key={h.id} className="timeline-item">

              {/* Attempt */}
              <div className="attempt-header">
                <strong>Attempt:</strong> {h.attemptNumber} / 3
              </div>

              {/* Status */}
              <div>
                <strong>Status:</strong>{" "}
                <span className={`status-badge ${h.status?.toLowerCase()}`}>
                  {h.status}
                </span>
              </div>

              {/* Admin Remark */}
              <div>
                <strong>Admin Remark:</strong>{" "}
                {h.adminRemark || "—"}
              </div>

              {/* Request Time */}
              <div>
                <strong>User Requested At:</strong>{" "}
                {h.requestedAt
                  ? new Date(h.requestedAt).toLocaleString("en-IN", {
                      day: "2-digit",
                      month: "short",
                      year: "numeric",
                      hour: "2-digit",
                      minute: "2-digit",
                    })
                  : "—"}
              </div>

              {/* ================= ADMIN REVIEW TIME ================= */}

              <div>
                {h.status === "APPROVED" && (
                  <>
                    <strong>Admin Approved At:</strong>{" "}
                    {h.reviewedAt
                      ? new Date(h.reviewedAt).toLocaleString("en-IN", {
                          day: "2-digit",
                          month: "short",
                          year: "numeric",
                          hour: "2-digit",
                          minute: "2-digit",
                        })
                      : "—"}
                  </>
                )}

                {h.status === "REJECTED" && (
                  <>
                    <strong>Admin Rejected At:</strong>{" "}
                    {h.reviewedAt
                      ? new Date(h.reviewedAt).toLocaleString("en-IN", {
                          day: "2-digit",
                          month: "short",
                          year: "numeric",
                          hour: "2-digit",
                          minute: "2-digit",
                        })
                      : "—"}
                  </>
                )}

                {h.status === "REQUESTED" && (
                  <>
                    <strong>Admin Reviewed At:</strong> —
                  </>
                )}
              </div>

              {/* Window Start */}
              <div>
                <strong>Window Start:</strong>{" "}
                {h.windowStart
                  ? new Date(h.windowStart).toLocaleString("en-IN", {
                      day: "2-digit",
                      month: "short",
                      year: "numeric",
                      hour: "2-digit",
                      minute: "2-digit",
                    })
                  : "—"}
              </div>

              {/* Window End */}
              <div>
                <strong>Window End:</strong>{" "}
                {h.windowEnd
                  ? new Date(h.windowEnd).toLocaleString("en-IN", {
                      day: "2-digit",
                      month: "short",
                      year: "numeric",
                      hour: "2-digit",
                      minute: "2-digit",
                    })
                  : "—"}
              </div>

              

              {/* Admin Deactivated Time */}
              <div>
                <strong>Deactivated At:</strong>{" "}
                {h.deActivatedAt
                  ? new Date(h.deActivatedAt).toLocaleString("en-IN", {
                      day: "2-digit",
                      month: "short",
                      year: "numeric",
                      hour: "2-digit",
                      minute: "2-digit",
                    })
                  : "—"}
              </div>

              {/* User Updated Time */}
              <div>
                <strong>✅ User Updated At:</strong>{" "}
                {h.userUpdatedAt
                  ? new Date(h.userUpdatedAt).toLocaleString("en-IN", {
                      day: "2-digit",
                      month: "short",
                      year: "numeric",
                      hour: "2-digit",
                      minute: "2-digit",
                    })
                  : "Not Updated"}
              </div>

              <hr />
            </div>
          ))}

          {/* Max Attempt Warning */}
          {historyData.length >= 3 &&
            historyData[0]?.status === "REJECTED" && (
              <div className="max-attempt-warning">
                ⚠ Maximum 3 correction attempts reached.
              </div>
            )}
        </>
      )}

      <button
        className="close-btn"
        onClick={() => setShowHistory(false)}
      >
        Close
      </button>
    </div>
  </div>
)}

    </div>
  );
};

export default AdminCorrectionRequests;