package com.example.shuttlemobile.util;

import java.util.Base64;

public class JWTDecoder {
    public static String getPayloadJSON(String token) {
        String[] chunks = token.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String header = new String(decoder.decode(chunks[0]));
        String payload = new String(decoder.decode(chunks[1]));
        return payload;

//         Example result:
//         {"iss":"Shuttle-back","sub":"troy@gmail.com","aud":"web","iat":1672855832,"exp":1672857632,"id":3,"role":[{"name":"passenger"}]}
//
//         {
//             "iss": "Shuttle-back",
//             "sub": "troy@gmail.com",
//             "aud": "web",
//             "iat": 1672855832,
//             "exp": 1672857632,
//             "id":  3,
//             "role":[ {"name": "passenger"} ]
//         }
    }
}
