#!/usr/bin/env bash

javac ru/ifmo/rain/zakharevich/implementor/Implementor.java
java -cp . -p lib/:artifacts/ -m info.kgeorgiy.java.advanced.implementor jar-class ru.ifmo.rain.zakharevich.implementor.Implementor
rm ru/ifmo/rain/zakharevich/implementor/*.class