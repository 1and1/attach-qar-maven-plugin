/*
 * Copyright 2013 1&1.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.oneandone.maven.plugins.attachqars;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Test;
import static org.junit.Assume.assumeThat;
import static org.hamcrest.CoreMatchers.startsWith;

/**
 * @author Mirko Friedenhagen
 */
public class GetLatestVersionMojoIT extends AbstractGetLatestVersionMojoTest {

    /**
     * Test of openConnection method, of class GetLatestVersionMojo.
     */
    @Test
    public void testOpenConnection() throws IOException {
        final URI uri = URI.create("http://google.de/");
        final GetLatestVersionMojo sut = new GetLatestVersionMojo(
                createProject(), uri, REPOS_NAME);
        URL searchUrl = uri.toURL();
        sut.openConnection(searchUrl);
    }

    @Test
    public void testGetLatestCommonsLogging() throws MojoFailureException, MojoExecutionException {
        String artifactoryInstance = System.getenv("ARTIFACTORY_INSTANCE");
        assumeThat("ARTIFACTORY_INSTANCE must start with http://", artifactoryInstance, startsWith("http://"));
        URI uri = URI.create(artifactoryInstance);
        final GetLatestVersionMojo sut = new GetLatestVersionMojo(
                createProject(), uri, REPOS_NAME);
        sut.execute();
    }
}
