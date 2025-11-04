package org.iut.refactoring;

public class RemunerationService {

    public double calculSalaire(Salarie s) {
        double base = s.getSalaireBase();
        int exp = s.getExperience();

        return switch (s.getType()) {
            case DEVELOPPEUR -> {
                double salaire = base * 1.2;
                if (exp > 5) {
                    salaire *= 1.15;
                }
                if (exp > 10) {
                    salaire *= 1.05;
                }
                yield salaire;
            }
            case CHEF_DE_PROJET -> {
                double salaire = base * 1.5;
                if (exp > 3) {
                    salaire *= 1.1;
                }
                salaire += 5000;
                yield salaire;
            }
            case STAGIAIRE -> base * 0.6;
            case AUTRE -> base;
        };
    }

    public double calculBonus(Salarie s) {
        double base = s.getSalaireBase();
        int exp = s.getExperience();

        return switch (s.getType()) {
            case DEVELOPPEUR -> {
                double bonus = base * 0.1;
                if (exp > 5) {
                    bonus *= 1.5;
                }
                yield bonus;
            }
            case CHEF_DE_PROJET -> {
                double bonus = base * 0.2;
                if (exp > 3) {
                    bonus *= 1.3;
                }
                yield bonus;
            }
            case STAGIAIRE -> 0.0;
            case AUTRE -> 0.0;
        };
    }
}
