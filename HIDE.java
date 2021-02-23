package hide;

/**
 *
 * @author Frode Eika Sandnes
 */

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.IntSummaryStatistics;


public class HIDE
    {
    // global variable for experimentation to reduce repeated computation
    public List<String> salts;            
    
    // Soundex encoder.
    public String soundex(String s) 
        {
        char[] x = s.toUpperCase().toCharArray();
        char firstLetter = x[0];
        for (int i = 0; i < x.length; i++) 
            {
            switch (x[i]) 
                {
                case 'B':
                case 'F':
                case 'P':
                case 'V': 
                    {
                    x[i] = '1';
                    break;
                    }
                case 'C':
                case 'G':
                case 'J':
                case 'K':
                case 'Q':
                case 'S':
                case 'X':
                case 'Z': 
                    {
                    x[i] = '2';
                    break;
                    }
                case 'D':
                case 'T': 
                    {
                    x[i] = '3';
                    break;
                    }
                case 'L': 
                    {
                    x[i] = '4';
                    break;
                    }
                case 'M':
                case 'N': 
                    {
                    x[i] = '5';
                    break;
                    }
                case 'R': 
                    {
                    x[i] = '6';
                    break;
                    }
                default: 
                    {
                    x[i] = '0';
                    break;
                    }
                }
            }
        String output = "" + firstLetter;
        for (int i = 1; i < x.length; i++)
            {
            if (x[i] != x[i - 1] && x[i] != '0')
                {
                output += x[i];
                }
            }
        return output;
        }
   
   // Search for the most suitable salt and id length for the list of names.
   public AbstractMap.SimpleEntry findParamters(List<String> lines)
        {
        // generate soundex codes
        List<String> codes = new ArrayList();
        for (String name: lines)
            {
            String [] parts = preProcess(name);        Arrays.sort(parts);
            String code = "";
            for (String part: parts)
                {
                code += soundex(part);                   
                }
            if (!codes.contains(code))
                {
                codes.add(code);
                }
            }

        // search for the best salt
        int min = 20;
        String minSalt = "";
        for (String salt: salts)
            {    
            // get hash values
            List<Integer> hashes = new ArrayList();
            for (String code: codes)
                {
                code = code+salt;
                int hash = code.hashCode();
                if (!hashes.contains(hash))
                    {
                    hashes.add(hash);
                    }
                }

            // find smallest possible set of truncated hash values            
            for (int i=1;i<8;i++)
                {
                List<String> testChamber = new ArrayList();
                for (int hash : hashes)
                    {
                    String hashStr = "000000000"+hash;
                    hashStr = hashStr.substring(hashStr.length() - i);
                    if (testChamber.contains(hashStr))
                        {
                        break;
                        }
                    else
                        {
                        testChamber.add(hashStr);
                        }
                    }
                if (testChamber.size() == hashes.size())
                    {
//System.out.println(salt+", "+i);  // data for salt distribution                    
                    if (i<min)
                        {
                        min = i;
                        minSalt = salt;
                        }
                    else if (i == min)
                        {
                        if (minSalt.length()> salt.length())
                            {
                            min = i;
                            minSalt = salt;                             
                            }
                        }
                    break;
                    }
                }
            }
        AbstractMap.SimpleEntry e = new AbstractMap.SimpleEntry(minSalt,min);
        return e;
        }
   
    // Dump the list of names and the ids.
    public void outoutCoding(List<String> names, String code, int digits)
        {        
        for (String name: names)
            {
            System.out.println(encode(name,code,digits)+" "+name);
            }        
        }
    
    // Santitize the name and sort parts alphabetically.
    public String[] preProcess(String name)
        {
        name = name.replace(",", "");
        String [] parts = name.split(" ");
        Arrays.sort(parts);
        return parts;
        }
    
    // Encode the name parts as soundex codes.
    public String encodeSoundex(String [] parts, String salt)
        {
        String code = "";
        for (String part: parts)
            {
            code += soundex(part);                   
            }
        return code+salt;
        }
    
    // Hashing of name or soundex-code.
    public String applyHashing(String code, int digits)
        {
        int hash = code.hashCode();
        String hashStr = "000000000"+hash;
        return hashStr.substring(hashStr.length() - digits);        
        }
    
    // Encode a name into id.
    public String encode(String name, String salt, int digits)
        {
        String [] parts = preProcess(name);        Arrays.sort(parts);
        String code = encodeSoundex(parts,salt);
        return applyHashing(code,digits);
        }
    
    // Check list of names for duplicates.
    public Object collision(List L)
        {
        List t = new ArrayList();
        for (Object o: L)
            {
            if (t.contains(o))
                {
                return o;
                }
            t.add(o);
            }
        return null;
        }
    
    // Check for duplicate names and sound collisions
    public void checkNames(List<String> lines)
        {
        System.out.println("checking list");
        Object o = collision(lines);
        if (o != null)
            {
            System.out.println("Collision for "+o);
            }
        List<String> codes = new ArrayList();
        for (String s: lines)
            {
            String [] parts = preProcess(s);        
            Arrays.sort(parts);
            String code = encodeSoundex(parts,"");
            codes.add(code);
            }
        o = collision(codes);
        if (o != null)
            {
            System.out.println("Collision for "+o);
            }            
        }
    
    // Read names from text file.
    public List<String> getNames(String fn)
        {
        List<String> lines = new ArrayList();
        try
            {
            lines = Files.readAllLines(Paths.get(fn));
            }
        catch (Exception e)
            {
            e.printStackTrace();
            }
        return lines;
        }
    
    // Draw N random names from the namelist.
    List randomSelection(List L,int N)
        {
        Collections.shuffle(L);
        List t = new ArrayList(L.subList(0, N));        
        return t;
        }
    
    // Experiment for multiple sample sizes.
    void experimentComprehensive()
        { 
        List<String> lines = getNames("names-longlist.txt");
        int repetitions = 100;
        for (int i=10;i<=200;i+=10)
            {   // go through all the repetitions for each case
            List<Integer> withSeed = new ArrayList();
            System.out.print(i+", ");
            for (int j=0;j<repetitions;j++)
                {
                // create name list of right lenth            
                List<String> t = randomSelection(lines, i);
                // analyse
                AbstractMap.SimpleEntry<String,Integer> e = findParamters(t);
    //            AbstractMap.SimpleEntry<String,Integer> e = findParamtersBasic(t);
                int digits = e.getValue();
                withSeed.add(digits);
                }
            IntSummaryStatistics s = withSeed.stream()
                                     .mapToInt((x) -> x)
                                     .summaryStatistics(); 
            System.out.println(s);
            }
        }
    
    // Setup of the experiment.
    public static void main(String[] args)
        {
        // salts are read when the HIDE object is instanciated.        
        HIDE me = new HIDE();        
        me.salts = me.getNames("freqWords.txt");            
        
        me.experimentComprehensive();
        }  
    }

