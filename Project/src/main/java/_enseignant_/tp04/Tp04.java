package _enseignant_.tp04;



import javax.swing.*;
import java.awt.*;
import java.io.*;

import commun.FenetreDessin;

public class Tp04 {
    public static final byte TAILLE = 3;

    public static void main(String[] args) {
        FenetreDessin fd = new FenetreDessin("Sudoku", 640, 480);

        int[][] grille;
        boolean[][] grilleFixe;
        byte[] selection = new byte[] { -1, -1 };
        char charLu = 0;

        grille = new int[TAILLE*TAILLE][TAILLE*TAILLE];
        grilleFixe = new boolean[TAILLE*TAILLE][TAILLE*TAILLE];
        fd.imageDessinDirect(false);

        do {
            fd.animationAttendreProchaineImage();
            fd.effaceFenetre();
            dessineSudoku(fd, grille, grilleFixe, selection);
            fd.imageRafraichir();
            if (fd.sourisEvenement()) {
                if (fd.sourisPositionX() >= 40 && fd.sourisPositionX() < 440 &&
                        fd.sourisPositionY() >= 40 && fd.sourisPositionY() < 440) {
                    selection = new byte[] { -1, -1 };
                    byte caseX = (byte) ((fd.sourisPositionX() - 40)*grille.length / 400);
                    byte caseY = (byte) ((fd.sourisPositionY() - 40)*grille.length / 400);
                    if (fd.sourisBouton() == 1) {
                        selection = new byte[] {caseY, caseX};
                    }
                    if (fd.sourisBouton() == 3 && !grilleFixe[caseY][caseX]) {
                        grille[caseY][caseX] = 0;
                    }
                }
                if (fd.sourisPositionX() >= 500 && fd.sourisPositionX() < 530 &&
                        fd.sourisPositionY() >= 40 && fd.sourisPositionY() < 440) {
                    int numero = (fd.sourisPositionY() - 40)*grille.length/400 + 1;
                    if (fd.sourisBouton() == 1) {
                        assigneValeur(fd, grille, grilleFixe, selection, numero);
                    }
                }
            }
            if (fd.clavierEvenement()) {
                charLu = fd.clavierCharactere();
                if (charLu == 'f') { // Met les chiffres en lecture seule
                    fixeNumero(grille, grilleFixe);
                }
                if (charLu == 'F') { // Permet de modifier tous les chiffres
                    nettoieGrille(grilleFixe);
                }
                if (charLu == 'V') { // Vider la grille
                    nettoieGrille(grille);
                    nettoieGrille(grilleFixe);
                    selection = new byte[] { -1, -1 };
                }
                if (charLu >= '1' && charLu <= '9') { // Inscrire un numéro avec le clavier
                    int numero = charLu - '0';
                    assigneValeur(fd, grille, grilleFixe, selection, numero);
                }
                if (charLu == 'b') { // Crée une image bitmap de la grille
                    selection = new byte[] { -1, -1 };
                    fd.animationAttendreProchaineImage();
                    fd.effaceFenetre();
                    dessineSudoku(fd, grille, grilleFixe, selection);
                    fd.imageRafraichir();
                    ecrireImage(fd);
                }
                if (charLu == 'i') { // Importer une grille en CSV
                    if (importerGrille(grille)) {
                        nettoieGrille(grilleFixe);
                        selection = new byte[] { -1, -1 };
                        fixeNumero(grille, grilleFixe);
                    }
                }
                if (charLu == 'e') { // Exporter une grille en CSV
                    exporterGrille(grille);
                }
                if (charLu == 's') { // Sauvegarder la résolution
                    sauvegarderProgression(grille, grilleFixe);
                }
                if (charLu == 'r') { // Restaurer la résolution
                    restaurerProgression(grille, grilleFixe);
                    selection = new byte[] { -1, -1 };
                }
            }
        } while (charLu != 'Q');
    }

