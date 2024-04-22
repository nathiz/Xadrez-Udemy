package boardgame;

public class Board {

    private int rows;
	private int columns;
	private Piece[][] pieces;
	
	public Board(int rows, int columns) {
		if (rows < 1 || columns < 1) {
			throw new BoardException("Error creating board: there must be at least 1 row and 1 column");
		}
		this.rows = rows;
		this.columns = columns;
		pieces = new Piece[rows][columns];
	}

	public int getRows() {
		return rows;
	}

	public int getColumns() {
		return columns;
	}
	
	//Recebe as coordenadas de uma posição e retorna qual peça está nela
	public Piece piece(int row, int column) {
		if (!positionExists(row, column)) {
			throw new BoardException("Position not on the board");
		}
		return pieces[row][column];
	}
	
	//Recebe informações sobre a posição do tabuleiro retornando qual peça tem
	public Piece piece(Position position) {
		if (!positionExists(position)) {
			throw new BoardException("Position not on the board");
		}
		return pieces[position.getRow()][position.getColumn()];
	}
	
	//Recebe uma peça e uma posição, atribui uma peça a uma posição
	public void placePiece(Piece piece, Position position) {
		if (thereIsAPiece(position)) {
			throw new BoardException("There is already a piece on position " + position);
		}
		pieces[position.getRow()][position.getColumn()] = piece;
		piece.position = position;
	}
	
	//Para remover uma peça primeiro verifica a posição
	public Piece removePiece(Position position) {
		if (!positionExists(position)) {
			throw new BoardException("Position not on the board");
		}
		//Se a peça for nula não altera nada
		if (piece(position) == null) {
			return null;
		}
		//aux recebe a peça, e transforma em nula retirando do tabuleiro
		Piece aux = piece(position);
		aux.position = null;
		//Atribui a posição nula
		pieces[position.getRow()][position.getColumn()] = null;
		return aux;
	}
	

	//Recebe uma posição e verifica se ela existe por linha e coluna
	private boolean positionExists(int row, int column) {
		return row >= 0 && row < rows && column >= 0 && column < columns;
	}
	//Recebe uma posição e verifica se ela existe
	public boolean positionExists(Position position) {
		return positionExists(position.getRow(), position.getColumn());
	}

	//Verifica se tem peça na posição indicada e e tiver retorna qual peça
	public boolean thereIsAPiece(Position position) {
		if (!positionExists(position)) {
			throw new BoardException("Position not on the board");
		}
		return piece(position) != null;
	}
}