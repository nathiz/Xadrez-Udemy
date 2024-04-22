package boardgame;

public abstract class Piece {
    
    protected Position position;
	private Board board;
	
	public Piece(Board board) {
		this.board = board;
		position = null;
	}

	protected Board getBoard() {
		return board;
	}
	
    //Movimentos Possiveis
	public abstract boolean[][] possibleMoves();
	
    //Verifica se o movimento é possivel
	public boolean possibleMove(Position position) {
		return possibleMoves()[position.getRow()][position.getColumn()];
	}
	
    //Se a peça não ta presa e tem algum movimento
	public boolean isThereAnyPossibleMove() {
        //Matriz de movimentos possiveis
		boolean[][] mat = possibleMoves();
        //Verificar se a algumaposição verdadeira
		for (int i=0; i<mat.length; i++) {
			for (int j=0; j<mat.length; j++) {
				if (mat[i][j]) {
					return true;
				}
			}
		}
		return false;
	}
}