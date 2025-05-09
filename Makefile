# Makefile for Maven JavaFX project
# Usage:
#   make compile   — compile JavaFX app
#   make run       — run JavaFX app
#   make package   — create JAR
#   make clean     — clean up target directory

MVN = mvn

.PHONY: all compile run package clean zip


all: compile

compile:
	$(MVN) compile

run:
	$(MVN) javafx:run

package:
	$(MVN) package

clean:
	$(MVN) clean

zip: clean
	zip xhladi26 src/* readme.txt pom.xml requirements.pdf