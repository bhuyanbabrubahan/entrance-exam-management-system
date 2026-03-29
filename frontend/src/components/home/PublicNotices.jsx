import { useState } from "react";
import "../../styles/homepage/PublicNotices.css";

/*
  Temporary static data
  Later this can be replaced with API response
*/
const noticesData = [
  { text: "Admit Card for JEE(Main)-2026 [Session-I] is LIVE!", priority: "LIVE" },
  { text: "Advance City Intimation for JEE(Main)-2026 [Session-I] is LIVE!", priority: "LIVE" },
  { text: "Correction in Particulars of Online Application Form – Reg.", priority: "NEW" },
  { text: "Advisory for Candidates regarding Examination Day – Reg.", priority: "" },
  { text: "Schedule of JEE(Main)-2026 Examination – Reg.", priority: "" },
  { text: "Information Bulletin for JEE(Main)-2026 released", priority: "NEW" },
  { text: "Public Notice regarding CBT mode – Reg.", priority: "" },
  { text: "Instructions for PwD Candidates – Reg.", priority: "" }
];

// Number of notices per page
const ITEMS_PER_PAGE = 4;

const PublicNotices = () => {
  // Active tab (Public Notices / News & Events)
  const [activeTab, setActiveTab] = useState("notices");

  // Current pagination page
  const [currentPage, setCurrentPage] = useState(1);

  // Pagination calculations
  const totalPages = Math.ceil(noticesData.length / ITEMS_PER_PAGE);
  const startIndex = (currentPage - 1) * ITEMS_PER_PAGE;
  const visibleItems = noticesData.slice(startIndex, startIndex + ITEMS_PER_PAGE);

  // Check if there are any posts to display
  const hasPosts = activeTab === "notices" && noticesData.length > 0;

  return (
    <div className="notice-box">

      {/* =======================
          TABS SECTION
         ======================= */}
      <div className="notice-tabs">
        <button
          className={activeTab === "notices" ? "active" : ""}
          onClick={() => setActiveTab("notices")}
        >
          Public Notices
        </button>

        <button
          className={activeTab === "events" ? "active" : ""}
          onClick={() => setActiveTab("events")}
        >
          News & Events
        </button>
      </div>

      {/* =======================
          CONTENT SECTION
         ======================= */}
      <div className="notice-content">
        {hasPosts ? (
          <ul>
            {visibleItems.map((item, index) => (
              <li key={index}>
                {/* Priority badge (LIVE / NEW) */}
                {item.priority && (
                  <span className={`badge ${item.priority.toLowerCase()}`}>
                    {item.priority}
                  </span>
                )}
                {item.text}
              </li>
            ))}
          </ul>
        ) : (
          <p className="no-post">No post to display</p>
        )}
      </div>

      {/* =======================
          PAGINATION
         ======================= */}
      <div className="pagination">
        <button
          className="page-btn prev"
          disabled={!hasPosts || currentPage === 1}
          onClick={() => setCurrentPage(p => p - 1)}
        >
          Prev
        </button>

        <span className="page-info">
          Page {hasPosts ? currentPage : 0} of {hasPosts ? totalPages : 0}
        </span>

        <button
          className="page-btn next"
          disabled={!hasPosts || currentPage === totalPages}
          onClick={() => setCurrentPage(p => p + 1)}
        >
          Next
        </button>
      </div>

      {/* =======================
          VIEW ALL BUTTON
         ======================= */}
      <div className="view-all-wrapper">
        <button className="view-all-btn" disabled={!hasPosts}>
          View All
        </button>
      </div>

    </div>
  );
};

export default PublicNotices;
