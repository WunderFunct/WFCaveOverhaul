package wftech.caveoverhaul.virtualpack;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import net.minecraft.server.packs.resources.IoSupplier;
import wftech.caveoverhaul.CaveOverhaul;

public class MemoryBasedIoSupplier implements IoSupplier {

	private byte[] contents = null;
	private InputStream stream = null;
	private String key = null;
	
	public MemoryBasedIoSupplier(String contents, String key) {
		this.contents = contents.getBytes();
		this.stream = new ByteArrayInputStream(this.contents);
		this.key = null;
	}
	
	@Override
	public Object get() throws IOException {
        //StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        //for (StackTraceElement element : stackTrace) {
        //}
		//return stream;
		return stream;
	}

}