    /**
     * Dessine la grille de Sudoku ainsi que la case sélectionnée
     * @param fd Pointeur sur la fenêtre de dessin
     * @param grille Grille contenant les chiffres inscrits dans le Sudoku
     * @param grilleFixe Grille indiquant les chiffres en lecture seule
     * @param sel Position de la case sélectionnée [ligne, colonne]
     */
    public static void dessineSudoku(FenetreDessin fd, int[][] grille,
                                     boolean[][] grilleFixe, byte[] sel) {
        Color[] couleurs = { // Couleurs des secteurs
                new Color(1f,0.9f,0.9f), new Color(0.9f,1f,0.9f), new Color(0.9f,0.9f,1f),
                new Color(1f,0.95f,0.85f), new Color(0.95f,0.85f,1f), new Color(0.85f,1f,0.95f),
                new Color(0.9f,1f,1f), new Color(1f,1f,0.9f), new Color(1f,0.9f,1f)
        };

        // Dessin de la grille avec des fonds de différentes couleurs
        for (int i = 0; i < TAILLE; ++i) {
            for (int j = 0; j < TAILLE; ++j) {
                fd.couleurRemplissage(couleurs[(i*TAILLE+j)%couleurs.length]);
                fd.dessineRectanglePlein(40+j*400/TAILLE, 40+i*400/TAILLE,
                        40+(j+1)*400/TAILLE, 40+(i+1)*400/TAILLE);
            }
        }
        fd.couleurRemplissage(1, 1, 1);
        fd.couleurCrayon(0, 0, 0);
        fd.dessineSegment(500,  40,  500, 440);
        fd.dessineSegment(530,  40,  530, 440);
        for (int i = 0; i <= grille.length; ++i) {
            fd.dessineSegment(40,40 + i*400/grille.length,440,40 + i*400/grille.length);
            fd.dessineSegment(40 + i*400/grille.length,40,40 + i*400/grille.length,440);
            fd.dessineSegment(500,40 + i*400/grille.length,530,40 + i*400/grille.length);
            if (i % (int) Math.sqrt(grille.length) == 0) {
                fd.dessineSegment(40,39 + i*400/grille.length,440,39 + i*400/grille.length);
                fd.dessineSegment(39 + i*400/grille.length,40,39 + i*400/grille.length,440);
                fd.dessineSegment(40,41 + i*400/grille.length,440,41 + i*400/grille.length);
                fd.dessineSegment(41 + i*400/grille.length,40,41 + i*400/grille.length,440);
            }
        }

        // Colore le fond de la case sélectionnée
        if (sel[0] >= 0 && sel[1] >= 0) {
            fd.couleurRemplissage(0.9f, 0.9f, 0.9f);
            fd.couleurCrayon(1f, 1f, 1f);
            fd.dessineRectanglePlein(
                    30 + 200*(2*sel[1]+1)/grille.length, 32 + 200*(2*sel[0]+1)/grille.length,
                    50 + 200*(2*sel[1]+1)/grille.length, 48 + 200*(2*sel[0]+1)/grille.length);
            fd.couleurRemplissage(1, 1, 1);
            fd.couleurCrayon(0, 0, 0);
        }

        // Affiche chacun des chiffres dans la grille
        for (int i = 0; i < grille.length; ++i) {
            for (int j = 0; j < grille.length; ++j) {
                // Choisit la couleur du chiffre s'il est fixé
                if (!grilleFixe[i][j]) {
                    fd.couleurCrayon(Color.RED);
                }
                if (grille[i][j] > 0) {
                    fd.dessineTexte(36 - 4*(grille[i][j]/10) + 200*(2*j+1)/grille.length,
                            48 + 200*(2*i+1)/grille.length, "" + grille[i][j]);
                }
                fd.couleurCrayon(0, 0, 0);
            }
            // Affiche la liste des valeurs pour remplir la grille
            fd.couleurCrayon(Color.RED);
            if (sel[0] >= 0 && estDansLigne(grille, sel[0], i+1)) {
                fd.dessineSegment(505, 40 + 200*(2*i+1)/grille.length,
                        525, 40 + 200*(2*i+1)/grille.length);
            }
            if (sel[1] >= 0 && estDansColonne(grille, sel[1], i+1)) {
                fd.dessineSegment(515, 32 + 200*(2*i+1)/grille.length,
                        515, 48 + 200*(2*i+1)/grille.length);
            }
            if (sel[0] >= 0 && sel[1] >= 0 && estDansSecteur(grille, sel[0], sel[1], i+1)) {
                fd.dessineRectangle(505, 32 + 200*(2*i+1)/grille.length,
                        525, 48 + 200*(2*i+1)/grille.length);
            }
            fd.couleurCrayon(0, 0, 0);
            fd.dessineTexte(511 - 4*((i+1)/10), 48 + 200*(2*i+1)/grille.length, ""+(i+1));
        }
    }

