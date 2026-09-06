package br.com.digidatasistemas.starterPackage.controller.dto.response;

import java.util.List;
import java.util.Map;

public record LoginResponse(
        String token,
        Long expiresInToken,
        String nome,
        String username,
        Map<String, List<String>> resource
) {}
