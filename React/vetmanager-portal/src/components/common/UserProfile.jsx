import { useContext, useMemo, useState } from 'react';
import { AppContext } from '../../context/AppContext';
import { ROLE_CONFIG } from '../../data/roleConfig';
import { updateUserProfile } from '../../services/userApi';

const PROFILE_FIELDS = [
  { name: 'nome', label: 'Nome', type: 'text', required: true },
  { name: 'cognome', label: 'Cognome', type: 'text', required: true },
  { name: 'email', label: 'Email', type: 'email', required: true },
  { name: 'telefono', label: 'Telefono', type: 'tel', required: true },
  { name: 'indirizzo', label: 'Indirizzo', type: 'text' },
  { name: 'citta', label: 'Citta', type: 'text' },
];

const EMPTY_PROFILE_FORM = {
  nome: '',
  cognome: '',
  email: '',
  telefono: '',
  indirizzo: '',
  citta: '',
};

function buildProfileForm(user) {
  return {
    nome: user?.nome ?? '',
    cognome: user?.cognome ?? '',
    email: user?.email ?? '',
    telefono: user?.telefono ?? '',
    indirizzo: user?.indirizzo ?? '',
    citta: user?.citta ?? '',
  };
}

function getDisplayValue(value) {
  return value || 'Non disponibile';
}

function getInitials(user, fallbackName) {
  const initials = [user?.nome, user?.cognome]
    .filter(Boolean)
    .map((part) => part.trim().charAt(0))
    .join('');

  if (initials) {
    return initials.toUpperCase();
  }

  return fallbackName
    .split(' ')
    .filter(Boolean)
    .slice(0, 2)
    .map((part) => part.charAt(0))
    .join('')
    .toUpperCase();
}

function ProfileField({ label, value }) {
  return (
    <div className="profile-detail">
      <dt>{label}</dt>
      <dd>{getDisplayValue(value)}</dd>
    </div>
  );
}

export default function UserProfile({ description, fallbackName, title }) {
  const { currentRole, currentUser, updateCurrentUser } = useContext(AppContext);
  const roleConfig = ROLE_CONFIG[currentRole];
  const displayName = [currentUser?.nome, currentUser?.cognome].filter(Boolean).join(' ') || fallbackName || roleConfig?.userName || 'Utente';
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [form, setForm] = useState(EMPTY_PROFILE_FORM);
  const [status, setStatus] = useState({ type: '', message: '' });
  const [isSubmitting, setIsSubmitting] = useState(false);
  const profileFields = useMemo(() => {
    const fields = [
      { label: 'Nome', value: currentUser?.nome },
      { label: 'Cognome', value: currentUser?.cognome },
      { label: 'Email', value: currentUser?.email },
      { label: 'Telefono', value: currentUser?.telefono },
      { label: 'Indirizzo', value: currentUser?.indirizzo },
      { label: 'Citta', value: currentUser?.citta },
    ];

    return fields;
  }, [currentUser]);

  function openDialog() {
    setForm(buildProfileForm(currentUser));
    setStatus({ type: '', message: '' });
    setIsDialogOpen(true);
  }

  function closeDialog() {
    if (!isSubmitting) {
      setIsDialogOpen(false);
    }
  }

  function handleChange(event) {
    const { name, value } = event.target;
    setForm((currentForm) => ({
      ...currentForm,
      [name]: value,
    }));
  }

  async function handleSubmit(event) {
    event.preventDefault();

    if (!currentUser?.id) {
      setStatus({ type: 'error', message: 'Utente non disponibile.' });
      return;
    }

    setIsSubmitting(true);
    setStatus({ type: '', message: '' });

    try {
      const updatedUser = await updateUserProfile(currentUser.id, form);
      updateCurrentUser(updatedUser);
      setStatus({ type: 'success', message: 'Profilo aggiornato.' });
      setIsDialogOpen(false);
    } catch (error) {
      setStatus({
        type: 'error',
        message: error.message || 'Impossibile aggiornare il profilo.',
      });
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <div className="panel panel-narrow user-profile">
      <div className="profile-avatar-row">
        <div className="profile-avatar" aria-hidden="true">{getInitials(currentUser, displayName)}</div>
        <div>
          <h2>{displayName}</h2>
          <p className="muted-text">{description ?? title ?? roleConfig?.label}</p>
        </div>
      </div>

      <dl className="profile-details">
        {profileFields.map((field) => (
          <ProfileField key={field.label} label={field.label} value={field.value} />
        ))}
      </dl>

      {status.message && !isDialogOpen && (
        <p className={`form-status form-status--${status.type}`}>{status.message}</p>
      )}

      <button type="button" className="btn btn-primary align-start" onClick={openDialog}>
        Modifica
      </button>

      {isDialogOpen && (
        <div className="profile-dialog" role="presentation">
          <div className="profile-dialog__backdrop" onClick={closeDialog} aria-hidden="true"></div>
          <section className="profile-dialog__window" role="dialog" aria-modal="true" aria-labelledby="profile-dialog-title">
            <div className="toolbar-row">
              <h2 id="profile-dialog-title">Modifica Profilo</h2>
              <button type="button" className="btn btn-outline btn-sm" onClick={closeDialog} disabled={isSubmitting}>
                Chiudi
              </button>
            </div>

            <form className="stack-form" onSubmit={handleSubmit}>
              <div className="form-grid-two">
                {PROFILE_FIELDS.map((field) => (
                  <label key={field.name}>
                    {field.label}
                    <input
                      className="form-control"
                      name={field.name}
                      type={field.type}
                      value={form[field.name]}
                      onChange={handleChange}
                      required={field.required}
                    />
                  </label>
                ))}
              </div>

              {status.message && (
                <p className={`form-status form-status--${status.type}`}>{status.message}</p>
              )}

              <div className="actions-row">
                <button type="submit" className="btn btn-primary" disabled={isSubmitting}>
                  {isSubmitting ? 'Salvataggio...' : 'Salva Modifiche'}
                </button>
                <button type="button" className="btn btn-outline" onClick={closeDialog} disabled={isSubmitting}>
                  Annulla
                </button>
              </div>
            </form>
          </section>
        </div>
      )}
    </div>
  );
}
