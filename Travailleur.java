import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;

public class Travailleur {
    private long id;
    private String nom;
    private String prenom;
    private int numero;
    private boolean travailComencer;
    private boolean travailTerminer;
    private LocalTime enter;
    private LocalTime sortie;

    public LocalTime getEnter() {
        return enter;
    }

    public void setEnter(LocalTime enter) {
        this.enter = enter;
    }

    public LocalTime getSortie() {
        return sortie;
    }

    public void setSortie(LocalTime sortie) {
        this.sortie = sortie;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }




    @Override
    public String toString(){
        return Long.toString(this.id)+" "+ this.nom + " " + this.prenom + " " + this.numero;
    }
    /*
    * Constructors
    * */
    public Travailleur(long id,String nom, String prenom, int numero) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.numero = numero;
        this.travailComencer = false;
        this.travailTerminer = false;
    }
    /*
    * Getters and Setters
    * */
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public boolean isTravailComencer() {
        return travailComencer;
    }

    public void setTravailComencer(boolean travailComencer) {
        this.travailComencer = travailComencer;
    }

    public boolean isTravailTerminer() {
        return travailTerminer;
    }

    public void setTravailTerminer(boolean travailTerminer) {
        this.travailTerminer = travailTerminer;
    }

}
