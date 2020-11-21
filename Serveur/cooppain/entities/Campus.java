package cooppain.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "Campus")
public class Campus implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private int id;

    @Column(name = "Nom", nullable = false)
    private String nom;

    @Column(name = "Effectif")
    private int effectif;

    @Column(name = "VilleID")
    private int villeId;

    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getEffectif() {
        return effectif;
    }

    public void setEffectif(int effectif) {
        this.effectif = effectif;
    }

    public int getVilleId() {
        return villeId;
    }

    public void setVilleId(int villeId) {
        this.villeId = villeId;
    }

    public Campus() {}

    @Override
    public String toString() {
        return "\nCampus{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", effectif=" + effectif +
                ", villeId=" + villeId +
                '}';
    }
}
