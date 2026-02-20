package com.sfedu.touragency.util;

import org.junit.*;

import static org.junit.Assert.*;

public class TryOptionalUtilTest {

    @Test
    public void testFail() {
        assertFalse(TryOptionalUtil.of(this::fail).isPresent());
    }

    @Test
    public void testSuccess() {
        assertTrue(TryOptionalUtil.of(this::success).isPresent());
    }

    private String fail() {
        throw new RuntimeException();
    }

    private String success() {
        return "success";
    }
}
