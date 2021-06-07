package io.github.thiagolvlsantos.git.transactions;

import io.github.thiagolvlsantos.git.transactions.provider.IGitRouter;

public class RouterName implements IGitRouter {
	@Override
	public String route(String group, Object[] args) {
		String name = String.valueOf(args[0]);
		char last = name.charAt(name.length() - 1);
		if (last == '0' || last == '2' || last == '4' || last == '6' || last == '8') {
			return "even";
		}
		return "odd";
	}
}