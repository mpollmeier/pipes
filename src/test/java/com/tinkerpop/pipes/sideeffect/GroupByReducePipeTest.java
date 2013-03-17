package com.tinkerpop.pipes.sideeffect;

import com.tinkerpop.pipes.Pipe;
import com.tinkerpop.pipes.PipeFunction;
import com.tinkerpop.pipes.branch.LoopPipe;
import junit.framework.TestCase;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class GroupByReducePipeTest extends TestCase {

    public void testPipeBasic() {
        GroupByReducePipe<String, String, Integer, Integer> pipe = new GroupByReducePipe<String, String, Integer, Integer>(new PipeFunction<String, String>() {
            public String compute(String argument) {
                return argument.substring(0, 1);
            }
        }, new PipeFunction<String, Integer>() {
            public Integer compute(String argument) {
                return argument.length();
            }
        }, new PipeFunction<Iterator<Integer>, Integer>() {
            public Integer compute(Iterator<Integer> args) {
                int sum = 0;
                while (args.hasNext()) {
                    sum = sum + args.next();
                }
                return sum;
            }
        }
        );
        List<String> starts = Arrays.asList("marko", "josh", "peter", "pavel", "james");
        pipe.setStarts(starts);
        int counter = 0;
        while (pipe.hasNext()) {
            counter++;
            String string = pipe.next();
            assertTrue(starts.contains(string));
        }
        assertEquals(counter, starts.size());
        Map<String, Integer> map = pipe.getSideEffect();
        assertEquals(3, map.size());
        assertEquals(5, map.get("m").intValue());
        assertEquals(9, map.get("j").intValue());
        assertEquals(10, map.get("p").intValue());

    }


    public void untestPipeWithLoopingSameTypeInMapAsReduce(){
        GroupByReducePipe<String, String, Integer, Integer> groupByReducePipe = new GroupByReducePipe<String, String, Integer, Integer>(new PipeFunction<String, String>() {
            public String compute(String argument) {
                return argument.substring(0, 1);
            }
        }, new PipeFunction<String, Integer>() {
            public Integer compute(String argument) {
                return argument.length();
            }
        }, new PipeFunction<Iterator<Integer>, Integer>() {
            public Integer compute(Iterator<Integer> args) {
                int sum = 0;
                while (args.hasNext()) {
                    sum = sum + args.next();
                }
                return sum;
            }
        }
        );
        Pipe<String, String> pipe = new LoopPipe(groupByReducePipe, LoopPipe.createLoopsFunction(4));

        List<String> starts = Arrays.asList("marko", "josh", "peter", "pavel", "james");
        pipe.setStarts(starts);
        int counter = 0;
        while (pipe.hasNext()) {
            counter++;
            String string = pipe.next();
            assertTrue(starts.contains(string));
        }

        assertEquals(counter, starts.size());
        Map<String, Integer> map = groupByReducePipe.getSideEffect();
        assertEquals(3, map.size());
        assertEquals(15, map.get("m").intValue());
        assertEquals(30, map.get("p").intValue());
        assertEquals(27, map.get("j").intValue());
    }

    public void untestPipeWithLoopingDifferentTypeInMapAsReduce(){
        GroupByReducePipe<String, String, String, Integer> groupByReducePipe = new GroupByReducePipe<String, String, String, Integer>(new PipeFunction<String, String>() {
            public String compute(String argument) {
                return argument.substring(0, 1);
            }
        }, new PipeFunction<String, String>() {
            public String compute(String argument) {
                return argument;
            }
        }, new PipeFunction<Iterator<String>, Integer>() {
            public Integer compute(Iterator<String> args) {
                int sum = 0;
                while (args.hasNext()) {
                    sum = sum + args.next().length();
                }
                return sum;
            }
        }
        );
        Pipe<String, String> pipe = new LoopPipe(groupByReducePipe, LoopPipe.createLoopsFunction(4));

        List<String> starts = Arrays.asList("marko", "josh", "peter", "pavel", "james");
        pipe.setStarts(starts);
        int counter = 0;
        while (pipe.hasNext()) {
            counter++;
            String string = pipe.next();
            assertTrue(starts.contains(string));
        }

        assertEquals(counter, starts.size());
        Map<String, Integer> map = groupByReducePipe.getSideEffect();
        assertEquals(3, map.size());
        assertEquals(15, map.get("m").intValue());
        assertEquals(30, map.get("p").intValue());
        assertEquals(27, map.get("j").intValue());
    }
}