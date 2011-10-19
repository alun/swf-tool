#!/bin/bash

. common.sh
scala -cp "bin:$JARS" Tool "$@"
