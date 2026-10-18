import { Navigate } from 'react-router-dom';
import { session } from '../../api';

/** Redirects to /login when there is no token; the API still enforces access on every call. */
const PrivateRoute = ({ children }) => {
  return session.token ? children : <Navigate to="/login" replace />;
};

export default PrivateRoute;
