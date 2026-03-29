import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import TopBar from "../../components/layout/TopBar";
import Header from "../../components/layout/Header";
import TopNoticeBar from "../../components/layout/TopNoticeBar";
import MenuHeader from "../../components/layout/MenuHeader";

import "../../styles/register/Register.css";

const Register = () => {
  const navigate = useNavigate();

  const [form, setForm] = useState({
    firstName: "",
    middleName: "",
    lastName: "",
    gender: "",
    day: "",
    month: "",
    year: "",
    mobileNumber: "",
    email: "",
    aadharCard: "",
    password: "",
    confirmPassword: "",
    captcha: ""
  });

  const [captchaImage, setCaptchaImage] = useState("");
  const [captchaToken, setCaptchaToken] = useState("");
  const [captchaVerified, setCaptchaVerified] = useState(false);
  const [loading, setLoading] = useState(false);

  /* ================= TOAST ================= */
  const [toast, setToast] = useState({ show: false, text: "", type: "" });

  const showToast = (text, type = "success") => {
    setToast({ show: true, text, type });
    setTimeout(() => setToast({ show: false, text: "", type: "" }), 5000);
  };

  /* ================= LOAD CAPTCHA (JWT BASED) ================= */
const loadCaptcha = async () => {
  try {
    // Cleanup old captcha image
    // Cleanup old captcha image safely
if (captchaImage) {
  try {
    URL.revokeObjectURL(captchaImage);
  } catch (err) {
    console.warn("Failed to revoke old captcha URL", err);
  }
  setCaptchaImage(null); // set to null, not ""
}

    const res = await fetch("http://localhost:8080/captcha/generate", {
      method: "GET",
      cache: "no-store",
      headers: {
        "Accept": "image/png"
      }
    });

    // ❌ Unauthorized / forbidden
    if (res.status === 401 || res.status === 403) {
      showToast("Session expired. Please refresh the page.", "error");
      return;
    }

    // ❌ Any other failure
    if (!res.ok) {
      throw new Error(`Captcha fetch failed: ${res.status}`);
    }

    // ✅ Read captcha token from header
    const token = res.headers.get("captcha-token");
    if (!token) {
      throw new Error("Captcha token missing in response header");
    }

    setCaptchaToken(token);

    // ✅ Convert image to blob
    const blob = await res.blob();
    if (!blob || blob.size === 0) {
      throw new Error("Empty captcha image received");
    }

    const imageUrl = URL.createObjectURL(blob);
    setCaptchaImage(imageUrl);

    // Reset captcha input
    setForm((prev) => ({
      ...prev,
      captcha: ""
    }));

  } catch (error) {
    console.error("Captcha Error:", error);
    showToast("Failed to load captcha. Please try again.", "error");
  }
};

/* ================= INITIAL LOAD ================= */
useEffect(() => {
  loadCaptcha();

  // Cleanup on unmount
  return () => {
  if (captchaImage) {
    URL.revokeObjectURL(captchaImage);
    setCaptchaImage(null); // important to avoid empty string
  }
};
  // eslint-disable-next-line react-hooks/exhaustive-deps
}, []);

  /* ================= INPUT CHANGE ================= */
  const handleChange = (e) =>
    setForm({ ...form, [e.target.name]: e.target.value });

  /* ================= VERIFY CAPTCHA ================= */
  const handleVerifyCaptcha = async () => {
  if (!form.captcha) {
    showToast("Please enter captcha to proceed.", "error");
    return;
  }

  if (!captchaToken) {
    showToast("Captcha not loaded properly. Please refresh.", "error");
    return;
  }

  setLoading(true);
  showToast("Verifying captcha… Please wait.", "info");

  try {
    const res = await fetch(
      "http://localhost:8080/api/user/register/verify-captcha",
      {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          captchaToken,
          userInput: form.captcha
        })
      }
    );

    const data = await res.json();
    setLoading(false);

    if (!res.ok) {
      showToast(data.message || "Invalid captcha.", "error");
      loadCaptcha();
      return;
    }

    setCaptchaVerified(true);
    showToast("Captcha verified successfully.", "success");
  } catch (err) {
    setLoading(false);
    showToast("Server error while verifying captcha.", "error");
  }
};


  /* ================= SUBMIT ================= */
  const handleSubmit = async () => {
  if (!captchaVerified) {
    showToast("Please verify captcha first.", "error");
    return;
  }

  if (form.password !== form.confirmPassword) {
    showToast("Password and Confirm Password do not match.", "error");
    return;
  }

  setLoading(true);
  showToast("OTP is being sent. Please wait…", "info");

  const payload = {
    ...form,
    day: Number(form.day),
    month: Number(form.month),
    year: Number(form.year)
  };

  try {
    const res = await fetch(
      "http://localhost:8080/api/user/register",
      {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
      }
    );

    const data = await res.json();
    setLoading(false);

    if (!res.ok) {
      showToast(data.status || "Registration failed.", "error");
      return;
    }

    navigate("/verify-otp", {
      state: {
        email: form.email,
        registrationData: payload
      }
    });
  } catch (err) {
    setLoading(false);
    showToast("Server error. Please try again later.", "error");
  }
};


  return (
    <>
      <TopBar />
      <Header />
      <TopNoticeBar />
      <MenuHeader />

      {toast.show && (
        <div className={`page-message ${toast.type}`}>
          {toast.text}
        </div>
      )}

      <div className="register-wrapper">
        <div className="register-card">
          <h4>JEE / NTA – 2026 APPLICATION FORM</h4>

          <div className="row">
            <input disabled={captchaVerified} name="firstName" placeholder="First Name *" onChange={handleChange} />
            <input disabled={captchaVerified} name="middleName" placeholder="Middle Name" onChange={handleChange} />
            <input disabled={captchaVerified} name="lastName" placeholder="Last Name *" onChange={handleChange} />
          </div>

          <div className="row single">
  <select className="custom-select" disabled={captchaVerified} name="gender" onChange={handleChange}>
    <option value="">Select Gender</option>
    <option>Male</option>
    <option>Female</option>
    <option>Other</option>
  </select>
</div>

<div className="row">
  <select className="custom-select small" disabled={captchaVerified} name="day" onChange={handleChange}>
    <option>DD</option>
    {[...Array(31)].map((_, i) => <option key={i + 1}>{i + 1}</option>)}
  </select>

  <select className="custom-select small" disabled={captchaVerified} name="month" onChange={handleChange}>
    <option>MM</option>
    {[...Array(12)].map((_, i) => <option key={i + 1}>{i + 1}</option>)}
  </select>

  <select className="custom-select small" disabled={captchaVerified} name="year" onChange={handleChange}>
    <option>YYYY</option>
    {Array.from({ length: 46 }, (_, i) => 1980 + i).map(y => <option key={y}>{y}</option>)}
  </select>
</div>

          <div className="row">
            <input disabled={captchaVerified} name="mobileNumber" placeholder="Mobile Number *" onChange={handleChange} />
            <input disabled={captchaVerified} name="email" placeholder="Email *" onChange={handleChange} />
            <input disabled={captchaVerified} name="aadharCard" placeholder="Aadhaar Number *" onChange={handleChange} />
          </div>

          <div className="row">
            <input disabled={captchaVerified} type="password" name="password" placeholder="Password *" onChange={handleChange} />
            <input disabled={captchaVerified} type="password" name="confirmPassword" placeholder="Confirm Password *" onChange={handleChange} />
          </div>

          <div className="captcha-box">
            {captchaImage ? (
            <img src={captchaImage} alt="captcha" />
              ) : (
                <div className="captcha-placeholder">Loading captcha…</div>
            )}
            <button
              type="button"
              className="captcha-refresh-btn"
              onClick={loadCaptcha}
            >
              ↻
            </button>
            <input
              name="captcha"
              placeholder="Enter Captcha"
              onChange={handleChange}
            />
          </div>

          {!captchaVerified && (
            <button
              className="primary-btn"
              onClick={handleVerifyCaptcha}
              disabled={loading}
            >
              {loading ? "Verifying..." : "Verify Captcha"}
            </button>
          )}

          {captchaVerified && (
            <div className="confirm-actions">
              <button
                className="primary-btn"
                onClick={handleSubmit}
                disabled={loading}
              >
                Submit & Send OTP
              </button>
              <button
                className="primary-btn edit-btn"
                onClick={() => window.location.reload()}
              >
                Edit
              </button>
            </div>
          )}
        </div>
      </div>
    </>
  );
};

export default Register;
