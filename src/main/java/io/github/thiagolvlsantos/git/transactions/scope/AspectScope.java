package io.github.thiagolvlsantos.git.transactions.scope;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AspectScope implements Scope {

	private List<Map<String, Object>> scope = new ArrayList<>();
	private List<Map<String, Runnable>> destruction = new ArrayList<>();

	public void openAspect() {
		scope.add(new HashMap<>());
		destruction.add(new HashMap<>());
		log.debug("Scope openned.");
	}

	@Override
	public Object get(String name, ObjectFactory<?> objectFactory) {
		Object object = get(name);
		if (object == null) {
			object = objectFactory.getObject();
			log.debug("AspectScope put({},{})", name, object);
			put(name, object);
		}
		return object;
	}

	private Object get(String name) {
		for (int i = scope.size() - 1; i >= 0; i--) {
			if (scope.get(i).containsKey(name)) {
				return scope.get(i).get(name);
			}
		}
		return null;
	}

	private Map<String, Object> current() {
		return scope.get(scope.size() - 1);
	}

	private void put(String name, Object object) {
		current().put(name, object);
	}

	@Override
	public Object remove(String name) {
		Runnable callback = getD(name);
		if (callback != null) {
			try {
				callback.run();
			} catch (RuntimeException e) {
				log.debug("Runtime error on remove: " + e.getMessage(), e);
			} catch (Exception e) {
				log.debug("Typed error on remove: " + e.getMessage(), e);
			}
		}
		for (int i = scope.size() - 1; i >= 0; i--) {
			if (scope.get(i).containsKey(name)) {
				return scope.get(i).remove(name);
			}
		}
		return null;
	}

	private Runnable getD(String name) {
		for (int i = destruction.size() - 1; i >= 0; i--) {
			if (destruction.get(i).containsKey(name)) {
				return destruction.get(i).get(name);
			}
		}
		return null;
	}

	@Override
	public void registerDestructionCallback(String name, Runnable callback) {
		putD(name, callback);
	}

	private void putD(String name, Runnable object) {
		currentD().put(name, object);
	}

	private Map<String, Runnable> currentD() {
		return destruction.get(destruction.size() - 1);
	}

	@Override
	public Object resolveContextualObject(String key) {
		log.trace("Resolve contextual object: {}", key);
		return null;
	}

	@Override
	public String getConversationId() {
		log.trace("getConversationId()");
		return null;
	}

	public void closeAspect() {
		int scopePos = scope.size() - 1;
		if (!scope.isEmpty()) {
			if (scopePos >= 0) {
				scope.remove(scopePos);
			} else {
				log.info("Invalid scope:{}", scopePos);
			}
		}
		int destructionPos = destruction.size() - 1;
		if (!destruction.isEmpty()) {
			if (destructionPos >= 0) {
				destruction.remove(destructionPos);
			} else {
				log.info("Invalid destruction:{}", destructionPos);
			}
		}
		log.debug("Scope closed.");
	}
}