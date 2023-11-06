package ma.ensa.volleytp;

public class Student {

    private int id;
    private String name;
    private String email;
    private int phone;
    private Filiere filiere;
    private int filiereId;
    private String filiereLibelle; // Ajoutez le libellé de la filière

    public Student(int id, String name, String email, int phone, Filiere filiere) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.filiere = filiere;
    }

    public Student(int id, String name, String email, int phone,  int filiereId) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.filiereId = filiereId;

    }
    public Student(int id, String name, String email, int phone) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;


    }




    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public int getPhone() {
        return phone;
    }

    public Filiere getFiliere() {
        return filiere;
    }

    @Override
    public String toString() {
        return "Student:" +

                "\nname='" + name +
                "\nemail='" + email +
                "\nphone=" + phone

                ;
    }

}