    /**
     * Permet d'affecter le chiffre reçu en paramètre à la case sélectionnée si le chiffre
     * ne se trouve pas déjà dans la ligne, la colonne ou la zone.  Cette méthode ne
     * vérifie pas si la case est en lecture seule et ne fait pas d'affichage dans la
     * fenêtre de dessin dans le cas où le chiffre ne peut pas être ajouté.
     * @param grille Grille contenant les chiffres inscrits dans le Sudoku
     * @param lig Ligne sur laquelle on veut ajouter le chiffre
     * @param col Colonne dans laquelle on veut ajouter le chiffre
     * @param chiffre Chiffre que l'on veut inscrire dans la grille
     * @return true si le chiffre a été ajouté dans la grille, false sinon
     */
    public static boolean assigneValeur(int[][] grille, int lig, int col, int chiffre) {
        return assigneValeur(null, grille, new boolean[TAILLE*TAILLE][TAILLE*TAILLE],
                new byte[] {(byte) lig, (byte) col}, chiffre);
    }

    /**
     * Permet d'affecter le chiffre reçu en paramètre à la case sélectionnée si la case
     * n'est pas en lecture seule et que le chiffre ne se trouve pas déjà dans la ligne,
     * la colonne ou la zone.
     * @param fd Fenêtre de dessin afin d'encadrer en rouge la zone contenant déjà le chiffre
     * @param grille Grille contenant les chiffres inscrits dans le Sudoku
     * @param grilleFixe Grille indiquant les chiffres en lecture seule
     * @param pos Position de la case sélectionnée [ligne, colonne]
     * @param chiffre Chiffre que l'on veut inscrire dans la grille
     * @return true si le chiffre a été ajouté dans la grille, false sinon
     */
    public static boolean assigneValeur(FenetreDessin fd, int[][] grille,
                                        boolean[][] grilleFixe, byte[] pos, int chiffre) {
        boolean valide = false;
        if (pos[0] >= 0 && pos[1] >= 0 && !grilleFixe[pos[0]][pos[1]]) {
            valide = true;
            if (estDansLigne(grille, pos[0], chiffre)) {
                valide = false;
                if (fd != null) {
                    indiqueErreur(fd, grille, grilleFixe, pos, pos[0], -1);
                }
            }
            if (estDansColonne(grille, pos[1], chiffre)) {
                valide = false;
                if (fd != null) {
                    indiqueErreur(fd, grille, grilleFixe, pos, -1, pos[1]);
                }
            }
            if (estDansSecteur(grille, pos[0], pos[1], chiffre)) {
                valide = false;
                if (fd != null) {
                    indiqueErreur(fd, grille, grilleFixe, pos, pos[0], pos[1]);
                }
            }
            if (valide) {
                grille[pos[0]][pos[1]] = chiffre;
            }
        }
        return valide;
    }

