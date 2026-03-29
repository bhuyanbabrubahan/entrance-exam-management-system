import { Navigate, Outlet } from "react-router-dom";
import { isAuthenticated, isUser } from "../utils/auth";

const UserRoute = () => {
  const token = localStorage.getItem("jwtToken");
  const role = localStorage.getItem("userRole");

  if (!token) {
    return <Navigate to="/login" replace />;
  }

  if (!role || !role.includes("USER")) {
    return <Navigate to="/" replace />;
  }

  return <Outlet />;
};

export default UserRoute;
