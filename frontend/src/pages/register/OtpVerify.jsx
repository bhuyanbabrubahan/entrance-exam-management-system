import React, { useEffect, useRef, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import Header from "../../components/layout/Header";
import "../../styles/register/OtpVerify.css";

/* =====================================================
   HELPER: Mask Email Address
   ===================================================== */
const maskEmail = (email) => {
  if (!email) return "";
  const [name, domain] = email.split("@");
  return name.substring(0, 2) + "****@" + domain;
};

const OtpVerify = () => {

  /* =====================================================
     ROUTER DATA
     ===================================================== */
  const { state } = useLocation();
  const navigate = useNavigate();

  const email = state?.email;
  const registrationData = state?.registrationData;

  /* =====================================================
     SAFETY CHECK
     ===================================================== */
  useEffect(() => {
    if (!email || !registrationData) {
      navigate("/register", { replace: true });
    }
  }, [email, registrationData, navigate]);

  /* =====================================================
     STATE
     ===================================================== */
  const [otp, setOtp] = useState(Array(6).fill(""));
  const [loading, setLoading] = useState(false);
  const [pageLoading, setPageLoading] = useState(true);
  const [toast, setToast] = useState(null);
  const [timer, setTimer] = useState(10);
  const [blink, setBlink] = useState(false);

  const [otpVerified, setOtpVerified] = useState(false); // ✅ MASTER FLAG

  const inputsRef = useRef([]);

  /* =====================================================
     OTP TIMER
     ===================================================== */
  useEffect(() => {
    if (timer === 0 || otpVerified) return;

    const interval = setInterval(() => {
      setTimer((t) => t - 1);
    }, 1000);

    return () => clearInterval(interval);
  }, [timer, otpVerified]);

  /* =====================================================
     TOAST
     ===================================================== */
  const showToast = (msg, type = "success") => {
    setToast({ msg, type });
    setTimeout(() => setToast(null), 4000);
  };

  /* =====================================================
     OTP INPUT CHANGE
     ===================================================== */
  const handleChange = (value, index) => {
    if (otpVerified) return; // ⛔ HARD BLOCK
    if (!/^\d?$/.test(value)) return;

    const updated = [...otp];
    updated[index] = value;
    setOtp(updated);

    if (value && index < 5) {
      inputsRef.current[index + 1]?.focus();
    }
  };

  /* =====================================================
     PASTE HANDLER
     ===================================================== */
  const handlePaste = (e) => {
    if (otpVerified) return; // ⛔ BLOCK PASTE AFTER VERIFY

    const paste = e.clipboardData.getData("text").slice(0, 6);
    if (!/^\d+$/.test(paste)) return;

    setOtp(paste.split(""));
    inputsRef.current[5]?.focus();
  };

  /* =====================================================
     AUTO VERIFY (ONLY BEFORE VERIFIED)
     ===================================================== */
  useEffect(() => {
    if (
      otp.join("").length === 6 &&
      !loading &&
      !otpVerified
    ) {
      handleVerify();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [otp]);


  // =====================Otp Send after registration first time===========================
const otpInitRef = useRef(false); // 🔐 HARD BLOCK

useEffect(() => {
  if (!email) return;

  // ⛔ Prevent double execution (React StrictMode fix)
  if (otpInitRef.current) return;
  otpInitRef.current = true;

  const initOtpPage = async () => {
    try {
      setPageLoading(true);     // ⏳ boot screen ON
      setLoading(true);

      const res = await fetch(
        `http://localhost:8080/api/user/register/resend-otp?email=${encodeURIComponent(
          email
        )}`,
        { method: "POST" }
      );

      if (!res.ok) {
        throw new Error("OTP send failed");
      }

      await res.json();
      showToast("OTP sent successfully ✔", "success");

    } catch (err) {
      console.error(err);
      showToast("Failed to send OTP", "error");
    } finally {
      setLoading(false);
      setPageLoading(false);   // ✅ boot screen OFF
    }
  };

  initOtpPage();

  // eslint-disable-next-line react-hooks/exhaustive-deps
}, [email]);



  /* =====================================================
     VERIFY OTP
     ===================================================== */
  const handleVerify = async () => {
    if (loading || otpVerified) return; // ⛔ HARD BLOCK
    setLoading(true);

    try {
      const res = await fetch(
        `http://localhost:8080/api/user/register/verify-otp?email=${encodeURIComponent(
          email
        )}&otp=${otp.join("")}`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Accept: "application/json",
          },
          body: JSON.stringify(registrationData),
        }
      );

      const data = await res.json();

      if (!res.ok || data.status !== "REGISTRATION_SUCCESS") {
        setBlink(true);
        showToast(data.message || "Invalid OTP", "error");
        setOtp(Array(6).fill(""));
        setTimeout(() => setBlink(false), 500);
        return;
      }

      /* ✅ JWT STORE */
      if (data.token) {
        localStorage.setItem("jwtToken", data.token);
        console.log("🔐 JWT stored after OTP verification");
        localStorage.setItem("userRole", "ROLE_USER"); // ✅ REQUIRED
        localStorage.setItem("userEmail", email);      // ✅ OPTIONAL BUT GOOD
      } else {
        throw new Error("JWT not returned from server");
      }

      /* ✅ FINAL LOCK */
      setOtpVerified(true);
      showToast("OTP verified successfully ✔", "success");

    } catch (err) {
      console.error(err);
      showToast("Server error. Try again later.", "error");
    } finally {
      setLoading(false);
    }
  };

  /* =====================================================
     RESEND OTP
     ===================================================== */
  const handleResend = async () => {
    if (loading || otpVerified) return; // ⛔ BLOCK AFTER VERIFY
    setLoading(true);

    try {
      const res = await fetch(
        `http://localhost:8080/api/user/register/resend-otp?email=${encodeURIComponent(
          email
        )}`,
        { method: "POST" }
      );

      const data = await res.json();

      if (!res.ok) {
        showToast(data.message || "Failed to resend OTP", "error");
        return;
      }

      setOtp(Array(6).fill(""));
      setTimer(10);
      showToast("OTP resent successfully ✔", "success");

    } catch (err) {
      console.error("Resend OTP error:", err);
      showToast("Server error. Try again later.", "error");
    } finally {
      setLoading(false);
    }
  };

  /* =====================================================
     BOOT SCREEN
     ===================================================== */
  if (pageLoading) {
    return (
      <>
        <Header />
        <div className="otp-wrapper">
          <div className="otp-card">
            <h3>Preparing OTP Verification…</h3>
            <p>Sending OTP, please wait</p>
          </div>
        </div>
      </>
    );
  }

  /* =====================================================
     SUBMIT → DASHBOARD
     ===================================================== */
  const handleSubmit = () => {
    if (!otpVerified) return;
    navigate("/dashboard", { replace: true });
  };

  /* =====================================================
     UI
     ===================================================== */
  return (
    <>
      <Header />

      {toast && <div className={`toast ${toast.type}`}>{toast.msg}</div>}

      <div className="otp-wrapper">
        <div className="otp-card">

          <h3>OTP Verification</h3>

          <div className="nta-info">
            <ul>
              <li>OTP is valid for <b>2 minutes</b>.</li>
              <li>Do not share OTP with anyone.</li>
              <li>Use only the latest OTP.</li>
            </ul>
          </div>

          <p className="otp-email-text">
            OTP sent to <b>{maskEmail(email)}</b>
          </p>

          <div className="timer">
            OTP expires in: {timer}s
          </div>

          <div
            className={`otp-input-box ${blink ? "blink" : ""}`}
            onPaste={handlePaste}
          >
            {otp.map((digit, i) => (
              <input
                key={i}
                ref={(el) => (inputsRef.current[i] = el)}
                value={digit}
                maxLength="1"
                disabled={otpVerified} // ✅ LOCK INPUTS
                onChange={(e) => handleChange(e.target.value, i)}
              />
            ))}
          </div>

          {/* ❌ VERIFY + RESEND HIDDEN AFTER VERIFIED */}
          {!otpVerified && (
            <div className="otp-actions">
              <button
                className="otp-verify-btn"
                disabled={otp.join("").length < 6 || loading}
                onClick={handleVerify}
              >
                {loading ? "Verifying..." : "Verify OTP"}
              </button>

              <button
                className="otp-resend-btn"
                disabled={timer > 0 || loading}
                onClick={handleResend}
              >
                Resend OTP
              </button>
            </div>
          )}

          {/* ✅ SUBMIT ONLY AFTER VERIFIED */}
          {otpVerified && (
            <button
              className="otp-submit-btn"
              onClick={handleSubmit}
            >
              Submit & Go to Dashboard
            </button>
          )}

        </div>
      </div>
    </>
  );
};

export default OtpVerify;
