package org_pef_text;

import java.io.IOException;
import java.io.OutputStream;

import org.daisy.pipeline.core.event.MessageEvent;
import org.daisy.pipeline.core.transformer.Transformer;

public class PipelineMessageOutputStream extends OutputStream {
	private Transformer t;
	private byte[] buf;
	private int count;
	
	public PipelineMessageOutputStream(Transformer t) {
		this.t = t;
		this.buf = new byte[4096];
		this.count = 0;
	}
	
	private void sendMessage() {
		if (count>0) {
			String s = new String(buf,0,count);
			String lc = s.toLowerCase();
			if (lc.startsWith("warning:")) {
				t.sendMessage(s.substring(8).trim(), MessageEvent.Type.WARNING, MessageEvent.Cause.SYSTEM, null);
			} else if (lc.startsWith("debug:")) {
				t.sendMessage(s.substring(6).trim(), MessageEvent.Type.DEBUG, MessageEvent.Cause.SYSTEM, null);
			} else if (lc.startsWith("error:")) {
				t.sendMessage(s.substring(6).trim(), MessageEvent.Type.ERROR, MessageEvent.Cause.SYSTEM, null);
			} else {
				t.sendMessage(s, MessageEvent.Type.INFO, MessageEvent.Cause.SYSTEM, null);
			}
			count = 0;
		}
	}

	@Override
	public void write(int b) throws IOException {
		if (b=='\n' || b=='\r') {
			sendMessage();
		} else {
			buf[count++]= (byte)(b&0xFF);
		}
	}

}
