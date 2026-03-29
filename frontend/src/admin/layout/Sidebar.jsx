import { NavLink } from "react-router-dom";
import {
  FaTachometerAlt,
  FaUsers,
  FaEdit,
  FaNewspaper,
  FaClipboardList,
  FaTimes
} from "react-icons/fa";

import "./sidebar.css";

const Sidebar = ({ isOpen, setIsOpen }) => {
  return (
    <>
      <aside className={`admin-sidebar ${isOpen ? "open" : ""}`}>
        
        {/* ===== Logo & Close (Mobile) ===== */}
        <div className="sidebar-header">
          <span className="logo-text">National Examinaation Data Center</span>
          <FaTimes
            className="close-btn"
            onClick={() => setIsOpen(false)}
          />
        </div>

        {/* ===== Navigation Menu ===== */}
        <nav className="sidebar-menu">
          <NavLink
            to="/admin/dashboard"
            className={({ isActive }) =>
              isActive ? "sidebar-link active" : "sidebar-link"
            }
            onClick={() => setIsOpen(false)}
          >
            <FaTachometerAlt className="sidebar-icon" />
            <span>Dashboard</span>
          </NavLink>

          <NavLink
            to="/admin/candidates"
            className={({ isActive }) =>
              isActive ? "sidebar-link active" : "sidebar-link"
            }
            onClick={() => setIsOpen(false)}
          >
            <FaUsers className="sidebar-icon" />
            <span>Candidates</span>
          </NavLink>

          <NavLink
            to="/admin/correction"
            className={({ isActive }) =>
              isActive ? "sidebar-link active" : "sidebar-link"
            }
            onClick={() => setIsOpen(false)}
          >
            <FaEdit className="sidebar-icon" />
            <span>Correction Window</span>
          </NavLink>

          <NavLink
            to="/admin/news"
            className={({ isActive }) =>
              isActive ? "sidebar-link active" : "sidebar-link"
            }
            onClick={() => setIsOpen(false)}
          >
            <FaNewspaper className="sidebar-icon" />
            <span>News</span>
          </NavLink>

          <NavLink
            to="/admin/audit"
            className={({ isActive }) =>
              isActive ? "sidebar-link active" : "sidebar-link"
            }
            onClick={() => setIsOpen(false)}
          >
            <FaClipboardList className="sidebar-icon" />
            <span>Audit Logs</span>
          </NavLink>
        </nav>

        {/* ===== Footer ===== */}
        <div className="sidebar-footer">
          <span>© 2026 JEE System</span>
        </div>
      </aside>
    </>
  );
};

export default Sidebar;