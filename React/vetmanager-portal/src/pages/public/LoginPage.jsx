import { useContext, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { AppContext } from '../../context/AppContext';
import { ROLE_CONFIG } from '../../data/roleConfig';

export default function LoginPage() {
  const navigate = useNavigate();
  const { setCurrentRole, setIsLogged } = useContext(AppContext);
  const [selectedRole, setSelectedRole] = useState('client');

  function handleSubmit(event) {
    event.preventDefault();
    setCurrentRole(selectedRole);
    setIsLogged(true);
    navigate(ROLE_CONFIG[selectedRole].dashboard);
  }

  return (
    <div className="auth-shell">
      <div className="panel login-form auth-card">
        <div className="auth-heading">
          <h2>Accesso al Portale</h2>
          <p>Seleziona il tuo profilo ed entra nel pannello</p>
        </div>

        <form className="stack-form" onSubmit={handleSubmit}>
          <label htmlFor="roleSelect">
            Accedi come:
            <select
              id="roleSelect"
              name="roleSelect"
              value={selectedRole}
              onChange={(event) => setSelectedRole(event.target.value)}
            >
              <option value="client">Cliente (Andrea Rossi)</option>
              <option value="doctor">Veterinario (Dott. Zampetti)</option>
              <option value="receptionist">Reception (Giulia Ferri)</option>
              <option value="super-admin">Amministratore Studio</option>
            </select>
          </label>

          <label htmlFor="passwordDemo">
            Password Demo
            <input
              id="passwordDemo"
              type="password"
              className="form-control"
              placeholder="password"
              defaultValue="password123"
              required
            />
          </label>

          <button type="submit" className="btn btn-primary btn-full">
            Accedi
          </button>
        </form>
      </div>
    </div>
  );
}
