package chess;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.King;
import chess.pieces.Rook;

//Regras do jogo
public class ChessMatch {

	//Turno do jogador
	private int turn;
	//Jogador atual
	private Color currentPlayer;
	private Board board;

	private boolean check;
	private boolean checkMate;
	/*
	private ChessPiece enPassantVulnerable;
	private ChessPiece promoted;
	*/

	private List<Piece> piecesOnTheBoard = new ArrayList<>();
	private List<Piece> capturedPieces = new ArrayList<>();
	
	public ChessMatch() {
		board = new Board(8, 8);
		turn = 1;
		currentPlayer = Color.WHITE;
		initialSetup();
	}
	
	public int getTurn() {
		return turn;
	}
	
	public Color getCurrentPlayer() {
		return currentPlayer;
	}
	
	public boolean getCheck() {
		return check;
	}
	
	public boolean getCheckMate() {
		return checkMate;
	}
	/*
	public ChessPiece getEnPassantVulnerable() {
		return enPassantVulnerable;
	}
	
	public ChessPiece getPromoted() {
		return promoted;
	}*/
	
	//Retorna a matriz das peças da partida
	public ChessPiece[][] getPieces() {
		ChessPiece[][] mat = new ChessPiece[board.getRows()][board.getColumns()];
		//Percorrer a matriz
		for (int i=0; i<board.getRows(); i++) {
			for (int j=0; j<board.getColumns(); j++) {
				mat[i][j] = (ChessPiece) board.piece(i, j);
			}
		}
		return mat;
	}
	
	//Imprimir os movimentos possíveis a partir da posição de origem
	public boolean[][] possibleMoves(ChessPosition sourcePosition) {
		Position position = sourcePosition.toPosition();
		validateSourcePosition(position);
		return board.piece(position).possibleMoves();
	}
	
	//Realiza o movimento de uma peça
	public ChessPiece performChessMove(ChessPosition sourcePosition, ChessPosition targetPosition) {
		//Converte a posição da matriz paraposição de xadrez
		Position source = sourcePosition.toPosition();
		Position target = targetPosition.toPosition();
		//Valida se a posição de origem é válida
		validateSourcePosition(source);
		validateTargetPosition(source, target);
		Piece capturedPiece = makeMove(source, target);

		//Verifica se o rei está em xeque
		if (testCheck(currentPlayer)) {
			//Desfaz o movimento
			undoMove(source, target, capturedPiece);
			throw new ChessException("You can't put yourself in check");
		}

		//Verifica se o oponente está em check 
		check = (testCheck(opponent(currentPlayer))) ? true : false;

		nextTurn();
		return (ChessPiece)capturedPiece;
	}	
		
		/*
		
		ChessPiece movedPiece = (ChessPiece)board.piece(target);
		
		// #specialmove promotion
		promoted = null;
		if (movedPiece instanceof Pawn) {
			if ((movedPiece.getColor() == Color.WHITE && target.getRow() == 0) || (movedPiece.getColor() == Color.BLACK && target.getRow() == 7)) {
				promoted = (ChessPiece)board.piece(target);
				promoted = replacePromotedPiece("Q");
			}
		}
		
		check = (testCheck(opponent(currentPlayer))) ? true : false;

		if (testCheckMate(opponent(currentPlayer))) {
			checkMate = true;
		}
		else {
			nextTurn();
		}
		
		// #specialmove en passant
		if (movedPiece instanceof Pawn && (target.getRow() == source.getRow() - 2 || target.getRow() == source.getRow() + 2)) {
			enPassantVulnerable = movedPiece;
		}
		else {
			enPassantVulnerable = null;
		}
		
		return (ChessPiece)capturedPiece;
	}*/

	/*public ChessPiece replacePromotedPiece(String type) {
		if (promoted == null) {
			throw new IllegalStateException("There is no piece to be promoted");
		}
		if (!type.equals("B") && !type.equals("N") && !type.equals("R") & !type.equals("Q")) {
			return promoted;
		}
		
		Position pos = promoted.getChessPosition().toPosition();
		Piece p = board.removePiece(pos);
		piecesOnTheBoard.remove(p);
		
		ChessPiece newPiece = newPiece(type, promoted.getColor());
		board.placePiece(newPiece, pos);
		piecesOnTheBoard.add(newPiece);
		
		return newPiece;
	}*/
	
	/*private ChessPiece newPiece(String type, Color color) {
		if (type.equals("B")) return new Bishop(board, color);
		if (type.equals("N")) return new Knight(board, color);
		if (type.equals("Q")) return new Queen(board, color);
		return new Rook(board, color);
	}*/
	
