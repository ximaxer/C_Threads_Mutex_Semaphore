package RMI;
import javax.print.DocFlavor;
import java.util.ArrayList;

public final class CONST {
    public static final int MAX_BUFFER_SIZE = 1024*4;
    public static final int BIG_BUFFER_SIZE = 1024*3;
    public static final int BUFFER_SIZE = 256;

    public final class MULTICAST{
        public static final String MULTICAST_ADDRESS = "224.3.2.1";
        public static final int PORT = 9800;

        private MULTICAST(){}
    }

    public final class RMI{
        public static final int REGISTRY_PORT = 7001;
        public static final String REGISTRY_NAME = "RMI_SPACE";
    }

    private CONST(){}
}