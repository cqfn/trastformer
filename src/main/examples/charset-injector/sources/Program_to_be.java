import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;

class Program {
    public String convert(final String source) throws IOException {
        final InputStream stream = IOUtils.toInputStream(source, "UTF-8");
        final String result = IOUtils.toString(stream, "UTF-8");
        stream.close();
        return result;
    }
}