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
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.IOUtil;

/**
 * Retrieves the latest version of the project from Artifactory and put it into a property
 * for later usage in other plugins. 
 * 
 * <p>Similar to the
 * <a href="http://mojo.codehaus.org/buildnumber-maven-plugin/create-mojo.html">
 * <tt>buildnumber:create</tt></a> Mojo.
 * Invokes something like 
 * <tt>http://host/artifactory/api/search/latestVersion?reposname=repo1&amp;g=commons-logging&amp;a=commons-logging&amp;remote=1</tt>.
 * For more information take a look at the documention of the
 * <a href="http://wiki.jfrog.org/confluence/display/RTF/Artifactory+REST+API#ArtifactoryRESTAPI-ArtifactLatestVersionSearch">
 * REST-API</a> of Artifactory.</p>
 *
 * @author Mirko Friedenhagen
 */
@Mojo(name = "latest-version", aggregator = false, defaultPhase = LifecyclePhase.INSTALL)
public class GetLatestVersionMojo extends AbstractMojo {

    /**
     * The Maven project.
     */
    @Parameter(defaultValue = "${project}", readonly = true)
    private final MavenProject project;

    /**
     * URI pointing to Artifactory, e.g. http://localhost:8081/artifactory/.
     */
    @Parameter(property = "attach-qar.artifactory-uri", required = true)
    private final URI artifactoryUri;

    /**
     * Name of the property holding the latest version from the repository.
     */
    @Parameter(defaultValue = "latestVersionFromRepository", property = "attach-qar.latest-version-property-name",
            required = true)
    private final String latestVersionPropertyName;

    /**
     * Name of the repository for resolution of the latest version.
     */
    @Parameter(defaultValue = "libs-release-local", property = "attach-qar.repos-name", required = true)
    private final String reposName;
    /**
     * Constructor for tests.
     *
     * @param project to inspect.
     * @param artifactoryUri see {@link GetLatestVersionMojo#artifactoryUri}.
     * @param reposName see {@link GetLatestVersionMojo#reposName}.
     */
    GetLatestVersionMojo(MavenProject project, URI artifactoryUri, String reposName) {
        this.project = project;
        this.artifactoryUri = artifactoryUri;
        this.reposName = reposName;
        this.latestVersionPropertyName = "latestVersionFromRepository";
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        final String groupId = project.getGroupId();
        final String artifactId = project.getArtifactId();
        final URI searchUri = createUri(groupId, artifactId);
        try {
            final URL searchUrl = searchUri.toURL();
            final HttpURLConnection openConnection = openConnection(searchUrl);
            final InputStream in = openConnection.getInputStream();
            try {
                project.getProperties().setProperty(latestVersionPropertyName, IOUtil.toString(in));
            } finally {
                in.close();
            }
        } catch (MalformedURLException ex) {
            throw new MojoFailureException("Could not convert " + searchUri + " to URL", ex);
        } catch (IOException ex) {
            throw new MojoExecutionException("Could not fetch latestVersion: " + searchUri, ex);
        }
    }

    /**
     * Creates the search URI.
     *
     * @param groupId of the project.
     * @param artifactId of the project.
     * @return complete search URI.
     */
    URI createUri(final String groupId, final String artifactId) {
        final String searchPart = 
                "api/search/latestVersion?repos=" + reposName + "&g=" + groupId + "&a=" + artifactId + "&remote=1";
        if (artifactoryUri.getPath().endsWith("/")) {
            return artifactoryUri.resolve(searchPart);
        } else {
            return URI.create(artifactoryUri.toString() + "/").resolve(searchPart);
        }
    }
    /**
     * Open a connection, used for tests.
     * @param searchUrl to connect to.
     * @return connection.
     * @throws IOException when the connection could not be opened.
     */
    HttpURLConnection openConnection(final URL searchUrl) throws IOException {
        return (HttpURLConnection) searchUrl.openConnection();
    }
}
