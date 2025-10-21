package ru.volnenko.plugin.jacocohub;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.SneakyThrows;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import ru.volnenko.plugin.jacocohub.dto.Counter;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;

@Mojo(name = "publish", defaultPhase = LifecyclePhase.COMPILE)
public class JacocoPublisher extends AbstractMojo {

    private static final String JACOCO = "./target/site/jacoco/jacoco.xml";

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        final File file = new File(JACOCO);
        final boolean exists = file.exists();
        if (!exists) {
            System.err.println("WARN! Jacoco results not found...");
        }
        try {
            final Counter counter = new Counter();
            final XmlMapper mapper = new XmlMapper();
            final List<LinkedHashMap> root = mapper.readValue(new File(JACOCO), List.class);
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
                        counter.instructionCovered = Integer.parseInt(covered);
                        counter.instructionMissed = Integer.parseInt(missed);
                        counter.instructionTotal = counter.instructionCovered + counter.instructionMissed;
                        counter.instructionPercent = counter.instructionCovered / counter.instructionTotal * 100;
                    }
                    if ("BRANCH".equals(type)) {
                        counter.branchCovered = Integer.parseInt(covered);
                        counter.branchMissed = Integer.parseInt(missed);
                        counter.branchTotal = counter.branchCovered + counter.branchMissed;
                        counter.branchPercent = counter.branchCovered / counter.branchTotal * 100;
                    }
                    if ("LINE".equals(type)) {
                        counter.lineCovered = Integer.parseInt(covered);
                        counter.lineMissed = Integer.parseInt(missed);
                    }
                    if ("COMPLEXITY".equals(type)) {
                        counter.complexityCovered = Integer.parseInt(covered);
                        counter.complexityMissed = Integer.parseInt(missed);
                    }
                    if ("METHOD".equals(type)) {
                        counter.methodCovered = Integer.parseInt(covered);
                        counter.methodMissed = Integer.parseInt(missed);
                    }
                    if ("CLASS".equals(type)) {
                        counter.classCovered = Integer.parseInt(covered);
                        counter.classMissed = Integer.parseInt(missed);
                    }
                }
            }
            counter.percent = (counter.instructionPercent + counter.branchPercent) / 2;
        } catch (final Exception e) {
            System.err.println("Error! Jacoco results parse failed...");
        }
    }

    @SneakyThrows
    public static void main(final String[] args) {
        new JacocoPublisher().execute();
    }

}
