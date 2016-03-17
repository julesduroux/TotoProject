

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;


public class Client implements Runnable {

	enum Dir {
		NORD("N"), SUD("S"), EST("E"), OUEST("O"), JUMP_NORD("JN"), JUMP_SUD("JS"), JUMP_EST("JE"), JUMP_OUEST(
				"JO");
		String code;
		Dir(String dir) {
			code = dir;
		}
	}
	
	private String ipServer;
	private long teamId;
	private String secret;
	private int socketNumber;
	private long gameId;

	Random rand = new Random();

	

	public Client(String ipServer, long teamId, String secret, int socketNumber, long gameId) {
		this.ipServer = ipServer;
		this.teamId = teamId;
		this.secret = secret;
		this.socketNumber = socketNumber;
		this.gameId = gameId;
	}

	public void run() {
		System.out.println("Demarrage du client");
		Socket socket = null;
		String message;
		// Dictionnaire de joueurs
		Hashtable<Integer,Joueur> joueurs = new Hashtable<Integer,Joueur>();
		Joueur currentPlayer;
		int[] OrdreJoueurs = new int[6];
		int[] OrdreJoueurs2 = new int[6];
		BufferedReader in;
		PrintWriter out;
		try {
			System.out.println("Pour voir la partie : http://" + ipServer + ":8080/?gameId=" + gameId);
			socket = new Socket(ipServer, socketNumber);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream());
			System.out.println("Envoi de l'incription");
			out.println(secret + "%%inscription::" + gameId + ";" + teamId);
			out.flush();

			do {
				message = in.readLine();
				//System.out.println("Message recu : " + message);
				if (message != null) {
					if (message.equalsIgnoreCase("Inscription OK")) {
						System.out.println("Je me suis bien inscrit a la battle");
					} else if (message.startsWith("worldstate::")) {
						//Start timer
						long tStart = System.currentTimeMillis();
						Hashtable<Integer,Drapeau> drapeaux = new Hashtable<Integer,Drapeau>();
						// Lire le worldState
						String[] components = message.substring("worldstate::".length()).split(";", -1);
						// Déduire le numéro du tour
						int round = Integer.parseInt(components[0]);
						// Mettre à jour l'état des joueurs
						String PlayerState = components[1];
						String[] PlayerStateComponents = PlayerState.split(":", -1);
						// Si c'est la première lecture, on instancie les objets
						if (round == 1)
						{
							boolean insert = false;
							int cleTableau = 5;
							int cleTableau2 = 0;
							int NumeroJoueur = 0;
							for (String s : PlayerStateComponents) 
							{
								String[] PlayerComponents = s.split(",", -1);
								int teamID = Integer.parseInt(PlayerComponents[0]);
								NumeroJoueur += 1;
								joueurs.put(teamID,new Joueur(teamID,NumeroJoueur));
								
								// Creation de la liste des joueurs qui sera toujours jouée dans le même sens
								// C'est important pour le calcul des immunités. On insere les joueurs à partir de nous
								if ((long)teamID == this.teamId)
								{
									insert = true;
								}
								if (insert)
								{
									OrdreJoueurs2[cleTableau2]=teamID;
									OrdreJoueurs[cleTableau]=teamID;
									cleTableau--;
									cleTableau2++;
								}
							}
							
							// On doit reboucler pour completer la liste
							for (String s : PlayerStateComponents) 
							{
								String[] PlayerComponents = s.split(",", -1);
								int teamID = Integer.parseInt(PlayerComponents[0]);
								if ((long)teamID == this.teamId)
								{
									insert = false;
								}
								if (insert)
								{
									OrdreJoueurs2[cleTableau2]=teamID;
									OrdreJoueurs[cleTableau]=teamID;
									cleTableau--;
									cleTableau2++;
								}
							}

						}
						// On définit le joueur par défaut
						currentPlayer = joueurs.get((int)this.teamId);
						
						// On met à jour les joueurs
						for (String s : PlayerStateComponents) 
						{
							String[] PlayerComponents = s.split(",", -1);
							int posX = Integer.parseInt(PlayerComponents[1]);
							int posY = Integer.parseInt(PlayerComponents[2]);
							int teamID = Integer.parseInt(PlayerComponents[0]);
							int points = Integer.parseInt(PlayerComponents[3]);
							String etat = PlayerComponents[4];
							joueurs.get(teamID).Update(posX,posY,etat, points, round);
						}
						// On met à jour les immunités
						// On a besoin de tenir une liste des attaques ayant été resolues ce tour
						// En effet, on ne peut pas avoir deux personnes qui s'entretuent dans le même tour.
						// On commence par le dernier joueur qui vient de jouer. En cas de doute, 
						// c'est toujours le dernier à avoir joué qui a mis une baffe à l'autre
						ArrayList<String> attaques = new ArrayList<String>();
				        for(Integer key: OrdreJoueurs)
				        {
				        	Joueur attaquant = joueurs.get(key);
				        	int nbAttaques = 0;
				        	ArrayList<Integer> cibles = new ArrayList<Integer>();
							if (attaquant.HasMovedThisTurn())
							{
								for(Integer indexDefenseur: joueurs.keySet())
								{
									Joueur defenseur = joueurs.get(indexDefenseur);
									// Règle safe home désactivée sur serveur de test !!!
									//if (utils.AreAdjascent(attaquant, defenseur) && !defenseur.InBase() && !attaques.contains(indexDefenseur.toString()+key.toString()))	
									if (utils.AreAdjascent(attaquant, defenseur) && !attaques.contains(indexDefenseur.toString()+key.toString()))
									{
										nbAttaques++;
										if  (!defenseur.HasImmunity(attaquant.getTeamID()))
										{
											cibles.add(defenseur.getTeamID());
											defenseur.immunites.add(key);
											attaques.add(key.toString()+indexDefenseur.toString());										}
									}
								}
							}
							if (nbAttaques == 1 && cibles.size() == 1)
							{
								for (Integer teamID : OrdreJoueurs)
								{
									if ((int)cibles.get(0) != teamID)
									{
										joueurs.get(teamID).immunites.remove(key);
									}
								}
							}
							else if (nbAttaques > 1)
							{
								for (Integer teamID : OrdreJoueurs)
								{
									joueurs.get(teamID).immunites.remove(key);
								}
							}
						}
						// Déduire le tableau des drapeaux
						String FlagState = components[2];
						String[] FlagStateComponents = FlagState.split(":", -1);
						int indexDrapeau = 0;
						for (String s : FlagStateComponents) 
						{
							String[] FlagComponents = s.split(",", -1);
							int posX = Integer.parseInt(FlagComponents[0]);
							int posY = Integer.parseInt(FlagComponents[1]);
							indexDrapeau++;
							drapeaux.put(indexDrapeau,new Drapeau(posX,posY));
						}
						
						// Déduire les drapeaux libres et les drapeaux pris
						for (int drapeau : drapeaux.keySet()) 
						{
							for (int joueur : joueurs.keySet())
							{
								if ((drapeaux.get(drapeau).getX() == joueurs.get(joueur).getBaseX()) && (drapeaux.get(drapeau).getY() == joueurs.get(joueur).getBaseY()) )
								{
									
								}
								else if ((drapeaux.get(drapeau).getX() == joueurs.get(joueur).getX()) && (drapeaux.get(drapeau).getY() == joueurs.get(joueur).getY()) )
								{
									joueurs.get(joueur).SetDrapeau(true);
									drapeaux.get(drapeau).SetFree(false);
								}
							}
						}
						
						// Liste des drapeaux libres
						ArrayList<Integer> drapeauxLibres = new ArrayList<Integer>();
						for (int drapeau : drapeaux.keySet()) 
						{
							if (drapeaux.get(drapeau).IsFree())
							{
								drapeauxLibres.add(drapeau);
							}
						}
						System.out.println(OrdreJoueurs2);
						
						//Determiner qui a le plus de chances de prendre les drapeaux libres
						// Pour chaque drapeau, trouver le joueur qui y arrivera en premier
						for (int drapeau : drapeauxLibres) 
						{
							int mincout = 999;
							int joueurPlusProbable = 0;
							for (int joueur : OrdreJoueurs2)
							{
								// cout pour aller chercher le drapeau
								int coutJoueur =  Math.abs(drapeaux.get(drapeau).getX() - joueurs.get(joueur).getX()) + Math.abs(drapeaux.get(drapeau).getY() - joueurs.get(joueur).getY());
								// plus ce qui est déjà dépensé
								coutJoueur += joueurs.get(joueur).champAnalyse;
								if (joueurs.get(joueur).MaybeFlag == true)
								{
									//cout pour aller de la base jusqu'au drapeau
									coutJoueur = Math.abs(drapeaux.get(drapeau).getX() - joueurs.get(joueur).getBaseX()) + Math.abs(drapeaux.get(drapeau).getY() - joueurs.get(joueur).getBaseY());
									// plus ce qui est déjà dépensé
									coutJoueur += joueurs.get(joueur).champAnalyse;
								}
								else if (joueurs.get(joueur).HasFlag())
								{
									// cout pour aller jusqu'à sa base
									coutJoueur = Math.abs(joueurs.get(joueur).getX() - joueurs.get(joueur).getBaseX()) + Math.abs(joueurs.get(joueur).getY() - joueurs.get(joueur).getBaseY());
									// plus cout pour aller de la base jusqu'au drapeau
									coutJoueur += Math.abs(drapeaux.get(drapeau).getX() - joueurs.get(joueur).getBaseX()) + Math.abs(drapeaux.get(drapeau).getY() - joueurs.get(joueur).getBaseY());
									// plus ce qui est déjà dépensé
									coutJoueur += joueurs.get(joueur).champAnalyse;
								}
								if ( coutJoueur < mincout)
								{
									mincout = coutJoueur;
									joueurPlusProbable = joueur;
								}
							}
							// Ajouter à ce joueur les points prévisionnel
							if (joueurs.get(joueurPlusProbable).champAnalyse + mincout + Math.abs(drapeaux.get(drapeau).getX() - joueurs.get(joueurPlusProbable).getBaseX()) + Math.abs(drapeaux.get(drapeau).getY() - joueurs.get(joueurPlusProbable).getBaseY()) <= 51)
							{
								joueurs.get(joueurPlusProbable).pointsPrevisonnels += 31;
							}
							joueurs.get(joueurPlusProbable).champAnalyse += mincout;
							// plus cout pour revenir
							joueurs.get(joueurPlusProbable).champAnalyse += Math.abs(drapeaux.get(drapeau).getX() - joueurs.get(joueurPlusProbable).getBaseX()) + Math.abs(drapeaux.get(drapeau).getY() - joueurs.get(joueurPlusProbable).getBaseY());
							
							joueurs.get(joueurPlusProbable).drapeauxAPrendre.add(drapeau);
							
							joueurs.get(joueurPlusProbable).MaybeFlag = true;
						}
							
						// Regarder qui a le plus de points à prendre. 
						int compteurMeilleurs = 0;
						for (int joueur : OrdreJoueurs2)
						{
							if (currentPlayer.pointsPrevisonnels <= joueurs.get(joueur).pointsPrevisonnels && currentPlayer.champAnalyse > joueurs.get(joueur).champAnalyse)
							{
								compteurMeilleurs++;
							}
						}
						
						String modeJeu = "";
						//Si on fait partie de la première moitiée on va chercher les points
						/*
						if (drapeauxLibres.size() == 0)
						{
							modeJeu = "sauvage";
						}
						*/
						if (currentPlayer.HasFlag() || (compteurMeilleurs <= 2 && currentPlayer.drapeauxAPrendre.size() > 0))
						{
							modeJeu = "defensif";
						}
						// Sinon on agresse ceux qui vont faire le plus de points
						else
						{
							modeJeu = "aggressif";
						}
						
						//On choisit l'objectif
						int xObj = 0;
						int yObj = 0;
						
						// Quel que soit le mode de jeu
						// S'il y a un adversaire avec un drapeau, que l'on a pas de drapeau, que l'on a une immunité vers l'adversaire
						// Et qu'il n'en a pas contre nous
						// et que cet adversaire est à 1 case ou a deux case et qu'il nous reste des jumps on l'agresse
						int adversaireCible = 0;
						if (modeJeu.equals("aggressif"))
						{
							int distanceMin = 999;
							for (int joueur : OrdreJoueurs2)
							{
								boolean test = joueurs.get(joueur).HasFlag();
								// Si il n'a pas l'imunité contre nous
								test = test && !joueurs.get(joueur).HasImmunity(currentPlayer.getTeamID());
								// Si on est plus proche de sa base que lui
								test = test && ((Math.abs(currentPlayer.getX() - joueurs.get(joueur).getBaseX()) + Math.abs(currentPlayer.getY() - joueurs.get(joueur).getBaseY())) <= (Math.abs(joueurs.get(joueur).getX() - joueurs.get(joueur).getBaseX()) + Math.abs(joueurs.get(joueur).getY() - joueurs.get(joueur).getBaseY())));
								if (test)
								{
									System.out.println("IN");
									int distance = Math.abs(currentPlayer.getX() - joueurs.get(joueur).getX()) + Math.abs(currentPlayer.getY() - joueurs.get(joueur).getY());
									//Trouver le plus proche de nous
									if (distance < distanceMin && distance > 0)
									{
										distanceMin = distance;
										adversaireCible = joueur;
										xObj = joueurs.get(joueur).getBaseX();
										yObj = joueurs.get(joueur).getBaseY();
									}
								}
							}
							// Si aucun n'adversiaire ne convient, on repasse en mode defensif
							if (adversaireCible == 0)
							{
								modeJeu = "defensif";
							}
						}
						if (modeJeu.equals("defensif"))
						{
							
							// Pour le moment : l'objectif est le drapeau le plus proche si on n'a pas de drapeau
							if (!currentPlayer.HasFlag() && drapeauxLibres.size() > 0)
							{
								int minValue = 999;
								int drapeauChoisi = 1;
								for (Integer drapeau : drapeauxLibres)
								{
									int distance = Math.abs(drapeaux.get(drapeau).getX() - currentPlayer.getX()) + Math.abs(drapeaux.get(drapeau).getY() - currentPlayer.getY());
									if ( distance < minValue )
									{
										minValue = distance;
										drapeauChoisi = drapeau;
									}
								}
								
								xObj = drapeaux.get(drapeauChoisi).getX();
								yObj = drapeaux.get(drapeauChoisi).getY();
							}
							// Si on a un drapeau, c'est sa base
							else
							{
								xObj = currentPlayer.getBaseX();
								yObj = currentPlayer.getBaseY();
							}
						}
						
						// On joue
						String order = "";
						ArrayList<Client.Dir> PossibleDirections = utils.MouvementPossibles(currentPlayer, joueurs);
						
						if (modeJeu.equals("agressif") && adversaireCible != 0)
						{
							Joueur adversaire = joueurs.get(adversaireCible);
							//On se met entre l'adversaire et sa base
							//Si l'adversaire et sa base sont en haut, on monte
							if (PossibleDirections.contains(Dir.NORD) && adversaire.getBaseY() < currentPlayer.getY() && adversaire.getY() < currentPlayer.getY())
							{
								order = Dir.NORD.code;
							}
							//Si l'adversaire et sa base sont en bas, on descend
							else if (PossibleDirections.contains(Dir.SUD) && adversaire.getBaseY() > currentPlayer.getY() && adversaire.getY() > currentPlayer.getY())
							{
								order = Dir.SUD.code;
							}
							//Si l'adversaire et sa base sont à droite, on va à droite
							else if (PossibleDirections.contains(Dir.EST) && adversaire.getBaseX() > currentPlayer.getX() && adversaire.getX() > currentPlayer.getX())
							{
								order = Dir.EST.code;
							}
							//Si l'adversaire et sa base sont à gauche, on va à gauche
							else if (PossibleDirections.contains(Dir.OUEST) && adversaire.getBaseX() < currentPlayer.getX() && adversaire.getX() < currentPlayer.getX())
							{
								order = Dir.OUEST.code;
							}
							else if ( adversaire.getBaseX() == currentPlayer.getX() && adversaire.getBaseY() == currentPlayer.getY())
							{
								xObj = adversaire.getX();
								yObj = adversaire.getY();
							}
						}
						if (order.equals(""))
						{						
							if (PossibleDirections.contains(Dir.EST) && xObj > currentPlayer.getX())
							{
								order = Dir.EST.code;
							}
							else if (PossibleDirections.contains(Dir.OUEST) && xObj < currentPlayer.getX())
							{
								order = Dir.OUEST.code;
							}
							else if (PossibleDirections.contains(Dir.NORD) && yObj < currentPlayer.getY())
							{
								order = Dir.NORD.code;
							}
							else if (PossibleDirections.contains(Dir.SUD) && yObj > currentPlayer.getY())
							{
								order = Dir.SUD.code;
							}
							else
							{
								//Essayer de retirer tous les sauts. S'il reste au moins une direction on la garde
								int index = rand.nextInt(PossibleDirections.size());
								order = PossibleDirections.get(index).code;
							}
						}
						//Si on n'a pas le temps de recupérer un objectif on fonce dans le tas
						
						//On affiche l'état du monde
						for(Integer key: OrdreJoueurs)
						{
							System.out.println(round+" "+this.teamId+" "+modeJeu+" "+order+" "+joueurs.get(key).Afficher());
						}
						
						// On envoie l'ordre au serveur
						String action = secret + "%%action::" + teamId + ";" + gameId + ";" + round + ";"
								+ order;
						//System.out.println(action);
						long tEnd = System.currentTimeMillis();
						long tDelta = tEnd - tStart;
						System.out.println("Ellapsed time : "+tDelta);
						out.println(action);
						out.flush();
					} else if (message.equalsIgnoreCase("Inscription KO")) {
						System.out.println("inscription KO");
					} else if (message.equalsIgnoreCase("game over")) {
						System.out.println("game over");
						System.exit(0);
					} else if (message.equalsIgnoreCase("action OK")) {
						//System.out.println("Action bien pris en compte");
					}
				}
				//System.out.println("Pour voir la partie : http://" + ipServer + ":8080/?gameId=" + gameId);
			} while (message != null);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					// Tant pis
				}
			}

		}
	}

	public Dir computeDirection() {
		return Dir.values()[rand.nextInt(Dir.values().length)];
	}

}
