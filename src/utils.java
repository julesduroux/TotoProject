import java.util.ArrayList;
import java.util.Hashtable;

public final class utils {
	// Empeche d'instancer
    private utils() {  }
    
    public static boolean AreAdjascent(Joueur j1, Joueur j2)
    {
		return ((j1.getX() - j2.getX() == 0) && Math.abs(j1.getY() - j2.getY()) == 1) || ((j1.getY() - j2.getY() == 0) && Math.abs(j1.getX() - j2.getX()) == 1);
    }
    
    public static boolean AreAdjascent(Joueur j, Drapeau d)
    {
		return ((j.getX() - d.getX() == 0) && Math.abs(j.getY() - d.getY()) == 1) || ((j.getY() - d.getY() == 0) && Math.abs(j.getX() - d.getX()) == 1);
    }
    
    public static ArrayList<Client.Dir> getMovesToAttack(Joueur a, Joueur d)
    {
    	ArrayList<Client.Dir> directionsPossibles = new ArrayList<Client.Dir>();
    	// Si les joueurs sont sur la même colonne
    	if (a.getX() == d.getX())
    	{
    		if (a.getY() + 1  == d.getY())
    		{
    			directionsPossibles.add(Client.Dir.JUMP_SUD);
    		}
    		if (a.getY() - 1  == d.getY())
    		{
    			directionsPossibles.add(Client.Dir.JUMP_NORD);
    		}
    		if (a.getY() + 2  == d.getY())
    		{
    			directionsPossibles.add(Client.Dir.SUD);
    		}
    		if (a.getY() - 2  == d.getY())
    		{
    			directionsPossibles.add(Client.Dir.NORD);
    		}
    		if (a.getY() + 3  == d.getY())
    		{
    			directionsPossibles.add(Client.Dir.JUMP_SUD);
    		}
    		if (a.getY() - 3  == d.getY())
    		{
    			directionsPossibles.add(Client.Dir.JUMP_NORD);
    		}
    	}
    	// Si les joueurs sont sur la même ligne
    	if (a.getY() == d.getY())
    	{
    		if (a.getX() + 1  == d.getX())
    		{
    			directionsPossibles.add(Client.Dir.JUMP_EST);
    		}
    		if (a.getX() - 1  == d.getX())
    		{
    			directionsPossibles.add(Client.Dir.JUMP_OUEST);
    		}
    		if (a.getX() + 2  == d.getX())
    		{
    			directionsPossibles.add(Client.Dir.EST);
    		}
    		if (a.getX() - 2  == d.getX())
    		{
    			directionsPossibles.add(Client.Dir.OUEST);
    		}
    		if (a.getX() + 3  == d.getX())
    		{
    			directionsPossibles.add(Client.Dir.JUMP_EST);
    		}
    		if (a.getX() - 3  == d.getX())
    		{
    			directionsPossibles.add(Client.Dir.JUMP_OUEST);
    		}
    	}
    	// Si les joueurs sont en diagonale
    	if ((a.getX() - 1 == d.getX() ) && (a.getY() - 1 == d.getY()))
		{
    		directionsPossibles.add(Client.Dir.NORD);
    		directionsPossibles.add(Client.Dir.OUEST);
		}
    	if ((a.getX() + 1 == d.getX() ) && (a.getY() + 1 == d.getY()))
		{
    		directionsPossibles.add(Client.Dir.SUD);
    		directionsPossibles.add(Client.Dir.EST);
		}
    	if ((a.getX() + 1 == d.getX() ) && (a.getY() - 1 == d.getY()))
		{
    		directionsPossibles.add(Client.Dir.NORD);
    		directionsPossibles.add(Client.Dir.EST);
		}
    	if ((a.getX() - 1 == d.getX() ) && (a.getY() + 1 == d.getY()))
		{
    		directionsPossibles.add(Client.Dir.SUD);
    		directionsPossibles.add(Client.Dir.OUEST);
		}
    	// Si les joueurs sont en position de cavalier
    	if ((a.getX() - 2 == d.getX() ) && (a.getY() - 1 == d.getY()))
		{
    		directionsPossibles.add(Client.Dir.JUMP_OUEST);
		}
    	if ((a.getX() + 2 == d.getX() ) && (a.getY() + 1 == d.getY()))
		{
    		directionsPossibles.add(Client.Dir.JUMP_EST);
		}
    	if ((a.getX() + 1 == d.getX() ) && (a.getY() - 2 == d.getY()))
		{
    		directionsPossibles.add(Client.Dir.JUMP_NORD);
		}
    	if ((a.getX() - 1 == d.getX() ) && (a.getY() + 2 == d.getY()))
		{
    		directionsPossibles.add(Client.Dir.JUMP_SUD);
		}
    	
		return directionsPossibles;
    }
    
