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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import org.apache.maven.project.MavenProject;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author Mirko Friedenhagen
 */
public class GetLatestVersionMojoTest {

    private static final String EXPECTED = "http://myrepo/artifactory/api/search/latestVersion?repos=repo1&g=commons-logging&a=commons-logging";
    private static final URI ARTIFACTORY_URL_WITHOUT_SLASH = URI.create("http://myrepo/artifactory");
    private static final URI ARTIFACTORY_URL_WITH_SLASH = URI.create("http://myrepo/artifactory/");
    private static final String REPOS_NAME = "repo1";

    public GetLatestVersionMojoTest() {
    }

    /**
     * Test of execute method, of class GetLatestVersionMojo.
     */
    @Test
    public void testExecute() throws Exception {
        MavenProject project = createProject();
        project.setArtifactId("foo");
        final GetLatestVersionMojo sut = new GetLatestVersionMojo(
                project, URI.create("http://mamrepo.united.domain/artifactory"), REPOS_NAME) {
            @Override
            HttpURLConnection openConnection(URL searchUrl) throws IOException {                
                final HttpURLConnection mockConnection = mock(HttpURLConnection.class);                
                when(mockConnection.getInputStream()).thenReturn(new ByteArrayInputStream("1.1.1".getBytes()));
                return mockConnection;
            }            
        };
        sut.execute();
    }

    /**
     * Test of createUri method, of class GetLatestVersionMojo.
     */
    @Test
    public void checkCreateUriWithoutSlash() {
        MavenProject project = createProject();
        final GetLatestVersionMojo sut = new GetLatestVersionMojo(project, ARTIFACTORY_URL_WITHOUT_SLASH, REPOS_NAME);
        final URI result = sut.createUri(project.getGroupId(), project.getArtifactId());
        assertEquals(EXPECTED, result.toString());

    }

    /**
     * Test of createUri method, of class GetLatestVersionMojo.
     */
    @Test
    public void checkCreateUriWithSlash() {
        MavenProject project = createProject();
        final GetLatestVersionMojo sut = new GetLatestVersionMojo(project, ARTIFACTORY_URL_WITH_SLASH, REPOS_NAME);
        final URI result = sut.createUri(project.getGroupId(), project.getArtifactId());
        assertEquals(EXPECTED, result.toString());

    }

    private MavenProject createProject() {
        final MavenProject project = new MavenProject();
        project.setGroupId("commons-logging");
        project.setArtifactId("commons-logging");
        return project;
    }
}
