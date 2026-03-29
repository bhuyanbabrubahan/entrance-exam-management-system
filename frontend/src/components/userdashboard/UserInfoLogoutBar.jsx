import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import "../../styles/userdashboard/UserInfoLogoutBar.css";

const storedUser =
  JSON.parse(localStorage.getItem("user")) || {};

const storedLoginInfo =
  JSON.parse(localStorage.getItem("loginInfo")) || {};

const UserInfoLogoutBar = ({ user = {}, loginInfo = {} }) => {
  const navigate = useNavigate();

  /* =====================================================
     LIVE DATE & TIME (updates every second)
  ===================================================== */
  const [currentDateTime, setCurrentDateTime] = useState(
    new Date().toLocaleString("en-IN", {
      weekday: "short",
      day: "2-digit",
      month: "short",
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit",
      second: "2-digit",
    })
  );

  useEffect(() => {
    const timer = setInterval(() => {
      setCurrentDateTime(
        new Date().toLocaleString("en-IN", {
          weekday: "short",
          day: "2-digit",
          month: "short",
          year: "numeric",
          hour: "2-digit",
          minute: "2-digit",
          second: "2-digit",
        })
      );
    }, 1000);

    return () => clearInterval(timer);
  }, []);

  /* =====================================================
     TIME AGO FORMATTER (SAFE + PRODUCTION READY)
  ===================================================== */
  const formatTimeAgo = (dateString) => {
  if (!dateString) return "—";

  const date = new Date(dateString);
  if (isNaN(date)) return "—";

  const now = new Date();
  const diffInSeconds = Math.floor((now - date) / 1000);

  // 🔥 JUST NOW (avoid 0 seconds issue)
  if (diffInSeconds < 5) return "Just now";

  if (diffInSeconds < 60)
    return `${diffInSeconds}s ago`;

  const diffInMinutes = Math.floor(diffInSeconds / 60);
  if (diffInMinutes < 60)
    return `${diffInMinutes}m ago`;

  const diffInHours = Math.floor(diffInMinutes / 60);
  if (diffInHours < 24)
    return `${diffInHours}h ago`;

  const diffInDays = Math.floor(diffInHours / 24);
  if (diffInDays < 7)
    return `${diffInDays}d ago`;

  // Fallback full formatted date
  return date.toLocaleString("en-IN", {
    day: "2-digit",
    month: "short",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
};

  /* =====================================================
     AUTO REFRESH TIME AGO (every 60 seconds)
  ===================================================== */
  const [refreshKey, setRefreshKey] = useState(0);

  useEffect(() => {
    const interval = setInterval(() => {
      setRefreshKey((prev) => prev + 1);
    }, 60000); // refresh every 1 minute

    return () => clearInterval(interval);
  }, []);

  /* =====================================================
     LOGOUT HANDLER
  ===================================================== */
  const handleLogout = () => {
    localStorage.removeItem("jwtToken");

    localStorage.removeItem("user");
    localStorage.removeItem("loginInfo");
    localStorage.clear()
    navigate("/login");
  };

  /* =====================================================
     UI
  ===================================================== */
  return (
    <div className="dashboard-header-bar">
      {/* ---------- LEFT: WELCOME SECTION ---------- */}
      <div className="dashboard-welcome">
        Welcome {(user?.firstName || storedUser?.firstName) || "User"}
        <p>
          Application No: {(user?.applicationNumber || storedUser?.applicationNumber) || "—"}
        </p>
      </div>

      {/* ---------- RIGHT: DATE | LOGOUT | LOGIN INFO ---------- */}
      <div className="dashboard-login-bar">
        {/* Top row: Date & Logout */}
        <div className="login-top-row">
          <span className="current-datetime">{currentDateTime}</span>

          <button className="logout-btn" onClick={handleLogout}>
            Logout
          </button>
        </div>

        {/* Bottom row: Last login info */}
        <small className="login-info" key={refreshKey}>
          Last Login: {formatTimeAgo(loginInfo?.loginTime || storedLoginInfo?.loginTime)}{" "}|{" "}
          IP: {(loginInfo?.ipAddress || storedLoginInfo?.ipAddress) || "—"}
        </small>
      </div>
    </div>
  );
};

export default UserInfoLogoutBar;