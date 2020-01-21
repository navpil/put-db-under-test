package io.github.navpil.dbtests;

import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;

public class TryTestContainers {

    @Rule
    public GenericContainer redis = new GenericContainer<>("redis:5.0.3-alpine")
            .withExposedPorts(6379);


    @Test
    public void testrun() {
        final String address = redis.getContainerIpAddress();
        final Integer port = redis.getFirstMappedPort();

        System.out.println(address);
        System.out.println(port);
    }
}
