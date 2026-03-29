import { useEffect, useState } from "react";
import axios from "axios";
import "./UserActivityTrack.css";

const UserActivityTrack = () => {

  const [activities, setActivities] = useState([]);
  const [search, setSearch] = useState("");
  const [loading, setLoading] = useState(true);

  const pageName = "User Activity Monitor";

  /* ================= START SESSION ================= */
  const startSession = async () => {
    try {
      const token = localStorage.getItem("jwtToken");
      if (!token) return;

      await axios.post(
        "http://localhost:8080/api/user/page-session/start",
        { pageName: pageName },
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
    } catch (err) {
      console.error("Session start error", err);
    }
  };

  /* ================= END SESSION ================= */
  const endSession = async () => {
    try {
      const token = localStorage.getItem("jwtToken");
      if (!token) return;

      await axios.post(
        "http://localhost:8080/api/user/page-session/end",
        { pageName: pageName },
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
    } catch (err) {
      console.error("Session end error", err);
    }
  };

  /* ================= FETCH ACTIVITY ================= */
  const fetchActivity = async () => {
    try {
      const token = localStorage.getItem("jwtToken");
      if (!token) return;

      const res = await axios.get(
        "http://localhost:8080/api/admin/activity/latest",
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      // Map activities: if pageName or durationSeconds missing, provide defaults
      const mappedActivities = res.data.map((item) => {
        return {
          ...item,
          pageName: item.pageName || "User Activity Monitor",
          durationSeconds:
            item.durationSeconds != null
              ? item.durationSeconds
              : Math.floor(
                  (new Date().getTime() -
                    new Date(item.activityTime).getTime()) /
                    1000
                ), // live duration fallback
        };
      });

      console.log("Mapped activities:", mappedActivities);
      setActivities(mappedActivities);
      setLoading(false);
    } catch (err) {
      console.error("Activity fetch error", err);
      setLoading(false);
    }
  };

  /* ================= PAGE LOAD EFFECT ================= */
  useEffect(() => {
    fetchActivity();
    startSession();

    const interval = setInterval(fetchActivity, 10000);

    const handleUnload = () => {
      endSession();
    };

    window.addEventListener("beforeunload", handleUnload);

    return () => {
      clearInterval(interval);
      window.removeEventListener("beforeunload", handleUnload);
      endSession();
    };
  }, []);

  /* ================= FORMAT TIME ================= */
  const formatTime = (time) => {
    if (!time) return "-";
    return new Date(time).toLocaleString();
  };

  /* ================= FORMAT PAGE TIME ================= */
  const formatDuration = (seconds) => {
    if (seconds === null || seconds === undefined) return "-";
    const min = Math.floor(seconds / 60);
    const sec = seconds % 60;
    return `${min}m ${sec}s`;
  };

  /* ================= SEARCH FILTER ================= */
  const filtered = activities.filter((a) =>
    a.email?.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <div className="activity-container">
      <div className="activity-header">
        <h2>User Activity Monitor</h2>
        <span className="live-indicator">● LIVE</span>
      </div>

      <div className="search-box">
        <input
          placeholder="Search user email..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
      </div>

      <div className="activity-table-wrapper">
        <table className="activity-table">
          <thead>
            <tr>
              <th>Email</th>
              <th>Endpoint</th>
              <th>Method</th>
              <th>IP</th>
              <th>Device</th>
              <th>Browser</th>
              <th>Time</th>
              <th>Page</th>
              <th>Page Time</th>
            </tr>
          </thead>

          <tbody>
            {loading ? (
              <tr>
                <td colSpan="9" className="center">
                  Loading activity...
                </td>
              </tr>
            ) : filtered.length === 0 ? (
              <tr>
                <td colSpan="9" className="center">
                  No activity found
                </td>
              </tr>
            ) : (
              filtered.map((item) => (
                <tr key={item.id}>
                  <td className="email">{item.email}</td>
                  <td className="endpoint">{item.endpoint}</td>
                  <td>
                    <span className={`method ${item.method}`}>{item.method}</span>
                  </td>
                  <td>{item.ipAddress}</td>
                  <td>
                    <span className="device-badge">{item.device}</span>
                  </td>
                  <td>{item.browser}</td>
                  <td>{formatTime(item.activityTime)}</td>
                  <td>{item.pageName || "-"}</td>
                  <td className="page-time">{formatDuration(item.durationSeconds)}</td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default UserActivityTrack;