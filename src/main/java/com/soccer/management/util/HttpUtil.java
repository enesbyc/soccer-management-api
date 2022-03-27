package com.soccer.management.util;

import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author enes.boyaci
 */
public class HttpUtil {

    public static String getTokenFromHeader() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder
                        .getRequestAttributes();
        if (Objects.isNull(servletRequestAttributes))
            return null;
        HttpServletRequest request = servletRequestAttributes.getRequest();
        String header = request.getHeader("Authorization");
        if (Objects.nonNull(header)) {
            String[] arr = header.split("\\s+");
            if (arr.length == 2)
                return header.split("\\s+")[1];

        }
        return "";
    }

}
