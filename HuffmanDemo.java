import java.io.IOException;

public class HuffmanDemo {
    public static void main(String[] args) throws IOException 
    {
        // Calls the encode method
        Huffman.encode();

        // Print line in order to space out inputs
        System.out.println("\n\n* * * * *\n\n");
        
        // Calls the decode method
        Huffman.decode();
    }
}
