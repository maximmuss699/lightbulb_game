# Makefile for Maven JavaFX project
# Usage:
#   make compile   — compile JavaFX app
#   make run       — run JavaFX app
#   make package   — create JAR
#   make clean     — clean up target directory

MVN = mvn

.PHONY: all compile run package clean

all: compile

compile:
	$(MVN) compile

run:
	$(MVN) javafx:run

package:
	$(MVN) package

clean:
	$(MVN) clean