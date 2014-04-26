package com.yammer.metrics.core;

public class PDToolGUIHealthCheck extends HealthCheck {

    public PDToolGUIHealthCheck (java.lang.String p1) {
        super (p1);
    }

    // need to implement a real heath check here
    //
    @Override
    protected Result check() throws Exception {
        return Result.healthy();
    }
}