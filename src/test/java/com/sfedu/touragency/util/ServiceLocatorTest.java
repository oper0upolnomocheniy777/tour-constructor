package com.sfedu.touragency.util;

import org.junit.*;

import static org.junit.Assert.*;

public class ServiceLocatorTest {
    private ServiceLocator serviceLocator;

    @Before
    public void setUp() throws Exception {
        ServiceLocator.INSTANCE.clear();
        serviceLocator = ServiceLocator.INSTANCE;

    }

    @Test
    public void testPublishByClassAndGet() {
        serviceLocator.publish(Dummy.class);
        assertEquals(new Dummy(), serviceLocator.get(Dummy.class));
    }

    @Test
    public void testGetNothing() {
        assertNull(serviceLocator.get("nothing"));
    }

    @Test
    public void testPublishByNameAndGet() {
        serviceLocator.publish("dummy", new Dummy("dummy"));
        assertEquals(new Dummy("dummy"), serviceLocator.get("dummy"));
        assertNull(serviceLocator.get(Dummy.class));
    }

    @Test
    public void testPublishRemoveByClass() {
        serviceLocator.publish(Dummy.class);
        assertNotNull(serviceLocator.get(Dummy.class));
        serviceLocator.remove(Dummy.class);
        assertNull(serviceLocator.get(Dummy.class));
    }

    private static class Dummy {
        private String s;

        public Dummy() {
            this("42");
        }

        public Dummy(String s) {
            this.s = s;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Dummy)) return false;

            Dummy dummy = (Dummy) o;

            return s != null ? s.equals(dummy.s) : dummy.s == null;

        }

        @Override
        public int hashCode() {
            return s != null ? s.hashCode() : 0;
        }
    }
}
