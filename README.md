# Java Collections Framework Benchmark Tool
Developed for test purposes with old code base from Leo Lewis.

The performance of data structures, especially collections, 
is a recurrent subject when coding. If you have never heard about such a topic, 
here is the chance, otherwise it's the hundredth time you'll have seen such title, 
and you are probably thinking "Another article about this topic, 
I'll probably not learn anything new, but anyway I'm bored so I'm going to read it …". 
Furthermore, you are probably 90% right, nothing new here, 
but I promise you a couple of colorful and 
beautiful charts that we do not have the opportunity to see every day 
(and the ability to create your own).
The first time I started wondering about collection performances was 
when I started working with some > 100 000 elements collections. 
At that time, I heard some bad jokes such as, 
"I just understood why the Java logo is a cup of coffee because 
Java collections are so slow that when manipulating them, 
you have the time to go and grab some coffee before they do the job.
At that time, I want to use the implementation of a java.util.List that 
would have good performances on all the standard methods provided by the interface 
(let's say get(index), add(Object), remove(Object), remove(index), contains(Object), 
iterator(), add other methods that you like), without wondering about memory usage 
(I mean, even this List would take 4 times the size of a LinkedList 
it wouldn't be a big deal).
In other words, some List that would not be instantiated a million times 
in my application, but a couple of times and each instance 
will have great performances. For example, the model of a GUI Table, 
or some other GUI component, which data will evolve frequently, 
and which performances will sometimes be critical.

![alt text](http://4.bp.blogspot.com/-eor4DBhjVFU/UEFtIsHli3I/AAAAAAAAAGA/vg6oUjFMjDU/s1600/ListPerf.png)

Source: https://dzone.com/articles/java-collection-performance



# Example Command-Line Output:

Performances of java.util.ArrayList populated with 100000 elt(s)
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
add 100000 elements ... 11954628ns
remove 10000 elements given Object ... 2198635266ns
addAll 1000 times 1000 elements ... 9876948ns
contains 1000 times ... 171416227ns
removeAll 10 times 1000 elements ... 2146870488ns
iterator 100000 times ... 1954648ns
containsAll 5000 times ... 289926603ns
toArray 5000 times ... 281973736ns
clear ... 1229017ns
retainAll 10 times ... 222959390ns
add at a given index 100000 elements ... 1538695909ns
addAll 1000 times 1000 elements at a given index ... 191518350ns
get 50000 times ... 1588727ns
indexOf 5000 times ... 653176755ns
lastIndexOf 5000 times ... 679453638ns
set 100000 times ... 9351907ns
subList on a 25000 elts sublist 100000 times ... 3456169ns
listIterator 100000 times ... 2791617ns
listIterator at a given index 100000 times ... 3647271ns
remove 10000 elements given index (index=list.size()/2) ... 60270354ns
Benchmark done in 8.785s
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Performances of java.util.LinkedList populated with 100000 elt(s)
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
add 100000 elements ... 11598953ns
remove 10000 elements given Object ... 2999367471ns
addAll 1000 times 1000 elements ... 28390378ns
contains 1000 times ... 294803495ns
removeAll 10 times 1000 elements ... 2240336768ns
iterator 100000 times ... 15748563ns
containsAll 5000 times ... 347938163ns
toArray 5000 times ... 2004670509ns
clear ... 2135799ns
retainAll 10 times ... 267656070ns
add at a given index 100000 elements ... 14820140198ns
addAll 1000 times 1000 elements at a given index ... 65481606ns
get 50000 times ... 1810218479ns
indexOf 5000 times ... 1328047502ns
lastIndexOf 5000 times ... 1355441980ns
set 100000 times ... 3559965400ns
subList on a 25000 elts sublist 100000 times ... 6006734ns
listIterator 100000 times ... 4041602ns
listIterator at a given index 100000 times ... 3540624751ns
remove 10000 elements given index (index=list.size()/2) ... 675866715ns
Benchmark done in 35.609s
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Performances of java.util.HashSet populated with 100000 elt(s)
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
add 100000 elements ... 4615123ns
remove 10000 elements given Object ... 1713849ns
addAll 1000 times 1000 elements ... 19392449ns
contains 1000 times ... 490512ns
removeAll 10 times 1000 elements ... 2185246ns
iterator 100000 times ... 6270990ns
containsAll 5000 times ... 56066540ns
toArray 5000 times ... 21761596ns
clear ... 22203ns
retainAll 10 times ... 331913ns
Benchmark done in 0.242s
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Performances of java.util.LinkedHashSet populated with 100000 elt(s)
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
add 100000 elements ... 2858868ns
remove 10000 elements given Object ... 1931466ns
addAll 1000 times 1000 elements ... 15980706ns
contains 1000 times ... 59566ns
removeAll 10 times 1000 elements ... 2044548ns
iterator 100000 times ... 7611056ns
containsAll 5000 times ... 463613ns
toArray 5000 times ... 21722742ns
clear ... 13056ns
retainAll 10 times ... 338500ns
Benchmark done in 0.207s
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Performances of java.util.TreeSet populated with 100000 elt(s)
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
add 100000 elements ... 4633693ns
remove 10000 elements given Object ... 1130269ns
addAll 1000 times 1000 elements ... 29157931ns
contains 1000 times ... 7595372ns
removeAll 10 times 1000 elements ... 2319594ns
iterator 100000 times ... 7951915ns
containsAll 5000 times ... 653099ns
toArray 5000 times ... 11392183ns
clear ... 12108ns
retainAll 10 times ... 477196ns
Benchmark done in 0.221s
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Performances of java.util.PriorityQueue populated with 100000 elt(s)
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
add 100000 elements ... 11318800ns
remove 10000 elements given Object ... 2929849124ns
addAll 1000 times 1000 elements ... 46453076ns
contains 1000 times ... 491613832ns
removeAll 10 times 1000 elements ... 2234806171ns
iterator 100000 times ... 1680046ns
containsAll 5000 times ... Timeout (>15000000000ns) after 406 loop(s)
toArray 5000 times ... 256311786ns
clear ... 591605ns
retainAll 10 times ... 237720798ns
Benchmark done in 21.406s
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Performances of java.util.ArrayDeque populated with 100000 elt(s)
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
add 100000 elements ... 4640536ns
remove 10000 elements given Object ... 2433937797ns
addAll 1000 times 1000 elements ... 21535318ns
contains 1000 times ... 132950417ns
removeAll 10 times 1000 elements ... 2276423884ns
iterator 100000 times ... 2265735ns
containsAll 5000 times ... 291926791ns
toArray 5000 times ... 269968353ns
clear ... 1545539ns
retainAll 10 times ... 260729278ns
Benchmark done in 5.813s
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
java.util.ArrayList Object size : 440932 bytes
java.util.LinkedList Object size : 316950 bytes
java.util.HashSet Object size : 0 bytes
java.util.LinkedHashSet Object size : 0 bytes
java.util.TreeSet Object size : 0 bytes
java.util.PriorityQueue Object size : 580818 bytes
java.util.ArrayDeque Object size : 10609 bytes
