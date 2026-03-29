// EducationDetails.jsx
import React, { useEffect, useState, useMemo } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import axios from "axios";
import Header from "../../components/layout/Header";
import UserInfoLogoutBar from "../../components/userdashboard/UserInfoLogoutBar";
import "../../styles/userdashboard/EducationDetails.css";

const EducationDetails = () => {
  const navigate = useNavigate();
  const location = useLocation();



  // ===== HEADER / USER INFO =====
  const [user, setUser] = useState(null);
  const [loginInfo, setLoginInfo] = useState(null);
 
  const [correctionStatus, setCorrectionStatus] = useState(null);

  // ===== EDUCATION FORM =====
  const [formData, setFormData] = useState({
    board10: "",
    schoolName10: "",
    rollNumber10: "",
    passingYear10: "",
    marksType10: "PERCENTAGE",
    percentage10: "",

    board12: "",
    schoolName12: "",
    rollNumber12: "",
    passingYear12: "",
    stream12: "",
    marksType12: "PERCENTAGE",
    percentage12: "",
    pcmPercentage: "",

    examStatus: "PASSED",
    appearingYear: "",
  });

  const [educationStatus, setEducationStatus] = useState("NOT_STARTED");
const [loading, setLoading] = useState(true);

// ===== PAGE MODE =====
const pageMode = location.state?.mode || "EDIT";

/* =====================================================
   LOCK LOGIC (UNCHANGED - PERFECT)
===================================================== */
const isLocked =
  educationStatus === "COMPLETED" &&
  !(
    correctionStatus &&
    correctionStatus.correctionActive === true &&
    correctionStatus.educationEditable === true
  );

const { isViewOnly, isEditable } = useMemo(() => {
  return {
    isViewOnly: isLocked,
    isEditable: !isLocked,
  };
}, [isLocked]);

/* =====================================================
   DEBUG LOGGER
===================================================== */
useEffect(() => {
  console.log("========== EDUCATION DEBUG ==========");
  console.log("educationStatus:", educationStatus);
  console.log("correctionStatus:", correctionStatus);
  console.log("correctionActive:", correctionStatus?.correctionActive);
  console.log("educationEditable:", correctionStatus?.educationEditable);
  console.log("FINAL isLocked:", isLocked);
  console.log("=====================================");
}, [educationStatus, correctionStatus, isLocked]);

/* =====================================================
   FETCH EDUCATION DATA FUNCTION (REUSABLE)
===================================================== */
const fetchEducationDetails = () => {
  const token = localStorage.getItem("jwtToken");
  if (!token) return;

  const headers = { Authorization: `Bearer ${token}` };

  console.log("📡 Fetching education details from DB...");

  axios
    .get("http://localhost:8080/api/user/education-details/current", { headers })
    .then((res) => {
      if (res.data?.status === "SUCCESS") {
        const edu = res.data.education || {};

        // 🔥 FIXED: get status correctly
        const statusFromBackend =
          res.data.educationStatus ||
          res.data.user?.educationStatus ||
          "NOT_STARTED";

        console.log("✅ Status from backend:", statusFromBackend);

        setEducationStatus(statusFromBackend);

        setFormData({
          board10: edu.board10 || "",
          schoolName10: edu.schoolName10 || "",
          rollNumber10: edu.rollNumber10 || "",
          passingYear10: edu.passingYear10 ? String(edu.passingYear10) : "",
          marksType10: edu.marksType10 || "PERCENTAGE",
          percentage10: edu.percentage10 ? String(edu.percentage10) : "",

          board12: edu.board12 || "",
          schoolName12: edu.schoolName12 || "",
          rollNumber12: edu.rollNumber12 || "",
          passingYear12: edu.passingYear12 ? String(edu.passingYear12) : "",
          stream12: edu.stream12 || "",
          marksType12: edu.marksType12 || "PERCENTAGE",
          percentage12: edu.percentage12 ? String(edu.percentage12) : "",
          pcmPercentage: edu.pcmPercentage ? String(edu.pcmPercentage) : "",

          examStatus: edu.examStatus || "PASSED",
          appearingYear: edu.appearingYear ? String(edu.appearingYear) : "",
        });

        console.log("📝 Form data synced from DB");
      }
      setLoading(false);
    })
    .catch((err) => {
      console.error("❌ Education fetch error:", err);
      setLoading(false);
    });
};

/* =====================================================
   INITIAL PAGE LOAD
===================================================== */
useEffect(() => {
  const token = localStorage.getItem("jwtToken");

  if (!token) {
    navigate("/login", { replace: true });
    return;
  }

  const headers = { Authorization: `Bearer ${token}` };

  console.log("🚀 Education page loaded");

  // Header Data
  fetch("http://localhost:8080/api/user/dashboard/current", { headers })
    .then((res) => res.json())
    .then((data) => {
      setUser(data.user);
      setLoginInfo(data.loginInfo);
    })
    .catch(() => navigate("/login", { replace: true }));

  // 🔥 Fetch education details
  fetchEducationDetails();

}, [navigate]);

/* =====================================================
   CORRECTION STATUS (UNCHANGED)
===================================================== */
useEffect(() => {

  const fetchCorrectionStatus = () => {
    const token = localStorage.getItem("jwtToken");
    if (!token) return;

    fetch("http://localhost:8080/api/user/correction-status", {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then(res => res.json())
      .then(data => {
        console.log("📡 Education Correction API:", data);
        setCorrectionStatus(data);
      })
      .catch(err => console.error("Correction fetch error:", err));
  };

  fetchCorrectionStatus();

  const interval = setInterval(fetchCorrectionStatus, 15000);
  return () => clearInterval(interval);

}, []);

  // ===== FORM HANDLERS =====
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  // ===== CONVERT STRINGS TO NUMBERS BEFORE SENDING =====
  const convertPayload = () => ({
    ...formData,
    passingYear10: formData.passingYear10 ? parseInt(formData.passingYear10) : null,
    percentage10: formData.percentage10 ? parseFloat(formData.percentage10) : null,
    passingYear12: formData.passingYear12 ? parseInt(formData.passingYear12) : null,
    percentage12: formData.percentage12 ? parseFloat(formData.percentage12) : null,
    pcmPercentage: formData.pcmPercentage ? parseFloat(formData.pcmPercentage) : null,
    appearingYear: formData.appearingYear ? parseInt(formData.appearingYear) : null,
  });


  // ===== SAVE DRAFT =====
const handleSave = () => {
  const token = localStorage.getItem("jwtToken");
  if (!token) {
    alert("Session expired. Please login again.");
    navigate("/login", { replace: true });
    return;
  }

  console.log("💾 Saving draft...");

  axios
    .post(
      "http://localhost:8080/api/user/education-details/save",
      formData,
      { headers: { Authorization: `Bearer ${token}` } }
    )
    .then((res) => {
      if (res.data.status === "SUCCESS") {

        console.log("✅ Draft saved successfully");

        setEducationStatus(res.data.educationStatus || "IN_PROGRESS");

        // 🔥 REFETCH LATEST DATA FROM DB
        fetchEducationDetails();

        alert("Draft saved successfully!");
      } else {
        alert("Save failed: " + (res.data.message || "Unknown error"));
      }
    })
    .catch((err) => {
      console.error("❌ Save error:", err);
      alert(
        "Save failed: " +
        (err.response?.data?.message || err.message || "Unknown error")
      );
    });
};


// ===== SUBMIT =====
const handleSubmit = () => {
  const token = localStorage.getItem("jwtToken");
  if (!token) {
    alert("Session expired. Please login again.");
    navigate("/login", { replace: true });
    return;
  }

  if (!window.confirm("Once submitted, details cannot be edited. Continue?"))
    return;

  console.log("🚀 Submitting education details...");

  axios
    .post(
      "http://localhost:8080/api/user/education-details/submit",
      formData,
      { headers: { Authorization: `Bearer ${token}` } }
    )
    .then((res) => {
      if (res.data.status === "SUCCESS") {

        console.log("✅ Education submitted & locked");

        setEducationStatus(res.data.educationStatus || "COMPLETED");

        alert("Education details submitted successfully!");

        navigate("/dashboard");
      } else {
        alert("Submission failed: " + (res.data.message || "Unknown error"));
      }
    })
    .catch((err) => {
      console.error("❌ Submit error:", err);
      alert(
        "Submission failed: " +
        (err.response?.data?.message || err.message || "Unknown error")
      );
    });
};



  // ===== LOADING GUARDS =====
  if (loading) return <div className="dashboard-loading">Loading...</div>;
  if (!user) return <div>User not found</div>;

  // ===== RENDER =====
  return (
    <>
      <Header />
      <UserInfoLogoutBar user={user} loginInfo={loginInfo} />

      <div className="page-title">
       
        {/* ===== CORRECTION WINDOW ACTIVE ===== */}
          {correctionStatus?.correctionActive &&
            correctionStatus?.educationEditable && (
              <div className="correction-banner">
                🟢 Correction Window Active — Education section editable
              </div>
            )}
         
        <h2>🎓 EDUCATION DETAILS</h2>
       
      </div>
      

      
      {/* ===== VIEW ONLY CARD ===== */}
      {isViewOnly && (
        <div className="info-card">
          <div className="info-card-header">
            <h4>🔒 View Mode</h4>
            <p style={{ fontSize: 12, color: "red" }}>
          DEBUG → status: {educationStatus} | locked: {String(isLocked)} 
      </p>
            <span className={`status-pill ${educationStatus === "COMPLETED" ? "completed" : "not_completed"}`}>
               <span className="status-label">Status:</span> {educationStatus}
            </span>
          </div>

         

        </div>
      )}

      {/* ===== CLASS 10 / CLASS 12 / EXAM STATUS / ACTION BUTTONS ===== */}
        {/* ===== CLASS 10 ===== */}
      <div className="card editable-card">
        <h4>🏫 Class 10 (Secondary)</h4>
        <div className="card-row two-col">
          <div className="field-group">
            <label>Board Name *</label>
            <input
              name="board10"
              value={formData.board10}
              onChange={handleChange}
              disabled={!isEditable}
            />
          </div>
          <div className="field-group">
            <label>School Name *</label>
            <input
              name="schoolName10"
              value={formData.schoolName10}
              onChange={handleChange}
              disabled={!isEditable}
            />
          </div>
        </div>
        <div className="card-row two-col">
          <div className="field-group">
            <label>Roll Number *</label>
            <input
              name="rollNumber10"
              value={formData.rollNumber10}
              onChange={handleChange}
              disabled={!isEditable}
            />
          </div>
          <div className="field-group">
            <label>Passing Year *</label>
            <input
              name="passingYear10"
              value={formData.passingYear10}
              onChange={handleChange}
              disabled={!isEditable}
            />
          </div>
        </div>
        <div className="card-row two-col">
          <div className="field-group">
            <label>Marks Type *</label>
            <select
              name="marksType10"
              value={formData.marksType10}
              onChange={handleChange}
              disabled={!isEditable}
            >
              <option value="PERCENTAGE">Percentage</option>
              <option value="CGPA">CGPA</option>
            </select>
          </div>
          <div className="field-group">
            <label>Marks / Percentage *</label>
            <input
              name="percentage10"
              value={formData.percentage10}
              onChange={handleChange}
              disabled={!isEditable}
            />
          </div>
        </div>
      </div>

      {/* ===== CLASS 12 ===== */}
      <div className="card editable-card">
        <h4>🎓 Class 12 (Senior Secondary)</h4>
        <div className="card-row two-col">
          <div className="field-group">
            <label>Board Name *</label>
            <input
              name="board12"
              value={formData.board12}
              onChange={handleChange}
              disabled={!isEditable}
            />
          </div>
          <div className="field-group">
            <label>School Name *</label>
            <input
              name="schoolName12"
              value={formData.schoolName12}
              onChange={handleChange}
              disabled={!isEditable}
            />
          </div>
        </div>
        <div className="card-row two-col">
          <div className="field-group">
            <label>Roll Number *</label>
            <input
              name="rollNumber12"
              value={formData.rollNumber12}
              onChange={handleChange}
              disabled={!isEditable}
            />
          </div>
          <div className="field-group">
            <label>Passing Year *</label>
            <input
              name="passingYear12"
              value={formData.passingYear12}
              onChange={handleChange}
              disabled={!isEditable}
            />
          </div>
        </div>
        <div className="card-row two-col">
          <div className="field-group">
            <label>Stream *</label>
            <select
              name="stream12"
              value={formData.stream12}
              onChange={handleChange}
              disabled={!isEditable}
            >
              <option value="">Select Stream</option>
              <option value="SCIENCE">Science</option>
              <option value="COMMERCE">Commerce</option>
              <option value="ARTS">Arts</option>
            </select>
          </div>
          <div className="field-group">
            <label>Percentage *</label>
            <input
              name="percentage12"
              value={formData.percentage12}
              onChange={handleChange}
              disabled={!isEditable}
            />
          </div>
        </div>

        {formData.stream12 === "SCIENCE" && (
          <div className="card-row single">
            <div className="field-group">
              <label>PCM Percentage *</label>
              <input
                name="pcmPercentage"
                value={formData.pcmPercentage}
                onChange={handleChange}
                disabled={!isEditable}
              />
            </div>
          </div>
        )}
      </div>

      {/* ===== EXAM STATUS ===== */}
      <div className="card editable-card">
        <h4>📄 Exam Status</h4>
        <div className="card-row two-col">
          <div className="field-group">
            <label>Status *</label>
            <select
              name="examStatus"
              value={formData.examStatus}
              onChange={handleChange}
              disabled={!isEditable}
            >
              <option value="PASSED">Passed</option>
              <option value="APPEARING">Appearing</option>
            </select>
          </div>
          {formData.examStatus === "APPEARING" && (
            <div className="field-group">
              <label>Appearing Year *</label>
              <input
                name="appearingYear"
                value={formData.appearingYear}
                onChange={handleChange}
                disabled={!isEditable}
              />
            </div>
          )}
        </div>
      </div>

          {/* ===== IMPORTANT ===== */}
          <div className="info-card">
            <h4>⚠️ IMPORTANT</h4>
            <ul>
              <li>Please verify all details carefully</li>
              <li>Once submitted, editing will be locked</li>
              <li>Admin approval required for further changes</li>
            </ul>
          </div>

      {/* ===== ACTION BUTTONS ===== */}
      <div className="action-buttons">

            {!isLocked && (
              <>
                <button onClick={handleSave}>
                  SAVE DRAFT
                </button>

                <button onClick={handleSubmit}>
                  SUBMIT & LOCK
                </button>
              </>
            )}

            {isLocked && (
              <>
                <button disabled>
                  🔒 Locked
                </button>

                <button
                  className="btn outline back-dashboard-btn"
                  onClick={() => navigate("/dashboard")}
                >
                  ⬅ Back to Dashboard
                </button>
              </>
            )}

          </div>
      {/* ... end ... */}

    </>
  );
};

export default EducationDetails;
