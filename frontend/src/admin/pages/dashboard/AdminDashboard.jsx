import React, { useEffect, useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";

import "./AdminDashboard.css";

const AdminDashboard = () => {
  const [stats, setStats] = useState(null);
  const [recentActivity, setRecentActivity] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const navigate = useNavigate();

  const fetchDashboardData = async () => {
    const token = localStorage.getItem("jwtToken");

    if (!token) {
      navigate("/login");
      return;
    }

    try {
      const response = await axios.get(
        "http://localhost:8080/api/admin/dashboard/stats",
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );

      setStats(response.data);

      // Optional recent activity API
      try {
        const activityRes = await axios.get(
          "http://localhost:8080/api/admin/dashboard/activity",
          {
            headers: { Authorization: `Bearer ${token}` },
          }
        );
        setRecentActivity(activityRes.data);
      } catch (e) {
        console.log("No activity API yet");
      }

      setError("");
    } catch (err) {
      if (err.response?.status === 401 || err.response?.status === 403) {
        navigate("/login");
      } else {
        setError("Cannot connect to backend server.");
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDashboardData();
  }, []);

  if (loading) return <h3>Loading dashboard...</h3>;
  if (error) return <h3 style={{ color: "red" }}>{error}</h3>;

  return (
    <div className="dashboard-wrapper">
      <h2 className="dashboard-heading">Admin Dashboard</h2>

      {/* ================= STATS CARDS ================= */}
      <div className="card-container">
        <div
          className="card clickable"
          onClick={() => navigate("/admin/candidates")}
        >
          <h4>Total Candidates</h4>
          <p>{stats?.totalCandidates ?? 0}</p>
        </div>

        <div className="card clickable">
          <h4>Submitted Applications</h4>
          <p>{stats?.submittedApplications ?? 0}</p>
        </div>

        <div className="card clickable">
          <h4>Pending Reviews</h4>
          <p>{stats?.pendingReviews ?? 0}</p>
        </div>

        <div className="card clickable">
          <h4>Upcoming Exams</h4>
          <p>{stats?.upcomingExams ?? 0}</p>
        </div>
      </div>

      {/* ================= QUICK ACTIONS ================= */}
      <div className="quick-actions">
        <h3>Quick Actions</h3>

        <div className="quick-grid">
          <button onClick={() => navigate("/admin/candidates")}>
            Manage Candidates
          </button>

          <button onClick={() => navigate("/admin/news")}>
            Manage News
          </button>

          <button onClick={() => navigate("/admin/correction")}>
            Correction Window
          </button>

          <button onClick={() => navigate("/admin/audit")}>
            View Audit Logs
          </button>

          <button onClick={() => navigate("/admin/user-requested-admin-correction")}>
            User Requested Correction Window
          </button>

          <button onClick={() => navigate("/admin/user_activity_track")}>
            User Activity track
          </button>
        </div>
      </div>

      {/* ================= RECENT ACTIVITY ================= */}
      <div className="recent-section">
        <h3>Recent Activity</h3>

        {recentActivity.length === 0 ? (
          <p>No recent activity available.</p>
        ) : (
          <ul>
            {recentActivity.map((item, index) => (
              <li key={index}>
                {item.message} - <span>{item.date}</span>
              </li>
            ))}
          </ul>
        )}
      </div>
    </div>
  );
};

export default AdminDashboard;