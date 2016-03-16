public final class utils {
	// Empeche d'instancer
    private utils() {  }
    
    public static boolean AreAdjascent(Joueur j1, Joueur j2)
    {
		return ((j1.getX() - j2.getX() == 0) && Math.abs(j1.getY() - j2.getY()) == 1) || ((j1.getY() - j2.getY() == 0) && Math.abs(j1.getX() - j2.getX()) == 1);
    }
}