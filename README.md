RA-IL-1 S5

KHODJAOUI Elias

# Refactorisation du module `org.iut.refactoring`

---

## Liste des modifications

- Supprimer l’usage de structures non typées (`Object[]`) pour représenter un salarié.
- Centraliser la logique de calcul des salaires et des bonus.
- Introduire de vraies classes métier (`Salarie`) et un enum (`TypeSalarie`) pour éviter les chaînes de caractères “magiques”.
- Garder les signatures de méthodes déjà utilisées (`ajouteSalarie`, `calculSalaire`, `generationRapport`, …) pour ne pas casser le code appelant.
- Préparer le code à être mieux testé et plus évolutif.

---

## Fichiers ajoutés

### `Salarie.java`
Classe représentant un salarié :

- `id` (généré automatiquement)
- `type` (`TypeSalarie`)
- `nom`
- `salaireBase`
- `experience`
- `equipe`

Cette classe permet de manipuler les employés de façon typée plutôt qu’avec des tableaux d’objets.

---

### `TypeSalarie.java`
Enum qui remplace les chaînes de caractères utilisées pour identifier le type d’employé.
Ceci évite des typos sur les chaînes.

Valeurs principales :
- `DEVELOPPEUR`
- `CHEF_DE_PROJET`
- `STAGIAIRE`
- `AUTRE`

Il contient aussi une méthode utilitaire :

```java
public static TypeSalarie fromLabel(String label)
```

# 3. `RemunerationService.java`

Ce fichier a été ajouté pour **centraliser** toutes les règles métier liées à la rémunération. Avant le refactor, ces règles étaient dupliquées à plusieurs endroits dans `GestionPersonnel` (dans l’ajout d’un salarié, dans le calcul du salaire, parfois même au moment de la promotion), ce qui créait un risque d’incohérence.

Le service expose deux méthodes principales :

```java
public double calculSalaire(Salarie s)
public double calculBonus(Salarie s)
```

Ces méthodes appliquent les mêmes règles que dans le code d’origine, mais depuis un seul endroit.

## Règles implémentées

### Développeur
- salaire de base × 1.2
- si expérience > 5 ans → salaire × 1.15
- si expérience > 10 ans → salaire × 1.05 (bonus supplémentaire)

### Chef de projet
- salaire de base × 1.5
- si expérience > 3 ans → salaire × 1.1
- 5000 de bonus fixe

### Stagiaire
- salaire de base × 0.6

### Autre
- salaire de base (pas de majoration)

---

## Intérêts de `RemunerationService`

- Une seule source de vérité : si demain la règle change (par exemple le bonus fixe du chef de projet passe à 6000), on ne modifie qu’un seul fichier.
- Moins de duplications : `GestionPersonnel` n’a plus besoin de refaire les calculs à chaque méthode.
- Tests plus simples : on peut tester la rémunération indépendamment de la gestion de la liste d’employés.
