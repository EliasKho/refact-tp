package org.iut.refactoring;

import java.util.UUID;

public class Salarie {

    private final String id;
    private String nom;
    private TypeSalarie type;
    private double salaireBase;
    private int experience;
    private String equipe;

    public Salarie(TypeSalarie type, String nom, double salaireBase, int experience, String equipe) {
        this.id = UUID.randomUUID().toString();
        this.type = type;
        this.nom = nom;
        this.salaireBase = salaireBase;
        this.experience = experience;
        this.equipe = equipe;
    }

    public String getId() {
        return id;
    }

    public TypeSalarie getType() {
        return type;
    }

    public void setType(TypeSalarie type) {
        this.type = type;
    }

    public String getNom() {
        return nom;
    }

    public double getSalaireBase() {
        return salaireBase;
    }

    public int getExperience() {
        return experience;
    }

    public String getEquipe() {
        return equipe;
    }

    public void setEquipe(String equipe) {
        this.equipe = equipe;
    }
}
