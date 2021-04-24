package com.thiagolvlsantos.gitt.config;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "gitt")
@Data
public class GittConfig {

	private Map<String, Object> repository;

	@SuppressWarnings("unchecked")
	public String get(String path) {
		String[] ps = path.split("\\.");
		Map<String, Object> map = repository;
		for (int i = 0; i < ps.length - 1; i++) {
			map = (Map<String, Object>) map.get(ps[i]);
			if (map == null) {
				throw new RuntimeException("Missing property>" + path);
			}
		}
		return (String) map.get(ps[ps.length - 1]);
	}
}