# Simple Lisp
Simple lisp is a toy lisp implementation that supports the bare minimum lisp constructs. Here is a simple program written in simpleplisp 
```
(define sum (fn (x)
                (+ x 10)))

(sum 10)
```
The above function evaluates to 20

Simple lisp is built with Kotlin

## Building
```
gradle build test
```