    /**
     * Permet d'afficher un rectangle rouge autour de la zone associée à l'erreur
     * @param fd Pointeur sur la fenêtre de dessin
     * @param grille Grille contenant les chiffres inscrits dans le Sudoku
     * @param grilleFixe Grille indiquant les chiffres en lecture seule
     * @param pos Position de la case sélectionnée [ligne, colonne]
     * @param i Ligne de la case en erreur, -1 si c'est une erreur de colonne
     * @param j Colonne de la case en erreur, -1 si c'est une erreur de ligne
     */
    public static void indiqueErreur(FenetreDessin fd, int[][] grille,
                                     boolean[][] grilleFixe, byte[] pos, int i, int j) {
        int x1 = 40, y1 = 40, x2 = 440, y2 = 440;
        if (j < 0) { // Indiquer la ligne
            y1 = 40 + i*400/grille.length;
            y2 = 40 + (i+1)*400/grille.length;
        } else if (i < 0) { // Indiquer la colonne
            x1 = 40 + j*400/grille.length;
            x2 = 40 + (j+1)*400/grille.length;
        } else { // Indiquer le secteur
            int tailleSecteur = (int) Math.sqrt(grille.length);
            x1 = 40 + (j/tailleSecteur * tailleSecteur)*400/grille.length;
            y1 = 40 + (i/tailleSecteur * tailleSecteur)*400/grille.length;
            x2 = 40 + (j/tailleSecteur * tailleSecteur+tailleSecteur)*400/grille.length;
            y2 = 40 + (i/tailleSecteur * tailleSecteur+tailleSecteur)*400/grille.length;
        }
        fd.couleurCrayon(1, 0.5f, 0.5f);
        fd.dessineRectangle(x1, y1, x2, y2);
        fd.couleurCrayon(0, 0, 0);
        fd.imageRafraichir();
        FenetreDessin.attendre(750);
        fd.effaceFenetre();
        dessineSudoku(fd, grille, grilleFixe, pos);
        fd.imageRafraichir();
    }

    /**
     * Vérifie si le chiffre à ajouter existe déjà dans la ligne spécifiée
     * @param grille Grille contenant les chiffres inscrits dans le Sudoku
     * @param ligne Numéro de ligne que l'on doit vérifier
     * @param chiffre Chiffre que l'on veut inscrire dans la grille
     * @return true si le chiffre existe dans la ligne, false sinon
     */
    public static boolean estDansLigne(int[][] grille, int ligne, int chiffre) {
        boolean trouve = false;
        for (int j = 0; j < grille[ligne].length; ++j) {
            if (grille[ligne][j] == chiffre) {
                trouve = true;
            }
        }
        return trouve;
    }

    /**
     * Vérifie si le chiffre à ajouter existe déjà dans la colonne spécifiée
     * @param grille Grille contenant les chiffres inscrits dans le Sudoku
     * @param colonne Numéro de colonne que l'on doit vérifier
     * @param chiffre Chiffre que l'on veut inscrire dans la grille
     * @return true si le chiffre existe dans la colonne, false sinon
     */
    public static boolean estDansColonne(int[][] grille, int colonne, int chiffre) {
        boolean trouve = false;
        for (int i = 0; i < grille.length; ++i) {
            if (grille[i][colonne] == chiffre) {
                trouve = true;
            }
        }
        return trouve;
    }

    /**
     * Vérifie si le chiffre se retrouve dans un secteur de n*n de la grille
     * NOTE: La position reçue ne correspond pas nécessairement à un coin du secteur
     * @param grille Grille contenant les chiffres inscrits dans le Sudoku
     * @param lig Ligne sur laquelle on veut ajouter le chiffre
     * @param col Colonne dans laquelle on veut ajouter le chiffre
     * @param chiffre Chiffre que l'on veut inscrire dans la grille
     * @return true si le chiffre existe dans le secteur, false sinon
     */
    public static boolean estDansSecteur(int[][] grille, int lig, int col, int chiffre) {
        int tailleSecteur = (int) Math.sqrt(grille.length);
        int coinI = lig / tailleSecteur * tailleSecteur;
        int coinJ = col / tailleSecteur * tailleSecteur;
        boolean trouve = false;
        for (int i = coinI; i < coinI+tailleSecteur; ++i) {
            for (int j = coinJ; j < coinJ+tailleSecteur; ++j) {
                if (grille[i][j] == chiffre) {
                    trouve = true;
                }
            }
        }
        return trouve;
    }

    /**
     * Permet de mettre en lecture seule les chiffres contenus actuellement dans la grille
     * Pour ce faire, on met à true les cases correspondantes dans grilleFixe
     * @param grille Grille contenant les chiffres inscrits dans le Sudoku
     * @param grilleFixe Grille indiquant les chiffres en lecture seule
     */
    public static void fixeNumero(int[][] grille, boolean[][] grilleFixe) {
        for (int i = 0; i < grille.length; ++i) {
            for (int j = 0; j < grille[i].length; ++j) {
                if (grille[i][j] > 0) {
                    grilleFixe[i][j] = true;
                }
            }
        }
    }

