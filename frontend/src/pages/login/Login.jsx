import React, { useState } from "react";
import { useSearchParams, Link, useNavigate } from "react-router-dom";
import axios from "axios";

import TopBar from "../../components/layout/TopBar";
import Header from "../../components/layout/Header";

import { login } from "../../services/authService";
import "../../styles/login/Login.css";
import newIcon from "../../assets/icons/new.svg";

const Login = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();

  const loginType = searchParams.get("type") || "user";
  const isAdmin = loginType === "admin";

  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);

    const payload = {
      username: e.target.username.value.trim(),
      password: e.target.password.value,
    };

    try {
      const res = await login(payload);

      // 🔹 Save JWT
localStorage.setItem("jwtToken", res.token);

// 🔹 Normalize role
const userRole = res.role.startsWith("ROLE_") ? res.role : `ROLE_${res.role}`;
localStorage.setItem("userRole", userRole);

localStorage.setItem("userEmail", res.email);

// 🔹 Save USER INFO
localStorage.setItem(
  "user",
  JSON.stringify({
    firstName: res.firstName,
    applicationNumber: res.applicationNumber
  })
);

// 🔹 Save LOGIN INFO
localStorage.setItem(
  "loginInfo",
  JSON.stringify({
    loginTime: res.loginTime,
    ipAddress: res.ipAddress
  })
);

      // 🔹 Set default axios header for all future requests
      axios.defaults.headers.common["Authorization"] = `Bearer ${res.token}`;

      console.log("LOGIN ROLE:", userRole);

      // 🔹 ROLE BASED REDIRECT
      if (userRole === "ROLE_ADMIN") {
        navigate("/admin/dashboard", { replace: true });
      } else if (userRole === "ROLE_USER") {
        navigate("/dashboard", { replace: true });
      } else {
        setError("Unauthorized role");
      }

    } catch (err) {
      setError(
        err?.response?.data?.message ||
        "Login failed. Please check credentials."
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      <TopBar />
      <Header />

      <div className="login-wrapper">
        <div className="login-card">
          <h3>{isAdmin ? "Admin Login" : "Candidate Login"}</h3>
          <p>
            {isAdmin
              ? "Login with admin credentials"
              : "Enter your application credentials"}
          </p>

          {error && <div className="login-error">{error}</div>}

          <form onSubmit={handleSubmit}>
            <label>
              {isAdmin
                ? "Admin Email / Username *"
                : "Application Number or Email *"}
            </label>

            <input
              name="username"
              required
              placeholder={
                isAdmin
                  ? "Enter Admin Email or Username"
                  : "Enter Application Number OR Email"
              }
            />

            <label>Password *</label>
            <input
              type="password"
              name="password"
              required
              placeholder="Enter Password"
            />

            <button type="submit" disabled={loading}>
              {loading
                ? "Logging in..."
                : isAdmin
                  ? "Admin Login"
                  : "Login"}
            </button>

            {!isAdmin && (
              <div className="login-links">
                <Link to="/forgot-password">Forgot Password?</Link>
                <Link to="/forgot-application">
                  Forgot Application Number?
                </Link>
              </div>
            )}
          </form>

          {!isAdmin && (
            <div className="register-section">
              <p>If not registered</p>
              <Link to="/register" className="register-btn">
                <img src={newIcon} alt="New" />
                New Registration
              </Link>
            </div>
          )}
        </div>
      </div>
    </>
  );
};

export default Login;