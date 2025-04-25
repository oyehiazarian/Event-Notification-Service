import logo from './logo.svg';
import './App.css';
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";import Login from './components/login/Login';
import Header from './components/header/Header'
import Home from './components/home/Home'
function App() {
  return (
    <BrowserRouter>
      
      <Routes>
        <Route path="/" element={<Navigate to="/login" replace />} />
        <Route path="/login" element={<Login />} />
         {/* Home route with Header */}
         <Route
          path="/home"
          element={
            <>
              <Header />
              <Home />
            </>
          }
        />
       
      </Routes>
    </BrowserRouter>
    
  );
}

export default App;
