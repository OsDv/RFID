import java.time.LocalTime;

public class RFIDevenement {
    enum evenementStatus {
        ENTRER_VALIDER("ENTRER VALIDER"),
        SORTIE_VALIDER("SORTIE VALIDER"),
        ID_INCONUE("ID INCONUE"),
        DEJA_ENTRER("DEJA ENTRER"),
        DEJA_SORTIE("DEJA SORTIE"),
        SORTIE_NON_VALIDER("SORTIE NON_VALIDER");
        private String status;
        evenementStatus(String st){this.status = st;}
        public String getStatus(){return this.status;}
    }
    private long id;
    private evenementStatus status;
    private LocalTime time;

    /*
    *   GETTERS AND SETTERS
    * */
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public evenementStatus getStatus() {
        return status;
    }

    public void setStatus(evenementStatus status) {
        this.status = status;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }
}
