#!/bin/bash

javac -cp lox -d build/ -Werror -implicit:none lox/*.java && java -cp build/ lox.Lox $@;

~/.jdks/openjdk-21.0.2/bin/java -classpath ./out/production/Lox tool.GenerateAst ./lox/
~/.jdks/openjdk-21.0.2/bin/java -classpath ./out/production/Lox lox.Lox ./main.lox
