package org.iut.refactoring;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class GestionPersonnelTest {

    private GestionPersonnel gestion;

    @BeforeEach
    void setUp() {
        gestion = new GestionPersonnel();
    }

    private void ajouterTousLesProfils() {
        gestion.ajouteSalarie("DEVELOPPEUR", "DevSenior", 50000, 6, "IT");     // dev > 5
        gestion.ajouteSalarie("DEVELOPPEUR", "DevJunior", 50000, 2, "IT");     // dev <= 5
        gestion.ajouteSalarie("DEVELOPPEUR", "DevExpert", 55000, 12, "IT");    // dev > 10
        gestion.ajouteSalarie("CHEF DE PROJET", "ChefSenior", 60000, 4, "RH"); // chef > 3
        gestion.ajouteSalarie("CHEF DE PROJET", "ChefJunior", 60000, 2, "RH"); // chef <= 3
        gestion.ajouteSalarie("STAGIAIRE", "Stagiaire", 20000, 0, "IT");
        gestion.ajouteSalarie("AUTRE", "Autre", 30000, 1, "OPS");              // type inconnu
    }

    @Test
    void testAjouteSalarieEtSalairesStockes() {
        ajouterTousLesProfils();
        // on doit avoir 7 employés
        assertEquals(7, gestion.employes.size());
        // les salaires calculés à l'ajout doivent exister dans la map
        for (Object[] emp : gestion.employes) {
            String id = (String) emp[0];
            assertTrue(gestion.salairesEmployes.containsKey(id));
        }
        // un log par ajout
        assertEquals(7, gestion.logs.size());
    }

    @Test
    void testCalculSalaireDeveloppeurAvecBonusExperience() {
        gestion.ajouteSalarie("DEVELOPPEUR", "DevSenior", 50000, 6, "IT");
        String id = (String) gestion.employes.getFirst()[0];

        double salaire = gestion.calculSalaire(id);
        // 50000 * 1.2 = 60000 ; exp > 5 => *1.15 = 69000
        assertEquals(50000 * 1.2 * 1.15, salaire, 0.001);
    }

    @Test
    void testCalculSalaireDeveloppeurTresExperimente() {
        gestion.ajouteSalarie("DEVELOPPEUR", "DevExpert", 55000, 12, "IT");
        String id = (String) gestion.employes.getFirst()[0];

        double salaire = gestion.calculSalaire(id);
        // 55000 * 1.2 = 66000
        // exp > 5 => *1.15 = 75900
        // exp > 10 => *1.05 = 79695
        assertEquals(79695.0, salaire, 0.001);
    }

    @Test
    void testCalculSalaireChefDeProjetSenior() {
        gestion.ajouteSalarie("CHEF DE PROJET", "Boss", 60000, 4, "RH");
        String id = (String) gestion.employes.getFirst()[0];

        double salaire = gestion.calculSalaire(id);
        // 60000 * 1.5 = 90000
        // exp > 3 => *1.1 = 99000
        // + 5000 = 104000
        assertEquals(104000.0, salaire, 0.001);
    }

    @Test
    void testCalculSalaireChefDeProjetJunior() {
        gestion.ajouteSalarie("CHEF DE PROJET", "BossJr", 60000, 2, "RH");
        String id = (String) gestion.employes.getFirst()[0];

        double salaire = gestion.calculSalaire(id);
        // 60000 * 1.5 = 90000
        // pas de *1.1
        // + 5000 = 95000
        assertEquals(95000.0, salaire, 0.001);
    }

    @Test
    void testCalculSalaireStagiaire() {
        gestion.ajouteSalarie("STAGIAIRE", "Stagiaire", 20000, 0, "IT");
        String id = (String) gestion.employes.getFirst()[0];

        double salaire = gestion.calculSalaire(id);
        assertEquals(20000 * 0.6, salaire, 0.001);
    }

    @Test
    void testCalculSalaireTypeInconnu() {
        gestion.ajouteSalarie("AUTRE", "Temp", 30000, 1, "OPS");
        String id = (String) gestion.employes.getFirst()[0];

        double salaire = gestion.calculSalaire(id);
        assertEquals(30000.0, salaire, 0.001);
    }

    @Test
    void testCalculSalaireEmployeInexistant() {
        // on capture la sortie juste pour exécuter la ligne d'erreur
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(out));

        double salaire = gestion.calculSalaire("ID-QUI-N-EXISTE-PAS");

        System.setOut(original);

        assertEquals(0.0, salaire, 0.001);
        assertTrue(out.toString().contains("ERREUR"));
    }

    @Test
    void testGenerationRapportsToutesBranches() {
        ajouterTousLesProfils();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(out));

        // SALAIRE sans filtre
        gestion.generationRapport("SALAIRE", null);
        // SALAIRE avec filtre vide
        gestion.generationRapport("SALAIRE", "");
        // SALAIRE avec filtre existant
        gestion.generationRapport("SALAIRE", "IT");
        // SALAIRE avec filtre inexistant
        gestion.generationRapport("SALAIRE", "PAS-LA");

        // EXPERIENCE avec filtre null (true)
        gestion.generationRapport("EXPERIENCE", null);
        // EXPERIENCE avec filtre inexistant (false)
        gestion.generationRapport("EXPERIENCE", "PAS-LA");

        // DIVISION
        gestion.generationRapport("DIVISION", null);

        // type inconnu
        gestion.generationRapport("TRUC-AUTRE", null);

        System.setOut(original);

        // 8 rapports => 8 logs en plus des 7 ajouts
        assertTrue(gestion.logs.size() >= 7 + 8);
        assertTrue(out.toString().contains("=== RAPPORT: SALAIRE ==="));
        assertTrue(out.toString().contains("=== RAPPORT: DIVISION ==="));
    }

    @Test
    void testAvancementEmployeSuccesEtEchec() {
        gestion.ajouteSalarie("DEVELOPPEUR", "Alice", 50000, 6, "IT");
        String id = (String) gestion.employes.getFirst()[0];

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(out));

        // succès
        gestion.avancementEmploye(id, "CHEF DE PROJET");
        // échec
        gestion.avancementEmploye("INCONNU", "CHEF DE PROJET");

        System.setOut(original);

        // le type a bien changé
        assertEquals("CHEF DE PROJET", gestion.employes.getFirst()[1]);

        // salaire recalculé : base 50000, chef, exp 6
        // 50000 * 1.5 = 75000
        // exp > 3 => *1.1 = 82500
        // + 5000 = 87500
        double salaireMisAJour = gestion.salairesEmployes.get(id);
        assertEquals(87500.0, salaireMisAJour, 0.001);

        String console = out.toString();
        assertTrue(console.contains("Employé promu avec succès!"));
        assertTrue(console.contains("ERREUR: impossible de trouver l'employé"));
    }

    @Test
    void testGetEmployesParDivision() {
        ajouterTousLesProfils();

        ArrayList<Object[]> it = gestion.getEmployesParDivision("IT");
        ArrayList<Object[]> rh = gestion.getEmployesParDivision("RH");
        ArrayList<Object[]> vide = gestion.getEmployesParDivision("FINANCE");

        assertFalse(it.isEmpty());
        assertFalse(rh.isEmpty());
        assertTrue(vide.isEmpty());
    }

    @Test
    void testPrintLogs() {
        ajouterTousLesProfils();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(out));

        gestion.printLogs();

        System.setOut(original);

        String txt = out.toString();
        assertTrue(txt.contains("=== LOGS ==="));
        assertTrue(txt.contains("Ajout de l'employé"));
    }

    @Test
    void testCalculBonusAnnuelTousTypes() {
        ajouterTousLesProfils();

        // On récupère les IDs dans l'ordre d'ajout
        String idDevSenior   = (String) gestion.employes.get(0)[0];
        String idDevJunior   = (String) gestion.employes.get(1)[0];
        String idDevExpert   = (String) gestion.employes.get(2)[0];
        String idChefSenior  = (String) gestion.employes.get(3)[0];
        String idChefJunior  = (String) gestion.employes.get(4)[0];
        String idStagiaire   = (String) gestion.employes.get(5)[0];
        String idAutre       = (String) gestion.employes.get(6)[0];

        // Dev senior: base 50000 -> 10% = 5000, exp>5 => *1.5 = 7500
        assertEquals(7500.0, gestion.calculBonusAnnuel(idDevSenior), 0.001);

        // Dev junior: base 50000 -> 10% = 5000, pas de *1.5
        assertEquals(5000.0, gestion.calculBonusAnnuel(idDevJunior), 0.001);

        // Dev expert: base 55000 -> 10% = 5500, exp>5 => *1.5 = 8250
        assertEquals(8250.0, gestion.calculBonusAnnuel(idDevExpert), 0.001);

        // Chef senior: base 60000 -> 20% = 12000, exp>3 => *1.3 = 15600
        assertEquals(15600.0, gestion.calculBonusAnnuel(idChefSenior), 0.001);

        // Chef junior: base 60000 -> 20% = 12000 (pas de *1.3)
        assertEquals(12000.0, gestion.calculBonusAnnuel(idChefJunior), 0.001);

        // Stagiaire: 0
        assertEquals(0.0, gestion.calculBonusAnnuel(idStagiaire), 0.001);

        // Autre: retombe dans le "return 0" final
        assertEquals(0.0, gestion.calculBonusAnnuel(idAutre), 0.001);
    }
}