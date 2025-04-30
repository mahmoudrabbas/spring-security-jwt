package com.workshop.module;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.workshop.security.config.SanitizingDeserializer;

public class SanitizingModule extends SimpleModule {
    public SanitizingModule(){
        super("SanitizingModule", Version.unknownVersion());
        addDeserializer(String.class, new SanitizingDeserializer());
    }
}
