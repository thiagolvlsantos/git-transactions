package io.github.thiagolvlsantos.git.transactions.scope;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AspectScope implements Scope {

	private List<Map<String, Object>> scope = new LinkedList<>();
	private List<Map<String, Runnable>> destruction = new LinkedList<>();

	public void openAspect() {
		scope.add(new HashMap<>());
		destruction.add(new HashMap<>());
		if (log.isDebugEnabled()) {
			log.info("Scope openned.");
		}
	}

	@Override
	public Object get(String name, ObjectFactory<?> objectFactory) {
		Object object = get(name);
		if (object == null) {
			object = objectFactory.getObject();
			if (log.isDebugEnabled()) {
				log.debug("AspectScope put({},{})", name, object);
			}
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
				if (log.isDebugEnabled()) {
					log.debug("Runtime error on remove: " + e.getMessage(), e);
				}
			} catch (Exception e) {
				if (log.isDebugEnabled()) {
					log.debug("Typed error on remove: " + e.getMessage(), e);
				}
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
		if (log.isTraceEnabled()) {
			log.trace("Resolve contextual object: {}", key);
		}
		return null;
	}

	@Override
	public String getConversationId() {
		if (log.isTraceEnabled()) {
			log.trace("getConversationId()");
		}
		return null;
	}

	public void closeAspect() {
		scope.remove(scope.size() - 1);
		destruction.remove(destruction.size() - 1);
		if (log.isDebugEnabled()) {
			log.debug("Scope closed.");
		}
	}
}