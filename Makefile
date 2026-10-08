SCALA_VERSION := 3.8.4
BIN_DIR ?= $(HOME)/.local/bin

.PHONY: build install

build:
	scala --power package . --server=false --scala $(SCALA_VERSION) --main-class com.exerinity.timein.Main --assembly --preamble -o timein --force

install: build
	install -Dm755 timein "$(BIN_DIR)/timein"