public class Drapeau {
	 private int xPosition;
	 private int yPosition;
	 private boolean libre;
	 
	 public int getX(){
	     return this.xPosition;
	 }
	 
	 public int getY(){
	     return this.yPosition;
	 }
	 
	 public boolean IsFree(){
	     return this.libre;
	 }
	 
	 public Drapeau(int X, int Y)
	 {     
		 this.xPosition = X;
		 this.yPosition = Y;
		 this.libre = true;
	 } 
}
