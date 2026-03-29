import React from 'react';
import emblem from '../../assets/images/national-emblem.png';
import ntaLogo from '../../assets/images/nta-logo.png';
import jeeLogo from '../../assets/images/jee-logo.png';
import '../../styles/Header.css'; // ✅ Corrected path

const Header = () => {
  return (
    <header className="header">
      <div className="header-left">
        <img src={emblem} alt="National Emblem" className="emblem" />
      </div>

      <div className="header-center">
        <h1>राष्ट्रीय परीक्षा एजेंसी</h1>
        <h2>National Testing Agency</h2>
        <h3>Joint Entrance Examination (Main)</h3>
      </div>

      <div className="header-right">
        <img src={ntaLogo} alt="NTA Logo" className="logo" />
        <img src={jeeLogo} alt="JEE Logo" className="logo" />
      </div>
    </header>
  );
};

export default Header;
