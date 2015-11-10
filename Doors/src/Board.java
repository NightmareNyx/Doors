import java.util.ArrayList;
import java.util.Stack;

/**
 * Backgammon board class.
 *
 * **HashMap-like implementation for speed.
 * **Could be done more easily by having a "position" field in each piece but would hurt the speed.
 * ------> oxi den 8a itan pio eukolo giati 8a diskoleue i anazitisi tou "posa poulia einai se mia 8esi"
 * ------> outws i allws ta poulia dn exoun "noumero" opote dn se noiazei i 8esi enos sigkekrimenou pouliou
 * ------> alla ta poulia se ka8e 8esi
 * For more extensibility we could create a class for the data structure and not just use ArrayList<Stack<Piece>>.
 * It looks kinda ugly.
 * ------> nomizw enas pinakas einai arketos kai grigoros gia tis leitourgies pou 8eloume...xwro den pianei poli...
 * ------> parola auta auto voleuei gia to grafiko kommati.....as skeftoume kai an ginetai alliws gia na diaxwrisoume
 * ------> ilopoiisi me grafiki diepafi
 */

////------------->>> THA PARATIRISETE POS XRISIMOPOIW PANTOU ENA n (-1 'h 1) // AFTO GINETAI GIA NA DIEFKOLINTHOUN OI PRAKSEIS AFKSOMIOSIS
//////////////////// STO TABLE
////////////----> H DIEFKOLINSI THA ITAN NA FTIAXNAME KLASI Player KAI NA HTAN Player[] table OSTE NA MIN KATHOMASTE NA SKEFTOMASTE OLI TIN ORA AYTO
///////// DEN KSERO VEVAIA AN THA VOLEYE PANTOU

public class Board {
	
	private static final int POS_NUM_0 = 0;
	private static final int POS_NUM_5 = 5;
	private static final int POS_NUM_7 = 7;
	private static final int POS_NUM_11 = 11;
	private static final int POS_NUM_16 = 16;
	private static final int POS_NUM_12 = 12;
	private static final int POS_NUM_18 = 18;
	private static final int POS_NUM_23 = 23;
	
	private static final int RED_START_POS = 0;
	private static final int GREEN_START_POS = 23;
	
	private static final int PIECE_TOTAL_NUM = 15;
	
    private int[] table; //a list of stacks of Pieces
    // list->positions of board, stack->pieces in each position
    
   // Checkers on bar (that have been hit).  eaten[0] is green player, eaten[1] is red player.
    private int[] eaten;
    
    private int pos; //starting position
    private int move; //target position
    private Player player;
    
    Dice dice;

    public Board(){
        table = new int[24];
        initBoard();
        eaten = new int[2];
        player = Player.NONE;
        dice = new Dice();
    }
    
    public Board(Player player){
    	 table = new int[24];
         initBoard();
         eaten = new int[2];
         this.player = player;
         dice = new Dice();
    }
	
	public Board(Board board){
		table = new int[24];
		setTable(board.getTable());
		eaten = new int[2];
		player = board.getPlayer();
		dice = new Dice();
	}
	
	public void setTable(int[] t){ 
		try {
		  System.arraycopy(t, 0, table, 0, t.length);
		}
		catch(Exception ex){
			ex.getMessage();
		}
	}
	
	public Player getPlayer(){
		return player;
	}
	
	public void setPlayer(Player player){
		this.player = player;
	}
	
	public int[] getTable() { return table; }
	
	//if returned list is empty, no valid moves can be done
	public ArrayList<Board> getChildren(Dice dice, Player player){
		//--->DEN EXEI AKOMA GIA DIPLES GIATI GAMIOUNTAI!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		ArrayList<Board> children = new ArrayList<Board>();
		byte move[] = dice.getValues();
		int pN = (player == Player.RED)? RED_START_POS : GREEN_START_POS;
		
		_getChildren(move, children, pN, player);
		return children;
		
	}
	
	//pN-> 0 when reds play, 23 when greens play
	// ---> isws fainetai pio mperdemeno, alla toulaxiston den yparxei idios kwdikas, whatever
	private void _getChildren(byte[] move, ArrayList<Board> children, int pN, Player player){
		
		Board child;
		int n = player.getSign();
		int fMove = pN + n; //fMove -> first move
		int sMove = pN + n; //sMove -> first move
		
		//first move
		for(int i = 0; i < 24 - move[0]; ++i){ //not taking into account the children states where moves lead out of board
											//at this moment
			fMove -= n; //increasing when reds, decreasing when greens
			
			if(!isValidMove(fMove, move[0], player)) continue;
			
			//second move
			for(int j = 0; j < 24 - move[0]; ++j){ //not taking into account the children states where moves lead out of board
												//at this moment
				sMove -= n; //increasing when reds, decreasing when greens
				if(!isValidMove(sMove, move[1], player)) continue;
			
				if(sMove != fMove){
					child = new Board(this); //clone this state
					child.move(fMove, move[0], n); 
					child.move(sMove, move[1], n);
					children.add(child);
				} else { //same piece
					if(isValidMove(fMove, move[0]+move[1], player)){
						child = new Board(this);
						child.move(fMove, move[0]+move[1], n);
						children.add(child);
					}
				}
			}

		}
	}
	
