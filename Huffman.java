import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class Huffman 
{

    public static void encode()throws IOException
    {
        // initialize Scanner to capture user input
        Scanner sc = new Scanner(System.in);

        // capture file information from user and read file
        System.out.print("Enter the filename to read from/encode: ");
        String f = sc.nextLine();

        // create File object and build text String
        File file = new File(f);
        Scanner input = new Scanner(file).useDelimiter("\\z");
        String text = input.next();

        // close input file
        input.close();

        // initialize Array to hold frequencies (indices correspond to
        // ASCII values)
        int[] freq = new int[256];
        // concatenate/sanitize text String and create character Array
        // the whitespace can be added back in during the encoding phase

        char[] chars = text.replaceAll("\\s", "").toCharArray();

        // count character frequencies
        for(char c: chars)
            freq[c]++;
        
        // ArrayList pairs to hold each created pair object
        ArrayList<Pair> pairs = new ArrayList<>();

        // in variable to determine whether something has been added or not
        boolean in = false;
        
        // For loop cycles through the freq array
        for(int i = 0; i<256; i++)
        {
            if(freq[i]!=0)
            {
                // this method of rounding is accurate enough
                Pair p = new Pair((char)i, Math.round(freq[i]*10000d/chars.length)/10000d);

                // If the pairs ArrayList is empty then the first pair is just added
                if (pairs.size() == 0) 
                    pairs.add(p);

                // Otherwise if there are elements in the pairs ArrayList
                else
                {
                    // For loop cycles through pairs
                    for (int j = 0; j < pairs.size(); j++) 
                    {
                        // If the probability of the pair object is less than or equal to the probability of the pair object in the ArrayList
                        if (p.getProb() <= pairs.get(j).getProb()) 
                        {
                            // The pair object is added at the index just before the pair object in the ArrayList it was compared to
                            pairs.add(j--, p);

                            // Variable in is set to true as the pair was added
                            in = true;

                            // For loop is broken out of to ensure the pair isn't addde again
                            break;
                        }
                    }

                    // If the element wasn't added, it is added to the end of the ArrayList
                    if (in == false) 
                        pairs.add(p);

                    // If the element was added, in is set back to false for the next pair object
                    else 
                        in = false;
                }
            }
        }

        // Leafs ArrayList is made to hold all the leaf nodes made out of the pair objects
        ArrayList<Peartree> leafs = new ArrayList<>();

        // While loop continues until the pairs ArrayList is emptied
        while (!pairs.isEmpty())
        {
            // A new Peartree object is made with the first pair object in the pairs ArrayList
            Peartree t = new Peartree(pairs.remove(0));

            // That Peartree object is adde to the leafs ArrayList
            leafs.add(t);
        }
        
        // While loop to cycles through the leafs ArrayList until only one Peartree is left
        while (leafs.size() != 1)
        {
            // The first two Peartrees in the leafs ArrayList are removed and stored in l1 and l2
            Peartree l1 = leafs.remove(0);
            Peartree l2 = leafs.remove(0);

            // A new Pair object is made with the sums of the two lowest probabilities, as well as the ⁂ character to show it is a parent node
            Pair p = new Pair('⁂', l1.getProb() + l2.getProb());

            // The new Pair object is turned into a parent Peartree
            Peartree parent = new Peartree(p);
            
            // The two Peartrees removed from the leafs ArrayList are added to the left and right of the parent node
            parent.attachLeft(l1); parent.attachRight(l2);

            // PearPresent variable to tell whether the parent node was added or not
            boolean pearPresent = false;

            // For loop cycles through the current elements in the leafs ArrayList
            for (int i = 0; i < leafs.size(); i++) 
            {
                // If the probability of the parent node is less than or equal to the probability of the node in the leafs ArrayList
                if (parent.getProb() <= leafs.get(i).getProb())
                {
                    // The parent node is added at the index just before the node in the leafs ArrayList it was compared to
                    leafs.add(i--, parent);

                    // PearPresent is set to true as the parent node was added
                    pearPresent = true;

                    // The loop is broken out of to ensure the node isn't added again
                    break;
                }
            }

            // If the parent node wasn't added to the leafs array, it's probability was greater than any probability in the ArrayList, so it's added to the end
            if (pearPresent == false) 
                leafs.add(parent);
        }

        
        //Recieves the codes from the start of the leafs ArrayList
        String[] codes = findEncoding(leafs.get(0));

        // Binary ArrayList is made to hold each of the binary codes relating to each character in the Peartree
        ArrayList<String> binary = new ArrayList<>();

        // For loop cycles through the codes array
        for (int i = 0; i < codes.length; i++) 
        {
            // If the current element of the codes array is null the loop continues
            if (codes[i] == null) 
                continue;

            // Otherwise if there is a value at the current element of the codes array
            else
            {
                // If nothing is in the binary ArrayList the element in codes is just added to binary
                if (binary.size() == 0) 
                    binary.add(codes[i]);
                
                // Otherwise if there are elements in the binary ArrayList
                else
                {
                    // BinaryPresent variable to determine whether the element was added to the binary ArrayList
                    boolean binaryPresent = false;

                    // For loop cycles through the binary ArrayList
                    for (int j = 0; j < binary.size(); j++) 
                    {
                        // First the length of each binary string is compared

                        // If the binary string in codes is smaller than the binary string in the binary ArrayList
                        if (codes[i].length() < binary.get(j).length())
                        {
                            // The binary string from codes is added to the binary ArrayList at the index just before the current index in the loop
                            binary.add(j--, codes[i]);

                            // BinaryPresent is set to true as the string from codes has been added
                            binaryPresent = true;

                            // The loop is broken out of to ensure it is not added more than once
                            break;
                        }

                        // Otherwise if the binary string in codes has the same length as the string in the binary ArrayList
                        else if (codes[i].length() == binary.get(j).length())
                        {
                            // Then secondly the binary values of each element are compared

                            // If the binary string from codes has a lower value than the one from the binary ArrayList
                            if (Integer.parseInt(codes[i]) < Integer.parseInt(binary.get(j)))
                            {
                                // The string is added to the index before the current index in the loop
                                binary.add(j--, codes[i]);

                                // BinaryPresent is set to true as the element has been added
                                binaryPresent = true;

                                // The loop is broken out of to ensure it is not added more than once
                                break;
                            }

                            // Otherwise if the binary string has a greater or equal value than the one from the ArrayList the loop continues
                            else continue;
                        }
                    }

                    // If the element has not been added then it is added to the end of the ArrayList 
                    if (binaryPresent == false) 
                        binary.add(codes[i]);
                }
            }
        }

        // Trees ArrayList is made to hold each leaf node from the tree, starting from the first found leaf down to the last found leafs
        ArrayList<Peartree> trees = new ArrayList<>();

        // A Queue is made to hold the tree and cycle through it
        Queue<Peartree> p = new LinkedList<>();
        p.add(leafs.get(0));

        // While loop used to go through the tree
        while (!p.isEmpty())
        {
            // The first element of the Queues is removed and stored in temp
            Peartree temp = p.remove();

            // If the character of temp isn't ⁂, then it is a leaf node so it is added to the trees ArrayList
            if (temp.getValue() != '⁂') trees.add(temp);

            // If the left branch of the temp Peartree has value, then it is added to the p Queue to continue going through the tree
            if (temp.getLeft() != null) p.add(temp.getLeft());

            // If the right branch of the temp Peartree has value, then it is added to the p Queue to continue going through the tree
            if (temp.getRight() != null) p.add(temp.getRight());
        }

        // PrintWriter object is made to write text into the Huffman.txt file
        PrintWriter output = new PrintWriter("Huffman.txt");

        // Print line is outputted to detail what's happening
        System.out.println("\nCodes generated. Printing codes to Huffman.txt \n");

        // The file is filled with each symbol and its corresponding probability of appearing, as well as it's binary value based on the Huffman tree
        output.println("Symbol Prob.\tHuffman Code");
        for (int i = 0; i < binary.size(); i++) 
            output.println(trees.get(i).getValue() + "\t\t\t\t" + trees.get(i).getProb() + "  " + binary.get(i));

        output.close();

        // Print line is outputted to detail what's happening
        System.out.println("Printing encoded text to Encoded.txt");

        // Second PrintWriter object is made to write text into the Encoded.txt file
        PrintWriter encoded = new PrintWriter("Encoded.txt");

        // For loop goes through the text of the inputted file
        for (int i = 0; i < text.length(); i++) 
        {
            // The current character of the text is stored in curr
            char curr = text.charAt(i);

            // If the current character is simply a space, it is printed back into the Encoded.txt file
            if (curr == ' ') 
                encoded.print(curr);

            //Otherwise if the current character is an actual character
            else
            {
                // For loop goes through the binary and trees ArrayList's
                for (int k = 0; k < binary.size(); k++) 
                    // When the index of the curr character is found, it's binary equivalent is printed into the Encoded.txt file
                    if (trees.get(k).getValue() == curr) 
                        encoded.print(binary.get(k));
            }
        }
        encoded.close(); 
    }


    public static void decode()throws IOException
    {
        // initialize Scanner to capture user input
        Scanner sc = new Scanner(System.in);

        // capture file information from user and read file
        System.out.print("Enter the filename to read from/decode: ");
        String f = sc.nextLine();

        // create File object and build text String
        File file = new File(f);
        Scanner input = new Scanner(file).useDelimiter("\\Z");
        String text = input.next();
        // ensure all text is consumed, avoiding false positive end of input String
        input.useDelimiter("\\z");
        //text += input.next();

        // close input file
        input.close();

        // capture file information from user and read file
        System.out.print("Enter the filename of document containing Huffman codes: ");
        f = sc.nextLine();

        // create File object and build text String
        file = new File(f);
        input = new Scanner(file).useDelimiter("\\Z");
        String codes = input.next();

        // close input file
        input.close();

        // Scanner to read from the Huffman.txt file
        Scanner kb = new Scanner(codes);

        // ArrayList's binary and characters to hold the binary codes and the corresponding characters 
        ArrayList<String> binary = new ArrayList<>();
        ArrayList<Character> characters = new ArrayList<>();

        // Discard the first header line
        kb.nextLine();

        // While the Huffman.txt file has another line
        while (kb.hasNextLine())
        {
            // The character is stored in c
            char c = kb.next().charAt(0);

            // The probability is discarded
            kb.next();

            // The binary code for the character just stored is stored in s
            String s = kb.next();

            // The character is added to the characters ArrayList
            characters.add(c);

            // The corresponding binary code is stored in the binary ArrayList
            binary.add(s);
        }

        // Scanner is closed
        kb.close();

        // Printwriter object is made to write text into the Decoded.txt file
        PrintWriter decoder = new PrintWriter("Decoded.txt");

        // Print line is outputted to detail what's happening
        System.out.println("Printing decoded text to Decoded.txt");

        // Empty string to be used to decode words
        String code = "";

        // For loop to read through each line in the Encoded.txt file
        for (int i = 0; i < text.length(); i++) 
        {
            // Curr variable stores the current character in the file
            char curr = text.charAt(i);

            // If the character is simply a space, that is just outputted
            if (curr == ' ') 
                decoder.print(" ");

            // Otherwise if the character has value
            else
            {
                // The character is added to the code string
                code = code + curr;

                // For loop goes through the binary ArrayList
                for (int j = 0; j < binary.size(); j++) 
                {
                    // If the code string is found in the binary ArrayList
                    if (code.equals(binary.get(j)))
                    {
                        // The corresponding character is printed into the Decoded.txt file
                        decoder.print(characters.get(j));

                        // The code variable is set back to being empty for the next code to be added to
                        code = "";
                    }

                    // Otherwise if the string isn't found, continue
                    else continue;
                }
            }
        }

        // The decoder Printwriter is closed
        decoder.close();
    }

    private static String[] findEncoding(Peartree pt)
    {
        // initialize String array with indices corresponding to ASCII values
        String[] result = new String[256];
        
        // first call from wrapper
        findEncoding(pt, result, "");
        return result;
    }

    private static void findEncoding(Peartree pt, String[] a, String prefix)
    {
        // test is node/tree is a leaf
        if (pt.getLeft()==null && pt.getRight()==null)
            a[pt.getValue()] = prefix;

        // recursive calls
        else
        {
            findEncoding(pt.getLeft(), a, prefix+"0");
            findEncoding(pt.getRight(), a, prefix+"1");
        }
    }
}