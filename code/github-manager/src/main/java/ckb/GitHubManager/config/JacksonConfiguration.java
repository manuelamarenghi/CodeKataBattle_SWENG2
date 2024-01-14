package ckb.GitHubManager.config;

import ckb.GitHubManager.deserializer.ImmutablePairDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfiguration {

    @Bean
    public SimpleModule immutablePairModule() {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(ImmutablePair.class, new ImmutablePairDeserializer());
        return module;
    }
}