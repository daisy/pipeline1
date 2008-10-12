package int_daisy_mathAltCreator.com.dessci.mathflow;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

/**
 * Read data from the Windows Registry. This class uses the reg.exe utility
 * which is available in Windows 2000 and above.
 * 
 * See http://www.rgagnon.com/javadetails/java-0480.html
 * 
 * @author Linus Ericson
 */
public class RegistryQuery {

    private static String REGSTR_TOKEN = "REG_SZ";

    /**
     * Reads a string from the registry.
     * 
     * @param key
     *            the key to read
     * @param name
     *            the name to read
     * @return the value, or <code>null</code> if the key didn't exist or the
     *         reg.exe command failed for some other reason.
     */
    public static String readString(String key, String name) {
        try {
            String[] cmd = new String[5];
            cmd[0] = "reg";
            cmd[1] = "query";
            cmd[2] = key;
            cmd[3] = "/v";
            cmd[4] = name;

            Process process = Runtime.getRuntime().exec(cmd);
            StreamReader reader = new StreamReader(process.getInputStream());

            reader.start();
            int exitValue = process.waitFor();
            reader.join();
            if (exitValue != 0) {
                return null;
            }

            String result = reader.getResult();
            int p = result.indexOf(REGSTR_TOKEN);

            if (p == -1) {
                return null;
            }

            return result.substring(p + REGSTR_TOKEN.length()).trim();
        } catch (Exception e) {
            return null;
        }
    }

    private static class StreamReader extends Thread {
        private InputStream is;

        private StringWriter sw;

        public StreamReader(InputStream is) {
            this.is = is;
            sw = new StringWriter();
        }

        public void run() {
            try {
                int c;
                while ((c = is.read()) != -1) {
                    sw.write(c);
                }
            } catch (IOException e) {
            }
        }

        public String getResult() {
            return sw.toString();
        }
    }

}
