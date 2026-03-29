import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axios from "axios";
import "../../styles/userdashboard/BasicCorrectionPage.css";

const BasicCorrectionPage = () => {

  const { requestId } = useParams();
  const navigate = useNavigate();

  const [loading, setLoading] = useState(true);
  const [requestedField, setRequestedField] = useState("");

  const [formData, setFormData] = useState({
    firstName: "",
    lastName: "",
    day: "",
    month: "",
    year: "",
    gender: "",
    aadharCard: ""
  });

  const [message, setMessage] = useState("");

  /* ================================================
     FETCH USER + REQUEST INFO
  ================================================= */

  useEffect(() => {

    axios
      .get(`http://localhost:8080/api/user/basic-correction-update/details/${requestId}`)
      .then((res) => {

        console.log("API RESPONSE:", res.data);

        const data = res.data;

        setRequestedField(data.fieldName);

        setFormData({
          firstName: data.firstName || "",
          lastName: data.lastName || "",
          day: data.day || "",
          month: data.month || "",
          year: data.year || "",
          gender: data.gender ? data.gender.toUpperCase() : "",
          aadharCard: data.aadharCard || ""
        });

        setLoading(false);
      })
      .catch(() => {
        setMessage("Failed to load data");
        setLoading(false);
      });

  }, [requestId]);

  /* ================================================
     HANDLE INPUT CHANGE
  ================================================= */

  const handleChange = (e) => {

    const { name, value } = e.target;

    setFormData((prev) => ({
      ...prev,
      [name]: value
    }));

  };

  /* ================================================
     SUBMIT UPDATE
  ================================================= */
const handleSubmit = async (e) => {
  e.preventDefault();

  try {
    const payload = {
      ...formData,
      day: Number(formData.day),
      month: Number(formData.month),
      year: Number(formData.year)
    };

    const { data } = await axios.put(
      `http://localhost:8080/api/user/basic-correction-update/update/${requestId}`,
      payload
    );

    // ✅ show backend message
    setMessage(data.message || "Basic details updated successfully");

    // ✅ redirect after 2 seconds
    setTimeout(() => {
      navigate("/dashboard/user-correction-request");
    }, 2000);

  } catch (err) {
    console.log("ERROR RESPONSE:", err.response);
    setMessage(err.response?.data?.message || "Update failed");
  }
};
  





  if (loading) return <div className="loading">Loading...</div>;

  return (

    <div className="basic-correction-container">

      <h2>Basic Details Correction</h2>

      {message && (
        <div className="message">{message}</div>
      )}

      <form onSubmit={handleSubmit}>

        {/* FIRST NAME */}
        <div className="form-group">
          <label>First Name</label>
          <input
            type="text"
            name="firstName"
            value={formData.firstName}
            disabled={requestedField !== "FIRST_NAME"}
            onChange={handleChange}
          />
        </div>

        {/* LAST NAME */}
        <div className="form-group">
          <label>Last Name</label>
          <input
            type="text"
            name="lastName"
            value={formData.lastName}
            disabled={requestedField !== "LAST_NAME"}
            onChange={handleChange}
          />
        </div>

        {/* DOB */}
        <div className="form-group">
          <label>Date of Birth</label>

          <div className="row">

            <select
              name="day"
              value={formData.day}
              disabled={requestedField !== "DOB"}
              onChange={handleChange}
            >
              <option value="">DD</option>
              {[...Array(31)].map((_, i) => (
                <option key={i + 1} value={i + 1}>{i + 1}</option>
              ))}
            </select>

            <select
              name="month"
              value={formData.month}
              disabled={requestedField !== "DOB"}
              onChange={handleChange}
            >
              <option value="">MM</option>
              {[...Array(12)].map((_, i) => (
                <option key={i + 1} value={i + 1}>{i + 1}</option>
              ))}
            </select>

            <select
              name="year"
              value={formData.year}
              disabled={requestedField !== "DOB"}
              onChange={handleChange}
            >
              <option value="">YYYY</option>
              {Array.from({ length: 46 }, (_, i) => 1980 + i).map((y) => (
                <option key={y} value={y}>{y}</option>
              ))}
            </select>

          </div>
        </div>

        {/* GENDER */}
        <div className="form-group">
          <label>Gender</label>

          <select
            name="gender"
            value={formData.gender}
            disabled={requestedField !== "GENDER"}
            onChange={handleChange}
          >
            <option value="">Select</option>
            <option value="MALE">Male</option>
            <option value="FEMALE">Female</option>
            <option value="OTHER">Other</option>
          </select>
        </div>

        {/* AADHAAR */}
        <div className="form-group">
          <label>Aadhaar Number</label>
          <input
            type="text"
            name="aadharCard"
            value={formData.aadharCard}
            disabled={requestedField !== "AADHAAR"}
            onChange={handleChange}
          />
        </div>

        <button className="submit-btn">
          Submit Correction
        </button>

      </form>

    </div>

  );
};

export default BasicCorrectionPage;