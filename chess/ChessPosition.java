package chess;

import boardgame.Position;

public class ChessPosition {
	
    //Posições do tabuleiro
	private char column;
	private int row;
	
	//Regrinha para quando as posições não forem as mesma que a do xadrez
	public ChessPosition(char column, int row) {
		if (column < 'a' || column > 'h' || row < 1 || row > 8) {
			throw new ChessException("Error instantiating ChessPosition. Valid values are from a1 to h8.");
		}
		this.column = column;
		this.row = row;
	}

	public char getColumn() {
		return column;
	}

	public int getRow() {
		return row;
	}

	//Convertendo a posição do xadrez para a posição do tabuleiro
	protected Position toPosition() {
		return new Position(8 - row, column - 'a');
	}
	
	//Convertendo a posição do Tabuleiro para a posição do Xadrez
	protected static ChessPosition fromPosition(Position position) {
		return new ChessPosition((char)('a' + position.getColumn()), 8 - position.getRow());
	}
	
	//Para mostar as posições do xadrez certinho
	@Override
	public String toString() {
		return "" + column + row;
	}
}
