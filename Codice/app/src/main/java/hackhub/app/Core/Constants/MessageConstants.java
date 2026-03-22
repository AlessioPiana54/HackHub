package hackhub.app.Core.Constants;

public final class MessageConstants {
    private MessageConstants() { }

    // Hackathon Service
    public static final String ERROR_UNAUTHORIZED_ORGANIZER = "L'utente specificato come organizzatore non ha i permessi necessari.";
    public static final String ERROR_NOT_JUDGE = "L'utente specificato come giudice non ha il ruolo di GIUDICE.";
    public static final String ERROR_NOT_MENTOR = "L'utente non ha il ruolo di MENTORE.";
    public static final String ERROR_HACKATHON_NOT_FOUND = "Hackathon non trovato";
    public static final String ERROR_HACKATHON_NOT_IN_EVALUATION = "L'Hackathon non è in fase di valutazione";
    public static final String ERROR_ONLY_JUDGE_EVALUATE = "Solo il giudice dell'Hackathon può terminare la valutazione";
    public static final String ERROR_UNEVALUATED_SUBMISSIONS = "Non tutte le sottomissioni sono state valutate. Impossibile terminare la fase di valutazione.";
    public static final String ERROR_RANKING_ONLY_IN_AWARDING = "La classifica è disponibile solo in fase di PREMIAZIONE o a Hackathon CONCLUSO.";
    public static final String ERROR_ONLY_ORGANIZER_OR_JUDGE_RANKING = "Solo l'organizzatore o il giudice dell'Hackathon possono visualizzare la classifica durante la fase di premiazione.";
    public static final String ERROR_SUBMISSION_WITHOUT_EVALUATION = "Trovata sottomissione senza valutazione in fase di premiazione";
    public static final String ERROR_ONLY_ORGANIZER_AWARD = "Solo l'organizzatore può proclamare il vincitore.";
    public static final String ERROR_NOT_IN_AWARDING_PHASE = "L'Hackathon non è in fase di premiazione.";
    public static final String ERROR_TEAM_NOT_REGISTERED_AWARD = "Il team selezionato non è iscritto o non ha effettuato sottomissioni per questo Hackathon.";
    public static final String ERROR_PAYMENT_FAILED = "Errore durante il pagamento: ";
    public static final String ERROR_REGISTRATION_CLOSED = "Le iscrizioni per questo hackathon sono chiuse.";
    public static final String ERROR_HACKATHON_NOT_IN_REGISTRATION = "L'hackathon non è aperto alle iscrizioni.";
    public static final String ERROR_ONLY_LEADER_REGISTER_TEAM = "Solo il leader del team può iscrivere il team all'hackathon.";
    public static final String ERROR_TEAM_ALREADY_REGISTERED = "Il team è già iscritto a questo hackathon.";
    public static final String ERROR_TEAM_NOT_FOUND = "Team non trovato";
    
    // Team Service
    public static final String ERROR_TEAM_NAME_EXISTS = "Esiste già un Team con questo nome.";
    public static final String ERROR_ONLY_LEADER_EDIT = "Solo il Leader del Team può modificare i dettagli del team.";
    public static final String ERROR_USER_NOT_IN_TEAM = "L'utente non fa parte di questo Team.";
    public static final String ERROR_LEADER_CANNOT_ABANDON_WITH_MEMBERS = "Il Leader può abbandonare il team solo se è l'unico membro. Deve prima cedere il ruolo a un altro membro.";
    public static final String ERROR_ONLY_CURRENT_LEADER_TRANSFER = "Solo l'attuale Leader può trasferire la leadership.";
    public static final String ERROR_USER_NOT_FOUND = "Utente non trovato";
    
    // Auth & General
    public static final String ERROR_UNAUTHORIZED = "L'utente specificato non ha i permessi necessari.";
}
