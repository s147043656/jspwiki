/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.wiki;

import java.io.*;
import java.util.*;

import org.apache.wiki.Release;

/**
 * Simple utility that shows you a sorted list of property differences between
 * the 'default en' and a given i18n file. It also warns if there are any duplicates.
 * <p>
 * The first argument is the language, and it is mandatory.
 * The second argument is the path.  If the path is not defined, uses current path (.)
 * <p>
 * For example (if you're compiling your classes to "classes"):
 * <code>
 * java -cp classes TranslationsCheck fi
 * </code>
 * 
 */
public class TranslationsCheck
{
    private static final TreeSet<String> allProps = new TreeSet<String>();  // sorted, no duplicates
    private static final TreeSet<String> duplProps = new TreeSet<String>();
    
    // Change these to your settings...
    static String base = ".";
    static String suffix = null;
   
    public static void main(String[] args) throws IOException
    {
        if( args.length == 0 )
        {
            System.out.println("Usage: java TranslationsCheck <language> [<path>]");
            return;
        }
        
        suffix = args[0];

        if( args.length >= 2 )
        {
            base = args[1];
        }
        
        System.out.println("Using code base " + Release.VERSTR);
        System.out.println("Internationalization property file differences between 'default en' and '"
                           + suffix + "' following:\n");

        try
        {
            diff("/etc/i18n/CoreResources.properties",
                 "/etc/i18n/CoreResources_" + suffix + ".properties");
            detectDuplicates("/etc/i18n/CoreResources_" + suffix + ".properties");
        }
        catch( FileNotFoundException e )
        {
            System.err.println("Unable to locate "+"/etc/i18n/CoreResources_" + suffix + ".properties");
        }

        try
        {
            diff("/etc/i18n/templates/default.properties",
                 "/etc/i18n/templates/default_" + suffix + ".properties");
            detectDuplicates("/etc/i18n/templates/default_" + suffix + ".properties");
        }
        catch( FileNotFoundException e )
        {
            System.err.println("Unable to locate "+"/etc/i18n/templates/default_" + suffix + ".properties");
        }    
        
        try
        {
            diff("/etc/i18n/plugin/PluginResources.properties",
                 "/etc/i18n/plugin/PluginResources_" + suffix + ".properties");
        
            detectDuplicates("/etc/i18n/plugin/PluginResources_" + suffix + ".properties");
        
            System.out.println("Duplicates overall (two or more occurences):");
            System.out.println("--------------------------------------------");
            Iterator iter = duplProps.iterator();
            if (duplProps.size() == 0)
                System.out.println("(none)");
            else
                while (iter.hasNext())
                    System.out.println(iter.next());
            System.out.println();
        }
        catch( FileNotFoundException e ) 
        {
            System.err.println("Unable to locate "+"/etc/i18n/plugin/PluginResources_" + suffix + ".properties");
        } 

        System.out.println("NOTE: Please remember that dependent on the usage of these i18n files, outdated " +
        		"properties maybe should not be deleted, because they may be used by previous releases. " +
        		"Moving them to a special section in the file may be the better solution.");
    }

    public static Map< String, Integer > diff(String source1, String source2) throws FileNotFoundException, IOException
    {
        int missing = 0, outdated = 0;
        // Standard Properties
        Properties p1 = new Properties();
        p1.load(new FileInputStream(new File(base + source1)));

        Properties p2 = new Properties();
        p2.load(new FileInputStream(new File(base + source2)));

        String msg = "Properties in file " + source2;
        System.out.println(msg);
        StringBuffer sb = new StringBuffer(msg.length());
        for (int i = 0; i < msg.length(); i++)
            sb.append("-");
        System.out.println(sb.toString());

        System.out.println("Missing:");
        System.out.println("--------");
        Iterator< String > iter = sortedNames(p1).iterator();
        while (iter.hasNext())
        {
            String name = iter.next();
            String value = p1.getProperty(name);

            if (p2.get(name) == null)
            {
                missing++;
                System.out.println(name + " = " + value);
            }
        }
        System.out.println();

        System.out.println("Outdated or superfluous:");
        System.out.println("------------------------");
        iter = sortedNames(p2).iterator();
        while (iter.hasNext())
        {
            String name = iter.next();
            String value = p2.getProperty(name);

            if (p1.get(name) == null)
            {
                outdated++;
                System.out.println(name + " = " + value);
            }
        }
        System.out.println();
        Map< String, Integer > diff = new HashMap< String, Integer >( 2 );
        diff.put( "missing", missing );
        diff.put( "outdated", outdated );
        return diff;
    }

    private static List<String> sortedNames(Properties p)
    {
        List<String> list = new ArrayList<String>();
        Enumeration iter = p.propertyNames();
        while (iter.hasMoreElements())
        {
            list.add( (String)iter.nextElement() );
        }

        Collections.sort(list);
        return list;
    }
    
    public static int detectDuplicates(String source) throws IOException
    {
        Properties p = new Properties();
        p.load(new FileInputStream(new File(base + source)));
        
        Enumeration iter = p.propertyNames();
        String currentStr;
        while (iter.hasMoreElements())
        {
            currentStr = (String) iter.nextElement();
            if (!allProps.add(currentStr))
                duplProps.add(currentStr);
        }
        return duplProps.size();
    }
    
    /**
     * Allows reuse from {@link org.apache.wiki.site.SiteGeneratorTest}
     */
    public static void clearDuplicates() 
    {
        allProps.clear();
        duplProps.clear();
    }
    
}
