package com.thiagolvlsantos.git.transaction.config;

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
		String group = ps[0];
		int pos = group.lastIndexOf("_");
		String qualifier = null;
		if (pos >= 0) {
			qualifier = group.substring(pos + 1);
			ps[0] = group.substring(0, pos) + "_template";
		}
		Map<String, Object> map = repository;
		for (int i = 0; i < ps.length - 1; i++) {
			map = (Map<String, Object>) map.get(ps[i]);
			if (map == null) {
				throw new RuntimeException("Missing property>" + path);
			}
		}
		String result = (String) map.get(ps[ps.length - 1]);
		if (result != null && qualifier != null) {
			result = result.replace("$route$", qualifier);
		}
		return result;
	}
}