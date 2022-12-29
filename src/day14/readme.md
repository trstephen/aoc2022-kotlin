## [Day 14: Regolith Reservoir](https://adventofcode.com/2022/day14)

**Part 1 Solution**: 825

**Part 2 Solution**: 26729

### Reflections
I eschewed the grid model, gambling that my implementation would "just work" because debugging would be a giant PITA with no easy way to visualize what I'm working on. Best I could do is log the `DescendResult` iterations. Thankfully, I only had to do that once for a sign error (descending is `y++`, not `y--` as the english would imply!).

With no grid I was free to treat the accumulated sand and rocks as just "places where sand can't fall" and store this in an `occupiedPoints: Set<Point>`. Reading through some [discussion][reddit-14] on this problem, performance was a common concern. To address this I set `occupiedPoints` into the `object` scope so it could be reused by various functions without copy-by-value of a Big Set. I think this had the desired effect: my Part02 solution ran (anecdotally) in a few hundred ms; commenters were saying this ran for several minutes in Kotlin.

The core logic is janky. I stuck it in two `while` loops: first for "have we hit the terminal condition?" and the inner one for "can we keep descending the current sand point?" I say it's janky because it's not really functional, but note concerns about copying large objects. I was able to implement Part02 with minimal changes, though! A slight modification to the floor height and terminal condition. I think the `DescendResult` helped me modify easily because it models three cases (`AtFloor`, `Falling`, `Settled`) for the core logic to play with. My first iteration of `descend` returned a `Point?`, null when the point would 'fall through' the floor; that would have been a huge pain to extend to Part02.

[reddit-14]: https://old.reddit.com/r/adventofcode/comments/zli1rd/2022_day_14_solutions/
