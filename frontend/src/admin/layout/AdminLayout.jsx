import React, { useState } from "react";
import Sidebar from "./Sidebar";
import Topbar from "./Topbar";
import { Outlet } from "react-router-dom";
import { FaBars } from "react-icons/fa";

import "./layout.css";

const AdminLayout = () => {
  const [sidebarOpen, setSidebarOpen] = useState(false);

  return (
    <div className="admin-container">
      
      {/* ===== Sidebar ===== */}
      <Sidebar isOpen={sidebarOpen} setIsOpen={setSidebarOpen} />
      

      {/* ===== Overlay (Mobile Only) ===== */}
      {sidebarOpen && (
        <div
          className="sidebar-overlay"
          onClick={() => setSidebarOpen(false)}
        ></div>
      )}

      {/* ===== Main Content ===== */}
      <div className="admin-main">
        
        {/* Mobile Hamburger */}
        <div className="mobile-header">
          <FaBars
            className="menu-toggle"
            onClick={() => setSidebarOpen(true)}
          />
          <span className="mobile-title">Admin Panel</span>
        </div>

        <Topbar />

        <div className="admin-content">
          <Outlet />
        </div>
      </div>
    </div>
  );
};

export default AdminLayout;