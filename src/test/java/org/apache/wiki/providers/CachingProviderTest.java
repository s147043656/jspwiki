/* 
    Licensed to the Apache Software Foundation (ASF) under one
    or more contributor license agreements.  See the NOTICE file
    distributed with this work for additional information
    regarding copyright ownership.  The ASF licenses this file
    to you under the Apache License, Version 2.0 (the
    "License"); you may not use this file except in compliance
    with the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied.  See the License for the
    specific language governing permissions and limitations
    under the License.  
 */

package org.apache.wiki.providers;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.Properties;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.PropertyConfigurator;
import org.apache.wiki.TestEngine;
import org.apache.wiki.WikiPage;
import org.apache.wiki.util.FileUtil;

public class CachingProviderTest extends TestCase
{
    protected TestEngine testEngine;

    public CachingProviderTest( String s )
    {
        super( s );
    }

    public void setUp()
        throws Exception
    {
        TestEngine.emptyWorkDir();

        Properties props2 = new Properties();
        props2.load( TestEngine.findTestProperties() );
        testEngine = new TestEngine(props2);
        PropertyConfigurator.configure(props2);
    }

    public void tearDown()
    {
        TestEngine.emptyWorkDir();
        testEngine.deleteTestPage("Testi");
    }

    /**
     *  Checks that at startup we call the provider once, and once only.
     */
    public void testInitialization()
        throws Exception
    {
        Properties props = new Properties();
        props.load( TestEngine.findTestProperties() );

        props.setProperty( "jspwiki.usePageCache", "true" );
        props.setProperty( "jspwiki.pageProvider", "org.apache.wiki.providers.CounterProvider" );
        props.setProperty( "jspwiki.cachingProvider.capacity", "100" );

        TestEngine engine = new TestEngine( props );

        CounterProvider p = (CounterProvider)((CachingProvider)engine.getPageManager().getProvider()).getRealProvider();

        assertEquals("init", 1, p.m_initCalls);
        assertEquals("getAllPages", 1, p.m_getAllPagesCalls);
        assertEquals("pageExists", 0, p.m_pageExistsCalls);
        assertEquals("getPage", 2, p.m_getPageCalls); // These two are for non-existant pages (with and without s)
        assertEquals("getPageText", 4, p.m_getPageTextCalls);

        engine.getPage( "Foo" );

        assertEquals("pageExists2", 0, p.m_pageExistsCalls);
        assertEquals("getPage2", 2, p.m_getPageCalls);
    }

    public void testSneakyAdd()
        throws Exception
    {
        Properties props = new Properties();
        props.load( TestEngine.findTestProperties() );

        props.setProperty( "jspwiki.cachingProvider.cacheCheckInterval", "2" );
        
        TestEngine engine = new TestEngine( props );
        
        String dir = props.getProperty( FileSystemProvider.PROP_PAGEDIR );
        
        File f = new File( dir, "Testi.txt" );
        String content = "[fuufaa]";
        
        PrintWriter out = new PrintWriter( new FileWriter(f) );
        FileUtil.copyContents( new StringReader(content), out );
        out.close();
        
        Thread.sleep( 4000L ); // Make sure we wait long enough
        
        WikiPage p = engine.getPage( "Testi" );
        assertNotNull( "page did not exist?", p );
        
        String text = engine.getText( "Testi");
        assertEquals("text", "[fuufaa]", text );
        
        // TODO: ReferenceManager check as well
    }
    
    public static Test suite()
    {
        return new TestSuite( CachingProviderTest.class );
    }

}
