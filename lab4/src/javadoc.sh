#!/usr/bin/env bash

kgeorgiy=info/kgeorgiy/java/advanced/implementor/

javadoc -cp artifacts/:lib/ -d ../javadoc -private ru/ifmo/rain/zakharevich/implementor/*.java ${kgeorgiy}ImplerException.java ${kgeorgiy}Impler.java ${kgeorgiy}JarImpler.java