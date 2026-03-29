import PublicNotices from "./PublicNotices";
import Introduction from "./Introduction";
import CandidateActivity from "./CandidateActivity";

import "../../styles/homepage/HomeContent.css";

const HomeContent = () => {
  return (
    <div className="home-wrapper">
      <div className="home-grid">
        {/* LEFT */}
        <PublicNotices />

        {/* RIGHT (STICKY) */}
        <div className="right-column">
          <Introduction />
          <CandidateActivity />
        </div>
      </div>
    </div>
  );
};

export default HomeContent;
