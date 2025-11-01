package com.sharehub.hub.config;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.hibernate5.jakarta.Hibernate5JakartaModule; // Import the module
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    /**
     * Registers the Hibernate Module with Jackson.
     * This module teaches Jackson how to safely handle proxies and lazy loading.
     */
    @Bean
    public Module hibernateModule() {
        // We set DISALLOW_UNWRAPPED_ROOT_ELEMENTS to false for compatibility
        Hibernate5JakartaModule module = new Hibernate5JakartaModule();

        // CRUCIAL STEP: Ensure Jackson forces initialization if necessary,
        // OR, more safely, just rely on the module to ignore proxies it can't handle.
        // We keep the default settings and let the module handle the proxies.

        return module;
    }
}