	private Piece makeMove(Position source, Position target) {
		//ChessPiece p = (ChessPiece)board.removePiece(source);
		//p.increaseMoveCount();

		//Retira a peça da posição de origem
		Piece p = board.removePiece(source);
		//Remove a peça que está na posição de destino
		Piece capturedPiece = board.removePiece(target);
		//Coloca a peça na posição de destino
		board.placePiece(p, target);
		
		//Verifica se a peça foi capturada e coloca na lista de peças capturadas
		if (capturedPiece != null) {
			piecesOnTheBoard.remove(capturedPiece);
			capturedPieces.add(capturedPiece);
		}

		return capturedPiece;
	
	}
		/*
		// #specialmove castling kingside rook
		if (p instanceof King && target.getColumn() == source.getColumn() + 2) {
			Position sourceT = new Position(source.getRow(), source.getColumn() + 3);
			Position targetT = new Position(source.getRow(), source.getColumn() + 1);
			ChessPiece rook = (ChessPiece)board.removePiece(sourceT);
			board.placePiece(rook, targetT);
			rook.increaseMoveCount();
		}

		// #specialmove castling queenside rook
		if (p instanceof King && target.getColumn() == source.getColumn() - 2) {
			Position sourceT = new Position(source.getRow(), source.getColumn() - 4);
			Position targetT = new Position(source.getRow(), source.getColumn() - 1);
			ChessPiece rook = (ChessPiece)board.removePiece(sourceT);
			board.placePiece(rook, targetT);
			rook.increaseMoveCount();
		}		
		
		// #specialmove en passant
		if (p instanceof Pawn) {
			if (source.getColumn() != target.getColumn() && capturedPiece == null) {
				Position pawnPosition;
				if (p.getColor() == Color.WHITE) {
					pawnPosition = new Position(target.getRow() + 1, target.getColumn());
				}
				else {
					pawnPosition = new Position(target.getRow() - 1, target.getColumn());
				}
				capturedPiece = board.removePiece(pawnPosition);
				capturedPieces.add(capturedPiece);
				piecesOnTheBoard.remove(capturedPiece);
			}
		}
		
		return capturedPiece;
	}*/
	
	//Desfaz o movimento caso o movimento seja invalido como se colocar em cheque
	private void undoMove(Position source, Position target, Piece capturedPiece) {
		//Tira a peça que foi para o destino
		Piece p = board.removePiece(target);
		//Devolve para a posição de origem
		board.placePiece(p, source);
		//Caso uma peça tenha sido capturada
		if (capturedPiece != null) {
			//Pega a peça capturada e coloca de volta no tabuleiro
			board.placePiece(capturedPiece, target);
			//Remove a peça capturada da lista de peças capturadas
			capturedPieces.remove(capturedPiece);
			//Adiciona a peça capturada de volta na lista de peças do tabuleiro
			piecesOnTheBoard.add(capturedPiece);
		}

		/*ChessPiece p = (ChessPiece)board.removePiece(target);
		//
		p.decreaseMoveCount();

		// #specialmove castling kingside rook
		/*if (p instanceof King && target.getColumn() == source.getColumn() + 2) {
			Position sourceT = new Position(source.getRow(), source.getColumn() + 3);
			Position targetT = new Position(source.getRow(), source.getColumn() + 1);
			ChessPiece rook = (ChessPiece)board.removePiece(targetT);
			board.placePiece(rook, sourceT);
			rook.decreaseMoveCount();
		}*/		
		// #specialmove castling queenside rook
		/*if (p instanceof King && target.getColumn() == source.getColumn() - 2) {
			Position sourceT = new Position(source.getRow(), source.getColumn() - 4);
			Position targetT = new Position(source.getRow(), source.getColumn() - 1);
			ChessPiece rook = (ChessPiece)board.removePiece(targetT);
			board.placePiece(rook, sourceT);
			rook.decreaseMoveCount();
		}*/
		
		// #specialmove en passant
		/*if (p instanceof Pawn) {
			if (source.getColumn() != target.getColumn() && capturedPiece == enPassantVulnerable) {
				ChessPiece pawn = (ChessPiece)board.removePiece(target);
				Position pawnPosition;
				if (p.getColor() == Color.WHITE) {
					pawnPosition = new Position(3, target.getColumn());
				}
				else {
					pawnPosition = new Position(4, target.getColumn());
				}
				board.placePiece(pawn, pawnPosition);
			}
		}*/
	}
	
