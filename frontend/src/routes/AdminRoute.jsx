import { Navigate, Outlet } from "react-router-dom";

const AdminRoute = () => {
  const token = localStorage.getItem("jwtToken");
  const role = localStorage.getItem("userRole");

  if (!token) {
    return <Navigate to="/login?type=admin" replace />;
  }

  if (role !== "ROLE_ADMIN") {
    return <Navigate to="/" replace />;
  }

  return <Outlet />;
};

export default AdminRoute;
