package app.main;

public class PokrInterpreter {
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
	
	public PitBoss pitb;
	protected boolean inGame = false;
	
	protected PokrInterpreter() {
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
	
	public int doAction(int code, String[] inputs, String cmd) {
		int res = Status.POKR_OK;
		switch(code) {
		case CMD_ADDP :
			int num = 1;
			if (inputs[1] != null)
				num = Integer.parseInt(inputs[1]);
			res = pitb.addPlayers(num);
			break;
		case CMD_DISTRIB_DBG :
			res = pitb.setDbgMode(cmd);
			break;
		case CMD_REM :
			if (inputs.length == 2)
				pitb.removePlayer(Integer.parseInt(inputs[1]));
			break;
		case CMD_FINISH :
			pitb.stop();
			pitb.deck.takeAllCards();
			inGame = false;
			break;
		case CMD_START : // start
			res = pitb.newDeal();
			inGame = true;
			break;
		case CMD_REPEAT :
			int repeat = Integer.parseInt(inputs[0]);
			int idx=1; while(cmd.charAt(idx++)!=' ');
			cmd = cmd.substring(idx); 
			inputs = cmd.split(" ");
			code = cmdCode(inputs[0].toLowerCase());
			while(repeat-- != 0 && res==Status.POKR_OK)
				res = doSubAction(code, inputs);
			break;
		default :
			res = doSubAction(code, inputs);
			break;
		}
		pitb.checkTurn();
		return res;
	}
	
	private int doSubAction(int code, String[] inputs) {
		if (!inGame) return Status.POKR_NOT_RUNNING;
		int r = Status.POKR_OK;
		switch(code) {
		case CMD_CHECK :
			r = pitb.pCheck();
			break;
		case CMD_CALL :
			r = pitb.pCall();
			break;
		case CMD_RAISE :
			if (inputs.length == 2)
				r = pitb.pRaise(Integer.parseInt(inputs[1]));
			break;
		case CMD_FOLD :
			pitb.pFold();
			break;
		case CMD_ALLIN :
			r = pitb.pAllIn();
			break;
		case CMD_NEXT :
			// do nothing
			break;
		default :
			printUsage();
			break;
		}
		return r;
	}
	
	public void printUsage() {
	}
}
