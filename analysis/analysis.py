#!/usr/bin/env python3

from collections import defaultdict
from functools import reduce
import json
from operator import mul
import re
import sys

SPECIAL_CHARS = re.compile(r"[|*{}+]")


def collect_regex_benchmarks(filename):
    data = read_file(filename)
    batches = defaultdict(dict)
    for result in data:
        score = result["primaryMetric"]["score"]
        test = result["benchmark"].split(".")[-1]
        if not "params" in result or not "regexString" in result["params"]:
            continue
        regex_string = result["params"]["regexString"]
        batches[regex_string][test] = score
        batches[regex_string]["category"] = categorize(regex_string)
    return batches


def java_pattern_comparison(data):
    ratio = 1
    count = data
    key_set = defaultdict(list)
    for k in data.keys():
        key_set[categorize(k)].append(k)
    comparisons = {}
    for key in key_set:
        comparisons[key] = compare_keys(data, key_set[key])
    return comparisons


def compare_keys(data, keys):
    ratios = [data[k]["javaRegex"] / data[k]["pattern"] for k in keys]
    ratio = reduce(mul, ratios, 1)
    geomean = ratio ** (1 / len(keys))
    best = max(ratios)
    worst = min(ratios)
    return {"geomean": geomean, "best": best, "worst": worst}


def collated(data):
    collated = {}
    for result in data:
        try:
            key = result["benchmark"] + ":" + result["params"]["regexString"]
            collated[key] = result
        except Exception:
            pass
    return collated


def compare_runs(loc1, loc2, threshold=0.01, no_java=False):
    data1 = collated(read_file(loc1))
    data2 = collated(read_file(loc2))
    ratio = 1
    left_better = []
    right_better = []
    for k, result in data1.items():
        if no_java and "java" in result["benchmark"]:
            continue

        score = result["primaryMetric"]["score"]
        test = result["benchmark"].split(".")[-1]

        key = test
        if "regexString" in result["params"]:
            regexString = result["params"]["regexString"]
            key += f"({regexString})"

        difference = score / data2[k]["primaryMetric"]["score"]
        cmp = {"key": key, "difference": difference, "category": categorize(key)}
        if difference > 1 + threshold:
            right_better.append(cmp)
        elif difference < (1 / (1 + threshold)):
            left_better.append(cmp)
        ratio *= difference
    return {
        "count": len(data1),
        "geomean": ratio ** (1 / len(data1)),
        "left_better": left_better,
        "right_better": right_better,
    }


def categorize(key):
    if key.startswith("pattern(") and key.endswith(")"):
        key = key[len("pattern(") : -1]
    if SPECIAL_CHARS.search(key):
        if not SPECIAL_CHARS.search(key.replace("|", "")):
            return "ALTERNATION"
        return "ARBITRARY"
    return "LITERAL"


def read_file(filename):
    with open(filename) as f:
        return json.loads(f.read())


if __name__ == "__main__":
    if sys.argv[1] == "--compare":
        print(compare_runs(sys.argv[2], sys.argv[3], 0.05, True))
    else:
        for k, v in java_pattern_comparison(
            collect_regex_benchmarks(sys.argv[1])
        ).items():
            print(k, v)
