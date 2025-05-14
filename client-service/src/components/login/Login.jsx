import './Login.css'
import { useNavigate } from "react-router-dom";
import { useState } from "react";



function Login() {
  const navigate = useNavigate();

  const [signupData, setSignupData] = useState({
    username: "",
    password: "",
  });

  const [loginData, setLoginData] = useState({
    username: "",
    password: "",
  });

  const handleSignupChange = (e) => {
    setSignupData({ ...signupData, [e.target.name]: e.target.value });
  };

  const handleLoginChange = (e) => {
    setLoginData({ ...loginData, [e.target.name]: e.target.value });
  };

  const handleSignupSubmit = async (e) => {
    e.preventDefault();

    const dataToSend = {
      username: signupData.username,
      password: signupData.password,
      vorname: "",
      nachname: "",
      telefonnummer: signupData.telefonnummer,
      role: "USER",
      createdAt: ""
    };

    try {
      const res = await fetch("http://api-service:8090/register", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(dataToSend),
      });

      if (res.ok) {
        const data = await res.json();
        const token = data.token; 
        localStorage.setItem("token", token);
        navigate("/home");
      } else {
        alert("Ошибка при регистрации");
      }
    } catch (err) {
      console.error("Ошибка:", err);
    }
  };

  const handleLoginSubmit = async (e) => {
    e.preventDefault();

    try {
      const res = await fetch("http://api-service:8090/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(loginData),
      });

      if (res.ok) {
        const data = await res.json();
        const token = data.token; 
        localStorage.setItem("token", token);
        console.log(token)
        navigate("/home");
      } else {
        alert("Неверный логин или пароль");
      }
    } catch (err) {
      console.error("Ошибка:", err);
    }
  };

  return (
    <div className="main">
      <input type="checkbox" id="chk" aria-hidden="true" />

      <div className="signup">
        <form onSubmit={handleSignupSubmit}>
          <label htmlFor="chk" aria-hidden="true">Sign up</label>

          <input
            type="text"
            name="username"
            placeholder="Username"
            required
            onChange={handleSignupChange}
          />
          <input
            type="text"
            name="telefonnummer"
            placeholder="Nummer"
            required
            onChange={handleSignupChange}/>
          <input
            type="password"
            name="password"
            placeholder="Password"
            required
            onChange={handleSignupChange}
          />
          <button type="submit">Sign up</button>
        </form>
      </div>

      <div className="login">
        <form onSubmit={handleLoginSubmit}>
          <label htmlFor="chk" aria-hidden="true">Login</label>
          <input
            type="username"
            name="username"
            placeholder="Username"
            required
            onChange={handleLoginChange}
          />
          <input
            type="password"
            name="password"
            placeholder="Password"
            required
            onChange={handleLoginChange}
          />
          <button type="submit">Login</button>
        </form>
      </div>
    </div>
  );
}

export default Login;