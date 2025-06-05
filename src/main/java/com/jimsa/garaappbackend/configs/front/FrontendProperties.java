package com.jimsa.garaappbackend.configs.front;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "app.frontend")
@Getter
@Setter
public class FrontendProperties {
    private List<String> origins;
}