	//Exceção para caso a posição da peça na hora da validação não seja correta
	private void validateSourcePosition(Position position) {
		if (!board.thereIsAPiece(position)) {
			throw new ChessException("There is no piece on source position");
		}
		//Verifica se a peça escolhida é do jogador atual
		if (currentPlayer != ((ChessPiece)board.piece(position)).getColor()) {
			throw new ChessException("The chosen piece is not yours");
		}
		//Verifica se a peça escolhida tem movimentos possíveis
		if (!board.piece(position).isThereAnyPossibleMove()) {
			throw new ChessException("There is no possible moves for the chosen piece");
		}
	}
	
	//Valida se a posição de destino está viavél
	private void validateTargetPosition(Position source, Position target) {
		if (!board.piece(source).possibleMove(target)) {
			throw new ChessException("The chosen piece can't move to target position");
		}
	}
	
	//Troca o turno do jogador
	private void nextTurn() {
		turn++;
		currentPlayer = (currentPlayer == Color.WHITE) ? Color.BLACK : Color.WHITE;
	}
	
	//Retorna a cor do oponente
	private Color opponent(Color color) {
		return (color == Color.WHITE) ? Color.BLACK : Color.WHITE;
	}
	
	//Procura o rei da cor
	private ChessPiece king(Color color) {
		//Procura o rei da cor na lista de peças do tabuleiro
		List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == color).collect(Collectors.toList());
		//Procura o rei
		for (Piece p : list) {
			if (p instanceof King) {
				return (ChessPiece)p;
			}
		}
		//Quando não existe o rei 
		throw new IllegalStateException("There is no " + color + " king on the board");
	}
	
	//Verifica se o rei está em xeque
	private boolean testCheck(Color color) {
		//Pega a posição do rei no formato matriz 
		Position kingPosition = king(color).getChessPosition().toPosition();
		//Pega as peças do oponente
		List<Piece> opponentPieces = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == opponent(color)).collect(Collectors.toList());
		//Pra cada peça na lista do oponente
		for (Piece p : opponentPieces) {
			//Movimentos possiveis da peça
			boolean[][] mat = p.possibleMoves();
			//Se o rei estiver em um dos movimentos possiveis da peça
			if (mat[kingPosition.getRow()][kingPosition.getColumn()]) {
				return true;
			}
		}
		return false;
	}
	
	//Teste do checkMate
	private boolean testCheckMate(Color color) {
		//Verifica se está em check, se for falso não entra no checkmate
		if (!testCheck(color)) {
			return false;
		}
		//Todas as peças da cor do check
		List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == color).collect(Collectors.toList());
		for (Piece p : list) {
			boolean[][] mat = p.possibleMoves();
			for (int i=0; i<board.getRows(); i++) {
				for (int j=0; j<board.getColumns(); j++) {
					if (mat[i][j]) {
						Position source = ((ChessPiece)p).getChessPosition().toPosition();
						Position target = new Position(i, j);
						Piece capturedPiece = makeMove(source, target);
						boolean testCheck = testCheck(color);
						undoMove(source, target, capturedPiece);
						if (!testCheck) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}	
	
	//Recebe coordenadas do xadrez
	private void placeNewPiece(char column, int row, ChessPiece piece) {
		//Converte para posição de matriz
		board.placePiece(piece, new ChessPosition(column, row).toPosition());
		//Adiciona a peça na lsita tabuleiro
		piecesOnTheBoard.add(piece);
	}
	
	//Inicia a aprtida colocando as peças no tabuleiro
	private void initialSetup() {
        placeNewPiece('c', 1, new Rook(board, Color.WHITE));
        placeNewPiece('c', 2, new Rook(board, Color.WHITE));
        placeNewPiece('d', 2, new Rook(board, Color.WHITE));
        placeNewPiece('e', 2, new Rook(board, Color.WHITE));
        placeNewPiece('e', 1, new Rook(board, Color.WHITE));
        placeNewPiece('d', 1, new King(board, Color.WHITE));
        placeNewPiece('c', 7, new Rook(board, Color.BLACK));
        placeNewPiece('c', 8, new Rook(board, Color.BLACK));
        placeNewPiece('d', 7, new Rook(board, Color.BLACK));
        placeNewPiece('e', 7, new Rook(board, Color.BLACK));
        placeNewPiece('e', 8, new Rook(board, Color.BLACK));
        placeNewPiece('d', 8, new King(board, Color.BLACK));
	}
}