    /**
     * Remet toutes les cases de la grille à false
     * @param grille Grille à effacer
     */
    public static void nettoieGrille(boolean[][] grille) {
        for (int i = 0; i < grille.length; ++i) {
            for (int j = 0; j < grille[i].length; ++j) {
                grille[i][j] = false;
            }
        }
    }

    /**
     * Remet toutes les cases de la grille à 0
     * @param grille Grille à effacer
     */
    public static void nettoieGrille(int[][] grille) {
        for (int i = 0; i < grille.length; ++i) {
            for (int j = 0; j < grille[i].length; ++j) {
                grille[i][j] = 0;
            }
        }
    }

    public static String nfs; // Nécessaire pour la sélection de fichiers
    /**
     * Affiche une boîte de dialogue permettant de sélectionner un fichier
     * @param lecture Afficher la boîte d'ouverture (true) ou de sauvegarde (false)
     * @return Nom du fichier sélectionné ou une chaîne vide si l'opération a été annulée
     */
    /**
     * Affiche une boîte de dialogue permettant de sélectionner un fichier
     * @param lecture Afficher la boîte d'ouverture (true) ou de sauvegarde (false)
     * @return Nom du fichier sélectionné ou une chaîne vide si l'opération a été annulée
     */
    public static String choixFichier(boolean lecture) {
        final String[] nfs = {""}; // Utiliser un tableau pour permettre la modification à partir d'une lambda
        try {
            // On doit se placer dans la queue d'événements pour que ça fonctionne.
            EventQueue.invokeAndWait(() -> {
                JFileChooser fc = new JFileChooser(new File(System.getProperty("user.dir")).getParent());

                int rv;
                if (lecture) {
                    rv = fc.showOpenDialog(null);
                } else {
                    rv = fc.showSaveDialog(null);
                }
                if (rv == JFileChooser.APPROVE_OPTION) {
                    nfs[0] = fc.getSelectedFile().toString();
                }
            });
        } catch (java.lang.reflect.InvocationTargetException | InterruptedException ex) {
            ex.printStackTrace();
        }
        return nfs[0];
    }


    /**
     * Lit un fichier CSV contenant une grille de Sudoku de départ
     * Chaque ligne du fichier correspond à une ligne de la grille et
     * les colonnes (cases) sont séparées par des barres verticales (|)
     * Un caractère de soulignement (_) représente une case vide
     * Les chiffres ou les caractères invalides sont ignorés (attention
     * aux exceptions) et un message d'erreur est alors affiché à la console
     * @param grille Grille contenant les chiffres inscrits dans le Sudoku
     * @return true si le fichier a été lu correctement, false sinon
     */

    public static boolean importerGrille(int[][] grille) {
        boolean reussi = false;
        String nf = choixFichier(true);

        // Vérifier l'extension du fichier
        if (nf == null || !nf.endsWith(".txt")) {
            System.out.println("Le fichier sélectionné n'a pas l'extension .txt.");
            return false;
        }

        // Nettoyer la grille
        nettoieGrille(grille);

        // Ouvrir un fichier texte en lecture
        try (BufferedReader reader = new BufferedReader(new FileReader(nf))) {
            String ligne;
            int row = 0;

            // Lire chaque ligne du fichier
            while ((ligne = reader.readLine()) != null && row < grille.length) {
                ligne = ligne.replace("_", "0"); // Remplacer les caractères de soulignement par des zéros
                String[] cases = ligne.split("\\|");

                if (cases.length != grille[row].length) {
                    System.out.println("Nombre de cases incorrect à la ligne " + (row + 1));

                }

                int col = 0;
                // Traiter chaque case de la ligne
                for (String uneCase : cases) {
                    if (col >= grille[row].length) break;

                    try {
                        uneCase = uneCase.trim();
                        int valeur = Integer.parseInt(uneCase);
                        if (valeur < 0 || valeur > 9) {
                            System.out.println("Chiffre illégal à la position [" + (row + 1) + ":" + (col + 1) + "]");
                        } else {
                            if (!assigneValeur(grille, row, col, valeur)) {
                               System.out.println("Impossible d'ajouter la valeur à la position [" + (row + 1) + ":" + (col + 1) + "]");
                            }
                        }
                    } catch (NumberFormatException e) {
                        if (!uneCase.isEmpty()) {
                            System.out.println("Caractère invalide : " + uneCase + " à la position [" + (row + 1) + ":" + (col + 1) + "]");
                        }
                    }
                    col++;
                }
                row++;
            }
            reussi = true;
        } catch (IOException e) {
            System.out.println("Erreur de lecture du fichier : " + e.getMessage());
        }

        return reussi;
    }

