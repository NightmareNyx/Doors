
public class Move {
	

	private int[][] move; //-->apo8ikeuoume tn 8esi apo tn opoia pirame to pouli i (move[i][0])
						//-->kai tin 8esi stn opoia to pigame (move[i][1])
	//--> 8ewrw oti oi kiniseis pragmatopoiountai diadoxika kai pws mporw na kanw to poli 4 kiniseis (4 einai sts diples)
	private int score;
	
	public Move()
	{
		move = new int[4][4];
		for(int i=0; i<move.length; i++)
			for(int j=0; j<move[0].length; j++) move[i][j] = -1;
		score = 0; //prepei na skeftw ena invalide noumero gia edw
	}
	
	public Move(int[][] moves)
	{
		move = new int[4][4];
		setMove(moves);
		score = 0;
	}
	
	public Move(int value)
	{
		move = new int[4][4];
		for(int i=0; i<move.length; i++)
			for(int j=0; j <move[0].length ; j++) 
				move[i][j] = -1;
		score = value;
	} 
	
	public Move(int[][] moves, int value)
	{
		move = new int[4][4];
		setMove(moves);
		score = value;
	}
	
	public int[][] getMove(){ return move; } 
	
	public int getScore(){ return score; }
	
	public void setMove(int[][] moves)
	{
		for(int i=0; i<move.length; i++){
			for(int j=0;  j < move[0].length; j++)
				move[i][j] = moves[i][j];
		}
	}
	
	public void setScore(int value)
	{
		score = value;
	}

}