// Import React (required for JSX)
import React from "react";

// useNavigate is used for programmatic navigation
import { useNavigate } from "react-router-dom";

// Import CSS for hero section styling
import "../../styles/HeroSection.css";

const HeroSection = () => {

  // Initialize navigation hook
  const navigate = useNavigate();

  return (
    // Main hero section container
    <section className="hero">

      {/* Heading text shown on homepage */}
      <h2>Welcome to JEE (Main) 2026</h2>

      {/* Button wrapper */}
      <div className="hero-buttons">

        {/* Apply button → redirects to register page */}
        <button
          className="btn primary-btn"
          onClick={() => navigate("/register")}
        >
          Register
        </button>

        {/* Candidate login → login page with user type */}
        <button
          className="btn secondary-btn"
          onClick={() => navigate("/login?type=user")}
        >
          Candidate Login
        </button>

        {/* Admin login → login page with admin type */}
        <button
          className="btn outline-btn"
          onClick={() => navigate("/login?type=admin")}
        >
          Admin Login
        </button>

      </div>
    </section>
  );
};

export default HeroSection;
