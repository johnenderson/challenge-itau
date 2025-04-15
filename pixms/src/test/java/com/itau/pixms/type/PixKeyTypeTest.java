package com.itau.pixms.type;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PixKeyTypeTest {

    @Test
    @DisplayName("Deve validar chaves Pix corretamente para cada tipo")
    void testIsValid() {
        assertTrue(PixKeyType.CPF.isValid("12345678901"));
        assertFalse(PixKeyType.CPF.isValid("123"));

        assertTrue(PixKeyType.CNPJ.isValid("12345678000199"));
        assertFalse(PixKeyType.CNPJ.isValid("abcdef"));

        assertTrue(PixKeyType.PHONE.isValid("+5511987654321"));
        assertFalse(PixKeyType.PHONE.isValid("11987654321"));

        assertTrue(PixKeyType.EMAIL.isValid("pix@bcb.gov.br"));
        assertTrue(PixKeyType.EMAIL.isValid("PIX@BCB.GOV.BR"));
        assertFalse(PixKeyType.EMAIL.isValid("email.com"));

        assertTrue(PixKeyType.EVP.isValid("123e4567-e89b-12d3-a456-426655440000"));
        assertFalse(PixKeyType.EVP.isValid("not-a-uuid"));
    }

    @Test
    @DisplayName("Deve detectar corretamente o tipo de chave Pix com detectType")
    void testDetectType() {
        assertEquals(PixKeyType.CPF, PixKeyType.detectType("12345678901"));
        assertEquals(PixKeyType.CNPJ, PixKeyType.detectType("12345678000199"));
        assertEquals(PixKeyType.PHONE, PixKeyType.detectType("+5511987654321"));
        assertEquals(PixKeyType.EMAIL, PixKeyType.detectType("pix@bcb.gov.br"));
        assertEquals(PixKeyType.EVP, PixKeyType.detectType("123e4567-e89b-12d3-a456-426655440000"));

        assertNull(PixKeyType.detectType("chaveinvalida"));
        assertNull(PixKeyType.detectType(null));
    }

    @Test
    @DisplayName("Não deve aceitar e-mails com mais de 77 caracteres")
    void testEmailMaxLength() {
        String tooLongEmail = "a".repeat(68) + "@mail.com"; // 68 + 9 = 77 (válido)
        assertTrue(PixKeyType.EMAIL.isValid(tooLongEmail));

        String invalidLongEmail = "a".repeat(69) + "@mail.com"; // 69 + 9 = 78 (inválido)
        assertFalse(PixKeyType.EMAIL.isValid(invalidLongEmail));
    }

}