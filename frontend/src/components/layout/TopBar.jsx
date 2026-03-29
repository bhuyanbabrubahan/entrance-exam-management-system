import React, { useEffect, useState } from "react";
import "../../styles/TopBar.css";

const TopBar = () => {
  const [dateTime, setDateTime] = useState(new Date());
  const [fontSize, setFontSize] = useState(13);
  const [language, setLanguage] = useState("en");

  useEffect(() => {
    const timer = setInterval(() => setDateTime(new Date()), 1000);
    return () => clearInterval(timer);
  }, []);

  const changeFont = (size) => {
    setFontSize(size);
    document.documentElement.style.fontSize = size + "px";
  };

  const options = {
    weekday: "long",
    day: "numeric",
    month: "long",
    year: "numeric",
  };

  return (
    <div className="topbar" style={{ fontSize }}>
      {/* LEFT SIDE */}
      <div className="topbar-left">
        {language === "en" ? (
          <>
            <span>Government of India</span>
            <span>Ministry of Education</span>
          </>
        ) : (
          <>
            <span>भारत सरकार</span>
            <span>शिक्षा मंत्रालय</span>
          </>
        )}
      </div>

      {/* RIGHT SIDE */}
      <div className="topbar-right">
        {/* Language Switch */}
        <div className="lang-switch">
          <button
            className={language === "en" ? "active" : ""}
            onClick={() => setLanguage("en")}
          >
            English
          </button>
          <span>|</span>
          <button
            className={language === "hi" ? "active" : ""}
            onClick={() => setLanguage("hi")}
          >
            हिंदी
          </button>
        </div>

        {/* Font Controls */}
        <div className="font-controls">
          <button onClick={() => changeFont(12)}>A−</button>
          <button onClick={() => changeFont(13)}>A</button>
          <button onClick={() => changeFont(14)}>A+</button>
        </div>

        {/* Date Time */}
        <span>{dateTime.toLocaleDateString("en-IN", options)}</span>
        <span>{dateTime.toLocaleTimeString("en-IN")}</span>
      </div>
    </div>
  );
};

export default TopBar;
