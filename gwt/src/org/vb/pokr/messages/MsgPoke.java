package org.vb.pokr.messages;

public class MsgPoke extends Message {

	public static final int TYPE = 0x00040000;
	public static final int MAX_ALLOWED_POKED_SID = 5;
	
	private int[] pids = null;
	private long[] sidFocused = null;
	private int count = 0;
	
	public MsgPoke() {
		super(TYPE, -1);
	}
	
	public int addPidSid(int pid, long si) {
		if (sidFocused == null || count >= MAX_ALLOWED_POKED_SID)
			return -1;
		pids[count] = pid;
		sidFocused[count++] = si;
		return 0;
	}
	
	public long getSid(int i) {
		if (null == sidFocused || i >= MAX_ALLOWED_POKED_SID || i < 0)
			return -1;
		return sidFocused[i];
	}
	
	public int getPid(int i) {
		if (null == sidFocused || i >= MAX_ALLOWED_POKED_SID || i < 0)
			return -1;
		return pids[i];
	}
	
	public int sidCount() {
		return count;
	}
	
	public void allocate(int s) {
		if (s > MAX_ALLOWED_POKED_SID) s = MAX_ALLOWED_POKED_SID;
		if (s <= 0) return;
		pids = new int[s];
		sidFocused = new long[s];
	}
}
