import React, { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import Header from "../../components/layout/Header";
import UserInfoLogoutBar from "../../components/userdashboard/UserInfoLogoutBar";
import "../../styles/userdashboard/UserDashboard.css";

const UserDashboard = () => {
  const navigate = useNavigate();

  const [user, setUser] = useState(null);
  const [loginInfo, setLoginInfo] = useState(null);
  const [loading, setLoading] = useState(true);
  const [doc, setDoc] = useState(null);
  const [correctionStatus, setCorrectionStatus] = useState(null);

  
 /* ================= FETCH DASHBOARD ================= */
useEffect(() => {
  const token = localStorage.getItem("jwtToken");
  if (!token) {
    navigate("/login");
    return;
  }

  // ===== DASHBOARD DATA =====
  fetch("http://localhost:8080/api/user/dashboard/current", {
    headers: { Authorization: `Bearer ${token}` },
  })
    .then((res) => {
      if (!res.ok) throw new Error("Unauthorized");
      return res.json();
    })
    .then((data) => {
      setUser(data.user);
      setLoginInfo(data.loginInfo);
    })
    .catch(() => {
      navigate("/login");
    });

  // ===== DOCUMENT DATA (PROFILE PHOTO) =====
  fetch("http://localhost:8080/api/user/document-details/current", {
    headers: { Authorization: `Bearer ${token}` },
  })
    .then((res) => (res.ok ? res.json() : null))
    .then((res) => {
      const d = res?.documents || {};
      setDoc({
        photo: d.photoUrl || d.photo,
      });
    })
    .finally(() => setLoading(false));

}, [navigate]);

/* ================= CORRECTION WINDOW STATUS ================= */
useEffect(() => {

  const token = localStorage.getItem("jwtToken");
  if (!token) return;

  fetch("http://localhost:8080/api/user/user-correction-request/status", {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  })
    .then(res => {
      console.log("STATUS CODE:", res.status);

      if (!res.ok) {
        throw new Error("Failed with status " + res.status);
      }

      return res.json();
    })
    .then(data => {
      console.log("Correction Status Data:", data);
      setCorrectionStatus(data);
    })
    .catch(error => {
      console.error("Correction Status Fetch Error:", error);
    });

}, []);


{/*===================Correction page status================== */}
useEffect(() => {
    fetchDashboardStatus();
  }, []);

  const fetchDashboardStatus = async () => {
    try {
      const token = localStorage.getItem("jwtToken");

      const response = await fetch(
        "http://localhost:8080/api/user/user-correction-request/dashboard-status",
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      const data = await response.json();
      setCorrectionStatus(data);

    } catch (error) {
      console.error("Error fetching dashboard status:", error);
    }
  };

  if (loading) return <div className="dashboard-loading">Loading...</div>;
  if (!user) return <div>User not found</div>;

  // ===== HELPERS =====
  const isCompleted = (status) => {
    const s = String(status || "").toUpperCase();
    return s === "COMPLETED" || s === "REOPENED";
  };

  const labelMap = {
    NOT_STARTED: "Not Started",
    PENDING: "Pending",
    IN_PROGRESS: "In Progress",
    COMPLETED: "Completed",
    REOPENED: "Reopened",
    UNKNOWN: "Unknown",
  };

  /* ================= DASHBOARD STEPS ================= */
  const steps = [
  {
    title: "Personal Details",
    status: user.personalStatus || "NOT_STARTED",
    route: "personal-details",   // ✅ FIX
    unlocked: true,
    buttonLabel: isCompleted(user.personalStatus) ? "View" : "Fill Now",
  },
  {
    title: "Education Details",
    status: user.educationStatus || "NOT_STARTED",
    route: "education-details",  // ✅ FIX
    // 🔥 Unlock only after Personal is completed
    unlocked: isCompleted(user.personalStatus),
    buttonLabel: isCompleted(user.educationStatus) ? "View" : "Fill Now",
  },
  {
    title: "Document Upload",
    status: user.documentStatus || "NOT_STARTED",
    route: "document-details",

    // ✅ UNLOCK WHEN EDUCATION IS COMPLETED
    unlocked: isCompleted(user.educationStatus),
    buttonLabel: isCompleted(user.documentStatus) ? "View" : "Fill Now",
  },
  {
    title: "Payment",
    status: user.paymentStatus || "NOT_STARTED",
    route: "payment", 
    unlocked: isCompleted(user.documentStatus),
    buttonLabel: isCompleted(user.paymentStatus) ? "View" : "Fill Now",
  },
];

  const completedSteps = steps.filter((s) => isCompleted(s.status)).length;
  const progressPercent = Math.round((completedSteps / steps.length) * 100);

  return (
    <>
      {/* ===== FIXED HEADER ===== */}
      <Header />
      <UserInfoLogoutBar user={user} loginInfo={loginInfo} />

      {/* ===== CORRECTION WINDOW ALERT ===== */}
        {correctionStatus?.active && (
          <div className="correction-banner">
            🟢 Correction Window Active —
            You can edit reopened sections before expiry.
          </div>
        )}

      {/* ========================================== DASHBOARD BODY ================================= */}
      <div className="dashboard-wrapper">
        <div className="dashboard-layout">
          {/* ================================================= */}
          {/* =============== LEFT COLUMN ===================== */}
          {/* ================================================= */}
          <div className="dashboard-left">
            {/* ===== PROFILE CARD ===== */}
            <div className="profile-card">
              {doc?.photo && (
                <img
                  src={doc.photo}
                  alt="Profile"
                  className="profile-image"
                />
              )}

              <h4>
                {user.firstName} {user.middleName} {user.lastName}
              </h4>

              <p className="app-no">
                Application No: <strong>{user.applicationNumber}</strong>
              </p>

              <div className="profile-info">
                <p>
                  <strong>Mobile:</strong> {user.mobileNumber}
                </p>
                <p>
                  <strong>Email:</strong> {user.email}
                </p>
                <p>
                  <strong>Status:</strong> {user.enabled ? "Active" : "Blocked"}
                </p>
              </div>

              <button
                className="btn outline"
                onClick={() => navigate("/profile-photo")}
              >
                Edit Profile Photo
              </button>
            </div>

            {/* ===== ACCOUNT SETTINGS (MOVED HERE) ===== */}
            <div className="dashboard-card">
              <h3>⚙️ Account Settings</h3>

              <div className="account-actions vertical">
                <button onClick={() => navigate("/change-password")}>
                  Change Password
                </button>

                <button onClick={() => navigate("/change-email")}>
                  Change Email
                </button>

                <button onClick={() => navigate("/change-mobile")}>
                  Change Mobile
                </button>

                <button onClick={() => navigate("/login-history")}>
                  Login History
                </button>

                <button
                  className="danger"
                  onClick={() => {
                    localStorage.removeItem("jwtToken");
                    navigate("/login");
                  }}
                >
                  Logout All Sessions
                </button>
              </div>
            </div>
          </div>

          {/* ================================================= */}
          {/* =============== RIGHT COLUMN ==================== */}
          {/* ================================================= */}
          <div className="dashboard-right">
            {/* ===== APPLICATION PROGRESS ===== */}
            <div className="dashboard-card">
              <h3>Application Progress</h3>

              <div className="progress-bar">
                <div
                  className="progress-fill"
                  style={{ width: `${progressPercent}%` }}
                ></div>
              </div>

              <p>
                <strong>{progressPercent}% Completed</strong>
              </p>
              <p className="muted">
                Current Status:{" "}
                <span className="application-status">
                  {completedSteps === steps.length
                    ? "APPLICATION SUBMITTED"
                    : "APPLICATION IN PROGRESS"}
                </span>
              </p>
            </div>

            {/* ===== APPLICATION STEPS ===== */}
            <div className="dashboard-grid">
              {steps.map((step, index) => {
                const completed = isCompleted(step.status);

                return (
                  <div
                    key={index}
                    className={`dashboard-card step-card ${
                      completed
                        ? "completed"
                        : step.unlocked
                        ? "active"
                        : "locked"
                    }`}
                  >
                    <h4>{step.title}</h4>
                    <p className="status">
                      Status:{" "}
                      <span className={completed ? "green" : "red"}>
                        {labelMap[step.status] || step.status}
                      </span>
                    </p>

                    <button
                      className={`btn ${
                        completed ? "view" : step.unlocked ? "primary" : "locked"
                      }`}
                      disabled={!step.unlocked}
                      onClick={() =>
                        step.unlocked &&
                        navigate(step.route, {
                          state: { mode: completed ? "VIEW" : "EDIT" },
                        })
                      }
                    >
                      {completed
                        ? "View"
                        : step.unlocked
                        ? "Fill Now"
                        : "Locked"}
                    </button>
                  </div>
                );
              })}
            </div>

            {/* ===== IMPORTANT NOTICES ===== */}
            <div className="dashboard-card">
              <h3>📢 Important Notices</h3>
              <ul className="notice-list">
                <li>Correction window will open from 02–05 Feb 2026</li>
                <li>Admit Card release date: 10 Feb 2026</li>
              </ul>
            </div>

              {/* ===== CORRECTION REQUEST STATUS ===== */}
                <div className="dashboard-card correction-section">
                  <h3>📝 Correction Requests</h3>

                  <div className="correction-box">
                    <p>
                      <strong>Total Requests:</strong> {correctionStatus?.totalRequests || 0}
                    </p>
                    <p>
                      <strong>Last Updated:</strong> {correctionStatus?.lastUpdated
                        ? new Date(correctionStatus.lastUpdated).toLocaleString()
                        : "—"}
                    </p>
                    <p>
                      <strong>Status:</strong>{" "}
                      <span className={
                        correctionStatus?.status === "APPROVED"
                          ? "green"
                          : correctionStatus?.status === "REJECTED"
                          ? "red"
                          : "pending"
                      }>
                        {correctionStatus?.status || "No Request"}
                      </span>
                    </p>

                    <button
                      className="btn primary"
                      onClick={() => navigate("/dashboard/user-correction-request")}
                    >
                      Request Correction
                    </button>
                  </div>
                </div>


                {/* ===== GRIEVANCE / HELP DESK ===== */}
                <div className="dashboard-card grievance-section">
                  <h3>🎫 Help Desk / Grievance</h3>

                  <p>
                    Submit queries related to form, payment, technical issues or admit card.
                  </p>

                  <button
                    className="btn outline"
                    onClick={() => navigate("/grievance")}
                  >
                    Raise Ticket
                  </button>
                </div>

            {/* ===== DOWNLOADS ===== */}
            {completedSteps === steps.length && (
              <div className="dashboard-card">
                <h3>📄 Downloads</h3>
                <div className="download-actions">
                  <button onClick={() => navigate("/print-form")}>
                    Application PDF
                  </button>
                  <button onClick={() => navigate("/payment-receipt")}>
                    Payment Receipt
                  </button>
                  <button onClick={() => navigate("/admit-card")}>
                    Admit Card
                  </button>
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </>
  );
};

export default UserDashboard;
