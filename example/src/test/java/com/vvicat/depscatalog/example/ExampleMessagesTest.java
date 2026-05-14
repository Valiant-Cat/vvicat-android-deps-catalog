package com.vvicat.depscatalog.example;

import org.junit.Assert;
import org.junit.Test;

public class ExampleMessagesTest {
    @Test
    public void brandNameReturnsExpectedValue() {
        Assert.assertEquals("VVICAT", ExampleMessages.brandName());
    }
}
