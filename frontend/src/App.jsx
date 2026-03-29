import { Routes, Route, Navigate } from "react-router-dom";

/* ================= PUBLIC PAGES ================= */
import Home from "./pages/Home";
import Register from "./pages/register/Register";
import OtpVerify from "./pages/register/OtpVerify";
import Login from "./pages/login/Login";


/* ================= ROUTE GUARDS ================= */
import AdminRoute from "./routes/AdminRoute";
import UserRoute from "./routes/UserRoute";

/* ================= USER ================= */
import UserDashboard from "./components/userdashboard/UserDashboard";
import PersonalDetails from "./components/userdashboard/PersonalDetails";
import EducationDetails from "./components/userdashboard/EducationDetails";
import DocumentDetails from "./components/userdashboard/DocumentDetails";
/* ================= CORRECTION ================= */
import UserCorrectionRequest from "./components/userdashboard/UserCorrectionRequest";
import BasicCorrectionPage from "./components/userdashboard/BasicCorrectionPage";

/* ================= ADMIN LAYOUT ================= */
import AdminLayout from "./admin/layout/AdminLayout";

/* ================= ADMIN PAGES ================= */
import AdminDashboard from "./admin/pages/dashboard/AdminDashboard";
import AdminCandidates from "./admin/pages/candidates/AdminCandidates";
import AdminCandidateDetails from "./admin/pages/candidates/AdminCandidateDetails";
import AdminCorrectionWindow from "./admin/pages/correction/AdminCorrectionWindow";
import AdminNews from "./admin/pages/news/AdminNews";
import AdminAuditLogs from "./admin/pages/audit/AdminAuditLogs";
import UserActivityTrack from "./admin/pages/audit/UserActivityTrack";

/* ================= PAYMENT PAGES ================= */
import PaymentPage from "./components/payment/PaymentPage";


import UserRequestedToAdminCorrectionRequests from "./admin/pages/correction/UserRequestedToAdminCorrectionRequests";
/* ================= ADMIN STYLES ================= */
import "./admin/styles/admin.css";


function App() {
  return (
    <Routes>

      {/* ================= PUBLIC ================= */}
      <Route path="/" element={<Home />} />
      <Route path="/register" element={<Register />} />
      <Route path="/verify-otp" element={<OtpVerify />} />
      <Route path="/login" element={<Login />} />

      {/* ================= USER PROTECTED ================= */}
      <Route path="/dashboard" element={<UserRoute />}>
        <Route index element={<UserDashboard />} />
        <Route path="personal-details" element={<PersonalDetails />} />
        <Route path="education-details" element={<EducationDetails />} />
        <Route path="document-details" element={<DocumentDetails />} />
        <Route path="user-correction-request" element={<UserCorrectionRequest />} />
        <Route path="basic-correction-update/:requestId" element={<BasicCorrectionPage />} />
        
        <Route path="payment" element={<PaymentPage />} />
      </Route>

      {/* ================= ADMIN PROTECTED ================= */}
      <Route path="/admin" element={<AdminRoute />}>
        <Route element={<AdminLayout />}>

          <Route path="dashboard" element={<AdminDashboard />} />

          <Route path="candidates" element={<AdminCandidates />} />
          <Route path="candidates/:id" element={<AdminCandidateDetails />} />

          <Route path="correction" element={<AdminCorrectionWindow />} />
          <Route path="user-requested-admin-correction" element={<UserRequestedToAdminCorrectionRequests  />} />
          <Route path="news" element={<AdminNews />} />

          <Route path="audit-logs" element={<AdminAuditLogs />} />

          {/* USER ACTIVITY TRACK */}
          <Route path="/admin/user_activity_track" element={<UserActivityTrack />} />

        </Route>
      </Route>

      {/* ================= FALLBACK ================= */}
      <Route path="*" element={<Navigate to="/" replace />} />

    </Routes>
  );
}

export default App;