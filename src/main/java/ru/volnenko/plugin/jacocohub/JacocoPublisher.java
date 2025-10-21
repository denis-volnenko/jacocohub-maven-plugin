package ru.volnenko.plugin.jacocohub;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.SneakyThrows;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import ru.volnenko.plugin.jacocohub.dto.Counter;
import ru.volnenko.plugin.jacocohub.dto.JacocoResultDto;

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
            final JacocoResultDto counter = new JacocoResultDto();
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
                        counter.setInstructionCovered(Integer.parseInt(covered));
                        counter.setInstructionMissed(Integer.parseInt(missed));
//                        counter.instructionTotal = counter.instructionCovered + counter.instructionMissed;
//                        counter.instructionPercent = counter.instructionCovered / counter.instructionTotal * 100;
                    }
                    if ("BRANCH".equals(type)) {
                        counter.setBranchCovered(Integer.parseInt(covered));
                        counter.setBranchMissed(Integer.parseInt(missed));
//                        counter.branchTotal = counter.branchCovered + counter.branchMissed;
//                        counter.branchPercent = counter.branchCovered / counter.branchTotal * 100;
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
//            counter.percent = (counter.instructionPercent + counter.branchPercent) / 2;
        } catch (final Exception e) {
            System.err.println("Error! Jacoco results parse failed...");
        }
    }

    @SneakyThrows
    public static void main(final String[] args) {
        new JacocoPublisher().execute();
    }

}
