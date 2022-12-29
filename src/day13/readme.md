## [Day 13: Distress Signal](https://adventofcode.com/2022/day13)

**Solution**: 6623

## Part 2

**Solution**: 23049

## Reflections
This was fun! After I gave up on implementing my own parser ^_^;

I used this as an excuse to learn the [`kotlinx.serialization`][gh-kotlinx-ser] lib. MEGO while reading the custom serialization stuff, and the pain of stuffing this in to my own data class didn't seem necessary. Mapping the raw `JsonElement`s to my custom classes felt like a good balance.

After that it was an exercise in implementing `Comparable` and reading the biz logic _incredibly closely_. With `Comparable` I was able to reuse default comparisons for `Int`. Only had to implement the `Int/List` comparison (wrap `Int` in list and reuse `compare`) and `List/List`. The latter was kind of awkward (note the nested `when`) but the sample input covered all the edge cases and I banged those out until it matched. Taking the time to pretty-print the lists and comparison results made this easy.

Actually solving the problem was trivial after implementing `Comparable`. Just `.filter { left <= right}` and `sorted()` to do the work.

[gh-kotlinx-ser]: https://github.com/Kotlin/kotlinx.serialization/
