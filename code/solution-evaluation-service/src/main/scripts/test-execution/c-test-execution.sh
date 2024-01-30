#!/usr/bin/bash

# exit codes will be the amount of points to be deducted
# deduction equal to 255, will be recognized as error

DEDUCTION_PER_FAIL=20

if [ "$#" -lt 2 ]; then
    echo "Not enough arguments provided. Usage: $0 <path> <official_repo_url>"
    exit 1
fi

# cd into cloned repo directory
cd "$1" || exit 255

# clone the official repo to get the official tests
rm -rf tests/ # have to remove any tests/directories

git clone "$2" "official_repo"

mv "official_repo/tests" "tests"

# run all tests
readarray -t inputFiles < <(find . -type f -name 'input_*')
readarray -t outputFiles < <(find . -type f -name 'output_*')

if (( ${#inputFiles[@]} != ${#outputFiles[@]} )); then
    echo "Number of input files does not match number of output files, exiting..."
    exit 255
fi

passedTests=0
failedTests=0
for (( i=1; i<=${#inputFiles[@]}; i++ )); do
    echo "Running test case $i"
    ./executable < tests/input_"$i".txt > output
    diff output tests/output_"$i".txt > difference
    if [ ! -s "difference" ]; then
      passedTests=$((passedTests + 1))
    else
      echo "Test case $i failed"
      failedTests=$((failedTests + 1))
    fi
done

totalDeduction=$((failedTests * DEDUCTION_PER_FAIL))

if (( totalDeduction > 100 )); then
    exit 100
fi

exit $totalDeduction