package org.iut.refactoring;

public enum TypeSalarie {
    DEVELOPPEUR,
    CHEF_DE_PROJET,
    STAGIAIRE,
    AUTRE;

    // Optionnel : pour compatibilité avec les anciennes chaînes
    public static TypeSalarie fromLabel(String label) {
        if (label == null) return AUTRE;
        return switch (label.trim().toUpperCase()) {
            case "DEVELOPPEUR" -> DEVELOPPEUR;
            case "CHEF DE PROJET", "CHEF_DE_PROJET" -> CHEF_DE_PROJET;
            case "STAGIAIRE" -> STAGIAIRE;
            default -> AUTRE;
        };
    }
}
