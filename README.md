# trollscript

Inspired by [Tom Bell's ruby implementation](https://github.com/tombell/trollscript) of trollscript, I have created a clojure trollscript interpreter.

## Usage

Using lein, your script can either run from the repl:

    lein repl

``` clojure
(interpret <trollscript here>)
```

or from the command line:

    lein run <trollscript here>

or import in your project.clj file

```clojure
(:dependencies [trollscript "1.0.0"])
```

## License

Copyright (C) 2011 Josh Comer

Distributed under the Eclipse Public License, the same as Clojure.
