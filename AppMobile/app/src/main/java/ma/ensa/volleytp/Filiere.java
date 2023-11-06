package ma.ensa.volleytp;

public class Filiere {
    private int id;
    private String code;
    private String libelle;

    public Filiere(int id, String code, String libelle) {
        this.id = id;
        this.code = code;
        this.libelle = libelle;
    }
    public Filiere(int id,  String libelle) {
        this.id = id;
        this.libelle = libelle;
    }

    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getLibelle() {
        return libelle;
    }

    @Override
    public String toString() {
        return  "\nLibelle: " + libelle;
    }
}
