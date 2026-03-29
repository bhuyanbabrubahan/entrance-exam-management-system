import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axios from "axios";
import "./AdminCandidateDetails.css";

const AdminCandidateDetails = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [candidate, setCandidate] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (id) fetchDetails();
  }, [id]);

  useEffect(() => {
  if (candidate) {
        console.log("Full Candidate:", candidate);
        console.log("Personal Details:", candidate?.personalDetails);
        console.log("Education Details:", candidate?.educationDetails);
        console.log("Document Details:", candidate?.documentDetails);
  }
}, [candidate]);
  const fetchDetails = async () => {
    setLoading(true);
    setError(null);

    try {
      const token = localStorage.getItem("jwtToken");
      if (!token) {
        setError("No token found. Please login as admin.");
        setLoading(false);
        return;
      }

      const response = await axios.get(
        `http://localhost:8080/api/admin/dashboard/candidate/${id}`,
        { headers: { Authorization: `Bearer ${token}` } }
      );

      console.log("Full Candidate Response:", response.data);
      setCandidate(response.data);

    } catch (err) {
      if (err.response?.status === 403)
        setError("Forbidden: Access denied.");
      else if (err.response?.status === 404)
        setError("Candidate not found.");
      else setError("Error fetching candidate details.");
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <p className="loading">Loading candidate details...</p>;
  if (error) return <p className="error">{error}</p>;
  if (!candidate) return <p>No candidate data found.</p>;

  // ✅ SAFE OBJECT HANDLING
const user = candidate?.user || {};
const personalDetails = candidate?.personalDetails || {};
const educationDetails = candidate?.educationDetails || {};
const documentDetails = candidate?.documentDetails || {};

const isPersonalEmpty =
  !candidate?.personalDetails ||
  candidate?.personalStatus === "NOT_STARTED";

const isEducationEmpty =
  !candidate?.educationDetails ||
  candidate?.educationStatus === "NOT_STARTED";

const isDocumentEmpty =
  !candidate?.documentDetails ||
  candidate?.documentStatus === "NOT_STARTED";

  return (
    <div className="admin-profile-container">

      {/* ================= HEADER ================= */}
      <div className="profile-header-card">
        <div className="profile-header-row">

          <div className="profile-left-section">
            <img
              src={documentDetails?.photo || "/default-user.png"}
              alt="Candidate"
              className="profile-photo"
            />

            <div className="profile-info">
              <h2>
                {personalDetails.firstName || user.firstName || ""}{" "}
                {personalDetails.middleName || ""}{" "}
                {personalDetails.lastName || user.lastName || ""}
              </h2>

              <p>
                <strong>Application No:</strong>{" "}
                {personalDetails.applicationNo ||
                  user.applicationNumber ||
                  "-"}
              </p>

              <p>
                <strong>Email:</strong>{" "}
                {personalDetails.email || user.email || "-"}
              </p>

              <p>
                <strong>Mobile:</strong>{" "}
                {personalDetails.mobileNumber ||
                  user.mobileNumber ||
                  "-"}
              </p>

              <div className="status-row">
                <span className="status-badge">
                  Personal: {candidate.personalStatus || "NOT_STARTED"}
                </span>
                <span className="status-badge">
                  Education: {candidate?.educationStatus || "NOT_STARTED"}
                </span>

                <span className="status-badge">
                  Documents: {candidate.documentStatus || "NOT_STARTED"}
                </span>
              </div>
            </div>
          </div>

          <div className="profile-right-section">
            <button className="back-btn" onClick={() => navigate(-1)}>
              ← Back
            </button>
          </div>

        </div>
      </div>

      {/* ================= PERSONAL DETAILS ================= */}
      <div className="details-card">
        <h3>Personal Information</h3>

        {isPersonalEmpty ? (
          <div className="empty-section">Personal details not submitted.</div>
        ) : (
          <>
            <div className="details-grid">
              <p><strong>Gender:</strong> {personalDetails.gender || "-"}</p>
              <p><strong>Date of Birth:</strong> {personalDetails.dob || "-"}</p>
              <p><strong>Aadhaar:</strong> {personalDetails.aadharCard || "-"}</p>
              <p><strong>Father Name:</strong> {personalDetails.fatherName || "-"}</p>
              <p><strong>Mother Name:</strong> {personalDetails.motherName || "-"}</p>
              <p><strong>Nationality:</strong> {personalDetails.nationality || "-"}</p>
              <p><strong>Category:</strong> {personalDetails.category || "-"}</p>
              <p><strong>Marital Status:</strong> {personalDetails.maritalStatus || "-"}</p>
              <p><strong>Disability:</strong> {personalDetails.disability || "-"}</p>
            </div>

            <h4>Address Details</h4>
            <div className="details-grid">
              <p><strong>Correspondence:</strong> {personalDetails.correspondenceAddress || "-"}</p>
              <p><strong>Permanent:</strong> {personalDetails.permanentAddress || "-"}</p>
              <p><strong>Country:</strong> {personalDetails.countryName || "-"}</p>
              <p><strong>State:</strong> {personalDetails.stateName || "-"}</p>
              <p><strong>District:</strong> {personalDetails.districtName || "-"}</p>
              <p><strong>City:</strong> {personalDetails.cityName || "-"}</p>
              <p><strong>Pincode:</strong> {personalDetails.pincode || "-"}</p>
            </div>
          </>
        )}
      </div>

      {/* ================= EDUCATION DETAILS ================= */}
      <div className="details-card">
        <h3>Education Information</h3>

        {isEducationEmpty ? (
          <div className="empty-section">Education details not submitted.</div>
        ) : (
          <>
            <h4>10th Details</h4>
            <div className="details-grid">
              <p><strong>Board:</strong> {educationDetails.board10 || "-"}</p>
              <p><strong>School:</strong> {educationDetails.schoolName10 || "-"}</p>
              <p><strong>Roll No:</strong> {educationDetails.rollNumber10 || "-"}</p>
              <p><strong>Year:</strong> {educationDetails.passingYear10 || "-"}</p>
              <p><strong>Marks Type:</strong> {educationDetails.marksType10 || "-"}</p>
              <p><strong>Percentage:</strong> {educationDetails.percentage10 || "-"}</p>
            </div>

            <h4>12th Details</h4>
            <div className="details-grid">
              <p><strong>Board:</strong> {educationDetails.board12 || "-"}</p>
              <p><strong>School:</strong> {educationDetails.schoolName12 || "-"}</p>
              <p><strong>Roll No:</strong> {educationDetails.rollNumber12 || "-"}</p>
              <p><strong>Year:</strong> {educationDetails.passingYear12 || "-"}</p>
              <p><strong>Stream:</strong> {educationDetails.stream12 || "-"}</p>
              <p><strong>Marks Type:</strong> {educationDetails.marksType12 || "-"}</p>
              <p><strong>Percentage:</strong> {educationDetails.percentage12 || "-"}</p>
              <p><strong>PCM %:</strong> {educationDetails.pcmPercentage || "-"}</p>
              <p><strong>Exam Status:</strong> {educationDetails.examStatus || "-"}</p>
              <p><strong>Appearing Year:</strong> {educationDetails.appearingYear || "-"}</p>
            </div>
          </>
        )}
      </div>

      {/* ================= DOCUMENTS ================= */}
      <div className="details-card">
        <h3>Uploaded Documents</h3>

        {isDocumentEmpty ? (
          <div className="empty-section">Documents not uploaded.</div>
        ) : (
          <>
            <div className="documents-grid">
              {documentDetails.signature && (
                <div>
                  <p><strong>Signature</strong></p>
                  <img src={documentDetails.signature} alt="Signature" />
                </div>
              )}

              {documentDetails.marksheet && (
                <div>
                  <p><strong>Marksheet</strong></p>
                  <a
                    href={documentDetails.marksheet}
                    target="_blank"
                    rel="noreferrer"
                    className="view-doc-btn"
                  >
                    View Marksheet
                  </a>
                </div>
              )}
            </div>

            <div className="details-grid">
              <p><strong>Upload Status:</strong> {documentDetails.uploadStatus || "-"}</p>
              <p><strong>Reopen Allowed:</strong> {documentDetails.reopenAllowed ? "Yes" : "No"}</p>
            </div>
          </>
        )}
      </div>

    </div>
  );
};

export default AdminCandidateDetails;