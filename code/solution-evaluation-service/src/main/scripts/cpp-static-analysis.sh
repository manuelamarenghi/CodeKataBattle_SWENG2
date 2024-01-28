#!/usr/bin/bash

cd "$1" || (echo "failed to cd into working directory"; exit)

cppcheck --xml --enable=all *.cpp &> "error-log"

echo "errors"
grep -E -cc '(severity="error")' "error-log"	

echo "warnings"
grep -E -cc '(severity="warning")' "error-log"

echo "style"
grep -E -cc '(severity="style")' "error-log"
