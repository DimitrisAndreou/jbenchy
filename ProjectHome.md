# Introduction #

JBenchy (formerly called "aggregator", but that would be confusing) helps you unleash the analytical and expressive power of SQL, to analyze experimental data, without having to write SQL code. It doesn't pretend to be an ORM solution (yikes!), it merely tries to make it as easy as possible for you to create tables appropriate for your experiment, and GROUP BY queries (with filtering and ordering optionally). Because, you know, it is easy to "find the average time per algorithm", but what if your experiment gets more and more complex? How many "simple" loops, gathering statistics, are you going to write by hand? We already have a declarative solution for this problem: SQL!

But who wants to pollute his Java code with random SQL snippets? I don't. I would be quite frustrated to do so. This project is meant to save you from both the frustration of polluting your code with quick and dirty solutions, and polluting it with SQL snippets.

# JBenchy approach #

How is an experiment characterized? By a set of parameters. E.g., lets say you want to benchmark parallel sorting algorithms, on various datasets. Each single experiment would be characterized by a tuple of variables (ALGORITHM, DATASET, NO\_OF\_CPUS, TIME).

For example, you might execute a parallel mergesort on a dataset called "words" (presumably you would separately test for, say, integers), with 4 cpus. Then you would simply run the experiment (preferably many times, lets say a thousand), and after each experiment, you would record the outcome. For example the record could be this tuple:
(ALGORITHM=PARALLEL\_MERGESORT, DATASET=WORDS, NO\_OF\_CPUS=4, TIME=4405)
Running it a thousand times would produce a thousand similar tuples (possibly differing at the TIME component).

You can then run other experiments, varying the algorithm used, the dataset, the number of cpus. All tuples are stored in the same denormalized, flat table.

The point of this? Then you can  for example, easily find, for a particular dataset ("words"), the average TIME per different (ALGORITHM, NO\_OF\_CPU) combinations. It would look like:
```
Records records = aggregator.
   filtered(Filters.eq(Variables.ALGORITHM, Algorithms.PARALLEL_MERGESORT)).
   averageOf(Variables.TIME).per(Variables.ALGORITHM, Variables.NO_OF_CPU);
```

It collect all tuples (data points) having the same ALGORITHM and NO\_OF\_CPU, and computes the average **of those**, thus you get an average per each ALGORITHM x NO\_OF\_CPU combination.

As a further convenience, JBenchy comes with some shortcuts to transform such a result to a graphical picture (either through JFreeChart or gnuplot), so you can e.g. create a jpeg file from it in just a couple of lines, just in case you are interested in that. Otherwise, you should also find the raw aggregate results useful to handle programmatically yourself.

See javadocs (starting from gr.forth.ics.jbenchy) for details. There is a more detailed tutorial (see on the right bar) too.

Also, some support for automatically creating visual diagrams out of reports, see gr.forth.ics.jbenchy.diagram, gr.forth.ics.jbenchy.diagram.gnuplot, gr.forth.ics.jbenchy.diagram.jfreechart packages.