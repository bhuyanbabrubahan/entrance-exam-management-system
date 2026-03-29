import React, { useEffect, useState, useRef } from "react";
import { useNavigate } from "react-router-dom";
import nationalEmblem from "../../assets/images/national-emblem.png";
import {
  FaBell,
  FaMoon,
  FaSun,
  FaSignOutAlt,
  FaUserCircle,
  FaCog,
  FaChevronDown
} from "react-icons/fa";

import "./topbar.css";


const Topbar = () => {
  const navigate = useNavigate();

  const [time, setTime] = useState(new Date());
  const [darkMode, setDarkMode] = useState(false);
  const [fontSize, setFontSize] = useState(16);
  const [showNotifications, setShowNotifications] = useState(false);
  const [showProfile, setShowProfile] = useState(false);

  const notificationRef = useRef();
  const profileRef = useRef();

  /* Load Settings */
  useEffect(() => {
    const savedTheme = localStorage.getItem("theme");
    const savedFont = localStorage.getItem("fontSize");

    if (savedTheme === "dark") {
      setDarkMode(true);
      document.body.classList.add("dark-theme");
    }

    if (savedFont) {
      const size = parseInt(savedFont);
      setFontSize(size);
      document.documentElement.style.fontSize = size + "px";
    }
  }, []);

  /* Live Clock */
  useEffect(() => {
    const interval = setInterval(() => {
      setTime(new Date());
    }, 1000);
    return () => clearInterval(interval);
  }, []);

  /* Close Dropdown Outside */
  useEffect(() => {
    const handleClickOutside = (e) => {
      if (notificationRef.current && !notificationRef.current.contains(e.target)) {
        setShowNotifications(false);
      }
      if (profileRef.current && !profileRef.current.contains(e.target)) {
        setShowProfile(false);
      }
    };

    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  /* Theme Toggle */
  const handleThemeToggle = () => {
    const newTheme = !darkMode;
    setDarkMode(newTheme);

    if (newTheme) {
      document.body.classList.add("dark-theme");
      localStorage.setItem("theme", "dark");
    } else {
      document.body.classList.remove("dark-theme");
      localStorage.setItem("theme", "light");
    }
  };

  /* Font Controls */
  const increaseFont = () => {
    const newSize = Math.min(fontSize + 1, 22);
    setFontSize(newSize);
    document.documentElement.style.fontSize = newSize + "px";
    localStorage.setItem("fontSize", newSize);
  };

  const decreaseFont = () => {
    const newSize = Math.max(fontSize - 1, 14);
    setFontSize(newSize);
    document.documentElement.style.fontSize = newSize + "px";
    localStorage.setItem("fontSize", newSize);
  };

  /* Logout */
  const handleLogout = () => {
    localStorage.removeItem("jwtToken");
    navigate("/login");
  };

  const formattedDate = time.toLocaleDateString("en-IN", {
    weekday: "short",
    year: "numeric",
    month: "short",
    day: "numeric"
  });

  const formattedTime = time.toLocaleTimeString();

  return (
    <header className="admin-topbar">
      {/* LEFT SIDE */}
      <div className="topbar-left">
  <div className="logo-wrapper">

    {/* Logo */}
    <img 
      src={nationalEmblem}
      alt="National Emblem"
      className="portal-logo"
    />

    {/* Portal Text */}
    <div className="logo-text">
      <h2 className="portal-title">
        Government Data Center
        <span className="highlight"> Admin Portal</span>
      </h2>
      <span className="subtitle">Government of India</span>
    </div>

    {/* Date & Time */}
    <div className="datetime">
      <span>{formattedDate}</span>
      <span className="clock">{formattedTime}</span>
    </div>

  </div>
</div>

      

      {/* RIGHT SIDE */}
      <div className="topbar-right">

        {/* Font Controls */}
        <div className="font-controls">
          <button onClick={increaseFont}>A+</button>
          <button onClick={decreaseFont}>A-</button>
        </div>

        {/* Theme Toggle */}
        <div className="icon-btn" onClick={handleThemeToggle}>
          {darkMode ? <FaSun /> : <FaMoon />}
        </div>

        {/* Notifications */}
        <div
          className="icon-btn notification-wrapper"
          ref={notificationRef}
          onClick={() => setShowNotifications(!showNotifications)}
        >
          <FaBell />
          <span className="badge">3</span>

          {showNotifications && (
            <div className="dropdown">
              <p>New candidate registered</p>
              <p>Application approved</p>
              <p>System maintenance tonight</p>
            </div>
          )}
        </div>

        {/* Profile */}
        <div
          className="profile-wrapper"
          ref={profileRef}
          onClick={() => setShowProfile(!showProfile)}
        >
          <div className="avatar">A</div>
          <span className="admin-name">Admin</span>
          <FaChevronDown className="down-icon" />

          {showProfile && (
            <div className="dropdown profile-dropdown">
              <p><FaUserCircle /> My Profile</p>
              <p><FaCog /> Settings</p>
              <button onClick={handleLogout}>
                <FaSignOutAlt /> Logout
              </button>
            </div>
          )}
        </div>

      </div>
    </header>
  );
};

export default Topbar;