import logo from './logo.svg';
import './App.css';

function App() {
  return (
    <div className="main">
      <input type="checkbox" id="chk" aria-hidden="true" />

    <div className="signup">
      <form>
        <label htmlFor="chk" aria-hidden="true">Sign up</label>
        <input type="text" name="txt" placeholder="Username" required={true} />
        <input type="email" name="email" placeholder="Email" required={true} />
        <input type="password" name="pswd" placeholder="Password" required={true} />
        <button type="submit">Sign up</button>
      </form>
    </div>

    <div className="login">
      <form>
        <label htmlFor="chk" aria-hidden="true">Login</label>
        <input type="email" name="email" placeholder="Email" required={true} />
        <input type="password" name="pswd" placeholder="Password" required={true} />
        <button type="submit">Login</button>
      </form>
    </div>
  </div>
  );
}

export default App;
