package com.workshop.security.config;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.workshop.annotation.SkipSanitizing;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

import java.io.IOException;

public class SanitizingDeserializer extends JsonDeserializer<String> implements ContextualDeserializer {
    private final boolean skip;

    private static final PolicyFactory policyFactory = Sanitizers.FORMATTING
            .and(Sanitizers.LINKS);

    public SanitizingDeserializer() {
        this.skip = false;
    }
    public SanitizingDeserializer(boolean hasSkipAnnotation, boolean skip) {
        this.skip = skip;
    }

    public SanitizingDeserializer(boolean skip) {
        this.skip = skip;
    }

    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
//        AnnotatedMember member = p.getCurrentValue()==null?null:ctxt.getParser().getCurrentLocation()==null?null:ctxt.getContextualType().get();
//        String originalText = p.getText();
//        return policyFactory.sanitize(originalText);

        String value = p.getText();
        if (skip) {
            return value;
        }
        return policyFactory.sanitize(value);
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
        if (property != null) {
            boolean hasSkipAnnotation = property.getAnnotation(SkipSanitizing.class) != null
                    || property.getMember().hasAnnotation(SkipSanitizing.class);
            
            return new SanitizingDeserializer(hasSkipAnnotation);
        }
        return this;
    }
}
