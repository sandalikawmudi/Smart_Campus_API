package com.smartcampus.api;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import java.util.HashMap;
import java.util.Map;

@ApplicationPath("/api/v1")
public class SmartCampusApplication extends Application {
    
    @Override
    public Map<String, Object> getProperties() {
        Map<String, Object> props = new HashMap<>();
        // Optional: configure application wide properties if needed
        return props;
    }
}
