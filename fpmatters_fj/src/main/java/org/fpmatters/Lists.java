package org.fpmatters;

import fj.F;
import fj.F2;
import fj.data.List;
import static fj.data.List.*;

import static fj.Function.*;
/**
 * Created by fmap on 21.10.15.
 */
public class Lists {

    public static void main(String[] args) {

        //listof ∗ ::= Nil | Cons ∗ (listof ∗)
        println("an empty list:", list());
        assertThat(list().equals(nil()));

        println("a list:", list(1, 2, 3));

        assertThat(cons(1,cons(2,cons(3,nil()))).equals(list(1,2,3)));

        println("Tail:", list(1,2,3).tail());
        println("Tail once more:", list(1,2,3).tail().tail());
        assertThat(list(1, 2, 3).tail().tail().tail().equals(nil()));



        /*
         * sum Nil = 0
           sum (Cons n list) = n + sum list
         */
        assertThat(sumSpecific(list(1, 2, 3, 4)) == 10);

        // sum = foldr (+) 0
        assertThat(list(1, 2, 3, 4).foldRight((x, y) -> x + y, 0) == 10);

        //product = foldr (∗) 1
        assertThat(list(1, 2, 3, 4).foldRight((x, y) -> x * y, 1) == 24);

        //anytrue = foldr (∨) False
        assertThat(
                list(false, true, false).foldRight((x, y) -> x || y, false) == true
        );
        assertThat(
                list(false).foldRight( (x,y) -> x || y, false) == false
        );

        //it’s obvious that (foldr Cons Nil ) just copies a list
        assertThat(
                list(1,2,3).foldRight((x,y)->cons(x,y), nil()).equals(list(1, 2, 3))
        );

        /*
            Since one list can be
            appended to another by Cons ing its elements onto the front, we find
            append a b = foldr Cons b a
         */
        assertThat(
                list(1,2,3).foldRight((x,y)->cons(x,y), list(4,5,6))
                        .equals(list(1,2,3,4,5,6))
        );

        /*
            We can count the number of elements in a list using the function length, defined by
            length = foldr count 0
            count a n = n + 1
         */
        F2<Integer, Integer, Integer> count = (x, soFar) -> soFar+1;
        assertThat(
                list(1,5,100).foldRight(count,0) == 3
        );



        /*
            A function that doubles all the elements of a list could be written as
            doubleall = foldr doubleandcons Nil
            where
            doubleandcons n list = Cons (2 ∗ n) list
         */

        F2<Integer,List<Integer>,List<Integer>> doubleAndCons = (x,xs) -> cons(2*x, xs);
        assertThat(
                list(1,2,4).foldRight(doubleAndCons,nil())
                        .equals(list(2,4,8))
        );


        /*
        The function doubleandcons can be modularized even further, first into
        doubleandcons = f andcons double
        where
        double n = 2 ∗ n
        f andcons f el list = Cons (f el ) list
         */
        F<Integer,Integer> doubleIt = x -> 2*x;
        doubleAndCons = andCons(doubleIt);
        assertThat(
                list(1,2,4).foldRight(doubleAndCons,nil())
                        .equals(list(2,4,8))
        );


        /*
         *  and then by
            f andcons f = Cons . f
            where “.” (function composition, a standard operator) is defined by
            (f . g) h = f (g h)
            We can see that the new definition of f andcons is correct by applying it to some
            arguments:
            f andcons f el
            = (Cons . f ) el
            = Cons (f el )
            so
            f andcons f el list = Cons (f el ) list
            The final version is
            doubleall = foldr (Cons . double) Nil
         */
        assertThat(
                list(1, 2, 4).foldRight( compose(cons(), doubleIt), nil() )
                        .equals(list(2, 4, 8))
        );

        /*
            With one further modularization we arrive at
            doubleall = map double
            map f = foldr (Cons . f ) Nil

            ,where map — another generally useful function — applies any function f to all
            the elements of a list.
         */

        assertThat(
                list(1, 2, 4).map( doubleIt )
                        .equals(list(2, 4, 8))
        );




    }

    private static int sumSpecific(List<Integer> xs) {
        /*
         * sum Nil = 0
           sum (Cons n list) = n + sum list
         */
        if(xs.isEmpty()) {
            return 0;
        } else {
            return xs.head() + sumSpecific(xs.tail());
        }
    }

    private static void println(Object... objs) {
        for(Object o : objs) {
            System.out.println(o);
        }
    }

    private static void assertThat(boolean val) {
        if(!val) {
            throw new AssertionError();
        }
    }

    private static <A,B> F2<B,List<A>,List<A>> andCons(F<B,A> f) {
        return (x,xs) -> cons( f.f(x), xs );
    }

}
