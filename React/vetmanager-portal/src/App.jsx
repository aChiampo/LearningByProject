import React, { useContext } from 'react';
import { AppContext } from './context/AppContext';
import Header from './components/Header';
import Sidebar from './components/Sidebar';
import HelpAction from './components/HelpAction';
import { ROLE_CONFIG } from './data/roleConfig';
// Piccolo helper per agganciare le funzioni del Context al form di login
function HookBridge() {
  const { setCurrentRole, setCurrentScreen, setIsLogged } = useContext(AppContext);
  window.__setRoleAndScreen = (role) => {
    setCurrentRole(role);
    setIsLogged(true); // <-- Dice all'app che l'utente è loggato!
    setCurrentScreen(`dashboard-${role}`);
  };
  return null;
}

export default function App() {
  // 1. Prendi lo schermo, il ruolo e lo stato di login dal Context
  const { currentScreen, setCurrentScreen, currentRole, isLogged } = useContext(AppContext);
  
  // 2. Recupera la configurazione del ruolo corrente (FONDAMENTALE per far funzionare config.animals, ecc.)
  const config = ROLE_CONFIG[currentRole];

  // Consideriamo "pubbliche" le schermate iniziali in cui non serve la Sidebar
  const isPublicScreen = 
    currentScreen === 'homepage' || 
    currentScreen === 'login' ||
    currentScreen === 'services' || 
    currentScreen === 'clinic' || 
    currentScreen === 'router';

  return (
    <>
      {/* L'Header rimane sempre visibile in alto */}
      <Header />
      
      {/* Questo container ha le classi strutturali del tuo stiles.css */}
      <main className="app-container">
        
        {/* La barra laterale compare solo se siamo dentro un'area riservata */}
        {!isPublicScreen && <Sidebar />}

        {/* Qui dentro inseriamo i contenuti che cambiano dinamicamente */}
        <div className="content-wrapper">
          
          {/* 1. HOMEPAGE PUBBLICA */}
          {currentScreen === 'homepage' && (
            <>
              {/* Sezione Hero con l'immagine di sfondo */}
              <section className="home-hero">
                <div className="hero-copy">
                  <h1>Studio Veterinario <br />San Luca</h1>
                  <p>Cura, visite e prevenzione per i tuoi amici animali a Bergamo Alta.</p>
                  
                  {/* --- INIZIO PULSANTI DINAMICI INTELLIGENTI --- */}
                  <div style={{ marginTop: '16px' }}>
                    
                    {/* CASO 1: L'utente NON è loggato */}
                    {!isLogged && (
                      <button 
                        className="btn btn-secondary"
                        onClick={() => setCurrentScreen('router')}
                      >
                        Prenota un servizio
                      </button>
                    )}

                    {/* CASO 2: L'utente È LOGGATO ed è un CLIENTE */}
                    {isLogged && currentRole === 'client' && (
                      <button 
                        className="btn btn-secondary"
                        onClick={() => setCurrentScreen('dashboard-client')}
                      >
                        Prenota un servizio
                      </button>
                    )}

                    {/* CASO 3: L'utente È LOGGATO ed è la RECEPTIONIST */}
                    {isLogged && currentRole === 'receptionist' && (
                      <button 
                        className="btn btn-secondary"
                        onClick={() => setCurrentScreen('dashboard-receptionist')}
                        style={{ background: 'var(--ink)', color: 'var(--surface)' }}
                      >
                        Gestisci appuntamenti
                      </button>
                    )}

                    {/* CASO 4: L'utente È LOGGATO ed è un VETERINARIO */}
                    {isLogged && currentRole === 'doctor' && (
                      <button 
                        className="btn btn-secondary"
                        onClick={() => setCurrentScreen('dashboard-doctor')}
                      >
                        Visualizza registro visite
                      </button>
                    )}

                    {/* CASO 5: L'utente È LOGGATO ed è l'ADMIN */}
                    {isLogged && currentRole === 'super-admin' && (
                      <button 
                        className="btn btn-secondary"
                        onClick={() => setCurrentScreen('dashboard-super-admin')}
                      >
                        Pannello Admin
                      </button>
                    )}

                  </div>
                  {/* --- FINE PULSANTI DINAMICI INTELLIGENTI --- */}

                </div>
              </section>

              {/* Box dei punti di forza (Highlights) */}
              <div className="home-highlights">
                <div>
                  <strong>Bergamo Alta</strong>
                  <p>Sede storica facilmente raggiungibile</p>
                </div>
                <div>
                  <strong>Dott. Zampetti</strong>
                  <p>Specialista in piccoli animali e chirurgia</p>
                </div>
                <div>
                  <strong>Pronto Soccorso</strong>
                  <p>Reperibilità e supporto continuo</p>
                </div>
              </div>

              {/* Messaggio dello Studio */}
              <section className="home-content">
                <div className="clinic-message">
                  <h2>La nostra filosofia</h2>
                  <p>Ci prendiamo cura dei vostri compagni di vita con le migliori tecnologie e una profonda passione, garantendo controlli accurati e terapie personalizzate in un ambiente sereno.</p>
                </div>
              </section>
            </>
          )}

         {/* 2. SCHERMATA DI LOGIN INTERATTIVA */}
          {currentScreen === 'router' && (
            <div style={{ maxWidth: '450px', margin: '60px auto', padding: '0 16px' }}>
              <div className="panel login-form" style={{ padding: '32px', borderRadius: '12px' }}>
                <div style={{ textAlign: 'center', marginBottom: '24px' }}>
                  <h2 style={{ fontSize: '1.8rem', marginBottom: '8px' }}>Accesso al Portale</h2>
                  <p style={{ color: 'var(--muted)', fontSize: '0.95rem' }}>Seleziona il tuo profilo ed entra nel pannello</p>
                </div>

                <form 
                  onSubmit={(e) => { 
                    e.preventDefault(); 
                    // Recuperiamo il ruolo scelto dal menu a tendina
                    const selectedRole = e.target.roleSelect.value;
                    
                    // Aggiorniamo lo STATO GLOBALE con il ruolo scelto
                    const { setCurrentRole } = React.unstable_batchedUpdates ? { setCurrentRole: (r) => r } : require('./context/AppContext'); 
                    // Nota: Usiamo la funzione recuperata dal context direttamente nel submit
                  }}
                  // Per farlo in modo pulito e immediato con il nostro Context, usiamo una funzione inline agganciata ai valori:
                  onClick={(e) => {
                    if(e.target.tagName === 'BUTTON' && e.target.type === 'submit') {
                      e.preventDefault();
                      const selectEl = e.currentTarget.querySelector('#roleSelect');
                      const chosenRole = selectEl.value;
                      
                      // Questa riga magica cambia sia il ruolo che la schermata!
                      window.__setRoleAndScreen(chosenRole);
                    }
                  }}
                  style={{ display: 'flex', flexDirection: 'column', gap: '18px' }}
                >
                  <div>
                    <label htmlFor="roleSelect" style={{ display: 'block', marginBottom: '6px', fontWeight: 'bold', fontSize: '0.9rem' }}>
                      Accedi come:
                    </label>
                    <select 
                      id="roleSelect"
                      name="roleSelect"
                      style={{ width: '100%', padding: '12px', borderRadius: '6px', border: '1px solid var(--line)', background: 'var(--surface)', fontSize: '1rem' }}
                    >
                      <option value="client">Cliente (Andrea Rossi)</option>
                      <option value="doctor">Veterinario (Dott. Zampetti)</option>
                      <option value="receptionist">Reception (Giulia Ferri)</option>
                      <option value="super-admin">Amministratore Studio</option>
                    </select>
                  </div>

                  <div>
                    <label style={{ display: 'block', marginBottom: '6px', fontWeight: 'bold', fontSize: '0.9rem' }}>
                      Password Demo
                    </label>
                    <input 
                      type="password" 
                      className="form-control" 
                      placeholder="••••••••" 
                      defaultValue="password123"
                      required 
                      style={{ width: '100%', padding: '12px', borderRadius: '6px', border: '1px solid var(--line)', background: 'var(--paper)' }}
                    />
                  </div>

                  {/* Iniettiamo una funzione temporanea nel render per agganciare le funzioni del context */}
                  <HookBridge />

                  <button type="submit" className="btn btn-primary" style={{ padding: '12px', marginTop: '10px', fontSize: '1rem', width: '100%' }}>
                    Accedi
                  </button>
                </form>
              </div>
            </div>
          )}

          {/* 3. DASHBOARD CLIENTE COMPLETA */}
          {currentScreen === 'dashboard-client' && (
            <div>
              <div className="page-title">
                <p className="eyebrow">Area Riservata</p>
                <h1>Dashboard Cliente</h1>
              </div>

              {/* Sezione Animali Dinamica */}
              <section className="section-block">
                <h2>I tuoi animali</h2>
                
                {/* Controlliamo se ci sono animali nell'array del file roleConfig.js */}
                {config?.animals && config.animals.length > 0 ? (
                  <div className="grid-cards" style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))', gap: '20px', marginTop: '16px' }}>
                    {config.animals.map((animale, index) => (
                      <article key={index} className="panel card">
                        <div className="card-header">
                          <h3>{animale.nome}</h3>
                          <span className="badge">{animale.specie}</span>
                        </div>
                        <p style={{ margin: '12px 0', color: 'var(--muted)' }}>Razza: {animale.razza} • Età: {animale.eta}</p>
                        <div style={{ display: 'flex', gap: '10px' }}>
                          <button className="btn btn-primary btn-sm" onClick={() => setCurrentScreen('client-booking')}>Prenota Visita</button>
                          <button className="btn btn-outline btn-sm">Apri Cartella</button>
                        </div>
                      </article>
                    ))}
                  </div>
                ) : (
                  /* Messaggio se non ci sono animali (Array vuoto) */
                  <p style={{ color: 'var(--muted)', fontStyle: 'italic', marginTop: '16px' }}>
                    Nessun animale registrato nel tuo profilo.
                  </p>
                )}
              </section>

              {/* Sezione Prossimi Appuntamenti Dinamica */}
              <section className="section-block" style={{ marginTop: '40px' }}>
                <h2>Prossimi Appuntamenti</h2>
                
                {config?.appointments && config.appointments.length > 0 ? (
                  <div className="panel table-responsive" style={{ marginTop: '16px' }}>
                    <table className="data-table" style={{ width: '100%', borderCollapse: 'collapse' }}>
                      <thead>
                        <tr style={{ borderBottom: '2px solid var(--line)', textAlign: 'left' }}>
                          <th style={{ padding: '12px' }}>Animale</th>
                          <th style={{ padding: '12px' }}>Data e Ora</th>
                          <th style={{ padding: '12px' }}>Prestazione</th>
                          <th style={{ padding: '12px' }}>Veterinario</th>
                          <th style={{ padding: '12px' }}>Stato</th>
                        </tr>
                      </thead>
                      <tbody>
                        {config.appointments.map((app, index) => (
                          <tr key={index} style={{ borderBottom: '1px solid var(--line)' }}>
                            <td style={{ padding: '12px' }}><strong>{app.animalName}</strong></td>
                            <td style={{ padding: '12px' }}>{app.date}</td>
                            <td style={{ padding: '12px' }}>{app.type}</td>
                            <td style={{ padding: '12px' }}>{app.doctor}</td>
                            <td style={{ padding: '12px' }}>
                              <span className="badge badge-success" style={{ background: 'var(--primary-soft)', color: 'var(--primary-dark)' }}>
                                {app.status}
                              </span>
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                ) : (
                  /* Messaggio se non ci sono appuntamenti (Array vuoto) */
                  <p style={{ color: 'var(--muted)', fontStyle: 'italic', marginTop: '16px' }}>
                    Non ci sono appuntamenti in programma.
                  </p>
                )}
              </section>
            </div>
          )}

          {/* 3.1 PROFILO CLIENTE */}
          {currentScreen === 'profile-client' && (
            <div>
              <div className="page-title">
                <p className="eyebrow">Area Riservata</p>
                <h1>Profilo Personale</h1>
              </div>
              
              <div className="panel" style={{ maxWidth: '600px', marginTop: '24px' }}>
                <form onSubmit={(e) => e.preventDefault()} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
                  <div>
                    <label style={{ display: 'block', marginBottom: '6px', fontWeight: 'bold' }}>Nome Completo</label>
                    <input type="text" className="form-control" defaultValue={config?.userName || "Andrea Rossi"} style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid var(--line)' }} />
                  </div>
                  <div>
                    <label style={{ display: 'block', marginBottom: '6px', fontWeight: 'bold' }}>Email</label>
                    <input type="email" className="form-control" defaultValue="andrea.rossi@example.com" style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid var(--line)' }} />
                  </div>
                  <div>
                    <label style={{ display: 'block', marginBottom: '6px', fontWeight: 'bold' }}>Telefono</label>
                    <input type="tel" className="form-control" defaultValue="+39 333 1234567" style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid var(--line)' }} />
                  </div>
                  <div>
                    <label style={{ display: 'block', marginBottom: '6px', fontWeight: 'bold' }}>Ruolo di Accesso</label>
                    <input type="text" className="form-control" value={config?.label || ""} disabled style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid var(--line)', background: 'var(--soft)', cursor: 'not-allowed' }} />
                  </div>
                  <div>
                    <button type="submit" className="btn btn-primary" style={{ alignSelf: 'flex-start', marginTop: '10px' }}>Salva Modifiche</button>
                  </div>
                </form>
              </div>
            </div>
          )}

          {/* 3.2 PRENOTAZIONE APPUNTAMENTO CLIENTE */}
          {currentScreen === 'client-booking' && (
            <div>
              <div className="page-title">
                <p className="eyebrow">Nuova Richiesta</p>
                <h1>Prenota Appuntamento</h1>
              </div>

              <div className="panel" style={{ maxWidth: '600px', marginTop: '24px' }}>
                <form onSubmit={(e) => { e.preventDefault(); alert('Richiesta inviata con successo!'); setCurrentScreen('dashboard-client'); }} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
                  <div>
                    <label style={{ display: 'block', marginBottom: '6px', fontWeight: 'bold' }}>Seleziona l'animale</label>
                    <select style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid var(--line)', background: 'var(--surface)' }}>
                      {config?.animals && config.animals.length > 0 ? (
                        config.animals.map((animale, index) => (
                          <option key={index}>{animale.nome} ({animale.specie})</option>
                        ))
                      ) : (
                        <option>Nessun animale salvato - Inserimento manuale</option>
                      )}
                    </select>
                  </div>
                  <div>
                    <label style={{ display: 'block', marginBottom: '6px', fontWeight: 'bold' }}>Tipo di prestazione</label>
                    <select style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid var(--line)', background: 'var(--surface)' }}>
                      <option>Vaccino Annuale</option>
                      <option>Visita di Controllo</option>
                      <option>Chirurgia / Intervento</option>
                    </select>
                  </div>
                  <div>
                    <label style={{ display: 'block', marginBottom: '6px', fontWeight: 'bold' }}>Veterinario preferito</label>
                    <select style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid var(--line)', background: 'var(--surface)' }}>
                      <option>Dott. Camillo Zampetti</option>
                      <option>Qualsiasi Veterinario dello Studio</option>
                    </select>
                  </div>
                  <div>
                    <label style={{ display: 'block', marginBottom: '6px', fontWeight: 'bold' }}>Data e Fascia Oraria</label>
                    <input type="date" style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid var(--line)', marginBottom: '10px' }} />
                    <select style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid var(--line)', background: 'var(--surface)' }}>
                      <option>Mattina (09:00 - 12:30)</option>
                      <option>Pomeriggio (14:30 - 18:30)</option>
                    </select>
                  </div>
                  <div style={{ display: 'flex', gap: '12px', marginTop: '10px' }}>
                    <button type="submit" className="btn btn-primary">Invia Richiesta</button>
                    <button type="button" className="btn btn-outline" onClick={() => setCurrentScreen('dashboard-client')}>Annulla</button>
                  </div>
                </form>
              </div>
            </div>
          )}

          {/* 3.3 GESTIONE PAGAMENTI CLIENTE */}
          {currentScreen === 'client-billing' && (
            <div>
              <div className="page-title">
                <p className="eyebrow">Amministrazione</p>
                <h1>I tuoi Pagamenti</h1>
              </div>

              {/* Controlliamo se ci sono fatture/ricevute nell'array */}
              {config?.billing && config.billing.length > 0 ? (
                <>
                  {/* Sezione Dinamica "Da Saldare" (Fatture non pagate) */}
                  <section className="section-block" style={{ marginTop: '24px' }}>
                    <h2 style={{ color: 'var(--danger)', fontSize: '1.2rem', marginBottom: '12px' }}>Da Saldare</h2>
                    {config.billing.filter(b => b.status === 'Da Saldare').map((fattura, index) => (
                      <div key={index} className="panel" style={{ padding: '20px', display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderLeft: '4px solid var(--danger)', marginBottom: '10px' }}>
                        <div style={{ flexGrow: 1 }}>
                          <strong>{fattura.description}</strong>
                          <p style={{ color: 'var(--muted)', fontSize: '0.9rem', marginTop: '4px' }}>Data prestazione: {fattura.date}</p>
                        </div>
                        <div style={{ textAlign: 'right', display: 'flex', alignItems: 'center', gap: '20px' }}>
                          <span style={{ fontSize: '1.2rem', fontWeight: 'bold' }}>€ {fattura.amount}</span>
                          <button className="btn btn-primary btn-sm" onClick={() => alert('Simulazione pagamento riuscita!')}>Paga Ora</button>
                        </div>
                      </div>
                    ))}
                  </section>

                  {/* Storico Ricevute Dinamico */}
                  <section className="section-block" style={{ marginTop: '40px' }}>
                    <h2>Storico Ricevute e Fatture</h2>
                    <div className="panel table-responsive" style={{ marginTop: '16px' }}>
                      <table className="data-table" style={{ width: '100%', borderCollapse: 'collapse' }}>
                        <thead>
                          <tr style={{ borderBottom: '2px solid var(--line)', textAlign: 'left' }}>
                            <th style={{ padding: '12px' }}>Numero</th>
                            <th style={{ padding: '12px' }}>Data</th>
                            <th style={{ padding: '12px' }}>Descrizione</th>
                            <th style={{ padding: '12px' }}>Importo</th>
                            <th style={{ padding: '12px' }}>Stato</th>
                          </tr>
                        </thead>
                        <tbody>
                          {config.billing.map((fattura, index) => (
                            <tr key={index} style={{ borderBottom: '1px solid var(--line)' }}>
                              <td style={{ padding: '12px' }}>{fattura.id}</td>
                              <td style={{ padding: '12px' }}>{fattura.date}</td>
                              <td style={{ padding: '12px' }}>{fattura.description}</td>
                              <td style={{ padding: '12px' }}><strong>€ {fattura.amount}</strong></td>
                              <td style={{ padding: '12px' }}>
                                <span className="badge" style={{ 
                                  background: fattura.status === 'Saldato' ? 'var(--soft)' : 'var(--danger-soft)', 
                                  color: fattura.status === 'Saldato' ? 'var(--ink)' : 'var(--danger)' 
                                }}>
                                  {fattura.status}
                                </span>
                              </td>
                            </tr>
                          ))}
                        </tbody>
                      </table>
                    </div>
                  </section>
                </>
              ) : (
                /* Messaggio se non ci sono fatture (Array vuoto) */
                <p style={{ color: 'var(--muted)', fontStyle: 'italic', marginTop: '24px' }}>
                  Nessuna fattura o ricevuta presente nel tuo storico contabile.
                </p>
              )}
            </div>
          )}

          {/* 4. ESEMPIO DI PAGINA FAQ CLIENTE */}
          {currentScreen === 'faq-client' && (
            <div>
              <div className="page-title">
                <p className="eyebrow">Supporto cliente</p>
                <h1>FAQ</h1>
              </div>
              <section className="panel faq-list">
                <details>
                  <summary>Come prenoto una visita?</summary>
                  <p>Apri "Prenota appuntamento" dalla barra laterale, scegli l'animale e conferma.</p>
                </details>
              </section>
            </div>
          )}

          {/* ========================================================= */}
          {/* 4. AREA VETERINARIO (DOCTOR)                              */}
          {/* ========================================================= */}

          {/* 4.1 DASHBOARD VETERINARIO */}
          {currentScreen === 'dashboard-doctor' && (
            <div>
              <div className="page-title">
                <p className="eyebrow">Area Professionale</p>
                <h1>Dashboard Clinica</h1>
              </div>

              <div className="panel" style={{ marginTop: '24px' }}>
                <h2>Agenda Visite di Oggi</h2>
                <p style={{ color: 'var(--muted)', marginBottom: '16px' }}>Controllo appuntamenti attivi</p>
                
                {/* Controllo dinamico se ci sono visite oggi */}
                {config?.todayAppointments && config.todayAppointments.length > 0 ? (
                  <div className="table-responsive">
                    <table className="data-table" style={{ width: '100%', borderCollapse: 'collapse' }}>
                      <thead>
                        <tr style={{ borderBottom: '2px solid var(--line)', textAlign: 'left' }}>
                          <th style={{ padding: '12px' }}>Orario</th>
                          <th style={{ padding: '12px' }}>Paziente</th>
                          <th style={{ padding: '12px' }}>Proprietario</th>
                          <th style={{ padding: '12px' }}>Motivo</th>
                          <th style={{ padding: '12px' }}>Azioni</th>
                        </tr>
                      </thead>
                      <tbody>
                        {config.todayAppointments.map((visita, index) => (
                          <tr key={index} style={{ borderBottom: '1px solid var(--line)' }}>
                            <td style={{ padding: '12px' }}><strong>{visita.orario}</strong></td>
                            <td style={{ padding: '12px' }}>{visita.pazienteNome} ({visita.pazienteRazza})</td>
                            <td style={{ padding: '12px' }}>{visita.proprietario}</td>
                            <td style={{ padding: '12px' }}>{visita.motivo}</td>
                            <td style={{ padding: '12px' }}>
                              <button className="btn btn-primary btn-sm" onClick={() => alert(`Apertura cartella clinica di ${visita.pazienteNome}`)}>Visita</button>
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                ) : (
                  <p style={{ color: 'var(--muted)', fontStyle: 'italic', padding: '12px 0' }}>
                    Nessun appuntamento programmato per la giornata di oggi.
                  </p>
                )}
              </div>
            </div>
          )}

          {/* 4.2 PROFILO VETERINARIO */}
          {currentScreen === 'profile-doctor' && (
            <div>
              <div className="page-title">
                <p className="eyebrow">Impostazioni Mediche</p>
                <h1>Profilo Medico</h1>
              </div>
              <div className="panel" style={{ maxWidth: '600px', marginTop: '24px' }}>
                <div style={{ display: 'flex', gap: '20px', alignItems: 'center', marginBottom: '24px' }}>
                  <div style={{ width: '80px', height: '80px', borderRadius: '50%', background: 'var(--primary-soft)', color: 'var(--primary-dark)', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '24px', fontWeight: 'bold' }}>
                    CZ
                  </div>
                  <div>
                    <h2>{config?.userName || "Dottore"}</h2>
                    <p style={{ color: 'var(--muted)' }}>Chirurgo ed Esperto Piccoli Animali</p>
                  </div>
                </div>
                <form onSubmit={(e) => e.preventDefault()} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
                  <div>
                    <label style={{ display: 'block', marginBottom: '6px', fontWeight: 'bold' }}>Numero Iscrizione Ordine</label>
                    <input type="text" className="form-control" defaultValue="N° 12345 - Bergamo" disabled style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid var(--line)', background: 'var(--soft)' }} />
                  </div>
                  <div>
                    <label style={{ display: 'block', marginBottom: '6px', fontWeight: 'bold' }}>Specializzazione Primaria</label>
                    <input type="text" className="form-control" defaultValue="Medicina e Chirurgia degli animali d'affezione" style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid var(--line)' }} />
                  </div>
                  <button type="submit" className="btn btn-primary" style={{ alignSelf: 'flex-start' }}>Aggiorna Profilo</button>
                </form>
              </div>
            </div>
          )}

          {/* 4.3 RICERCA E GESTIONE ANIMALI (MEDICINA) */}
          {currentScreen === 'view-animal-doctor' && (
            <div>
              <div className="page-title">
                <p className="eyebrow">Archivio Clinico</p>
                <h1>Registro Animali</h1>
              </div>
              
              <div className="panel" style={{ marginTop: '24px' }}>
                <div style={{ display: 'flex', gap: '12px', marginBottom: '20px' }}>
                  <input type="text" placeholder="Cerca animale per nome o proprietario..." style={{ flexGrow: 1, padding: '10px', borderRadius: '6px', border: '1px solid var(--line)' }} />
                  <button className="btn btn-primary" onClick={() => alert('Simulazione ricerca effettiva')}>Cerca</button>
                </div>

                {/* Controllo dinamico sul registro generale dei pazienti */}
                {config?.patientsRegistry && config.patientsRegistry.length > 0 ? (
                  <div className="table-responsive">
                    <table className="data-table" style={{ width: '100%', borderCollapse: 'collapse' }}>
                      <thead>
                        <tr style={{ borderBottom: '2px solid var(--line)', textAlign: 'left' }}>
                          <th style={{ padding: '12px' }}>Paziente</th>
                          <th style={{ padding: '12px' }}>Specie / Razza</th>
                          <th style={{ padding: '12px' }}>Ultima Visita</th>
                          <th style={{ padding: '12px' }}>Proprietario</th>
                          <th style={{ padding: '12px' }}>Azioni</th>
                        </tr>
                      </thead>
                      <tbody>
                        {config.patientsRegistry.map((paziente, index) => (
                          <tr key={index} style={{ borderBottom: '1px solid var(--line)' }}>
                            <td style={{ padding: '12px' }}><strong>{paziente.nome}</strong></td>
                            <td style={{ padding: '12px' }}>{paziente.specie} / {paziente.razza}</td>
                            <td style={{ padding: '12px' }}>{paziente.ultimaVisita}</td>
                            <td style={{ padding: '12px' }}>{paziente.proprietario}</td>
                            <td style={{ padding: '12px' }}>
                              <button className="btn btn-outline btn-sm" onClick={() => alert(`Mostra Storico Referti di ${paziente.nome}`)}>Vedi Cartella</button>
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                ) : (
                  <p style={{ color: 'var(--muted)', fontStyle: 'italic' }}>
                    Nessun animale presente nel registro clinico globale.
                  </p>
                )}
              </div>
            </div>
          )}

          {/* 4.4 FAQ VETERINARIO */}
          {currentScreen === 'faq-doctor' && (
            <div>
              <div className="page-title">
                <p className="eyebrow">Supporto Medico</p>
                <h1>FAQ Veterinario</h1>
              </div>
              <section className="panel faq-list" style={{ marginTop: '24px' }}>
                <details style={{ padding: '12px 0', borderBottom: '1px solid var(--line)' }}>
                  <summary style={{ fontWeight: 'bold', cursor: 'pointer' }}>Come trovo la cartella clinica di un animale?</summary>
                  <p style={{ marginTop: '8px', color: 'var(--muted)' }}>Vai su "Gestisci animali" dalla barra laterale, inserisci il nome del paziente e clicca su "Vedi Cartella".</p>
                </details>
                <details style={{ padding: '12px 0', borderBottom: '1px solid var(--line)' }}>
                  <summary style={{ fontWeight: 'bold', cursor: 'pointer' }}>Come inserisco un nuovo referto?</summary>
                  <p style={{ marginTop: '8px', color: 'var(--muted)' }}>Dentro la cartella clinica dell'animale, clicca su "Aggiungi Referto", compila il testo della diagnosi e premi salva.</p>
                </details>
              </section>
            </div>
          )}
          {/* ========================================================= */}
          {/* 5. AREA RECEPTION (RECEPTIONIST)                          */}
          {/* ========================================================= */}

          {/* 5.1 DASHBOARD RECEPTION */}
          {currentScreen === 'dashboard-receptionist' && (
            <div>
              <div className="page-title">
                <p className="eyebrow">Gestione Studio</p>
                <h1>Dashboard Reception</h1>
              </div>

              {/* Contatori Dinamici ricavati dall'oggetto config.stats */}
              <div className="home-highlights" style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '16px', margin: '24px 0' }}>
                <div className="panel" style={{ padding: '20px', textAlign: 'center' }}>
                  <strong style={{ fontSize: '24px', color: 'var(--primary)' }}>{config?.stats?.todayAppointmentsCount || 0}</strong>
                  <p style={{ margin: '4px 0 0', color: 'var(--muted)' }}>Appuntamenti Oggi</p>
                </div>
                <div className="panel" style={{ padding: '20px', textAlign: 'center' }}>
                  <strong style={{ fontSize: '24px', color: 'var(--secondary)' }}>{config?.stats?.newClientsCount || 0}</strong>
                  <p style={{ margin: '4px 0 0', color: 'var(--muted)' }}>Nuovi Clienti</p>
                </div>
                <div className="panel" style={{ padding: '20px', textAlign: 'center' }}>
                  <strong style={{ fontSize: '24px', color: 'var(--danger)' }}>€ {config?.stats?.pendingAmount || 0}</strong>
                  <p style={{ margin: '4px 0 0', color: 'var(--muted)' }}>In Sospeso</p>
                </div>
              </div>
            </div>
          )}

          {/* 5.2 PROFILO RECEPTION */}
          {currentScreen === 'profile-receptionist' && (
            <div>
              <div className="page-title">
                <p className="eyebrow">Personale</p>
                <h1>Profilo Segreteria</h1>
              </div>
              <div className="panel" style={{ maxWidth: '600px', marginTop: '24px' }}>
                <h2>{config?.userName || "Receptionist"}</h2>
                <p style={{ color: 'var(--muted)', marginBottom: '16px' }}>Responsabile Accoglienza e Amministrazione</p>
                <form onSubmit={(e) => e.preventDefault()} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
                  <div>
                    <label style={{ display: 'block', marginBottom: '6px', fontWeight: 'bold' }}>Sede Operativa</label>
                    <input type="text" className="form-control" defaultValue="Bergamo Alta - Reception Centrale" disabled style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid var(--line)', background: 'var(--soft)' }} />
                  </div>
                </form>
              </div>
            </div>
          )}

          {/* 5.3 GESTIONE APPUNTAMENTI */}
          {currentScreen === 'view-appointment-receptionist' && (
            <div>
              <div className="page-title">
                <p className="eyebrow">Planning</p>
                <h1>Gestione Appuntamenti</h1>
              </div>
              
              <div className="panel" style={{ marginTop: '24px' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '20px' }}>
                  <h2>Calendario Generale</h2>
                  <button className="btn btn-primary btn-sm" onClick={() => alert('Apertura popup per inserire un appuntamento sul posto')}>+ Nuovo Appuntamento</button>
                </div>
                
                {config?.allAppointments && config.allAppointments.length > 0 ? (
                  <div className="table-responsive">
                    <table className="data-table" style={{ width: '100%', borderCollapse: 'collapse' }}>
                      <thead>
                        <tr style={{ borderBottom: '2px solid var(--line)', textAlign: 'left' }}>
                          <th style={{ padding: '12px' }}>Data/Ora</th>
                          <th style={{ padding: '12px' }}>Paziente (Proprietario)</th>
                          <th style={{ padding: '12px' }}>Medico</th>
                          <th style={{ padding: '12px' }}>Stato</th>
                          <th style={{ padding: '12px' }}>Azioni</th>
                        </tr>
                      </thead>
                      <tbody>
                        {config.allAppointments.map((app, index) => (
                          <tr key={index} style={{ borderBottom: '1px solid var(--line)' }}>
                            <td style={{ padding: '12px' }}>{app.dataOra}</td>
                            <td style={{ padding: '12px' }}>{app.pazienteNome} ({app.proprietarioNome})</td>
                            <td style={{ padding: '12px' }}>{app.medicoNome}</td>
                            <td style={{ padding: '12px' }}><span className="badge">{app.stato}</span></td>
                            <td style={{ padding: '12px', display: 'flex', gap: '8px' }}>
                              <button className="btn btn-outline btn-sm" onClick={() => alert('Modifica orario/medico')}>Modifica</button>
                              <button className="btn btn-sm" style={{ background: 'var(--danger)', color: 'white' }} onClick={() => alert('Appuntamento eliminato')}>Cancella</button>
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                ) : (
                  <p style={{ color: 'var(--muted)', fontStyle: 'italic' }}>Nessun appuntamento registrato a sistema.</p>
                )}
              </div>
            </div>
          )}

          {/* 5.4 GESTIONE ANAGRAFICA CLIENTI */}
          {currentScreen === 'view-clients-receptionist' && (
            <div>
              <div className="page-title">
                <p className="eyebrow">Anagrafiche</p>
                <h1>Gestione Clienti</h1>
              </div>
              
              <div className="panel" style={{ marginTop: '24px' }}>
                {config?.allClients && config.allClients.length > 0 ? (
                  <div className="table-responsive">
                    <table className="data-table" style={{ width: '100%', borderCollapse: 'collapse' }}>
                      <thead>
                        <tr style={{ borderBottom: '2px solid var(--line)', textAlign: 'left' }}>
                          <th style={{ padding: '12px' }}>Cliente</th>
                          <th style={{ padding: '12px' }}>Contatti</th>
                          <th style={{ padding: '12px' }}>Animali Associati</th>
                          <th style={{ padding: '12px' }}>Azioni</th>
                        </tr>
                      </thead>
                      <tbody>
                        {config.allClients.map((cliente, index) => (
                          <tr key={index} style={{ borderBottom: '1px solid var(--line)' }}>
                            <td style={{ padding: '12px' }}><strong>{cliente.nominativo}</strong></td>
                            <td style={{ padding: '12px' }}>{cliente.email}</td>
                            <td style={{ padding: '12px' }}>{cliente.animaliString}</td>
                            <td style={{ padding: '12px' }}>
                              <button className="btn btn-outline btn-sm" onClick={() => alert('Apertura scheda anagrafica')}>Modifica Dati</button>
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                ) : (
                  <p style={{ color: 'var(--muted)', fontStyle: 'italic' }}>Nessun utente cliente registrato nell'anagrafica.</p>
                )}
              </div>
            </div>
          )}

          {/* 5.5 CONTABILITÀ E PAGAMENTI RECEPTION */}
          {currentScreen === 'view-payment-receptionist' && (
            <div>
              <div className="page-title">
                <p className="eyebrow">Cassa</p>
                <h1>Gestione Pagamenti</h1>
              </div>

              <div className="panel" style={{ marginTop: '24px' }}>
                <h2>Pendenze Attive</h2>
                {config?.pendingPayments && config.pendingPayments.length > 0 ? (
                  <div className="table-responsive" style={{ marginTop: '16px' }}>
                    <table className="data-table" style={{ width: '100%', borderCollapse: 'collapse' }}>
                      <thead>
                        <tr style={{ borderBottom: '2px solid var(--line)', textAlign: 'left' }}>
                          <th style={{ padding: '12px' }}>Intestatario</th>
                          <th style={{ padding: '12px' }}>Prestazione</th>
                          <th style={{ padding: '12px' }}>Importo</th>
                          <th style={{ padding: '12px' }}>Azioni</th>
                        </tr>
                      </thead>
                      <tbody>
                        {config.pendingPayments.map((pagamento, index) => (
                          <tr key={index} style={{ borderBottom: '1px solid var(--line)' }}>
                            <td style={{ padding: '12px' }}>{pagamento.clienteNome}</td>
                            <td style={{ padding: '12px' }}>{pagamento.descrizioneVisita}</td>
                            <td style={{ padding: '12px' }}><strong>€ {pagamento.cifra}</strong></td>
                            <td style={{ padding: '12px', display: 'flex', gap: '8px' }}>
                              <button className="btn btn-primary btn-sm" onClick={() => alert('Pagamento registrato')}>Registra Saldo</button>
                              <button className="btn btn-outline btn-sm" onClick={() => alert('Sollecito inviato')}>Invia Sollecito</button>
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                ) : (
                  <p style={{ color: 'var(--muted)', fontStyle: 'italic', marginTop: '16px' }}>Ottimo lavoro! Non ci sono pagamenti in sospeso da riscuotere.</p>
                )}
              </div>
            </div>
          )}

          {/* 5.6 FAQ RECEPTION */}
          {currentScreen === 'faq-receptionist' && (
            <div>
              <div className="page-title">
                <p className="eyebrow">Supporto Segreteria</p>
                <h1>FAQ Reception</h1>
              </div>
              <section className="panel faq-list" style={{ marginTop: '24px' }}>
                <details style={{ padding: '12px 0', borderBottom: '1px solid var(--line)' }}>
                  <summary style={{ fontWeight: 'bold', cursor: 'pointer' }}>Come si registra un pagamento?</summary>
                  <p style={{ marginTop: '8px', color: 'var(--muted)' }}>Vai su "Gestisci pagamenti", individua la riga del cliente desiderata e seleziona "Registra Saldo" per incassare la fattura.</p>
                </details>
                <details style={{ padding: '12px 0', borderBottom: '1px solid var(--line)' }}>
                  <summary style={{ fontWeight: 'bold', cursor: 'pointer' }}>Cosa fare per modificare o cancellare un appuntamento?</summary>
                  <p style={{ marginTop: '8px', color: 'var(--muted)' }}>Nel menu "Gestisci appuntamenti" puoi usare i bottoni "Modifica" o "Cancella" a fianco di ogni record del planning.</p>
                </details>
              </section>
            </div>
          )}
          {/* ========================================================= */}
          {/* 6. AREA SUPER ADMIN (STUDIO / AMMINISTRAZIONE)            */}
          {/* ========================================================= */}

          {/* 6.1 DASHBOARD SUPER ADMIN */}
          {currentScreen === 'dashboard-super-admin' && (
            <div>
              <div className="page-title">
                <p className="eyebrow">Amministrazione di Sistema</p>
                <h1>Stato del Portale</h1>
              </div>

              <div className="panel" style={{ marginTop: '24px' }}>
                <h2>Monitoraggio Moduli Applicativi</h2>
                <p style={{ color: 'var(--muted)', marginBottom: '20px' }}>Verifica dello stato di prontezza delle aree del mockup</p>
                
                <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', padding: '12px', background: 'var(--soft)', borderRadius: '6px' }}>
                    <span><strong>Area Pubblica (Homepage & Router)</strong></span>
                    <span style={{ color: 'green', fontWeight: 'bold' }}>● Completato (React)</span>
                  </div>
                  <div style={{ display: 'flex', justifyContent: 'space-between', padding: '12px', background: 'var(--soft)', borderRadius: '6px' }}>
                    <span><strong>Area Cliente (Thor & Luna)</strong></span>
                    <span style={{ color: 'green', fontWeight: 'bold' }}>● Completato (React)</span>
                  </div>
                  <div style={{ display: 'flex', justifyContent: 'space-between', padding: '12px', background: 'var(--soft)', borderRadius: '6px' }}>
                    <span><strong>Area Veterinario (Dott. Zampetti)</strong></span>
                    <span style={{ color: 'green', fontWeight: 'bold' }}>● Completato (React)</span>
                  </div>
                  <div style={{ display: 'flex', justifyContent: 'space-between', padding: '12px', background: 'var(--soft)', borderRadius: '6px' }}>
                    <span><strong>Area Reception (Giulia Ferri)</strong></span>
                    <span style={{ color: 'green', fontWeight: 'bold' }}>● Completato (React)</span>
                  </div>
                </div>
              </div>
            </div>
          )}

          {/* 6.2 PROFILO SUPER ADMIN */}
          {currentScreen === 'profile-super-admin' && (
            <div>
              <div className="page-title">
                <p className="eyebrow">Configurazione</p>
                <h1>Impostazioni di Sistema</h1>
              </div>
              <div className="panel" style={{ maxWidth: '600px', marginTop: '24px' }}>
                <h2>Parametri Globali</h2>
                <p style={{ color: 'var(--muted)', marginBottom: '16px' }}>Versione Mockup: v2.0-React (Vite)</p>
                <div style={{ padding: '12px', border: '1px dashed var(--line)', borderRadius: '6px', background: 'var(--paper)' }}>
                  <p><strong>Database:</strong> Simulato in memoria locale</p>
                  <p><strong>Routing:</strong> Gestito tramite Stato Globale (Context)</p>
                </div>
              </div>
            </div>
          )}

          {/* 6.3 FAQ SUPER ADMIN */}
          {currentScreen === 'faq-super-admin' && (
            <div>
              <div className="page-title">
                <p className="eyebrow">Manuale Amministratore</p>
                <h1>FAQ Presentazione</h1>
              </div>
              <section className="panel faq-list" style={{ marginTop: '24px' }}>
                <details style={{ padding: '12px 0', borderBottom: '1px solid var(--line)' }}>
                  <summary style={{ fontWeight: 'bold', cursor: 'pointer' }}>Come si cambia il ruolo dimostrativo?</summary>
                  <p style={{ marginTop: '8px', color: 'var(--muted)' }}>Usa la barra di navigazione orizzontale posizionata nell'Header in alto. Cliccando su ciascun ruolo, la Sidebar e i contenuti si adatteranno istantaneamente.</p>
                </details>
                <details style={{ padding: '12px 0', borderBottom: '1px solid var(--line)' }}>
                  <summary style={{ fontWeight: 'bold', cursor: 'pointer' }}>Come si cambia la palette di colori dell'interfaccia?</summary>
                  <p style={{ marginTop: '8px', color: 'var(--muted)' }}>Nell'angolo in alto a destra dell'Header, seleziona uno dei radio button colorati (Aurora, Lago, Energia, Iris, Cielo, Rosa) per applicare istantaneamente le variabili CSS corrispondenti su tutta l'applicazione.</p>
                </details>
              </section>
            </div>
          )}

           {/* NOTA: Qui sotto potrai aggiungere pian piano i blocchi per gli altri ruoli (doctor, receptionist, admin) */}
        </div>
      </main>

      {/* Il pulsante di aiuto fluttuante in basso a destra */}
      <HelpAction />
    </>
  );
}