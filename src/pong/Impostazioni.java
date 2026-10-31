// Pong — Programmazione Orientata agli Oggetti e Laboratori, Udine, a.a. 2026-27
// Consegna 2: le impostazioni. Scadenza: domenica 1 novembre 2026.
package pong;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Le impostazioni di una partita di Pong: quanti punti servono per vincere, con quale scarto
 * minimo, a che velocità va la palla. È un record (lezione 4): una volta create, non cambiano.
 *
 * IL PEZZO MANCANTE: il costruttore che rifiuta i valori sbagliati e il metodo leggi.
 * Ogni metodo ha il suo contratto, nel commento sopra. I test ufficiali (cartella test)
 * controllano i contratti.
 */
public record Impostazioni(int puntiPerVincere, int scartoMinimo, int velocitaPalla) {

    /* con queste costanti ausiliarie posso modificare i parametri velocemente per modifiche future */
    private static final int MIN_PUNTI = 1;
    private static final int MAX_PUNTI = 99;
    private static final int MIN_SCARTO = 1;
    private static final int MAX_SCARTO = 10;
    private static final int MIN_VELOCITA = 1;
    private static final int MAX_VELOCITA = 20;

    /** Le impostazioni standard: 11 punti per vincere, scarto minimo 2, velocità della palla 5. */
    public static final Impostazioni STANDARD = new Impostazioni(11, 2, 5);

    /**
     * Il costruttore (lezione 7: un record che nasce giusto).
     * Promette: se i tre valori sono nei loro intervalli, le impostazioni con quei valori.
     * Lancia IllegalArgumentException se puntiPerVincere non è fra 1 e 99, se scartoMinimo
     * non è fra 1 e 10, o se velocitaPalla non è fra 1 e 20 (estremi compresi). Il messaggio
     * dice quale valore è sbagliato e perché.
     */
    public Impostazioni {
        controlla("", "puntiPerVincere", puntiPerVincere, MIN_PUNTI, MAX_PUNTI);
        controlla("", "scartoMinimo", scartoMinimo, MIN_SCARTO, MAX_SCARTO);
        controlla("", "velocitaPalla", velocitaPalla, MIN_VELOCITA, MAX_VELOCITA);
    }

    /**
     * Legge le impostazioni dalle righe di un file, una impostazione per riga.
     * Pretende: righe non è null e non contiene null.
     * Promette:
     *  - le righe vuote (o di soli spazi) e quelle che, tolti gli spazi iniziali, cominciano
     *    con # sono ignorate;
     *  - ogni altra riga è nella forma  chiave = valore , con spazi qualsiasi attorno alla chiave,
     *    al segno = e al valore; le chiavi sono puntiPerVincere, scartoMinimo, velocitaPalla;
     *  - un'impostazione che nelle righe non c'è prende il valore standard;
     *  - il risultato sono le impostazioni lette.
     * Lancia IllegalArgumentException, con un messaggio che comincia con «riga N: » (N è il numero
     * della riga, contando da 1 tutte le righe, anche quelle vuote e i commenti), se in una riga:
     *  1. manca il segno = ;
     *  2. la chiave non è una delle tre;
     *  3. il valore non è un numero intero;
     *  4. la stessa chiave c'era già in una riga precedente;
     *  5. il valore è fuori dal suo intervallo (gli intervalli del costruttore).
     * Se ci sono errori in più righe, il messaggio è quello della riga più in alto.
     */
    public static Impostazioni leggi(List<String> righe) {
        int punti = STANDARD.puntiPerVincere();
        int scarto = STANDARD.scartoMinimo();
        int velocita = STANDARD.velocitaPalla();
        boolean vistoPunti = false;
        boolean vistoScarto = false;
        boolean vistoVelocita = false;

        for (int i = 0; i < righe.size(); i++) {
            int numero = i + 1;
            String riga = righe.get(i).trim();   // una riga sola, già senza spazi
            if (!riga.isEmpty() && !riga.startsWith("#")){
                int posizioneUguale = riga.indexOf("=");
                if (posizioneUguale == -1) {
                    throw new IllegalArgumentException(
                        "riga " + numero + ": manca l'uguale."
                    );
                }
                String chiave = riga.substring(0, posizioneUguale).trim();
                int valore;
                if (!chiave.equals("puntiPerVincere") && !chiave.equals("scartoMinimo") && !chiave.equals("velocitaPalla")) {
                    throw new IllegalArgumentException(
                        "riga " + numero + ": chiave sconosciuta."
                    );
                }
                try {
                    valore = Integer.parseInt(riga.substring(posizioneUguale + 1).trim());
                }
                catch (NumberFormatException e) {
                    throw new IllegalArgumentException(
                        "riga " + numero + ": il valore non è un numero intero."
                    );
                }
                if (chiave.equals("puntiPerVincere")) {
                    if (vistoPunti) {
                        throw new IllegalArgumentException(
                            "riga " + numero + ": chiave ripetuta."
                        );
                    }
                    punti = valore;
                    controlla("riga " + numero + ": ", "puntiPerVincere", punti, MIN_PUNTI, MAX_PUNTI);
                    vistoPunti = true;
                }
                else if (chiave.equals("scartoMinimo")) {
                    if (vistoScarto) {
                        throw new IllegalArgumentException(
                            "riga " + numero + ": chiave ripetuta."
                        );
                    }
                    scarto = valore;
                    controlla("riga " + numero + ": ", "scartoMinimo", scarto, MIN_SCARTO, MAX_SCARTO);
                    vistoScarto = true;
                }
                else if (chiave.equals("velocitaPalla")) {
                    if (vistoVelocita) {
                        throw new IllegalArgumentException(
                            "riga " + numero + ": chiave ripetuta."
                        );
                    }
                    velocita = valore;
                    controlla("riga " + numero + ": ", "velocitaPalla", velocita, MIN_VELOCITA, MAX_VELOCITA);
                    vistoVelocita = true;
                }
            }
        }
        return new Impostazioni(punti, scarto, velocita);
    }

    /**
     * Legge le impostazioni da un file di testo.
     * Promette: come leggi, con le righe del file.
     * Lancia IOException se il file non si può leggere (per esempio non esiste): non la cattura,
     * la lascia a chi chiama. Lancia IllegalArgumentException come leggi.
     */
    public static Impostazioni daFile(Path file) throws IOException {
        return leggi(Files.readAllLines(file));
    }

    /**
     * Controlla
     * Pretende: prefisso e nome non null, minimo <= massimo.
     * Promette: lancia IllegalArgumentException se valore non è compreso tra minimo e massimo
     * (inclusi), altrimenti non fa niente. Il messaggio dice quale valore è sbagliato e perché.
     */
    private static void controlla (String prefisso, String nome, int valore, int minimo, int massimo){
        if (valore < minimo || valore > massimo) {
            throw new IllegalArgumentException(
                prefisso + nome + " non compreso tra " + minimo + " e " + massimo + ", ma ho ricevuto: " + valore + "."
            );
        }
    }
}
