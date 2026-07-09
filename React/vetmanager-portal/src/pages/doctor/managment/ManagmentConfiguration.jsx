import { useState } from 'react';
import PageTitle from '../../../components/common/PageTitle';
import ManagmentSpecies from './ManagmentSpecies';
import ManagmentRaces from './ManagmentRaces';

export default function ManagmentConfiguration() {
    // Questo stato memorizza la tabella che il dottore vuole configurare
    const [sezioneAttiva, setSezioneAttiva] = useState('');

    return (
        <div>
            {/* Intestazione della pagina coerente con il resto del layout del dottore */}
            <PageTitle eyebrow="Impostazioni Mediche" title="Configurazione Tabelle Clinica" />

            {/* Pannello di selezione principale */}
            <div className="panel section-spaced-sm">
                <h2>Pannello di Controllo Gestione</h2>
                <p className="muted-text">
                    Seleziona cosa desideri inserire, modificare o visualizzare nel database.
                </p>

                {/* Menu a tendina per scegliere la tabella */}
                <div className="form-group" style={{ maxWidth: '350px', marginTop: '20px' }}>
                    <select
                        className="form-control"
                        value={sezioneAttiva}
                        onChange={(e) => setSezioneAttiva(e.target.value)}
                    >
                        <option value="">-- Scegli cosa configurare --</option>
                        <option value="specie">Specie Animali</option>
                        <option value="razze">Razze</option>
                        <option value="tipiVisita">Tipi Visita</option>
                        <option value="categorieVisita">Categorie Visita</option>
                        <option value="vaccini">Tipi Vaccino</option>
                    </select>
                </div>
            </div>

            {/* Zona dinamica: qui sotto apparirà il componente selezionato */}
            <div className="section-spaced-sm">

                {/* Messaggio iniziale se non è selezionato nulla */}
                {sezioneAttiva === '' && (
                    <div className="panel text-center">
                        <p className="muted-text" style={{ padding: '20px 0' }}>
                            Nessuna tabella selezionata. Scegli una voce dal menu sopra per visualizzare i dati.
                        </p>
                    </div>
                )}

                {/* Render del Blocco Specie */}
                {sezioneAttiva === 'specie' && (
                    <div className="panel">
                        <h2>Gestione Specie Animali</h2>
                        <p className="muted-text">Visualizza le specie esistenti o aggiungine una nuova.</p>
                        <ManagmentSpecies />
                    </div>
                )}

                {/* Render del Blocco Razze */}
                {sezioneAttiva === 'razze' && (
                    <div className="panel">
                        <h2>Gestione Razze</h2>
                        <p className="muted-text">Configura le razze accoppiandole alla loro specie di riferimento.</p>
                        <ManagmentRaces />
                    </div>
                )}

                {/* Placeholder per le prossime sezioni di backend */}
                {(sezioneAttiva === 'tipiVisita' || sezioneAttiva === 'categorieVisita' || sezioneAttiva === 'vaccini') && (
                    <div className="panel text-center">
                        <p className="muted-text" style={{ padding: '20px 0' }}>
                            Sezione in sviluppo. Configureremo i relativi endpoint di backend nei prossimi passaggi.
                        </p>
                    </div>
                )}

            </div>
        </div>
    );
}
