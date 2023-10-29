/*******************************************************************************
 *     ___                  _   ____  ____
 *    / _ \ _   _  ___  ___| |_|  _ \| __ )
 *   | | | | | | |/ _ \/ __| __| | | |  _ \
 *   | |_| | |_| |  __/\__ \ |_| |_| | |_) |
 *    \__\_\\__,_|\___||___/\__|____/|____/
 *
 *  Copyright (c) 2014-2019 Appsicle
 *  Copyright (c) 2019-2023 QuestDB
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 ******************************************************************************/

package io.questdb.test.std.fastdouble;

import io.questdb.log.Log;
import io.questdb.log.LogFactory;
import io.questdb.std.fastdouble.FastDoubleParser;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Random;
import java.util.stream.IntStream;

/**
 * The purpose of the tests in this class is to discover new cases, where
 * {@link FastDoubleParser#parseDouble(CharSequence, boolean)} does not
 * produce the same result like {@link Double#parseDouble(String)}.
 * <p>
 * Unfortunately, the space of input values is huge, it includes
 * all character sequences from lengths in the range
 * [0,{@value Integer#MAX_VALUE}].
 * <p>
 * Each run of this method produces new test cases with
 * input generated by applying the syntax rules for a double
 * value. The generation uses a random number generator.
 * <p>
 * The tests in this class are disabled by default. Enable them for
 * discovering new test cases.
 */
abstract class AbstractLexicallyGeneratedTest {

    private static final Log LOG = LogFactory.getLog(AbstractLexicallyGeneratedTest.class);

    /**
     * Seed for random number generator.
     * Specify a literal number to obtain repeatable tests.
     * Specify System.nanoTime to explore the input space.
     * (Make sure to take a note of the seed value if
     * tests failed.)
     */
    private static final long SEED = System.nanoTime();

    @BeforeClass
    public static void init() {
        LOG.info().$("seed=").$(SEED).$();
    }

    @Test
    public void testAsciiCharacterInputsUpTo4Characters() {
        int maxLength = 4;
        Random rng = new Random();
        IntStream.range(0, 10_000).forEach(i -> {
            char[] ch = new char[4];
            int n = rng.nextInt(maxLength) + 1;
            for (int j = 0; j < n; j++) {
                ch[j] = nextAsciiChar(rng);
            }
            StringBuilder str = new StringBuilder();
            for (int j = 0; j < 4; j++) {
                char c = ch[j];
                if (c >= ' ') {
                    str.append(c);
                }
            }
            testAgainstJdk(str.toString());
        });
    }

    @Test
    public void testRandomStringFrom10SyntaxRuleWithoutWhitespace() {
        Random rng = new Random(SEED);
        LexicalGenerator gen = new LexicalGenerator(false, true);
        IntStream.range(0, 10_000).forEach(i -> {
                    String str = gen.produceRandomInputStringFromLexicalRuleWithoutWhitespace(10, rng);
                    testAgainstJdk(str);
                }
        );
    }

    @Test
    public void testRandomStringFrom1SyntaxRuleWithoutWhitespace() {
        Random rng = new Random(SEED);
        LexicalGenerator gen = new LexicalGenerator(false, true);
        IntStream.range(0, 100).forEach(i -> {
                    String str = gen.produceRandomInputStringFromLexicalRuleWithoutWhitespace(1, rng);
                    testAgainstJdk(str);
                }
        );
    }

    @Test
    public void testRandomStringFrom2SyntaxRuleWithoutWhitespace() {
        Random rng = new Random(SEED);
        LexicalGenerator gen = new LexicalGenerator(false, true);
        IntStream.range(0, 10_000).forEach(i -> {
                    String str = gen.produceRandomInputStringFromLexicalRuleWithoutWhitespace(2, rng);
                    testAgainstJdk(str);
                }
        );
    }

    @Test
    public void testRandomStringFrom3SyntaxRuleWithoutWhitespace() {
        Random rng = new Random(SEED);
        LexicalGenerator gen = new LexicalGenerator(false, true);
        IntStream.range(0, 10_000).forEach(i -> {
                    String str = gen.produceRandomInputStringFromLexicalRuleWithoutWhitespace(3, rng);
                    testAgainstJdk(str);
                }
        );
    }

    @Test
    public void testRandomStringFrom4SyntaxRuleWithoutWhitespace() {
        Random rng = new Random(SEED);
        LexicalGenerator gen = new LexicalGenerator(false, true);
        IntStream.range(0, 10_000).forEach(i -> {
                    String str = gen.produceRandomInputStringFromLexicalRuleWithoutWhitespace(4, rng);
                    testAgainstJdk(str);
                }
        );
    }

    @Test
    public void testRandomStringsOfIncreasingLengthWithWhitespace() {
        Random rng = new Random(SEED);
        LexicalGenerator gen = new LexicalGenerator(false, true);
        IntStream.range(1, 100).forEach(i -> {
                    String str = gen.produceRandomInputStringFromLexicalRuleWithWhitespace(i, rng);
                    testAgainstJdk(str);
                }
        );
    }

    private static char nextAsciiChar(Random rng) {
        //U+0020 SPACE
        //U+007F DELETE
        return (char) (rng.nextInt(0x7f - 0x20) + 0x20);
    }

    /**
     * Given an input String {@code str},
     * tests if {@link FastDoubleParser#parseDouble(CharSequence, boolean)}
     * produces the same result like {@link Double#parseDouble(String)}.
     *
     * @param str the given input string
     */
    protected abstract void testAgainstJdk(String str);
}