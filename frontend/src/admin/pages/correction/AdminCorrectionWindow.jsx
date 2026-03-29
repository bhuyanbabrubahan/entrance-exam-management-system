import React, { useEffect, useState } from "react";
import axios from "axios";
import { toast, ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import "./AdminCorrectionWindow.css";

const AdminCorrectionWindow = () => {
  const [activeWindow, setActiveWindow] = useState(null);
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(5);
  const [totalPages, setTotalPages] = useState(1);
  const [form, setForm] = useState({
    startDateTime: "",
    endDateTime: "",
    unlockPersonal: false,
    unlockEducation: false,
    unlockDocuments: false,
  });
  const [countdown, setCountdown] = useState("");

  const token = localStorage.getItem("jwtToken");

  if (!token) {
    window.location.href = "/login";
  }

  const fetchActiveWindow = async () => {
    try {
      const res = await axios.get(
        "http://localhost:8080/api/admin/correction/active",
        { headers: { Authorization: `Bearer ${token}` } }
      );
      setActiveWindow(res.data);
    } catch (err) {
      console.error("Error fetching active window:", err);
      toast.error("Cannot fetch active correction window");
    }
  };

  const fetchHistory = async (pageNumber = 0) => {
    try {
      const res = await axios.get(
        `http://localhost:8080/api/admin/correction/history?page=${pageNumber}&size=${size}`,
        { headers: { Authorization: `Bearer ${token}` } }
      );
      setHistory(res.data.content || res.data); // adjust according to backend
      setTotalPages(res.data.totalPages || 1);
    } catch (err) {
      console.error("Error fetching history:", err);
      toast.error("Cannot fetch correction history");
    }
  };

  useEffect(() => {
    fetchActiveWindow();
    fetchHistory();
  }, []);

  // Countdown timer
  useEffect(() => {
    const timer = setInterval(() => {
      if (!activeWindow) return setCountdown("");
      const end = new Date(activeWindow.endDateTime).getTime();
      const now = new Date().getTime();
      const distance = end - now;

      if (distance <= 0) {
        setCountdown("Expired");
        clearInterval(timer);
        
      } else {
        const hours = Math.floor((distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
        const minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
        const seconds = Math.floor((distance % (1000 * 60)) / 1000);
        setCountdown(`${hours}h ${minutes}m ${seconds}s`);
      }
    }, 1000);

    return () => clearInterval(timer);
  }, [activeWindow]);

  const handleActivate = async () => {
    if (!form.startDateTime || !form.endDateTime) {
      toast.error("Start and end datetime required");
      return;
    }

    if (!window.confirm("Activate this correction window?")) return;

    try {
      await axios.post(
        "http://localhost:8080/api/admin/correction/activate",
        form,
        { headers: { Authorization: `Bearer ${token}` } }
      );
      toast.success("Correction window activated");
      fetchActiveWindow();
      fetchHistory();
    } catch (err) {
      console.error("Error activating window:", err);
      toast.error(
        err?.response?.data?.message ||
        "Failed to activate window"
      );
    }
  };

  const handleDeactivate = async () => {
    if (!window.confirm("Deactivate active window?")) return;

    try {
      await axios.put(
        "http://localhost:8080/api/admin/correction/deactivate",
        {},
        { headers: { Authorization: `Bearer ${token}` } }
      );
      toast.info("Correction window deactivated");
      fetchActiveWindow();
    } catch (err) {
      console.error("Error deactivating window:", err);
      toast.error("Failed to deactivate window");
    }
  };

  const handlePageChange = (newPage) => {
    setPage(newPage);
    fetchHistory(newPage);
  };

  return (
    <div className="correction-container">
      <ToastContainer />

      <h2>Correction Window Management</h2>

      {/* Active Window */}
      <div className="active-window-card">
        <h3>Active Window</h3>
        {activeWindow ? (
          <>
            <p>
              <strong>Start:</strong>{" "}
              {new Date(activeWindow.startDateTime).toLocaleString()}
            </p>
            <p>
              <strong>End:</strong>{" "}
              {new Date(activeWindow.endDateTime).toLocaleString()}
            </p>
            <p>
              <strong>Countdown:</strong> {countdown}
            </p>
            <p>
              <strong>Sections Unlocked:</strong>{" "}
              {[
                activeWindow.unlockPersonal ? "Personal" : null,
                activeWindow.unlockEducation ? "Education" : null,
                activeWindow.unlockDocuments ? "Documents" : null,
              ]
                .filter(Boolean)
                .join(", ") || "None"}
            </p>
            <button className="btn deactivate" onClick={handleDeactivate}>
              Deactivate
            </button>
          </>
        ) : (
          <p>No active window</p>
        )}
      </div>

      {/* New Window Form */}
      <div className="new-window-card">
        <h3>Create New Window</h3>
        <label>
          Start DateTime:
          <input
            type="datetime-local"
            value={form.startDateTime}
            onChange={(e) =>
              setForm({ ...form, startDateTime: e.target.value })
            }
          />
        </label>
        <label>
          End DateTime:
          <input
            type="datetime-local"
            value={form.endDateTime}
            onChange={(e) => setForm({ ...form, endDateTime: e.target.value })}
          />
        </label>
        <label>
          <input
            type="checkbox"
            checked={form.unlockPersonal}
            onChange={(e) =>
              setForm({ ...form, unlockPersonal: e.target.checked })
            }
          />
          Unlock Personal
        </label>
        <label>
          <input
            type="checkbox"
            checked={form.unlockEducation}
            onChange={(e) =>
              setForm({ ...form, unlockEducation: e.target.checked })
            }
          />
          Unlock Education
        </label>
        <label>
          <input
            type="checkbox"
            checked={form.unlockDocuments}
            onChange={(e) =>
              setForm({ ...form, unlockDocuments: e.target.checked })
            }
          />
          Unlock Documents
        </label>
        <button className="btn activate" onClick={handleActivate}>
          Activate Window
        </button>
      </div>

      {/* History Table */}
      <div className="history-card">
        <h3>Past Correction Windows</h3>
        <table>
          <thead>
            <tr>
              <th>#</th>
              <th>Start</th>
              <th>End</th>
              <th>Sections</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            {history.length === 0 ? (
              <tr>
                <td colSpan={5}>No past windows</td>
              </tr>
            ) : (
              history.map((w, idx) => (
                <tr key={w.id}>
                  <td>{page * size + idx + 1}</td>
                  <td>{new Date(w.startDateTime).toLocaleString()}</td>
                  <td>{new Date(w.endDateTime).toLocaleString()}</td>
                  <td>
                    {[
                      w.unlockPersonal ? "Personal" : null,
                      w.unlockEducation ? "Education" : null,
                      w.unlockDocuments ? "Documents" : null,
                    ]
                      .filter(Boolean)
                      .join(", ")}
                  </td>
                  <td>{w.active ? "Active" : "Expired"}</td>
                </tr>
              ))
            )}
          </tbody>
        </table>

        {/* Pagination */}
        <div className="pagination">
          {Array.from({ length: totalPages }, (_, i) => (
            <button
              key={i}
              className={i === page ? "active" : ""}
              onClick={() => handlePageChange(i)}
            >
              {i + 1}
            </button>
          ))}
        </div>
      </div>
    </div>
  );
};

export default AdminCorrectionWindow;