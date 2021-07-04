#!/usr/bin/env python3

from collections import defaultdict
from functools import reduce
import json
from operator import mul
import sys

def collect_regex_benchmarks(filename):
    data = read_file(filename)
    batches = defaultdict(dict)
    for result in data:
        score = result['primaryMetric']['score']
        test = result['benchmark'].split('.')[-1]
        if not 'params' in result or not 'regexString' in result['params']:
            continue
        batches[result['params']['regexString']][test] = score
    return batches

def java_pattern_comparison(data):
    ratio = 1
    count = data
    ratios = [v['javaRegex'] / v['pattern'] for v in data.values()]
    ratio = reduce(mul, ratios, 1)
    geomean = ratio ** (1 / len(data))
    best = max(ratios)
    worst = min(ratios)
    return {
        'geomean': geomean,
        'best': best,
        'worst': worst
    }

def collated(data):
    collated = {}
    for result in data:
        key = result['benchmark'] + ':' + result['params']['regexString']
        collated[key] = result
    return collated

def compare_runs(loc1, loc2):
    data1 = collated(read_file(loc1))
    data2 = collated(read_file(loc2))
    ratio = 1
    left_better = 0
    right_better = 0
    for k, result in data1.items():
        score = result['primaryMetric']['score']
        test = result['benchmark'].split('.')[-1]
        difference = score / data2[k]['primaryMetric']['score']
        print(result['benchmark'] + ':' + str(difference))
        if difference > 1.01:
            right_better += 1
        elif difference < .99:
            left_better += 1
        ratio *= difference
    return {
        'count': count,
        'geomean': ratio ** (1 / len(data)),
        'left_better': left_better,
        'right_better': right_better
    }

def read_file(filename):
    with open(filename) as f:
        return json.loads(f.read())

if __name__ == '__main__':
    print(java_pattern_comparison(collect_regex_benchmarks(sys.argv[1])))
    print(collect_regex_benchmarks(sys.argv[1]))
