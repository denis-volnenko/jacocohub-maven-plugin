package ru.volnenko.plugin.jacocohub;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

public class JacocoPublisherTest {

    private JacocoPublisher publisher = new JacocoPublisher();

    @Test
    @SneakyThrows
    public void test() {
        publisher.execute();
    }

}
