import React from "react";
import "../../styles/TopNoticeBar.css";

const notices = [
  "Advance City Intimation for JEE(Main)-2026 [Session-I] is LIVE!",
  "Admit Card for JEE(Main)-2026 [Session-I] is LIVE!",
  "Advance City Intimation for JEE(Main)-2026 [Session-I] is LIVE!",
];

const TopNoticeBar = () => {
  return (
    <div className="top-notice">
      {/* LEFT FIXED LABEL */}
      <div className="notice-label">NOTICE</div>

      {/* SCROLLING AREA */}
      <div className="notice-scroll">
        <div className="notice-marquee">
          {notices.map((text, index) => (
            <span key={index} className="notice-item">
              {text}
              <span className="separator">|</span>
            </span>
          ))}
        </div>
      </div>
    </div>
  );
};

export default TopNoticeBar;
