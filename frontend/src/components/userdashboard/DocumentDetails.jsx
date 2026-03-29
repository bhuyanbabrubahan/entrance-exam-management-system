import React, { useEffect, useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import axios from "axios";
import Header from "../../components/layout/Header";
import UserInfoLogoutBar from "../../components/userdashboard/UserInfoLogoutBar";
import "../../styles/userdashboard/DocumentDetails.css";

const DocumentDetails = () => {
  const navigate = useNavigate();
  const location = useLocation();

  /* ===== MODE ===== */
  const pageMode = location.state?.mode || "EDIT"; // EDIT | VIEW

  /* ===== HEADER DATA ===== */
  const [user, setUser] = useState(null);
  const [loginInfo, setLoginInfo] = useState(null);
  const [documentStatus, setDocumentStatus] = useState("NOT_STARTED");
  
  const [correctionStatus, setCorrectionStatus] = useState(null);

  /* ===== DOCUMENT ===== */
  const [doc, setDoc] = useState({});
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  
  // ===== CORRECTION CHECK =====
  const correctionAllowed =
    correctionStatus?.correctionActive === true &&
    correctionStatus?.documentEditable === true;

// ===== LOCK LOGIC =====
  const isLocked =
  documentStatus === "COMPLETED" && !correctionAllowed;

// ===== DEBUG =====
console.log("----- DOCUMENT DEBUG -----");
console.log("pageMode:", pageMode);
console.log("documentStatus:", documentStatus);
console.log("correctionStatus:", correctionStatus);
console.log("correctionAllowed:", correctionAllowed);
console.log("isLocked:", isLocked);
console.log("--------------------------");

  /* ================= FETCH DATA ================= */
  useEffect(() => {
    const token = localStorage.getItem("jwtToken");
    if (!token) {
      navigate("/login", { replace: true });
      return;
    }

    /* ===== DASHBOARD DATA ===== */
    axios
      .get("http://localhost:8080/api/user/dashboard/current", {
        headers: { Authorization: `Bearer ${token}` },
      })
      .then((res) => {
        const u = res.data.user;
        setUser(u);
        setLoginInfo(res.data.loginInfo);

        if (u.educationStatus !== "COMPLETED") {
          alert("⚠️ Please complete Education Details first");
          navigate("/education-details", { replace: true });
        }
      })
      .catch(() => navigate("/login"));

    /* ===== DOCUMENT DATA ===== */
    axios
  .get("http://localhost:8080/api/user/document-details/current", {
    headers: { Authorization: `Bearer ${token}` },
  })
  .then((res) => {
    const d = res.data.documents || {};

    // 🔑 SOURCE OF TRUTH FROM BACKEND
    setDocumentStatus(d.documentStatus || "NOT_STARTED");
   

    // 📄 DOCUMENT FILES
    setDoc({
      ...d,
      photo: d.photoUrl || d.photo,
      signature: d.signatureUrl || d.signature,
      marksheet: d.marksheetUrl || d.marksheet,
    });

    setLoading(false);
  })
  .catch(() => setLoading(false));
      
  }, [navigate]);


   /* ================= CORRECTION STATUS ================= */
  useEffect(() => {

  const fetchCorrectionStatus = () => {
    const token = localStorage.getItem("jwtToken");
    if (!token) return;

    fetch("http://localhost:8080/api/user/correction-status", {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then(res => res.json())
      .then(data => {
        console.log("📡 Document Correction:", data);
        setCorrectionStatus(data);
      })
      .catch(err => console.error(err));
  };

  fetchCorrectionStatus();           // 🔥 immediate
  const interval = setInterval(fetchCorrectionStatus, 10000);

  return () => clearInterval(interval);

}, []);

  /* ================= FILE CHANGE ================= */
  const handleFileChange = (e) => {
    const { name, files } = e.target;
    if (!files[0]) return;

    setDoc((prev) => ({
      ...prev,
      [name]: files[0],
      [`${name}Uploaded`]: true,
    }));
  };

  /* ================= FORM DATA ================= */
  const buildFormData = () => {
    const fd = new FormData();
    if (doc.photo instanceof File) fd.append("photo", doc.photo);
    if (doc.signature instanceof File) fd.append("signature", doc.signature);
    if (doc.marksheet instanceof File) fd.append("marksheet", doc.marksheet);
    return fd;
  };

  /* ================= SAVE DRAFT ================= */
  const saveDraft = async () => {
    setSaving(true);
      try {
      // 🔥 First save draft (important)
      await axios.post(
        "http://localhost:8080/api/user/document-details/save",
        buildFormData(),
        {
          headers: {
            Authorization: `Bearer ${localStorage.getItem("jwtToken")}`,
          },
        }
      );

      alert("✅ Draft saved successfully");

      const token = localStorage.getItem("jwtToken");
      const res = await axios.get(
        "http://localhost:8080/api/user/document-details/current",
        { headers: { Authorization: `Bearer ${token}` } }
      );

      const d = res.data.documents || {};
      setDocumentStatus(d.documentStatus || "NOT_STARTED");
      
      setDoc(d);

    } catch {
      alert("❌ Failed to save draft");
    } finally {
      setSaving(false);
    }
  };

  /* ================= SUBMIT ================= */
  const submitForm = async () => {
  if (!window.confirm("After submit, editing will be locked. Continue?")) return;

  setSubmitting(true);
  try {
    const fd = new FormData();
    if (doc.photo instanceof File) fd.append("photo", doc.photo);
    if (doc.signature instanceof File) fd.append("signature", doc.signature);
    if (doc.marksheet instanceof File) fd.append("marksheet", doc.marksheet);

    await axios.post(
      "http://localhost:8080/api/user/document-details/submit",
      fd,
      {
        headers: {
          Authorization: `Bearer ${localStorage.getItem("jwtToken")}`,
          "Content-Type": "multipart/form-data"
        },
      }
    );

    alert("✅ Documents submitted successfully");
    navigate("/dashboard");  

  } catch {
    alert("❌ Submission failed");
  } finally {
    setSubmitting(false);
  }
};

  /* ================= PREVIEW ================= */
  const renderPreview = (file, label) => {
    if (!file) return <span className="empty">Not Uploaded</span>;

    if (typeof file === "string") {
      if (file.endsWith(".pdf")) return <span className="file-name">{label}.pdf</span>;
      return <img src={file} alt={label} className="doc-preview-img" />;
    }

    if (file.type === "application/pdf") {
      return <span className="file-name">{file.name}</span>;
    }

    return <img src={URL.createObjectURL(file)} alt={label} className="doc-preview-img" />;
  };

  if (loading) return <div className="dashboard-loading">Loading...</div>;

  /* ================= RENDER ================= */
  return (
    <>
      <Header />
      <UserInfoLogoutBar user={user} loginInfo={loginInfo} />

      <div className="page-title">
        {/* ===== CORRECTION WINDOW ACTIVE ===== */}
          {correctionStatus?.correctionActive &&
            correctionStatus?.documentEditable && (
              <div className="correction-banner">
                🟢 Correction Window Active — Document section editable
              </div>
            )}
        
        <h2>DOCUMENT DETAILS</h2>
      </div>
      {/* <p style={{ color: "red", fontSize: 12 }}>
        DEBUG → pageMode: {pageMode} |
        status: {documentStatus} |
        correctionActive: {String(correctionStatus?.correctionActive)} |
        documentEditable: {String(correctionStatus?.documentEditable)} |
        correctionAllowed: {String(correctionAllowed)} |
        isLocked: {String(isLocked)}
      </p> */}

      <div className="dashboard-container">

        {/* ===== INFO CARD ===== */}
        <div className="info-card">
          <div className="info-card-header">
            <h4>{isLocked  ? "🔒 View Mode" : "📄 Document Details"}</h4>

            <span className={`status-pill ${documentStatus === "COMPLETED" ? "completed" : "not_completed"}`}>
              <span className="status-label">Status:</span>
              {documentStatus}
            </span>
          </div>

          <p>
            Upload clear photograph and signature.
            Marksheet can be image or PDF.
            After submit, editing is locked.
          </p>
        </div>

        {/* ===== DOCUMENT CARDS ===== */}
        {["photo", "signature", "marksheet"].map((field) => (
          <div className="doc-card" key={field}>
            <label>{field.toUpperCase()}</label>
            {renderPreview(doc[field], field)}

            {/* ✅ REMOVE INPUT WHEN LOCKED */}
            {!isLocked && (
              <input
                type="file"
                name={field}
                accept={field === "marksheet" ? ".pdf,image/*" : "image/*"}
                onChange={handleFileChange}
              />
            )}
          </div>
        ))}

        {/* ===== ACTION BUTTONS ===== */}
        <div className="action-buttons">

          {!isLocked && (
            <>
              <button className="btn save" onClick={saveDraft} disabled={saving}>
                {saving ? "Saving..." : "SAVE DRAFT"}
              </button>

              <button className="btn submit" onClick={submitForm} disabled={submitting}>
                {submitting ? "Submitting..." : "SUBMIT & LOCK"}
              </button>
            </>
          )}

          {isLocked && (
            <>
              <button className="lock-btn" disabled>🔒 Locked</button>

              <button
                className="btn outline back-dashboard-btn"
                onClick={() => navigate("/dashboard")}
              >
                ⬅ Back to Dashboard
              </button>
            </>
          )}
        </div>

        {/* ===== LOCK INFO ===== */}
        {isLocked && (
          <div className="reopen-info">

            <p>✋ Documents are locked after submission.</p>
            <p>🛠 Editing is allowed only when admin reopens your application.</p>
            <p>📩 Contact admin if correction is required.</p>
          </div>
        )}

      </div>
    </>
  );
};

export default DocumentDetails;
