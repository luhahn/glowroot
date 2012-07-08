/**
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.informantproject.plugin.servlet;

import java.util.Collections;
import java.util.Map;

import javax.annotation.Nullable;

import org.informantproject.api.UnresolvedMethod;

/**
 * @author Trask Stalnaker
 * @since 0.5
 */
// HttpServletRequest wrapper does not make assumptions about the @Nullable properties of the
// underlying javax.servlet.http.HttpServletRequest since it's just an interface and could
// theoretically return null even where it seems to not make sense
class HttpServletRequest {

    private static final UnresolvedMethod getSessionOneArgMethod = new UnresolvedMethod(
            "javax.servlet.http.HttpServletRequest", "getSession", boolean.class);
    private static final UnresolvedMethod getMethodMethod = new UnresolvedMethod(
            "javax.servlet.http.HttpServletRequest", "getMethod");
    private static final UnresolvedMethod getRequestURIMethod = new UnresolvedMethod(
            "javax.servlet.http.HttpServletRequest", "getRequestURI");
    private static final UnresolvedMethod getParameterMapMethod = new UnresolvedMethod(
            "javax.servlet.http.HttpServletRequest", "getParameterMap");
    private static final UnresolvedMethod getAttributeMethod = new UnresolvedMethod(
            "javax.servlet.http.HttpServletRequest", "getAttribute", String.class);

    private final Object realRequest;

    private HttpServletRequest(Object realRequest) {
        this.realRequest = realRequest;
    }

    @Nullable
    HttpSession getSession(boolean create) {
        Object realSession = getSessionOneArgMethod.invoke(realRequest, create);
        if (realSession == null) {
            return null;
        } else {
            return HttpSession.from(realSession);
        }
    }

    @Nullable
    String getMethod() {
        return (String) getMethodMethod.invoke(realRequest);
    }

    @Nullable
    String getRequestURI() {
        return (String) getRequestURIMethod.invoke(realRequest);
    }

    Map<?, ?> getParameterMap() {
        Map<?, ?> parameterMap = (Map<?, ?>) getParameterMapMethod.invoke(realRequest);
        if (parameterMap == null) {
            return Collections.emptyMap();
        } else {
            return parameterMap;
        }
    }

    @Nullable
    Object getAttribute(String name) {
        return getAttributeMethod.invoke(realRequest, name);
    }

    static HttpServletRequest from(Object realRequest) {
        return new HttpServletRequest(realRequest);
    }
}
