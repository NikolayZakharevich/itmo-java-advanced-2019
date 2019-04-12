#!/usr/bin/env bash

javac ru/ifmo/rain/zakharevich/implementor/Implementor.java
jar cfm Implementor.jar ru/ifmo/rain/zakharevich/implementor/Manifest.txt ru/ifmo/rain/zakharevich/implementor/*.class
rm ru/ifmo/rain/zakharevich/implementor/*.class