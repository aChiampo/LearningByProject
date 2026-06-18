## Gestione Branch
 
Per la gestione ordinaria delle task l'ideale sarebbe creare un branch apposta per lo sviluppo e chiuderlo dopo il superamento dei test e l'approvazione al `merge`.
Se preferite potete creare un singolo branch personale e usare sempre quello, l'importante è che durante lo sviluppo ognuno usi il proprio branch.

Esempio apertura:
```bash
git branch task_name
```

Esempio chiusura:
```bash
git branch -d task_name

git push origin --delete task_name
```

## Merge on `master`

  

Per effettuare un `merge` su `master` sono necessarie:

- 2 diverse approvazioni dai membri del team,

- che il branch attuale sia aggiornato al `master` più recente

(almeno credo di averlo configurato così   A.C.)

Questo per evitare errori accidentali