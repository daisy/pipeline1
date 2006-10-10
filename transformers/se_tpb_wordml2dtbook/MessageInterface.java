package se_tpb_wordml2dtbook;

import java.util.logging.Level;

public interface MessageInterface {

	public void sendMessage(Level level, String idstr, Object[] params);
}
