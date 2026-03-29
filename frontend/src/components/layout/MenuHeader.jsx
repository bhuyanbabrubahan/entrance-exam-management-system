import { NavLink } from "react-router-dom";
import { useState } from "react";
import "../../styles/MenuHeader.css";

const Header = () => {
  const [openDropdown, setOpenDropdown] = useState(false);

  return (
    <header className="main-header">
      <nav className="main-nav">
        <NavLink to="/" end>
          Home
        </NavLink>

        <NavLink to="/about">
          About Us
        </NavLink>

        <NavLink to="/contact">
          Contact Us
        </NavLink>

        {/* DROPDOWN */}
        <div
          className="dropdown"
          onMouseEnter={() => setOpenDropdown(true)}
          onMouseLeave={() => setOpenDropdown(false)}
        >
          <span
            className="dropdown-title"
            onClick={() => setOpenDropdown(!openDropdown)}
            role="button"
            tabIndex={0}
            aria-haspopup="true"
            aria-expanded={openDropdown}
          >
            Examination ▾
          </span>

          {openDropdown && (
            <div className="dropdown-menu">
              <NavLink to="/examination/schedule">
                Schedule
              </NavLink>
              <NavLink to="/examination/syllabus">
                Syllabus
              </NavLink>
            </div>
          )}
        </div>

        <NavLink to="/information">
          Information Bulletin
        </NavLink>
      </nav>
    </header>
  );
};

export default Header;
