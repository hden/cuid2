.PHONY: lint test repl

lint:
	clj-kondo --parallel --lint src test

test:
	lein test

coverage:
	lein cloverage

repl:
	lein repl
