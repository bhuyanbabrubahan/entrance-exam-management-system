/*****************************************************************
 ✅ USER CORRECTION REQUEST PAGE
 ✅ Production Ready
 ✅ Pagination + Scroll
 ✅ Fully Responsive
 ✅ Window Activation / Deactivation
 ✅ Countdown Timer
******************************************************************/

import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import Header from "../../components/layout/Header";
import UserInfoLogoutBar from "../../components/userdashboard/UserInfoLogoutBar";
import "../../styles/userdashboard/UserCorrection.css";

  const UserCorrectionRequest = () => {
  const [message, setMessage] = useState("");
  const [messageType, setMessageType] = useState("");

  /* =====================================================
      NAVIGATION + TOKEN
  ===================================================== */
  const navigate = useNavigate();
  const token = localStorage.getItem("jwtToken");

  /* =====================================================
      STATE MANAGEMENT
  ===================================================== */
  const [user, setUser] = useState({});
  const [loginInfo, setLoginInfo] = useState({});
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [selectedDocs, setSelectedDocs] = useState([]);
  const [remainingTime, setRemainingTime] = useState({});

  /* ---------- Pagination ---------- */
  const [currentPage, setCurrentPage] = useState(1);
  const rowsPerPage = 5;
 
  const [formData, setFormData] = useState({
    fieldName: "",
    requestedValue: "",
    reason: "",
    document: [],
  });

  useEffect(() => {
  if (message) {
    const timer = setTimeout(() => {
      setMessage("");
    }, 4000);

    return () => clearTimeout(timer);
  }
}, [message]);

  const formatDateTime = (date) => {
  if (!date) return "";

  return new Date(date)
    .toLocaleString("en-IN", {
      day: "numeric",
      month: "numeric",
      year: "numeric",
      hour: "numeric",
      minute: "2-digit",
      second: "2-digit",
      hour12: true,
    })
    .toLowerCase();
};
  /* =====================================================
      FETCH USER + HISTORY (WITH WINDOW INFO)
  ===================================================== */
  useEffect(() => {
    if (!token) {
      navigate("/dashboard", { replace: true });
      return;
    }

    const fetchDashboardData = async () => {
      try {
       
        /* ---------------- USER INFO ---------------- */
        const userRes = await fetch(
          "http://localhost:8080/api/user/user-correction-request/current",
          { headers: { Authorization: `Bearer ${token}` } }
        );
        const userData = await userRes.json();
        setUser(userData.user || {});
        setLoginInfo(userData.loginInfo || {});

        /* ---------------- HISTORY + WINDOW ---------------- */
        const historyRes = await fetch(
          "http://localhost:8080/api/user/user-correction-request/my-requests",
          { headers: { Authorization: `Bearer ${token}` } }
        );

        if (historyRes.ok) {
          const historyData = await historyRes.json();

          // Normalize data
          const updatedHistory = historyData.map(item => ({
            ...item,
            windowActive: item.windowActive === true,
            windowStart: item.windowStart ? new Date(item.windowStart) : null,
            windowEnd: item.windowEnd ? new Date(item.windowEnd) : null
          }));

          setHistory(updatedHistory);
        }

      } catch (error) {
        console.error("Dashboard Fetch Error:", error);
        localStorage.removeItem("jwtToken");
        navigate("/dashboard", { replace: true });
      } finally {
        setLoading(false);
      }
    };

    fetchDashboardData();
  }, [navigate, token]);

    /* =====================================================
      AUTO REFRESH HISTORY (EVERY 10 SECONDS)
    ===================================================== */
    useEffect(() => {
      const interval = setInterval(() => {
        fetchHistory();
      }, 10000); // refresh every 10 seconds

      return () => clearInterval(interval);
    }, []);

  /* =====================================================
      COUNTDOWN TIMER FOR WINDOWS
  ===================================================== */
  useEffect(() => {
    const interval = setInterval(() => {
      const updated = {};
      history.forEach(item => {
        if (item.windowActive && item.windowEnd) {
          const diff = item.windowEnd - new Date();
          if (diff > 0) {
            const days = Math.floor(diff / (1000*60*60*24));
            const hours = Math.floor((diff / (1000*60*60)) % 24);
            const minutes = Math.floor((diff / (1000*60)) % 60);
            const seconds = Math.floor((diff / 1000) % 60);
            updated[item.id] = `${days}d ${hours}h ${minutes}m ${seconds}s`;
          } else {
            updated[item.id] = "Expired";
          }
        }
      });
      setRemainingTime(updated);
    }, 1000);

    return () => clearInterval(interval);
  }, [history]);

  /* =====================================================
      INPUT HANDLER
  ===================================================== */
  const handleChange = (e) => {
    const { name, value, files } = e.target;
    if (name === "document") {
      setFormData(prev => ({ ...prev, document: files ? Array.from(files) : [] }));
    } else {
      setFormData(prev => ({ ...prev, [name]: value }));
    }
  };

  /* =====================================================
      FETCH HISTORY (ADD THIS FUNCTION)
===================================================== */
const fetchHistory = async () => {
  try {
    const historyRes = await fetch(
      "http://localhost:8080/api/user/user-correction-request/my-requests",
      { headers: { Authorization: `Bearer ${token}` } }
    );

    if (historyRes.ok) {
      const historyData = await historyRes.json();

      const updatedHistory = historyData.map(item => ({
        ...item,
        windowActive: item.windowActive === true,
        windowStart: item.windowStart ? new Date(item.windowStart) : null,
        windowEnd: item.windowEnd ? new Date(item.windowEnd) : null
      }));

      setHistory(updatedHistory);
    }
  } catch (error) {
    console.error("History refresh error:", error);
  }
};

  /* =====================================================
      SUBMIT REQUEST
  ===================================================== */
  const handleSubmit = async (e) => {
  e.preventDefault();

  if (!formData.fieldName) {
    setMessageType("error");
    setMessage("Please select field");
    return;
  }

  if (formData.document.length === 0) {
    setMessageType("error");
    setMessage("Please upload supporting document");
    return;
  }

  setSubmitting(true);

  try {

    const data = new FormData();
    data.append("fieldName", formData.fieldName);
    data.append("requestedValue", formData.requestedValue);
    data.append("reason", formData.reason);

    formData.document.forEach(file =>
      data.append("documents", file)
    );

    const res = await fetch(
      "http://localhost:8080/api/user/user-correction-request/request",
      {
        method: "POST",
        headers: { Authorization: `Bearer ${token}` },
        body: data
      }
    );

    const result = await res.json();

    if (!res.ok) {
      throw new Error(result.message || "Request failed");
    }

    setMessageType("success");
    setMessage(
      "✅ Your correction request has been submitted successfully.\nIt is now pending admin review."
    );

    /* ⭐ ADD THIS LINE */
    await fetchHistory();

    setFormData({
      fieldName: "",
      requestedValue: "",
      reason: "",
      document: []
    });

  } catch (err) {
    setMessageType("error");
    setMessage(err.message || "Already requested");

  } finally {
    setSubmitting(false);
  }
};

 
  /* =====================================================
      PAGINATION LOGIC
  ===================================================== */
  const indexLast = currentPage * rowsPerPage;
  const indexFirst = indexLast - rowsPerPage;
  const currentRows = history.slice(indexFirst, indexLast);
  const totalPages = Math.ceil(history.length / rowsPerPage);

  if (loading)
  return (
    <div className="dashboard-loading">
      ⏳ Loading correction requests...
    </div>
  );

  /* =====================================================
      RENDER LOGIC
  ===================================================== */
  return (
    <>
      <Header user={user} />
      <UserInfoLogoutBar user={user} loginInfo={loginInfo} />

      {/* Correction Form */}
      <div className="correction-wrapper">
        {/* =========message shows========*/}
        
        {message && (
          <div
            className={`correctionStatus_message ${messageType}`}
            style={{ whiteSpace: "pre-line" }}
          >
            {message}
          </div>
        )}

        <div className="correction-card">
          <h2>📝 Request Identity Correction</h2>
          <form onSubmit={handleSubmit} className="correction-form">
            <div className="form-group">
              <label>Select Field</label>
              <select name="fieldName" value={formData.fieldName} onChange={handleChange} required>
                <option value="">--Select--</option>
                <option value="FIRST_NAME">First Name</option>
                <option value="LAST_NAME">Last Name</option>
                <option value="DOB">DOB</option>
                <option value="GENDER">Gender</option>
                <option value="AADHAAR">Aadhaar Number</option>
              </select>
            </div>

            <div className="form-group">
              <label>Requested Value</label>
              <input type="text" name="requestedValue" value={formData.requestedValue} onChange={handleChange} required />
            </div>

            <div className="form-group full">
              <label>Reason</label>
              <textarea name="reason" value={formData.reason} onChange={handleChange} required />
            </div>

            <div className="form-group full">
              <label>Supporting Document</label>
              <input type="file" name="document" multiple accept=".pdf,.jpg,.jpeg,.png" onChange={handleChange} />
              {formData.document.length > 0 && <p>{formData.document.length} file(s) selected</p>}
            </div>

            <button type="submit" className="submit-btn" disabled={submitting}>
              {submitting ? "Submitting..." : "Submit Request"}
            </button>
          </form>
        </div>
        
           {/* =====================================================
  HISTORY TABLE
===================================================== */}
<div className="correction-card">

  <h3>📜 Previous Requests</h3>

  <div className="table-scroll">

    <table className="correction-table">

      <thead>
        <tr>
          <th>Field</th>
          <th>Original Value</th>
          <th>Requested Value</th>
          <th>My Reason</th>
          <th>Status</th>
          <th>Requested At</th>
          <th>Reviewed At</th>
          <th>Remark</th>
          <th>Window Started</th>
          <th>Window Expired</th>
          <th>View Documents</th>
          <th>Attempts</th>
        </tr>
      </thead>

      <tbody>

        {currentRows.length === 0 ? (
          <tr>
            <td colSpan="12" className="no-data">
              No Requests Found
            </td>
          </tr>
        ) : (

currentRows.map((item, index) => {

  let docs = [];

  /* ================= DOCUMENT HANDLING ================= */

  if (item.documentPaths) {

    if (Array.isArray(item.documentPaths)) {
      docs = item.documentPaths;
    }

    else if (typeof item.documentPaths === "string") {
      docs = item.documentPaths.split(",");
    }

  }

  docs = docs
    .filter(d => typeof d === "string" && d.trim() !== "")
    .map(d => d.trim());

  /* ================= WINDOW VARIABLES ================= */

  const now = new Date();

  const windowStart = item.windowStart ? new Date(item.windowStart) : null;
  const windowEnd = item.windowEnd ? new Date(item.windowEnd) : null;

 
  const isApproved = item.status === "APPROVED";
  const isRejected = item.status === "REJECTED";
  const isCompleted = item.status === "COMPLETED";

  const isDeactivated = item.deActivatedByAdmin === true;

  const isExpired =
    windowStart && windowEnd && now >= windowEnd;

  return (

<tr key={item.id}>

<td>{item.fieldName || "—"}</td>

<td>{item.oldValue || "—"}</td>

<td>{item.requestedValue || "—"}</td>

<td>{item.reason || "—"}</td>


{/* ================= STATUS COLUMN ================= */}

<td className={`status ${item.status?.toLowerCase()}`}>

{(() => {

  /* ================= REQUESTED ================= */
  if (item.status === "REQUESTED") {

    return (
      <>
        <div
          className="status-text"
          style={{ color: "#f59e0b", fontWeight: "600" }}
        >
          REQUESTED
        </div>

        {/* NORMAL REQUESTED */}
        {!item.deActivatedByAdmin && (
          <div className="waiting-text">
            ⏳ Waiting for Admin Approval
          </div>
        )}

        {/* ADMIN DEACTIVATED */}
        {item.deActivatedByAdmin && (
          <div className="expired-text" style={{ color: "red" }}>
            ⛔ Deactivated by Admin
            <div>
              Timestamp:{" "}
              {item.deActivatedAt
                ? new Date(item.deActivatedAt).toLocaleString("en-IN")
                : "—"}
            </div>
          </div>
        )}
      </>
    );
  }

  /* ================= COMPLETED ================= */
  if (item.status === "COMPLETED") {
    return (
      <>
        <div className="status-text" style={{color:"#f8faf9",fontWeight:"600"}}>
          <strong>COMPLETED</strong> 
        </div>

        <div style={{color:"#c5d1c9",fontWeight:"300"}}>
          ✔ User Updated Successfully
        </div>
      </>
    );
  }

  /* ================= REJECTED ================= */
  if (item.status === "REJECTED") {
    return (
      <>
        <div className="status-text" style={{color:"red"}}>
          REJECTED
        </div>

        <div className="expired-text">
          🚫 Rejected by Admin at{" "}
          {formatDateTime(item.reviewedAt)}
          {item.adminRemark && ` - Reason: ${item.adminRemark}`}
        </div>
      </>
    );
  }

  /* ================= APPROVED ================= */
if (item.status === "APPROVED") {

  const now = new Date();
  const windowStart = item.windowStart ? new Date(item.windowStart) : null;
  const windowEnd = item.windowEnd ? new Date(item.windowEnd) : null;

  const isExpired = windowEnd && now >= windowEnd;

  return (

    <>
      <div className="status-text" style={{color:"#16a34a",fontWeight:"600"}}>
        APPROVED
      </div>

      {/* 1️⃣ WINDOW NOT ACTIVATED */}
      {!windowStart && !item.deActivatedByAdmin && (
        <div className="waiting-text">
          ⏳ Waiting for admin to activate correction window
        </div>
      )}

      {/* 2️⃣ ADMIN DEACTIVATED */}
      {item.deActivatedByAdmin && (
        <div className="expired-text" style={{color:"red"}}>
          ⛔ Window Deactivated by Admin
          <div>
            At: {item.deActivatedAt
              ? new Date(item.deActivatedAt).toLocaleString("en-IN")
              : "—"}
          </div>
        </div>
      )}

      {/* 3️⃣ AUTO EXPIRED */}
      {windowStart && isExpired && !item.deActivatedByAdmin && (
        <div className="expired-text" style={{color:"red"}}>
          ⛔ Correction Window Auto Expired
          <div>
            At: {windowEnd?.toLocaleString("en-IN")}
          </div>
        </div>
      )}

      {/* 4️⃣ WINDOW ACTIVE */}
{item.windowActive && windowStart && !isExpired && !item.deActivatedByAdmin && (
  <div className="waiting-text">

    <button
      className="update-btn"
      onClick={() =>
        navigate(`/dashboard/basic-correction-update/${item.id}`)
      }
    >
      Update Basic Details
    </button>

    <div className="start-time">
      Window Start: {formatDateTime(item.windowStart)}
    </div>

    

  </div>
)}

    </>

  );
}

  return "—";

})()}

</td>


{/* ================= REQUESTED DATE ================= */}
<td>
  {item.requestedAt
  ? new Date(item.requestedAt).toLocaleString("en-IN")
  : "—"}
</td>


{/* ================= REVIEWED DATE ================= */}

<td>
    {item.reviewedAt ? (
    <>
      {item.status === "APPROVED" && "Approved at "}
      {item.status === "REJECTED" && "Rejected at "}
      {item.status === "COMPLETED" && "Completed at "}
      {new Date(item.reviewedAt).toLocaleString("en-IN")}
    </>
    ) : "—"}
</td>


{/* ================= ADMIN REMARK ================= */}

<td>{item.adminRemark || "—"}</td>


{/* ================= WINDOW STARTED ================= */}
<td>
    {windowStart ? (
    <div>
      <div>✅ <strong>Window Activated</strong></div>

        <div>
        Activated At: {formatDateTime(item.windowStart)}
        </div>

      </div>
    ) : "—"}
</td>

{/* ================= WINDOW STATUS ================= */}
<td>

{(() => {

  /* USER COMPLETED */
  if (item.status === "COMPLETED") {
    return (
      <div style={{color:"#16a34a",fontWeight:"600"}}>
        ✔ User Updated At:
        <div>
          {new Date(item.reviewedAt || item.updatedAt).toLocaleString("en-IN")}
        </div>
      </div>
    );
  }

  /* ADMIN DEACTIVATED */
  if (item.deActivatedByAdmin) {
    return (
      <div style={{color:"red",fontWeight:"600"}}>
        ⛔ User not Updated
        <div>⛔ Window Deactivated by admin At:</div>
        <div>
          {new Date(item.deActivatedAt).toLocaleString("en-IN")}
        </div>
      </div>
    );
  }

  /* AUTO EXPIRED */
  if (!item.windowActive && item.windowStart && item.status !== "COMPLETED") {
    return (
      <div style={{color:"red"}}>
        ⏰ Window Expired
        <div>
          Expired At:{" "}
          {item.windowEnd
            ? new Date(item.windowEnd).toLocaleString("en-IN")
            : "—"}
        </div>
      </div>
    );
  }

  return "—";

})()}

</td>




{/* ================= DOCUMENTS ================= */}

<td>

{docs.length > 0 ? (

<button
className="doc-btn"
onClick={() => setSelectedDocs(docs)}
>
View ({docs.length})
</button>

) : (

<span className="no-doc">—</span>

)}

</td>


{/* ================= ATTEMPTS ================= */}

<td>{item.attemptNumber ?? 0}</td>

</tr>

);

})

)}

</tbody>

</table>

</div>

</div>
          {/* ================= PAGINATION ================= */}

              <div className="pagination-bar">

                <button
                  className="page-btn"
                  disabled={currentPage === 1}
                  onClick={() => setCurrentPage(prev => prev - 1)}
                >
                  ◀ Prev
                </button>

                <div className="page-info">
                  Page {totalPages === 0 ? 0 : currentPage} of {totalPages}
                </div>

                <button
                  className="page-btn"
                  disabled={currentPage >= totalPages || totalPages === 0}
                  onClick={() => setCurrentPage(prev => prev + 1)}
                >
                  Next ▶
                </button>

              </div>  

        {/* ================= DOCUMENT MODAL ================= */}
        {selectedDocs.length > 0 && (
          <div
            className="modal-overlay"
            onClick={() => setSelectedDocs([])}
          >
            <div
              className="modal-content"
              onClick={(e) => e.stopPropagation()}
            >
              <button
                className="close-btn"
                onClick={() => setSelectedDocs([])}
              >
                ✖
              </button>

              <div className="modal-doc-container">
                {selectedDocs
              .filter(d => typeof d === "string" && d.trim() !== "")
              .map((doc, index) => {

                const fileUrl =
                  doc.startsWith("http")
                    ? doc
                    : `http://localhost:8080/${doc.replace(/^\/+/, "")}`;

                const isImage = /\.(jpg|jpeg|png)$/i.test(fileUrl);
                const isPdf = /\.pdf$/i.test(fileUrl);

                return (
                  <div key={index} className="doc-item">

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

                    {/* PDF PREVIEW + DOWNLOAD */}
                    {isPdf && (
                      <>
                        <iframe
                          src={fileUrl}   // ✅ FIXED HERE
                          width="100%"
                          height="400px"
                          title="PDF Preview"
                        />

                        <div style={{ marginTop: "8px" }}>
                          <a href={fileUrl} target="_blank" rel="noopener noreferrer">
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
                        <a href={fileUrl} target="_blank" rel="noopener noreferrer">
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
          </div>
        )}

      </div>
    </>
  );
};

export default UserCorrectionRequest;