import logo from './logo.svg';
import './App.css';
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";import Login from './components/login/Login';

function App() {
  return (
    <BrowserRouter>
      
      <Routes>
        <Route path="/" element={<Navigate to="/login" replace />} />
        <Route path="/login" element={<Login />} />
       
      </Routes>
    </BrowserRouter>
    
  );
}

export default App;
