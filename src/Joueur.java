import java.util.ArrayList;
import java.util.Arrays;

public class Joueur {
	
	// Attributs
	 private int teamID;
	 private int nbSauts;
	 private int xPosition;
	 private int yPosition;
	 private int xBasePosition;
	 private int yBasePosition;
	 private int points;
	 private boolean inactif;
	 private boolean porteUnDrapeau;
	 public ArrayList<Integer> immunites;
	 private String status;
	 private boolean MouvementCeTour;
	 public int pointsPrevisonnels;
	 public int champAnalyse;
	 public ArrayList<Integer> drapeauxAPrendre;
	 public boolean MaybeFlag;
	 
	 //Accesseurs
	 public int getNbSautsRestants()
	 {
		return  this.nbSauts;
	 }
	 
	 public int getX(){
	     return this.xPosition;
	 }
	 
	 public int getY(){
	     return this.yPosition;
	 }
	 
	 public boolean IsStunned(){
	     return this.status.equals("stunned");
	 }
	 
	 public int getTeamID()
	 {
		return  this.teamID;
	 }
	 
	 public int getBaseX(){
	     return this.xBasePosition;
	 }
	 
	 public int getBaseY(){
	     return this.yBasePosition;
	 }
	 
	 public int getPoints(){
	     return this.points;
	 }
	 
	 public boolean IsInactive(){
	     return this.inactif;
	 }
	 
	 public boolean HasFlag(){
	     return this.porteUnDrapeau;
	 }
	 
	 public boolean HasMovedThisTurn(){
		 return this.MouvementCeTour;
	 }
	 
	 public boolean HasImmunity(int ennemi)
	 {
		 return immunites.contains(ennemi);
	 }
	 
	 public void SetDrapeau(boolean bool)
	 {
		 this.porteUnDrapeau = bool;
	 }
	 
	 public boolean InBase(){
		 return (this.xBasePosition == this.xPosition) && (this.yBasePosition == this.yPosition);
	 }
	 
	 //Constructeur
	 public Joueur(int pTeamID, int pNumeroJoueur)
	 {     
		 this.nbSauts = 3;
		 this.xPosition = 1;
		 this.yPosition = 2 * pNumeroJoueur - 1;
		 this.status = "playing";
		 this.teamID = pTeamID;
		 this.xBasePosition = 1;
		 this.yBasePosition = 2 * pNumeroJoueur - 1;
		 this.inactif = true;
		 this.porteUnDrapeau = false;
		 this.MouvementCeTour = false;
		 this.immunites = new ArrayList<Integer>();
		 this.pointsPrevisonnels = 0;
		 this.champAnalyse = 1;
		 this.drapeauxAPrendre = new ArrayList<Integer>();
		 this.MaybeFlag = false;
	 } 
	 
	 //Mise à jour
	 public void Update( int pNewX, int pNewY, String pEtat, int pPoints, int pRound)
	 {
		 if (Math.abs(this.xPosition - pNewX) + Math.abs(this.yPosition - pNewY) == 2)
		 {
			this.nbSauts -= 1;
		 }
		 if (Math.abs(this.xPosition - pNewX) + Math.abs(this.yPosition - pNewY) >= 1)
		 {
			this.inactif = false;
			this.MouvementCeTour = true;
		 }
		 else
		 {
			 this.MouvementCeTour = false;
		 }
		 
		 this.xPosition = pNewX;
		 this.yPosition = pNewY;
		 this.status = pEtat;
		 if (pEtat.equals("stunned") || (this.xPosition == this.xBasePosition && this.yPosition == this.yBasePosition) )
		 {
			 this.porteUnDrapeau = false;
		 }
		 this.points = pPoints;
		 this.pointsPrevisonnels = pPoints;
		 this.champAnalyse = pRound;
		 this.drapeauxAPrendre = new ArrayList<Integer>();
		 this.MaybeFlag = false;
	 }
	 
	 //Affichage
	 public String Afficher()
	 {
		 //return "Le joueur	" + this.teamID + "	est en (	" + this.xPosition + "	,	" + this.yPosition + "	), a	" + this.points + "	points,	" + this.nbSauts + "	sauts restants, est	" + this.status + "	, a une immu contre	" + Arrays.toString(immunites.toArray()) + "	et sa base est en (" + this.getBaseX() + ", " + this.getBaseY() + ").";
		 return "Le joueur " + this.teamID + " est en (" + this.xPosition + ", " + this.yPosition + "), a " + this.points + " points, a un drapeau : " + this.porteUnDrapeau + ", a " + this.nbSauts + " sauts restants, est " + this.status + ", a une immu contre " + Arrays.toString(immunites.toArray()) + " et sa base est en (" + this.getBaseX() + ", " + this.getBaseY() + "), a "+this.pointsPrevisonnels+" Points previ, "+this.champAnalyse+" champAnalyse et "+this.drapeauxAPrendre +" drapeaux à prendre ";
	 }
}
