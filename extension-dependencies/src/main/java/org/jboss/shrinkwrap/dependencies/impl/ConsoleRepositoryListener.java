package org.jboss.shrinkwrap.dependencies.impl;

/*
 * Copyright (c) 2010 Sonatype, Inc. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0, 
 * and you may not use this file except in compliance with the Apache License Version 2.0. 
 * You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the Apache License Version 2.0 is distributed on an 
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */

import java.io.PrintStream;

import org.sonatype.aether.RepositoryEvent;
import org.sonatype.aether.util.listener.AbstractRepositoryListener;

public class ConsoleRepositoryListener
    extends AbstractRepositoryListener
{

    private PrintStream out;

    public ConsoleRepositoryListener( PrintStream out )
    {
        this.out = ( out != null ) ? out : System.out;
    }

    public void artifactDeployed( RepositoryEvent event )
    {
        out.println( "Deployed " + event.getArtifact() + " to " + event.getRepository() );
    }

    public void artifactDeploying( RepositoryEvent event )
    {
        out.println( "Deploying " + event.getArtifact() + " to " + event.getRepository() );
    }

    public void artifactDescriptorInvalid( RepositoryEvent event )
    {
        out.println( "Invalid artifact descriptor for " + event.getArtifact() + ": "
            + event.getException().getMessage() );
    }

    public void artifactDescriptorMissing( RepositoryEvent event )
    {
        out.println( "Missing artifact descriptor for " + event.getArtifact() );
    }

    public void artifactInstalled( RepositoryEvent event )
    {
        out.println( "Installed " + event.getArtifact() + " to " + event.getFile() );
    }

    public void artifactInstalling( RepositoryEvent event )
    {
        out.println( "Installing " + event.getArtifact() + " to " + event.getFile() );
    }

    public void artifactResolved( RepositoryEvent event )
    {
        out.println( "Resolved artifact " + event.getArtifact() + " from " + event.getRepository() );
    }

    public void artifactResolving( RepositoryEvent event )
    {
        out.println( "Resolving artifact " + event.getArtifact() );
    }

    public void metadataDeployed( RepositoryEvent event )
    {
        out.println( "Deployed " + event.getMetadata() + " to " + event.getRepository() );
    }

    public void metadataDeploying( RepositoryEvent event )
    {
        out.println( "Deploying " + event.getMetadata() + " to " + event.getRepository() );
    }

    public void metadataInstalled( RepositoryEvent event )
    {
        out.println( "Installed " + event.getMetadata() + " to " + event.getFile() );
    }

    public void metadataInstalling( RepositoryEvent event )
    {
        out.println( "Installing " + event.getMetadata() + " to " + event.getFile() );
    }

    public void metadataInvalid( RepositoryEvent event )
    {
        out.println( "Invalid metadata " + event.getMetadata() );
    }

    public void metadataResolved( RepositoryEvent event )
    {
        out.println( "Resolved metadata " + event.getMetadata() + " from " + event.getRepository() );
    }

    public void metadataResolving( RepositoryEvent event )
    {
        out.println( "Resolving metadata " + event.getMetadata() + " from " + event.getRepository() );
    }

}
