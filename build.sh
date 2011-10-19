#!/bin/bash

. common.sh
scalac -cp "$JARS" -d bin Tool.scala
