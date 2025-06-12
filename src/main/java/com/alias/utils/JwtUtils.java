package com.alias.utils;

import cn.hutool.core.date.DateUtil;
import com.alias.model.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JwtUtils {
    private static final String SECRET = "secret-key-alias";

    public static String generateToken(User user) {
        return Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .claim("openId", user.getOpenId())
                .setIssuedAt(new Date())
                .setExpiration(DateUtil.offsetDay(new Date(), 5))
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();
    }

    public static Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody();
        return Long.parseLong(claims.getSubject());
    }
}
