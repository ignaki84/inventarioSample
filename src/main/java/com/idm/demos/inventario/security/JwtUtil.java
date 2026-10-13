package com.idm.demos.inventario.security;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
	
	@Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    @Value("${jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    @Value("${jwt.issuer}")
    private String issuer;
    
    public String generarToken(UserDetails userDetails) {
    	Map<String, Object> extraClaims = new HashMap<>();
    	
    	List<String> permisos = userDetails.getAuthorities().stream()
    			.map(GrantedAuthority::getAuthority)
    			.collect(Collectors.toList());
    	extraClaims.put("permisos", permisos);
    	
    	return buildToken(extraClaims,userDetails);
    }
    
    public String generarRefreshToken(String idUsuario) {
    	return Jwts.builder()
                .setSubject(idUsuario)
                .setIssuer(issuer)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extraerIdUsuario(String token) {
        return extraerClaim(token, Claims::getSubject);
    }

    public boolean validarToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return !extraerClaim(token, Claims::getExpiration).before(new Date());
        } catch (Exception e) {
            return false;
        }
    }
    
    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails) {
    	return Jwts.builder()
        .setSubject(userDetails.getUsername())
        .setIssuer(issuer)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
        .addClaims(extraClaims)
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .compact();
    }

    private <T> T extraerClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
    }

    private Key getSigningKey() {
        // Genera la llave a partir del secret inyectado desde el properties/yaml
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

}
