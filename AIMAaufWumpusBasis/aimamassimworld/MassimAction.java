package massim.javaagents.aimamassimworld;

public enum MassimAction {
	
    WEST("West"), NORTH("North"), EAST("East"), SOUTH("South"), NOOP("No_op");

    public String getSymbol() {
        return symbol;
    }
    
    private String symbol;
    
    MassimAction(String sym) {
        symbol = sym;
    }

}
