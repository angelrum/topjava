package ru.javawebinar.topjava;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalTime;

public class SimplePrintRule extends TestWatcher {

    private final Logger log;

    private long start;

    public SimplePrintRule(Class clazz) {
        this.log = LoggerFactory.getLogger(clazz);
    }

    @Override
    protected void starting(Description description) {
        start = System.currentTimeMillis();
    }

    @Override
    protected void finished(Description description) {
        StringBuilder builder = new StringBuilder("Method name: ")
                .append(description.getMethodName())
                .append(". ")
                .append("Total time = ")
                .append(System.currentTimeMillis() - start)
                .append(" ms");
        log.info(builder.toString());
    }
}
