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
package org.apache.wiki.filters;


/**
 *  A generic PageFilter exception.
 *
 *  @since 2.1.112
 *  @deprecated will be removed in 2.10 scope. Consider using 
 *  {@link org.apache.wiki.api.exceptions.FilterException} instead
 */
@Deprecated
public class FilterException 
    extends org.apache.wiki.api.exceptions.FilterException
{
    private static final long serialVersionUID = 0L;
    
    /**
     *  Constructs an exception.
     *  
     *  @param msg {@inheritDoc}
     */
    public FilterException( String msg )
    {
        super( msg );
    }
}
