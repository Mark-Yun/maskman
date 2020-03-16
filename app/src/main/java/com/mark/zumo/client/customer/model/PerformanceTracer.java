package com.mark.zumo.client.customer.model;

import com.google.firebase.perf.FirebasePerformance;

/**
 * Created by mark on 20. 3. 17.
 */
public enum  PerformanceTracer {

    INSTANCE;

    private final FirebasePerformance firebasePerformance;

    PerformanceTracer() {
        firebasePerformance = FirebasePerformance.getInstance();
    }
}