    public static ArrayList<Client.Dir> MouvementPossibles(Joueur j, Hashtable<Integer,Joueur> joueurs)
    {
    	ArrayList<Client.Dir> directionsPossibles = new ArrayList<Client.Dir>();
		 
    	//On commence par ajouter les directions possible en fonction de la taille du plateau
		 if (j.getX() < 15)
		 {
			 directionsPossibles.add(Client.Dir.EST);
		 }
		 if (j.getX() < 14 && j.getNbSautsRestants() > 0)
		 {
			 directionsPossibles.add(Client.Dir.JUMP_EST);
		 }
		 if (j.getY() > 0)
		 {			 
			 directionsPossibles.add(Client.Dir.NORD);
		 }
		 if (j.getY() > 1 && j.getNbSautsRestants() > 0)
		 {	
			 directionsPossibles.add(Client.Dir.JUMP_NORD);
		 }
		 if (j.getX() > 0)
		 {	
			 directionsPossibles.add(Client.Dir.OUEST);
		 }
		 if (j.getX() > 1 && j.getNbSautsRestants() > 0)
		 {	
			 directionsPossibles.add(Client.Dir.JUMP_OUEST);
		 }
		 if (j.getY() < 12)
		 {	
			 directionsPossibles.add(Client.Dir.SUD);
		 }
		 if (j.getY() < 11 && j.getNbSautsRestants() > 0)
		 {	
			 directionsPossibles.add(Client.Dir.JUMP_SUD);
		 }
		 
		// On continue en retirant des possibilitées si on tombe sur un joueur
	    	for (Integer key : joueurs.keySet())
	    	{
	    		if ((joueurs.get(key).getX() - j.getX() == 0) && (joueurs.get(key).getY() - j.getY() == 1))
	    		{
	    			directionsPossibles.remove(Client.Dir.SUD);
	    		}	    		
	    		if ((joueurs.get(key).getX() - j.getX() == 0) && (joueurs.get(key).getY() - j.getY() == -1))
	    		{
	    			directionsPossibles.remove(Client.Dir.NORD);
	    		}
	    		if ((joueurs.get(key).getX() - j.getX() == 1) && (joueurs.get(key).getY() - j.getY() == 0))
	    		{
	    			directionsPossibles.remove(Client.Dir.EST);
	    		}
	    		if ((joueurs.get(key).getX() - j.getX() == -1) && (joueurs.get(key).getY() - j.getY() == 0))
	    		{
	    			directionsPossibles.remove(Client.Dir.OUEST);
	    		}
	    		if (j.getNbSautsRestants() > 0)
	    		{
	    			if ((joueurs.get(key).getX() - j.getX() == 0) && (joueurs.get(key).getY() - j.getY() == 2))
		    		{
		    			directionsPossibles.remove(Client.Dir.JUMP_SUD);
		    		}
	    			if ((joueurs.get(key).getX() - j.getX() == 0) && (joueurs.get(key).getY() - j.getY() == -2))
		    		{
		    			directionsPossibles.remove(Client.Dir.JUMP_NORD);
		    		}
	    			if ((joueurs.get(key).getX() - j.getX() == -2) && (joueurs.get(key).getY() - j.getY() == 0))
		    		{
		    			directionsPossibles.remove(Client.Dir.JUMP_OUEST);
		    		}
		    		if ((joueurs.get(key).getX() - j.getX() == 2) && (joueurs.get(key).getY() - j.getY() == 0))
		    		{
		    			directionsPossibles.remove(Client.Dir.JUMP_EST);
		    		}
	    		}
	    	}
	    	
		 return directionsPossibles;
    }
}