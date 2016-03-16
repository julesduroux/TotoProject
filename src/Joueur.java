
public class Joueur {
	
	// Attributs
	 private int teamID;
	 private int nbSauts;
	 private int xPosition;
	 private int yPosition;
	 private int xBasePosition;
	 private int yBasePosition;
	 private int points;
	 private String status;

	 
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
	 
	 public String getStatus(){
	     return this.status;
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
	 } 
	 
	 //Mise à jour
	 public void Update( int pNewX, int pNewY, String pEtat, int pPoints)
	 {
		 if (Math.abs(this.xPosition - pNewX) + Math.abs(this.yPosition - pNewY) == 2)
		 {
			this.nbSauts -= 1; 
		 }
		 this.xPosition = pNewX;
		 this.yPosition = pNewY;
		 this.status = pEtat;
		 this.points = pPoints;
	 }
	 
	 //Affichage
	 public String Afficher()
	 {
		 return "Le joueur " + this.teamID + " est en position (" + this.xPosition + ", " + this.yPosition + "), a  " + this.points + " points, dispose de " + this.nbSauts + " sauts restants, est à l'état " + this.status + " et sa base est en (" + this.getBaseX() + ", " + this.getBaseY() + ").";
	 }
}