    /**
     * Exporte le contenu de la grille de Sudoku dans un fichier CSV
     * Chaque ligne du fichier correspond à une ligne de la grille et
     * les colonnes (cases) sont séparées par des barres verticales (|)
     * Les chiffres sont inscrits directement et on remplace chacune des
     * cases vides (valeur de 0) par un caractère de soulignement (_)
     * @param grille Grille contenant les chiffres inscrits dans le Sudoku
     */
    public static void exporterGrille(int[][] grille) {
        String nf = choixFichier(false);

        // Vérifier si le nom du fichier est une chaîne vide
        if (nf == null || nf.isEmpty()) {
            return;
        }

        // Ajouter l'extension .txt si nécessaire
        if (!nf.endsWith(".txt")) {
            nf += ".txt";
        }

        // Ouvrir un fichier texte en écriture
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nf))) {
            // Écrire chaque ligne de la grille sur une ligne du fichier
            for (int i = 0; i < grille.length; i++) {
                StringBuilder line = new StringBuilder();

                for (int j = 0; j < grille[i].length; j++) {
                    if (j > 0) {
                        line.append("|");
                    }

                    if (grille[i][j] == 0) {
                        line.append("_");
                    } else {
                        line.append(grille[i][j]);
                    }
                }

                writer.write(line.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Erreur d'écriture du fichier : " + e.getMessage());
        }
    }

    /**
     * Permet de sauvegarder l'état du Sudoku en cours de résolution
     * Crée ou écrase le fichier d'objets nommé "sudoku.save", qui contient
     * la grille des chiffres placés et celle des chiffres en lecture seule
     * @param grille Grille contenant les chiffres inscrits dans le Sudoku
     * @param grilleFixe Grille indiquant les chiffres en lecture seule
     */
    public static void sauvegarderProgression(int[][] grille, boolean[][] grilleFixe) {
        String nf = "sudoku.save";
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(nf))) {
            oos.writeObject(grille);
            oos.writeObject(grilleFixe);
            System.out.println("Progression sauvegardée avec succès.");
        } catch (IOException e) {
            System.out.println("Erreur lors de la sauvegarde de la progression : " + e.getMessage());
        }
    }


    /**
     * Restaure l'état d'un Sudoku en cours de résolution
     * Lis le fichier d'objets nommé "sudoku.save", s'il existe, qui contient
     * la grille des chiffres placés et celle des chiffres en lecture seule
     * Les informations lues remplacent les grilles correspondantes en mémoire
     * @param grille Grille contenant les chiffres inscrits dans le Sudoku
     * @param grilleFixe Grille indiquant les chiffres en lecture seule
     */
    public static void restaurerProgression(int[][] grille, boolean[][] grilleFixe) {
        String nf = "sudoku.save";
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(nf))) {
            int[][] tempGrille = (int[][]) ois.readObject();
            boolean[][] tempGrilleFixe = (boolean[][]) ois.readObject();

            // Copier les tableaux temporaires dans grille et grilleFixe
            for (int i = 0; i < grille.length; i++) {
                System.arraycopy(tempGrille[i], 0, grille[i], 0, grille[i].length);
                System.arraycopy(tempGrilleFixe[i], 0, grilleFixe[i], 0, grilleFixe[i].length);
            }
            System.out.println("Progression restaurée avec succès.");
        } catch (FileNotFoundException e) {
            System.out.println("Fichier de sauvegarde introuvable.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Erreur lors de la restauration de la progression : " + e.getMessage());
        }
    }

    /**
     * Crée un fichier image BMP qui est une copie de la grille de Sudoku
     * Les différentes couleurs à inscrire dans le BMP sont lues à
     * partir de la fenêtre de dessin (fd)
     * @param fd Pointeur sur la fenêtre de dessin pour extraire les couleurs
     */
    public static void ecrireImage(FenetreDessin fd) {
        int tailleX = 404;                        // Largeur de l'image en pixels
        int tailleY = 404;                        // Hauteur de l'image en pixels
        int padding = (4 - (tailleX * 3) % 4) % 4;  // Calcul du padding si X pas multiple de 4
        int tailleI = tailleX * tailleY * 3;        // Taille des données de l'image
        tailleI += tailleY * padding;                // Ajout de la taille du padding
        int tailleF = tailleI + 54;                // Taille totale du BMP
        // En-têtes d'un BMP en RGB (Little-endian)
        byte[] enTete = {                        // Bloc en-tête du BMP
                'B', 'M',                         // Signature "BM"
                (byte) (tailleF & 0xFF),    // Taille du fichier
                (byte) (tailleF >> 8 & 0xFF),
                (byte) (tailleF >> 16 & 0xFF),
                (byte) (tailleF >> 24 & 0xFF),
                0x0, 0x0, 0x0, 0x0,             // Réservé (doit être 0)
                0x36, 0x0, 0x0, 0x0             // Décalage des données
        };
        byte[] info = {                         // Bloc information du BMP
                0x28, 0x0, 0x0, 0x0,             // Taille de info
                (byte) (tailleX & 0xFF),    // Largeur de l'image en pixels
                (byte) (tailleX >> 8 & 0xFF),
                (byte) (tailleX >> 16 & 0xFF),
                (byte) (tailleX >> 24 & 0xFF),
                (byte) (tailleY & 0xFF),    // Hauteur de l'image en pixels
                (byte) (tailleY >> 8 & 0xFF),
                (byte) (tailleY >> 16 & 0xFF),
                (byte) (tailleY >> 24 & 0xFF),
                0x01, 0x0,                         // Nombre de plans (doit être 1)
                0x18, 0x0,                         // Bits par pixel
                0x0, 0x0, 0x0, 0x0,             // Compression (0:sans compression)
                (byte) (tailleI & 0xFF),    // Taille des données de l'image
                (byte) (tailleI >> 8 & 0xFF),
                (byte) (tailleI >> 16 & 0xFF),
                (byte) (tailleI >> 24 & 0xFF),
                0x40, 0x0B, 0x0, 0x0,             // Pixels X par mètre
                0x40, 0x0B, 0x0, 0x0,             // Pixels Y par mètre
                0x0, 0x0, 0x0, 0x0,             // Nombre de couleurs
                0x0, 0x0, 0x0, 0x0,             // Nombre de couleurs importantes
        };

        String nf = choixFichier(false);

        // Vérifier si le nom du fichier est une chaîne vide
        if (nf == null || nf.isEmpty()) {
            return;
        }

        // Ajouter l'extension .bmp si nécessaire
        if (!nf.endsWith(".bmp")) {
            nf += ".bmp";
        }

        // Ouvrir un fichier binaire en écriture
        try (FileOutputStream fos = new FileOutputStream(nf)) {
            // Écrire enTete et info
            fos.write(enTete);
            fos.write(info);

            // Écrire les lignes dans l'ordre inverse d'affichage
            for (int y = tailleY - 1; y >= 0; y--) {
                for (int x = 0; x < tailleX; x++) {
                    // Écrire les composantes B V R pour chaque pixel (39;39 à 442;442)
                    float b = fd.litCouleurPixelB(x + 39, y + 39);
                    float v = fd.litCouleurPixelV(x + 39, y + 39);
                    float r = fd.litCouleurPixelR(x + 39, y + 39);
                    fos.write((byte) (b * 255));
                    fos.write((byte) (v * 255));
                    fos.write((byte) (r * 255));
                }
                // Ajouter du padding si nécessaire
                for (int p = 0; p < padding; p++) {
                    fos.write(0);
                }
            }
            System.out.println("Image BMP écrite avec succès.");
        } catch (IOException e) {
            System.out.println("Erreur lors de l'écriture de l'image : " + e.getMessage());
        }
    }
}
