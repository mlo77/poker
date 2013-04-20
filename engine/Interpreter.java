package app.main;

public class Interpreter {
	public final static int CMD_DISTRIB_DBG = 100;
	public final static int CMD_ADDP = 1;
	public final static int CMD_REM = 2;
	public final static int CMD_START = 3;
	public final static int CMD_CHECK = 4;
	public final static int CMD_RAISE = 5;
	public final static int CMD_CALL = 6;
	public final static int CMD_FOLD = 7;
	public final static int CMD_ALLIN = 8;
	public final static int CMD_FINISH = 9;
	public final static int CMD_NEXT = 10;
	public final static int CMD_REPEAT = 11;
	
	private PitBoss pitb;
	boolean inGame;
	
	Interpreter() {
        pitb = new PitBoss();
	}
	
	protected int cmdCode(String cmd) {
		cmd = cmd.toLowerCase();
		if (cmd.equals("add")) {
			return CMD_ADDP;
		}
		else if (cmd.equals("rem")) {
			return CMD_REM;
		}
		else if (cmd.equals("ca")) {
			return CMD_CALL;
		}
		else if (cmd.equals("ch")) {
			return CMD_CHECK;
		}
		else if (cmd.equals("ra")) {
			return CMD_RAISE;
		}
		else if (cmd.equals("fo")) {
			return CMD_FOLD;
		}
		else if (cmd.equals("st") || cmd.equals("start")) {
			return CMD_START;
		}
		else if (cmd.equals("fi") || cmd.equals("fin")) {
			return CMD_FINISH;
		}
		else if (cmd.equals("all")) {
			return CMD_ALLIN;
		}
		else if (cmd.equals("n")) {
			return CMD_NEXT;
		}
		else if (cmd.equals("dbg_distrib")) {
			return CMD_DISTRIB_DBG;
		}
		else if (cmd.charAt(0) > '0' && cmd.charAt(0) < '9') {
			return CMD_REPEAT;
		}		
		
		return -1;
	}
	
	public void doAction(int code, String[] inputs, String cmd) {
		switch(code) {
		case CMD_ADDP :
			int num = 1;
			if (inputs[1] != null)
				num = Integer.parseInt(inputs[1]);
			pitb.addPlayers(num);
			break;
		case CMD_DISTRIB_DBG :
			pitb.setDbgMode(cmd);
			break;
		case CMD_REM :
			if (inputs.length == 2)
				pitb.removePlayer(Integer.parseInt(inputs[1]));
			break;
		case CMD_FINISH :
			pitb.deck.takeAllCards();
			break;
		case CMD_START : // start
			pitb.newDeal();
			inGame = true;
			break;
		case CMD_REPEAT :
			int repeat = Integer.parseInt(inputs[0]);
			int idx=1; while(cmd.charAt(idx++)!=' ');
			cmd = cmd.substring(idx); 
			inputs = cmd.split(" ");
			code = cmdCode(inputs[0].toLowerCase());
			while(repeat-- != 0)
				doSubAction(code, inputs);
			break;
		default :
			doSubAction(code, inputs);
			break;
		}
		pitb.checkTurn();
	}
	
	private void doSubAction(int code, String[] inputs) {
		if (!inGame) return;
		switch(code) {
		case CMD_CHECK :
			pitb.pCheck();
			break;
		case CMD_CALL :
			pitb.pCall();
			break;
		case CMD_RAISE :
			if (inputs.length == 2)
				pitb.pRaise(Integer.parseInt(inputs[1]));
			break;
		case CMD_FOLD :
			pitb.pFold();
			break;
		case CMD_ALLIN :
			pitb.pAllIn();
			break;
		case CMD_NEXT :
			// do nothing
			break;
		default :
			printUsage();
			break;
		}
	}
	
	public void printUsage() {
	}
}
