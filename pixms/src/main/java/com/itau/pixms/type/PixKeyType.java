package com.itau.pixms.type;

import java.util.regex.Pattern;

/**
 * See https://www.bcb.gov.br/content/estabilidadefinanceira/pix/API-DICT.html#tag/Key
 */
public enum PixKeyType {
    CPF("CPF","^[0-9]{11}$"),
    CNPJ("CNPJ",  "^[0-9]{14}$"),
    PHONE("PHONE", "^\\+[1-9]\\d{1,14}$"),
    EMAIL("EMAIL", "^[a-z0-9.!#$&'*+\\/=?^_`{|}~-]+@[a-z0-9](?:[a-z0-9-]{0,61}[a-z0-9])?(?:\\.[a-z0-9](?:[a-z0-9-]{0,61}[a-z0-9])?)*$"),
    EVP("EVP", "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$");

    private final String type;
    private final String regex;
    private final Pattern pattern;

    PixKeyType(String type, String regex) {
        this.type = type;
        this.regex = regex;
        this.pattern = Pattern.compile(regex);
    }

    public boolean isValid(String value) {
        if (value == null) return false;
        if (this == EMAIL && value.length() > 77) return false;
        return pattern.matcher(value.toLowerCase()).matches();
    }

    public static PixKeyType detectType(String value) {
        if (value == null) return null;
        for (PixKeyType type : values()) {
            if (type.isValid(value)) {
                return type;
            }
        }
        return null; // ou lançar exceção, dependendo do caso
    }

    public String getType() {
        return type;
    }

    public String getRegex() {
        return regex;
    }

}
