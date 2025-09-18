package co.com.pragma.reportes.authentication.utils;

import java.util.List;

public record TokenVerification(
        String subject,
        List<String> authorities,
        boolean valid
) {
}
