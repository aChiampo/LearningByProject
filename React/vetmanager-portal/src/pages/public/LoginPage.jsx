import { useContext, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { AppContext } from '../../context/AppContext';
import { ROLE_CONFIG } from '../../data/roleConfig';

export default function LoginPage() {
  const navigate = useNavigate();
  const { currentRole, isLogged, login } = useContext(AppContext);
  const [form, setForm] = useState({
    email: '',
    password: '',
  });
  const [status, setStatus] = useState({ type: '', message: '' });
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    if (isLogged) {
      navigate(ROLE_CONFIG[currentRole]?.dashboard ?? '/', { replace: true });
    }
  }, [currentRole, isLogged, navigate]);

  function handleChange(event) {
    const { name, value } = event.target;
    setForm((currentForm) => ({
      ...currentForm,
      [name]: value,
    }));
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setIsSubmitting(true);
    setStatus({ type: '', message: '' });

    try {
      const session = await login(form);
      navigate(ROLE_CONFIG[session.user.role]?.dashboard ?? '/', { replace: true });
    } catch (error) {
      setStatus({
        type: 'error',
        message: error.message || 'Accesso non riuscito.',
      });
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <div className="auth-shell">
      <div className="panel login-form auth-card">
        <div className="auth-heading">
          <h2>Accesso al Portale</h2>
          <p>Inserisci le tue credenziali per entrare nel pannello</p>
        </div>

        <form className="stack-form" onSubmit={handleSubmit}>
          <label htmlFor="loginEmail">
            Email
            <input
              id="loginEmail"
              name="email"
              type="email"
              className="form-control"
              autoComplete="email"
              placeholder="nome@esempio.it"
              value={form.email}
              onChange={handleChange}
              required
            />
          </label>

          <label htmlFor="loginPassword">
            Password
            <input
              id="loginPassword"
              name="password"
              type="password"
              className="form-control"
              autoComplete="current-password"
              placeholder="Password"
              value={form.password}
              onChange={handleChange}
              required
            />
          </label>

          <button type="submit" className="btn btn-primary btn-full" disabled={isSubmitting}>
            {isSubmitting ? 'Accesso...' : 'Accedi'}
          </button>

          {status.message && (
            <p className={`form-status form-status--${status.type}`}>{status.message}</p>
          )}
        </form>
      </div>
    </div>
  );
}
