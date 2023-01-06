## [Day 15: Beacon Exclusion Zone](https://adventofcode.com/2022/day/15)

**Part 1 Solution**: 6,425,133
```text
== Perf report
Runtime: 4,375 ms
Memory : 1,523,854 kB
```

**Part 2 Solution**: 10,996,191,429,555
```text
== Perf report
Runtime: 64 ms
Memory : 4,191 kB
```

### Reflections
_I solved part1 a couple of days before part2, when I'm writing this. Most of my reflections are on part 2._

This problem tempts you into brute force approaches for modeling the sensor->beacon circles. The example has 14 sensor/beacon pairs and the puzzle input has 28, but the area of circles and part2's search area are several orders of magnitude larger in the puzzle. Hence, the brute force approach fails when you try to model all discrete points in a circle. We need to solve the puzzles with techniques that let us ignore most of the points.

For the first, we "bend" the radius along the target line (if it can reach) and see how many unique points were covered. For the puzzle input this generates 13.4M points, only ~6M of which are unique. That's the most likely reason perf is much worse than part2.

Part2 has the same trap. The brute force approach is to model the 400M X 400M search space and punch out anything covered by the sensor/beacon circles. But that's a lot of points to model! And repeated work! The radiuses are around 100k. I didn't even both with this.

My initial insight is the single uncovered point must be in the 'perimeter' of the circles i.e. one point outside the radius. Maybe we can generate these points and check which aren't covered by a sensor/beacon circle? Lol we can't, it OOMs when I try this. Generating the 20 x 100k perimeter points is OK but the "does this lie in any perimeter" OOMs.

Second attempt reduced the size of perimeter points by modeling it as a set of four line segments instead of `Set<Point>`. The perimeter is now the same size no matter how large the circle is! Without a set of points to check we have to generate them when needed. But we don't care about every point, just those that could contain the target point. A bit of thinking lead me to the realization this would be the intersection point of at least three perimeters. A helpful illustration where `X` is the uncovered point; `1-3` are circumferences (perimeter-1) of circles:
```text
2...1.3
.2.1.3.
..1X3..
.1.2...
1.3.2..
```

Now the problem is middle school algebra. "Do these lines intersect?" which is easy to answer if [the slope and intercept are known][wiki-intercept].

The spicy comparison is the O(n^2) comparisons for "given an intersection point, is it contained in any circle?" We can't really decrease the number of circles to check (28) so we need to whittle away the candidates:
1. All intersection points: 1,025
1. Intersection point occurs >= 3 times: 53
1. Within search area: 49

The whole thing runs 70->80ms!

I can measure perf now and found a few surprising things. First, the functional method (below) for generating intersection points adds ~700kb in memory. Intermediate data structures for filtering didn't affect runtime or memory, but that may be because I used `.also` to log intermediate size. Things to play around with in the future.

```kotlin
val intersectionFreq = reports.flatMap { r ->
    r.perimeter.flatMap { p ->
        perimeters.mapNotNull { pp -> p.intersectionPointWith(pp) }
    }
}.groupBy { it }.mapValues { (_, v) -> v.size }
```

[wiki-intercept]: https://en.wikipedia.org/wiki/Line-line_intersection#Given_two_line_equations
