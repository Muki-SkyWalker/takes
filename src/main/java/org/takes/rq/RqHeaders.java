/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Yegor Bugayenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.takes.rq;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import lombok.EqualsAndHashCode;
import org.takes.Request;

/**
 * Request decorator, for HTTP headers parsing.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.1
 */
@EqualsAndHashCode(of = "origin")
public final class RqHeaders implements Request {

    /**
     * Original request.
     */
    private final transient Request origin;

    /**
     * Ctor.
     * @param req Original request
     */
    public RqHeaders(final Request req) {
        this.origin = req;
    }

    /**
     * Get single header.
     * @param key Header name
     * @return List of values (can be empty)
     * @throws IOException If fails
     */
    public List<String> header(final String key) throws IOException {
        List<String> values = this.map().get(
            key.toLowerCase(Locale.ENGLISH)
        );
        if (values == null) {
            values = Collections.emptyList();
        }
        return Collections.unmodifiableList(values);
    }

    /**
     * Get all header names.
     * @return All names
     * @throws IOException If fails
     */
    public Collection<String> headers() throws IOException {
        return this.map().keySet();
    }

    @Override
    public List<String> head() throws IOException {
        return this.origin.head();
    }

    @Override
    public InputStream body() throws IOException {
        return this.origin.body();
    }

    /**
     * Parse them all in a map.
     * @return Map of them
     * @throws IOException If fails
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    private Map<String, List<String>> map() throws IOException {
        final List<String> head = this.origin.head();
        final Map<String, List<String>> map =
            new HashMap<String, List<String>>(head.size());
        for (final String line : head.subList(0, head.size())) {
            final String[] parts = line.split(":", 2);
            final String key = parts[0].toLowerCase(Locale.ENGLISH);
            if (!map.containsKey(key)) {
                map.put(key, new LinkedList<String>());
            }
            map.get(key).add(parts[1]);
        }
        return map;
    }

}
