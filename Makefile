# Makefile for Maven JavaFX project
# Usage:
#   make compile   — compile JavaFX app
#   make run       — run JavaFX app
#   make package   — create JAR
#   make clean     — clean up target directory

MVN = mvn

.PHONY: all compile run package clean zip


all: compile javadoc package

compile:
	$(MVN) compile
run:
	$(MVN) javafx:run

package:
	$(MVN) package

clean:
	$(MVN) clean

javadoc:
	$(MVN) javadoc:javadoc


zip: clean
	mkdir -p xhladi26
	cp -r src readme.txt pom.xml requirements.pdf xhladi26/
	zip -r xhladi26.zip xhladi26
	rm -rf xhladi26
