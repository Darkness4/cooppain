package cooppain.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "Subvention")
public class Subvention implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private int id;

    @Column(name = "Libelle", nullable = false)
    private String libelle;

    @Column(name = "ExpiresAt", nullable = false)
    private Timestamp expiresAt;

    @Column(name = "EstDesactive")
    private boolean estDesactive;

    @Column(name = "Description")
    private String description;

    @Column(name = "CampusID", nullable = false)
    private int campusID;

    public Subvention() {}

    public Subvention(String libelle, Timestamp expiresAt, boolean estDesactive, String description) {
        this.libelle = libelle;
        this.expiresAt = expiresAt;
        this.estDesactive = estDesactive;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public Timestamp getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Timestamp expiresAt) {
        this.expiresAt = expiresAt;
    }

    public boolean estDesactive() {
        return estDesactive;
    }

    public void setEstDesactive(boolean estDesactive) {
        this.estDesactive = estDesactive;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCampusID() {
        return campusID;
    }

    public void setCampusID(int campusID) {
        this.campusID = campusID;
    }

    @Override
    public String toString() {
        return "\nSubvention{" +
                "id=" + id +
                ", libelle='" + libelle + '\'' +
                ", expiresAt=" + expiresAt +
                ", estDesactive=" + estDesactive +
                ", description='" + description + '\'' +
                ", campusID=" + campusID +
                '}';
    }
}