	/**
	 * Initializes the board for a Doors game
	 */
	protected void initBoard(){
		//initialization of a doors game
        //RED: 0->23 -integer
        //GREEN: 23->0 +integer
		table[POS_NUM_0] = -2;
		table[POS_NUM_5] = 5;
		table[POS_NUM_7] = 3;
		table[POS_NUM_11] = -5;
		table[POS_NUM_12] = 5;
		table[POS_NUM_16] = -3;
		table[POS_NUM_18] = -5;
		table[POS_NUM_23] = 2;
	}
	
	/** Checks if the move is legal
	 * @param pos current position of the piece to move
	 * @param move step the piece has to move
	 * @param n n = -1 for RED, n = 1 for GREEN
	 * @return true is move is legal
	 */
	protected boolean isValidMove(int pos, int move, Player player){
		if(player == null) player = this.player;
		if(piecesOnBar(player) > 0) return false;
		
		byte n = player.getSign();
		int signp = (int) Math.signum(table[pos]);
		
		if((!isValidPick(pos, player)) || signp != 0) return false;
		//this position does not contain any of the player's pieces
		
		if(!checkDirection(pos, move, player)) return false;
		
		int signm = (int) Math.signum(table[pos+move]);
		if(signm == n || table[pos+move]+n == 0 || table[pos+move] == 0)
			return true; //this position either contains player's pieces, either is empty, or it contains just one of opponent's pieces
		return false;
	}
	
	protected int piecesOnBar(Player player){
		
		if (player == Player.GREEN && eaten[0] > 0)
			return eaten[0];
		else if (player == Player.RED && eaten[1] > 0)
			return eaten[1];
		else return 0;
	}
	
	/**
	 * Checks if you picked a correct piece
	 */
	public boolean isValidPick(int pos, Player player){
		if (player == null) player = this.player;
		if(Math.signum(table[pos]) == player.getSign()){
			//setStatus("Got your piece, mate!");
			return true;
		} else {
			//setStatus("Wrong color, bro!");
			return false;
		}
	}
	
	/** Performs the move
	 * @param pos current position of the piece to move
	 * @param move step the piece has to move
	 * @param n n = -1 for RED, n = 1 for GREEN
	 */
	protected void move(int pos, int move, int n){
		//validity of the move is already checked
		table[pos] -= n; //decrease the absolute value
		
		int prev = table[pos+move];
		table[pos+move] += n;
		//move done
		//izzy pizzy
		if(Math.signum(prev) < Math.signum(table[pos+move])){
			if (prev < 0) eaten[1]++;
			else if (prev > 0) eaten[0]++;
		}
	}
	
	public int getNumberOfPiecesAt(int pos){
		return Math.abs(table[pos]);
	}
	
	public boolean isValidTarget(int moveTarget, Player player){
		if(player == null) player = this.player;
		int n = player.getSign();
		int signm = (int) Math.signum(table[moveTarget]);
		if(signm == n || table[moveTarget]+n == 0 || table[moveTarget] == 0)
			return true;
		return false;
	}
	
	/**
	 * Check if the moving direction is correct
	 */
	protected boolean checkDirection(int pos, int move, Player player) {
		if ((player == Player.RED && move <= pos) || (player == Player.GREEN && move >= pos)) {
			//setStatus("You're going in the wrong direction!");
			return false;
		}
		return true;
	}

    /**
     * Checks if there is a red door in that position.
     */
    public boolean isRedDoor(short position){ //--> xreiazetai elegxo an einai entos oriwn t pinaka
    										//--------> apla ti tha petaei tote? false? kalitera na mas vgazei outofbounds
        return table[position] < -1; 
    }
    
    /**
     * Checks if there is a green door in that position.
     */
    public boolean isGreenDoor(short position){
        return table[position] > 1;
    }
    
    public Player colorAt(int pos){
    	if(table[pos] < 0) return Player.RED;
    	else if (table[pos] > 0) return Player.GREEN;
    	else return Player.NONE;
    }
	
    /**
     * @return true if Red has reached the final destination on the board
     */
    public boolean hasRedReachedDestination(){
    	short reds = 0;
		for(int i = 0; i < 6; ++i){
			if(table[i] < 0)
				reds += table[i];
		}
		return reds == -15;
    }

    /**
     * @return true if Green has reached the final destination on the board
     */
    public boolean hasGreenReachedDestination(){
        short greens = 0;
        for(int i = 18; i < 24; ++i){
        	if(table[i] > 0)
				greens += table[i];
        }
        return greens == 15;
    }
    
    public int getGreensEaten(){
    	return eaten[0];
    }
    
    public int getRedsEaten(){
    	return eaten[1];
    }
    
    public int getRedsLeft(){
    	return PIECE_TOTAL_NUM - eaten[0];
    }
    
    public int getGreensLeft(){
    	return PIECE_TOTAL_NUM - eaten[1];
    }
    
    public byte[] rollDice(){
    	return dice.roll();
    }
    
    public byte[] getDice(){
    	return dice.getValues();
    }
    
    public byte[] getDiceMoveset(Dice dice){
    	
    	byte[] moves = new byte[4];
    	moves[0] = dice.getValues()[0];
    	moves[1] = dice.getValues()[1];
    	
    	if(dice.isDouble()){
    		moves[2] = moves[0];
    		moves[3] = moves[0];
    	}
    	
    	return moves;
    }
    
    public byte[] getDiceMoveset(){
    	Dice dice = new Dice();
    	dice.roll();
    	return getDiceMoveset(dice);
    }

}
