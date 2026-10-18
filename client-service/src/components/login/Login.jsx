import './Login.css';
import { useNavigate } from 'react-router-dom';
import { useState } from 'react';
import { api, session } from '../../api';

const emptySignup = { username: '', password: '', vorname: '', nachname: '', telefonnummer: '' };

function Login() {
  const navigate = useNavigate();

  const [signupData, setSignupData] = useState(emptySignup);
  const [loginData, setLoginData] = useState({ username: '', password: '' });
  const [signupError, setSignupError] = useState('');
  const [loginError, setLoginError] = useState('');

  const handleSignupChange = (e) => setSignupData({ ...signupData, [e.target.name]: e.target.value });
  const handleLoginChange = (e) => setLoginData({ ...loginData, [e.target.name]: e.target.value });

  const submit = async (path, body, setError) => {
    setError('');
    try {
      const data = await api(path, { method: 'POST', body, authenticated: false });
      session.save(data);
      navigate('/home');
    } catch (err) {
      setError(err.message);
    }
  };

  const handleSignupSubmit = (e) => {
    e.preventDefault();
    submit('/auth/register', signupData, setSignupError);
  };

  const handleLoginSubmit = (e) => {
    e.preventDefault();
    submit('/auth/login', loginData, setLoginError);
  };

  return (
    <div className="login-page">
      <div className="main">
        <input type="checkbox" id="chk" aria-hidden="true" />

        <div className="signup">
          <form onSubmit={handleSignupSubmit}>
            <label htmlFor="chk" aria-hidden="true">Sign up</label>

            <input type="text" name="username" placeholder="Username (= Telegram username)" required onChange={handleSignupChange} />
            <input type="password" name="password" placeholder="Password (min. 8 characters)" minLength={8} required onChange={handleSignupChange} />
            <input type="text" name="vorname" placeholder="Vorname" required onChange={handleSignupChange} />
            <input type="text" name="nachname" placeholder="Nachname" required onChange={handleSignupChange} />
            <input type="text" name="telefonnummer" placeholder="Nummer" onChange={handleSignupChange} />
            <button type="submit">Sign up</button>
            {signupError && <p className="form-error" role="alert">{signupError}</p>}
          </form>
        </div>

        <div className="login">
          <form onSubmit={handleLoginSubmit}>
            <label htmlFor="chk" aria-hidden="true">Login</label>
            <input type="text" name="username" placeholder="Username" required onChange={handleLoginChange} />
            <input type="password" name="password" placeholder="Password" required onChange={handleLoginChange} />
            <button type="submit">Login</button>
            {loginError && <p className="form-error" role="alert" style={{ color: '#b91c1c' }}>{loginError}</p>}
          </form>
        </div>
      </div>
    </div>
  );
}

export default Login;
