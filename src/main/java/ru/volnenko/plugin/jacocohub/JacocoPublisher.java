package ru.volnenko.plugin.jacocohub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import ru.volnenko.plugin.jacocohub.dto.JacocoResultDto;
import ru.volnenko.plugin.jacocohub.enumerated.ArtifactType;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;

@Mojo(name = "publish", defaultPhase = LifecyclePhase.COMPILE)
public class JacocoPublisher extends AbstractMojo {

    private static final String JACOCO = "./target/site/jacoco/jacoco.xml";

    @Getter
    @Setter
    @Parameter(property = "jacocohub")
    private String jacocohub = "http://localhost:8080";

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    @Getter
    @Setter
    @Parameter(property = "artifactId")
    private String artifactId;

    @Getter
    @Setter
    @Parameter(property = "groupId")
    private String groupId;

    @Getter
    @Setter
    @Parameter(property = "version")
    private String version;

    @Getter
    @Setter
    @Parameter(property = "master")
    private String branch = "master";

    @Getter
    @Setter
    @Parameter(property = "type")
    private ArtifactType type = ArtifactType.APPLICATION;

    private static final XmlMapper XML_MAPPER = new XmlMapper();

    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        final File file = new File(JACOCO);
        final boolean exists = file.exists();
        if (!exists) {
            System.err.println("WARN! Jacoco results not found...");
        }
        try {
            final JacocoResultDto counter = new JacocoResultDto();
            final List<LinkedHashMap> root = XML_MAPPER.readValue(new File(JACOCO), List.class);
            for (final LinkedHashMap item: root) {
                if (item.containsKey("type")) {
                    final Object typeObject = item.get("type");
                    final Object missedObject = item.get("missed");
                    final Object coveredObject = item.get("covered");
                    if (typeObject == null) continue;
                    if (missedObject == null) continue;
                    if (coveredObject == null) continue;
                    final String type = typeObject.toString();
                    final String missed = missedObject.toString();
                    final String covered = coveredObject.toString();
                    if ("INSTRUCTION".equals(type)) {
                        counter.setInstructionCovered(Integer.parseInt(covered));
                        counter.setInstructionMissed(Integer.parseInt(missed));
                    }
                    if ("BRANCH".equals(type)) {
                        counter.setBranchCovered(Integer.parseInt(covered));
                        counter.setBranchMissed(Integer.parseInt(missed));
                    }
                    if ("LINE".equals(type)) {
                        counter.setLineCovered(Integer.parseInt(covered));
                        counter.setLineMissed(Integer.parseInt(missed));
                    }
                    if ("COMPLEXITY".equals(type)) {
                        counter.setComplexityCovered(Integer.parseInt(covered));
                        counter.setComplexityMissed(Integer.parseInt(missed));
                    }
                    if ("METHOD".equals(type)) {
                        counter.setMethodCovered(Integer.parseInt(covered));
                        counter.setMethodMissed(Integer.parseInt(missed));
                    }
                    if ("CLASS".equals(type)) {
                        counter.setClassCovered(Integer.parseInt(covered));
                        counter.setClassMissed(Integer.parseInt(missed));
                    }
                }
            }

            if (artifactId == null || artifactId.isEmpty()) {
                if (project != null) counter.setArtifactId(project.getArtifactId());
            } else {
                counter.setArtifactId(artifactId);
            }

            if (groupId == null || groupId.isEmpty()) {
                if (project != null) counter.setGroupId(project.getGroupId());
            } else {
                counter.setGroupId(groupId);
            }

            if (version == null || version.isEmpty()) {
                if (project != null) counter.setVersion(project.getVersion());
            } else {
                counter.setVersion(version);
            }

            counter.setType(type);

            final CloseableHttpClient httpclient = HttpClients.createDefault();
            final String jsonString = JSON_MAPPER.writeValueAsString(counter);
            final String url = jacocohub + "/api/v1/result/jacoco/publish";
            final HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("Content-Type", ContentType.APPLICATION_JSON.toString());
            System.out.println(jsonString);
            final StringEntity entity = new StringEntity(jsonString, ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);

            System.out.println("CALL: " + url);
            try (final CloseableHttpResponse response = httpclient.execute(httpPost)) {
                final int statusCode = response.getStatusLine().getStatusCode();
                final String responseBody = EntityUtils.toString(response.getEntity());
                if (statusCode == 200) {
                    System.out.println();
                    System.out.println("SUCCESS! JACOCO RESULTS PUBLISHED...");
                    System.out.println();
                }
            } finally {
                try {
                    httpclient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (final Exception e) {
            System.err.println("Error! Jacoco send result failed...");
            e.printStackTrace();
        }
    }

    @SneakyThrows
    public static void main(final String[] args) {
        new JacocoPublisher().execute();
    }

}
